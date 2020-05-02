package com.ihrm.system.service;

import com.ihrm.common.entity.ResultCode;
import com.ihrm.common.exception.CommonException;
import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.BeanMapUtils;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.domain.constant.PermissionConstants;
import com.ihrm.domain.system.Permission;
import com.ihrm.domain.system.PermissionApi;
import com.ihrm.domain.system.PermissionMenu;
import com.ihrm.domain.system.PermissionPoint;
import com.ihrm.system.dao.PermissionApiDao;
import com.ihrm.system.dao.PermissionDao;
import com.ihrm.system.dao.PermissionMenuDao;
import com.ihrm.system.dao.PermissionPointDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/21 21:44
 */
@Service
@Transactional
public class PermissionService extends BaseService<Permission> {
    @Autowired
    private PermissionDao permissionDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private PermissionMenuDao permissionMenuDao;
    @Autowired
    private PermissionPointDao permissionPointDao;
    @Autowired
    private PermissionApiDao permissionApiDao;

    //1保存
    public void save(Map<String, Object> map) throws Exception {
        //设置主键的值
        String id = idWorker.nextId() + "";
        //1.通过map构造permission对象
        Permission permission = BeanMapUtils.mapToBean(map, Permission.class);
        permission.setId(id);
        //2保存资源
        saveResource(map, id, permission);
        //3.保存
        permissionDao.save(permission);
    }

    //2保存资源
    private void saveResource(Map<String, Object> map, String id, Permission permission) throws Exception {
        int type = permission.getType();
        //2.根据类型构造不同的资源对象（菜单，按钮，api）
        switch (type) {
            case PermissionConstants.PERMISSION_MENU:
                PermissionMenu permissionMenu = BeanMapUtils.mapToBean(map, PermissionMenu.class);
                permissionMenu.setId(id);
                permissionMenuDao.save(permissionMenu);
                break;
            case PermissionConstants.PERMISSION_POINT:
                PermissionPoint permissionPoint = BeanMapUtils.mapToBean(map, PermissionPoint.class);
                permissionPoint.setId(id);
                permissionPointDao.save(permissionPoint);
                break;
            case PermissionConstants.PERMISSION_API:
                PermissionApi permissionApi = BeanMapUtils.mapToBean(map, PermissionApi.class);
                permissionApi.setId(id);
                permissionApiDao.save(permissionApi);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }
    }
    //更新权限
    public void update(Map<String, Object> map) throws Exception {
        Permission perm = BeanMapUtils.mapToBean(map, Permission.class);
        //1.通过传递的权限id查询权限
        Permission permission = permissionDao.findById(perm.getId()).get();
        permission.setName(perm.getName());
        permission.setCode(perm.getCode());
        permission.setDescription(perm.getDescription());
        permission.setEnVisible(perm.getEnVisible());
        //2.根据类型构造不同的资源
        updateResource(map, perm, permission);
        permissionDao.save(permission);
    }

    //更新资源
    private void updateResource(Map<String, Object> map, Permission perm, Permission permission) throws Exception {
        int type = perm.getType();
        switch (type) {
            case PermissionConstants.PERMISSION_MENU:
                PermissionMenu menu = permissionMenuDao.findById(perm.getId()).get();
                PermissionMenu permissionMenu = BeanMapUtils.mapToBean(map, PermissionMenu.class);
                if (!StringUtils.isEmpty(permissionMenu)) {
                    if (!StringUtils.isEmpty(permissionMenu.getMenuIcon())) {
                        menu.setMenuIcon(permissionMenu.getMenuIcon());
                    }
                    if (!StringUtils.isEmpty(permissionMenu.getMenuOrder())) {
                        menu.setMenuOrder(permissionMenu.getMenuOrder());
                    }
                    permissionMenuDao.save(menu);
                }
                break;
            case PermissionConstants.PERMISSION_POINT:
                PermissionPoint point = permissionPointDao.findById(perm.getId()).get();
                PermissionPoint permissionPoint = BeanMapUtils.mapToBean(map, PermissionPoint.class);
                if (!StringUtils.isEmpty(permissionPoint)) {
                    if (!StringUtils.isEmpty(permissionPoint.getPointClass())) {
                        point.setPointClass(permissionPoint.getPointClass());
                    }
                    if (!StringUtils.isEmpty(permissionPoint.getPointIcon())) {
                        point.setPointIcon(permissionPoint.getPointIcon());
                    }
                    if (!StringUtils.isEmpty(permissionPoint.getPointStatus())) {
                        point.setPointStatus(permissionPoint.getPointStatus());
                    }
                    permissionPointDao.save(point);
                }
                break;
            case PermissionConstants.PERMISSION_API:
                PermissionApi api = permissionApiDao.findById(perm.getId()).get();
                PermissionApi permissionApi = BeanMapUtils.mapToBean(map, PermissionApi.class);
                if (!StringUtils.isEmpty(permissionApi)) {
                    if (!StringUtils.isEmpty(permissionApi.getApiLevel())) {
                        api.setApiLevel(permissionApi.getApiLevel());
                    }
                    if (!StringUtils.isEmpty(permissionApi.getApiMethod())) {
                        api.setApiMethod(permissionApi.getApiMethod());
                    }
                    if (!StringUtils.isEmpty(permissionApi.getApiUrl())) {
                        api.setApiUrl(permissionApi.getApiUrl());
                    }
                    permissionApiDao.save(api);
                }
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }
    }

    /**
     * 3.根据id查询
     *      //1.查询权限
     *      //2.根据权限的类型查询资源
     *      //3.构造map集合
     */
    public Map<String,Object> getById(String id) throws Exception {
        Permission permission = permissionDao.findById(id).get();
        int type = permission.getType();
        Object object = null;
        if(type==PermissionConstants.PERMISSION_MENU){
           object =  permissionMenuDao.findById(id).get();
        }else if (type==PermissionConstants.PERMISSION_POINT){
            object =  permissionPointDao.findById(id).get();
        }else if (type==PermissionConstants.PERMISSION_API){
            object =  permissionApiDao.findById(id).get();
        }else{
            throw new CommonException(ResultCode.FAIL);
        }
        Map<String, Object> map = BeanMapUtils.beanToMap(object);

        map.put("name",permission.getName());
        map.put("type",permission.getType());
        map.put("code",permission.getCode());
        map.put("description",permission.getDescription());
        map.put("pid",permission.getPid());
        map.put("enVisible",permission.getEnVisible().toString());
        System.out.println(permission.getEnVisible());
        return map;
    }

    /**
     * 4.查询全部
     * type      : 查询全部权限列表type：0：菜单 + 按钮（权限点） 1：菜单2：按钮（权限点）3：API接口
     * enVisible : 0：查询所有saas平台的最高权限，1：查询企业的权限
     * pid ：父id
     */
    public List<Permission> findAll(Map<String, Object> map) {
        Specification<Permission> spec = new Specification<Permission>() {
            @Override
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> list = new ArrayList<>();
                //2.动态拼接查询条件
                if (!StringUtils.isEmpty(map.get("pid"))) {
                    list.add(criteriaBuilder.equal(root.get("pid").as(String.class), (String) map.get("pid")));
                }
                if (!StringUtils.isEmpty(map.get("enVisible"))) {
                    list.add(criteriaBuilder.equal(root.get("enVisible").as(String.class), (String) map.get("enVisible")));
                }
                if (!StringUtils.isEmpty(map.get("type"))) {
                    String type = (String) map.get("type");
                    CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("type"));
                    if (PermissionConstants.PERMISSION_MENU_POINT.equals(type)) {
                        in.value(PermissionConstants.PERMISSION_MENU).value(PermissionConstants.PERMISSION_POINT);
                    } else {
                        in.value(Integer.parseInt(type));
                    }
                    list.add(in);
                }
                return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
            }
        };
        return permissionDao.findAll(spec);
    }

    /**
     * 5.根据id删除
     *  //1.删除权限
     *  //2.删除权限对应的资源
     *
     */
    public void delete(String id) throws Exception {
        Permission permission = permissionDao.findById(id).get();
        permissionDao.delete(permission);
        int type = permission.getType();
        switch (type) {
            case PermissionConstants.PERMISSION_MENU:
                permissionMenuDao.deleteById(id);
                break;
            case PermissionConstants.PERMISSION_POINT:
                permissionPointDao.deleteById(id);
                break;
            case PermissionConstants.PERMISSION_API:
                permissionApiDao.deleteById(id);
                break;
            default:
                throw new CommonException(ResultCode.FAIL);
        }
    }
}
