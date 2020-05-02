package com.ihrm.system.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.system.Permission;
import com.ihrm.system.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
public class PermissionController extends BaseController{
    @Autowired
    private PermissionService permissionService;
    //1.保存
    @RequestMapping(value = "/permission",method = RequestMethod.POST)
    public Result save(@RequestBody Map<String,Object> map)throws Exception{
        permissionService.save(map);
        return Result.SUCCESS();
    }
    //2.更新
    @RequestMapping(value = "/permission/{id}",method = RequestMethod.PUT)
    public Result update(@PathVariable String id,@RequestBody Map<String,Object> map) throws Exception{
        map.put("id",id);
        permissionService.update(map);
        return Result.SUCCESS();
    }

    //删除用户
    @RequestMapping(value = "/permission/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable(name = "id") String id) throws Exception {
        permissionService.delete(id);
        return Result.SUCCESS();
    }

    /**
     * 根据ID查询用户
     */
    @RequestMapping(value = "/permission/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable(name = "id") String id) throws Exception {
        Map<String,Object> map = permissionService.getById(id);
        return new Result(ResultCode.SUCCESS,map);
    }

    /**
     * 分页查询用户
     */
    @RequestMapping(value = "/permission", method = RequestMethod.GET)
    public Result findAll(@RequestParam Map map){
        List<Permission> list = permissionService.findAll(map);
        return new Result(ResultCode.SUCCESS,list);
    }
}
