package com.ihrm.system.service;

import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.constant.PermissionConstants;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.Role;
import com.ihrm.system.dao.PermissionDao;
import com.ihrm.system.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/22 10:24
 */
@Service
public class RoleService extends BaseService<Role>{
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private PermissionDao permissionDao;

    public void save(Role role){
        role.setId(idWorker.nextId()+"");
        roleDao.save(role);
    }

    public void update(Role role){
        Role target = roleDao.getOne(role.getId());
        target.setDescription(role.getDescription());
        target.setName(role.getName());
        roleDao.save(target);
    }

    public Role getById(String id){
        return roleDao.findById(id).get();
    }

    public List<Role> findAll(String companyId){
        return roleDao.findAll(getSpecification(companyId));
    }

    public void delete(String id){
        roleDao.deleteById(id);
    }

    public Page<Role> findByPage(String compangyId,int page,int size){
        return roleDao.findAll(getSpecification(compangyId),PageRequest.of(page-1,size));
    }

    /**
     * 给角色分配权限
     * @param roleId
     * @param permIds
     */
    public void assignPerm(String roleId, List<String> permIds) {
        Role role = roleDao.findById(roleId).get();
        Set<Permission> permissions = new HashSet<>();
        for (String permId : permIds) {
            Permission permission = permissionDao.findById(permId).get();
            //需要根据父id和类型查询API权限列表
            List<Permission> perms = permissionDao.findByTypeAndPid(PermissionConstants.PERMISSION_API, permission.getId());
            permissions.addAll(perms); //自定赋予API权限
            permissions.add(permission);//当前菜单或按钮的权限
        }
        //3.设置角色和权限的关系
        role.setPermissions(permissions);
        roleDao.save(role);
    }
}
