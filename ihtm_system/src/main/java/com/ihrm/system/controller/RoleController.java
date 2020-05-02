package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.PageResult;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.system.Role;
import com.ihrm.domain.system.response.RoleResult;
import com.ihrm.system.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/22 10:36
 */
@RestController
@RequestMapping("/system")
public class RoleController extends BaseController{
    @Autowired
    private RoleService roleService;

    @RequestMapping(value = "/role",method = RequestMethod.POST)
    public Result addRole(@RequestBody Role role)throws Exception{
        role.setCompanyId(companyId);
        roleService.save(role);
        return Result.SUCCESS();
    }

    @RequestMapping(value = "/role/{id}",method = RequestMethod.PUT)
    public Result updateRole(@PathVariable(name = "id")String id, @RequestBody Role role){
        roleService.update(role);
        return Result.SUCCESS();
    }

    @RequestMapping(value = "/role/{id}",method = RequestMethod.DELETE)
    public Result deleteRole(@PathVariable(name = "id")String id){
        roleService.delete(id);
        return Result.SUCCESS();
    }

    @RequestMapping(value = "/role/{id}",method = RequestMethod.GET)
    public Result findById(@PathVariable(name = "id")String id){
        Role role = roleService.getById(id);
        RoleResult roleResult = new RoleResult(role);
        return new Result(ResultCode.SUCCESS,roleResult);
    }

    @RequestMapping(value = "/role/list",method = RequestMethod.GET)
    public Result findAll(){
        System.out.println(StringUtils.isEmpty(companyId));
        List<Role> roles = roleService.findAll(companyId);
        return new Result(ResultCode.SUCCESS,roles);
    }

    @RequestMapping(value = "/role",method = RequestMethod.GET)
    public Result findByPage(int page,int pagesize){
        Page<Role> rolePage = roleService.findByPage(companyId, page, pagesize);
        PageResult<Role> rolePageResult = new PageResult<>(rolePage.getTotalElements(), rolePage.getContent());
        return new Result(ResultCode.SUCCESS,rolePageResult);
    }

    //给角色分配权限
    @RequestMapping(value = "/role/assignPerm",method = RequestMethod.PUT)
    public Result assignPerm(@RequestBody Map<String,Object> map)throws Exception{
        String roleId = (String) map.get("roleId");
        List<String> permIds = (List<String>) map.get("permIds");
        roleService.assignPerm(roleId,permIds);
        return Result.SUCCESS();
    }

}
