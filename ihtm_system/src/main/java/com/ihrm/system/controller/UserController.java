package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.poi.ExcelImportUtil;
import com.ihrm.common.utils.JwtUtils;
import com.ihrm.domain.system.User;
import com.ihrm.domain.system.response.ProfileResult;
import com.ihrm.domain.system.response.UserResult;
import com.ihrm.system.client.DepartmentFeignClient;
import com.ihrm.system.service.PermissionService;
import com.ihrm.system.service.UserService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/21 22:17
 */
@CrossOrigin  //解决跨域
@RestController
@RequestMapping("/system")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private PermissionService permissionService;
    @Autowired
    private DepartmentFeignClient client;

    @RequestMapping(value = "/user/upload/{id}")
    public Result upload(@PathVariable String id,@RequestParam(name = "file")MultipartFile file)throws Exception {
        System.out.println(id);
        String imgUrl = userService.uploadImage(id,file);
        return new Result(ResultCode.SUCCESS,imgUrl);
    }

    /**
     * excel导入
     * @param file
     * @return
     * @throws Exception
     */
   /* @RequestMapping(value = "/user/import",method = RequestMethod.POST)
    public Result importUser(@RequestParam(name = "file") MultipartFile file)throws Exception{
        //1.解析Excel
        //1.1.根据Excel文件创建工作簿
        XSSFWorkbook workbook = new XSSFWorkbook(file.getInputStream());
        //1.2.获取Sheet
        XSSFSheet sheet = workbook.getSheetAt(0);
        //1.3.获取Sheet中的每一行，和每一个单元格
        //2.获取用户数据列表
        List<User> list = new ArrayList<>();
        for (int rowNum = 1; rowNum <=sheet.getLastRowNum() ; rowNum++) {
            Row row = sheet.getRow(rowNum);
            Object[] values = new Object[row.getLastCellNum()];
            for (int cellNum = 1; cellNum <row.getLastCellNum() ; cellNum++) {
                Cell cell = row.getCell(cellNum);
                Object value = getCellValue(cell);
                values[cellNum] = value;
            }
            User user = new User(values);
            list.add(user);
        }
        //3.批量保存用户
        userService.saveAll(list,companyId,companyName);
        return new Result(ResultCode.SUCCESS);
    }*/

    /**
     * 使用excel工具类实现excel导入
     * @param file
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/user/import",method = RequestMethod.POST)
    public Result importUser(@RequestParam(name = "file") MultipartFile file)throws Exception{
        List<User> list = new ExcelImportUtil<User>(User.class).readExcel(file.getInputStream(), 1, 1);
        //3.批量保存用户
        userService.saveAll(list,companyId,companyName);
        return new Result(ResultCode.SUCCESS);
    }

    private Object getCellValue(Cell cell) {
        //1.获取到单元格的属性类型
        CellType cellType = cell.getCellType();
        //2.根据单元格数据类型获取数据
        Object value = null;
        switch (cellType){
            case STRING:
                value=cell.getStringCellValue();
                break;
            case BOOLEAN:
                value=cell.getBooleanCellValue();
                break;
            case NUMERIC:
                if(DateUtil.isCellDateFormatted(cell)){
                    value=cell.getDateCellValue();
                }else {
                    value=cell.getNumericCellValue();
                }
                break;
            case FORMULA:
                value=cell.getCellFormula();
                break;
            default:
                break;
        }
        return value;
    }

    //测试通过系统微服务调用企业服务的方法
    @RequestMapping(value = "/test/{id}")
    public Result testFeign(@PathVariable("id")String id) throws Exception {
        Result result = client.findById(id);
        return result;
    }
    //1.保存
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public Result save(@RequestBody User user) throws Exception {
        user.setCompanyId(companyId);
        user.setCompanyName(companyName);
        userService.save(user);
        return Result.SUCCESS();
    }

    //2.更新
    @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
    public Result update(@PathVariable String id, @RequestBody User user) throws Exception {
        user.setId(id);
        userService.update(user);
        return Result.SUCCESS();
    }

    //删除用户
    @RequiresPermissions("API-USER-DELETE")
    @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE,name = "API-USER-DELETE")
    public Result delete(@PathVariable(name = "id") String id) throws Exception {
        userService.delete(id);
        return Result.SUCCESS();
    }

    /**
     * 根据ID查询用户
     */
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable(name = "id") String id) throws Exception {
        User user = userService.getById(id);
        UserResult userResult = new UserResult(user);
        return new Result(ResultCode.SUCCESS, userResult);
    }

    /**
     * 分页查询用户
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public Result findUserList(@RequestParam Map map, int page, int size) {
        map.put("companyId", companyId);
        Page<User> userPage = userService.findAll(map, page, size);
        PageResult<User> userPageResult = new PageResult<>(userPage.getTotalElements(), userPage.getContent());
        return new Result(ResultCode.SUCCESS, userPageResult);
    }

    //给用户分配角色
    @RequestMapping(value = "/user/assignRoles", method = RequestMethod.PUT)
    public Result assignRoles(@RequestBody Map<String, Object> map) throws Exception {
        //1.获取用户id
        String userId = (String) map.get("id");
        //2.获取分配的角色列表
        List<String> roleIds = (List<String>) map.get("roleIds");
        //3.调用service完成角色分配
        userService.assignRoles(userId, roleIds);
        return Result.SUCCESS();
    }

    /**
     * 登录
     *
     * @param loginMap
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result login(@RequestBody Map<String, Object> loginMap) throws Exception {
        String mobile = (String) loginMap.get("mobile");
        String password = (String) loginMap.get("password");
        //1.jwt方式
//        User user = userService.findByMobile(mobile);
//        if (user == null || !user.getPassword().equals(password)) {
//            return new Result(ResultCode.MOBILEORPASSWORDERROR);
//        } else {
//            StringBuffer sb = new StringBuffer();
//            for (Role role : user.getRoles()) {
//                for (Permission perm : role.getPermissions()) {
//                    if(perm.getType()== PermissionConstants.PERMISSION_API){
//                        sb.append(perm.getCode()).append(",");
//                    }
//                }
//            }
//            Map<String, Object> map = new HashMap<>();
//            map.put("apis",sb.toString());
//            map.put("companyId", user.getCompanyId());
//            map.put("companyName", user.getCompanyName());
//            String token = jwtUtils.createJwt(user.getId(), user.getUsername(), map);
//            return new Result(ResultCode.SUCCESS, token);
//        }
        //2.shiro登录
        try {
            //1.构造登录令牌 UsernamePasswordToken
            //加密密码
            password = new Md5Hash(password,mobile,3).toString();
            UsernamePasswordToken upToken = new UsernamePasswordToken(mobile,password);
            //2.获取subject
            Subject subject = SecurityUtils.getSubject();
            //3.调用login方法，进入realm完成认证
            subject.login(upToken);
            //4.获取sessionId
            String sessionId = (String) subject.getSession().getId();
            //5.构造返回结果
            return new Result(ResultCode.SUCCESS,sessionId);
        }catch (Exception e){
            return new Result(ResultCode.MOBILEORPASSWORDERROR);
        }
    }

    /**
     * 用户登录成功之后，获取用户信息
     * 1.获取用户id
     * 2.根据用户id查询用户
     * 3.构建返回值对象
     * 4.响应
     */

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public Result profile(HttpServletRequest request) throws Exception {
        //jwt认证
       /* String userId = claims.getId();
        User user = userService.getById(userId);
        //根据不同的级别获取用户权限
        ProfileResult result = null;
        if (UserLevel.USER.equals(user.getLevel())) {
            result = new ProfileResult(user);
        } else {
            Map map = new HashMap();
            if (UserLevel.COADMIN.equals(user.getLevel())) {
            map.put("enVisible","1");
            }
            List<Permission> list = permissionService.findAll(map);
            result = new ProfileResult(user,list);
        }
        return new Result(ResultCode.SUCCESS, result);
    }*/
        //获取session中的安全数据
        Subject subject = SecurityUtils.getSubject();
        //1.subject获取所有的安全数据集合
        PrincipalCollection principals = subject.getPrincipals();
        //2.获取安全数据
        ProfileResult result = (ProfileResult) principals.getPrimaryPrincipal();
        return new Result(ResultCode.SUCCESS,result);
    }
    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public Result test(HttpServletRequest request) throws Exception {
        throw new CommonException(false,10002,"出错了");
    }

}
