package com.ihrm.system.service;

import com.baidu.aip.util.Base64Util;
import com.ihrm.common.entity.UserLevel;
import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.common.utils.QiniuUploadUtil;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.User;
import com.ihrm.system.client.DepartmentFeignClient;
import com.ihrm.system.dao.RoleDao;
import com.ihrm.system.dao.UserDao;
import com.ihrm.system.utils.BaiduAiUtil;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/21 21:44
 */
@Service
public class UserService extends BaseService<User> {
    @Autowired
    private UserDao userDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private RoleDao roleDao;
    @Autowired
    private DepartmentFeignClient client;

    public void save(User user) {
        String id = idWorker.nextId() + "";
        String password = new Md5Hash("123456", user.getMobile(), 3).toString();
        user.setPassword(password);
        user.setLevel(UserLevel.USER);
        user.setEnableState(1);
        user.setId(id);
        userDao.save(user);
    }

    public static void main(String[] args) {
        System.out.println(new Md5Hash("123456", "13800000001", 3).toString());
    }

    public void update(User user) {
        User target = userDao.findById(user.getId()).get();
        target.setUsername(user.getUsername());
        String password = new Md5Hash(user.getPassword(), user.getMobile(), 3).toString();
        target.setPassword(password);
        target.setDepartmentId(user.getDepartmentId());
        target.setDepartmentName(user.getDepartmentName());
        userDao.save(target);
    }

    public User getById(String id) {
        return userDao.findById(id).get();
    }

    public Page<User> findAll(Map<String, Object> map, int page, int size) {
        //1.需要查询条件
//        Specification<User> spec = new Specification<User>() {
//            @Nullable
//            @Override
//            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//                List<Predicate> list = new ArrayList<>();
//                //2.动态拼接查询条件
//                if(!StringUtils.isEmpty(map.get("companyId"))){
//                    list.add(criteriaBuilder.equal(root.get("companyId").as(String.class),(String)map.get("companyId")));
//                }
//                if(!StringUtils.isEmpty(map.get("departmentId"))){
//                    list.add(criteriaBuilder.equal(root.get("departmentId").as(String.class),(String)map.get("departmentId")));
//                }
//                if(StringUtils.isEmpty(map.get("hasDept"))||"0".equals((String) map.get("hasDept"))){
//                    list.add(criteriaBuilder.isNull(root.get("departmentId")));
//                }else {
//                    list.add(criteriaBuilder.isNotEmpty(root.get("departmentId")));
//                }
//                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
//            }
//        };
        //1.需要查询条件
//        Specification<User> spec = new Specification<User>() {
//            /**
//             * 动态拼接查询条件
//             * @return
//             */
//            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//                List<Predicate> list = new ArrayList<>();
//                //根据请求的companyId是否为空构造查询条件
//                if (!StringUtils.isEmpty(map.get("companyId"))) {
//                    list.add(criteriaBuilder.equal(root.get("companyId").as(String.class), (String) map.get("companyId")));
//                }
//                //根据请求的部门id构造查询条件
//                if (!StringUtils.isEmpty(map.get("departmentId"))) {
//                    list.add(criteriaBuilder.equal(root.get("departmentId").as(String.class), (String) map.get("departmentId")));
//                }
//                if (!StringUtils.isEmpty(map.get("hasDept"))) {
//                    //根据请求的hasDept判断  是否分配部门 0未分配（departmentId = null），1 已分配 （departmentId ！= null）
//                    if ("0".equals((String) map.get("hasDept"))) {
//                        list.add(criteriaBuilder.isNull(root.get("departmentId")));
//                    } else {
//                        list.add(criteriaBuilder.isNotNull(root.get("departmentId")));
//                    }
//                }
//                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
//            }
//        };
//
//        //2.分页
//        Page<User> pageUser = userDao.findAll(spec, new PageRequest(page - 1, size));
//        return pageUser;
        return userDao.findAll(createSpecification(map), PageRequest.of(page - 1, size));
    }

    private Specification<User> createSpecification(Map<String, Object> map) {
        return new Specification<User>() {
            @Override
            public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                //2.动态拼接查询条件
                if (!StringUtils.isEmpty(map.get("companyId"))) {
                    list.add(criteriaBuilder.equal(root.get("companyId").as(String.class), (String) map.get("companyId")));
                }
                if (!StringUtils.isEmpty(map.get("departmentId"))) {
                    list.add(criteriaBuilder.equal(root.get("departmentId").as(String.class), (String) map.get("departmentId")));
                }
                if (!StringUtils.isEmpty(map.get("hasDept"))) {
                    if ("0".equals((String) map.get("hasDept"))) {
                        list.add(criteriaBuilder.isNull(root.get("departmentId")));

                    } else {
                        list.add(criteriaBuilder.isNotEmpty(root.get("departmentId")));
                    }
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
    }

    public void delete(String id) {
        userDao.deleteById(id);
    }

    //给用户分配角色
    public void assignRoles(String userId, List<String> roleIds) {
        User user = userDao.findById(userId).get();
        Set<Role> roles = new HashSet<>();
        //2.设置用户的角色集合
        for (String roleId : roleIds) {
            Role role = roleDao.findById(roleId).get();
            roles.add(role);
        }
        //设置用户和角色集合的关系
        user.setRoles(roles);
        //3.更新用户
        userDao.save(user);
    }

    //登录
    public User findByMobile(String moblie) {
        return userDao.getUserByMobile(moblie);
    }

    @Transactional
    public void saveAll(List<User> list, String companyId, String companyName) {
        for (User user : list) {
            user.setPassword(new Md5Hash("123456", user.getMobile(), 3).toString());
            user.setId(idWorker.nextId() + "");
            user.setCompanyId(companyId);
            user.setCompanyName(companyName);
            user.setInServiceStatus(1);
            user.setEnableState(1);
            user.setLevel(UserLevel.USER);

            //填充部门信息
            Department department = client.findByCode(user.getDepartmentId(), companyId);
            if (department != null) {
                user.setDepartmentId(department.getId());
                user.setDepartmentName(department.getName());

            }
            userDao.save(user);
        }
    }

    /**
     * 使用dataUrl完成头像上传
     * @param id
     * @param file
     * @return
     */
    /*public String uploadImage(String id, MultipartFile file) throws Exception {
        //根据id查询用户
        User user = userDao.findById(id).get();
        //对上传文件进行Base64编码
        String encode = Base64.encode(file.getBytes());
        System.out.println(encode);
        //拼接DataURL数据头
        String imgUrl = new String("data:image/jpg;base64," + encode);
        //保存图片信息
        user.setStaffPhoto(imgUrl);
        userDao.save(user);
        return imgUrl;
    }*/

    @Autowired
    private BaiduAiUtil baiduAiUtil;
    /**
     * 使用七牛云存储完成头像上传
     *注册到百度云AI人脸库
     * @param id
     * @param file
     * @return
     * @throws Exception
     */
    public String uploadImage(String id, MultipartFile file) throws Exception {
        //根据id查询用户
        User user = userDao.findById(id).get();
        //对上传文件进行Base64编码
        String imgUrl = new QiniuUploadUtil().upload(user.getId(), file.getBytes());
        //保存图片信息
        if (!StringUtils.isEmpty(imgUrl)) {
            user.setStaffPhoto(imgUrl);
            userDao.save(user);
        }

        //判断是否注册面部信息
        Boolean aBoolean = baiduAiUtil.faceExit(id);
        String imageBase64 = Base64Util.encode(file.getBytes());
        if(aBoolean){
            //存在=>更新
            baiduAiUtil.faceUpdate(id,imageBase64);
        }else {
            //不存=>注册
            baiduAiUtil.faceRegister(id,imageBase64);
        }
        return imgUrl;
    }
}
