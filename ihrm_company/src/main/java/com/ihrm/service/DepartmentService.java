package com.ihrm.service;

import com.ihrm.common.service.BaseService;
import com.ihrm.common.utils.IdWorker;
import com.ihrm.dao.DepartmentDao;
import com.ihrm.domain.company.Department;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/21 15:18
 */
@Service
public class DepartmentService extends BaseService<Department> {
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private DepartmentDao departmentDao;

    /**
     * 添加部门
     */
    public void save(Department department) {
        //填充其他参数
        department.setId(idWorker.nextId() + "");
        department.setCreateTime(new Date());
        departmentDao.save(department);
    }

    /**
     * 更新部门信息
     */
    public void update(Department department) {
        Department sourceDepartment = departmentDao.findById(department.getId()).get();
        sourceDepartment.setName(department.getName());
        sourceDepartment.setPid(department.getPid());
        sourceDepartment.setManagerId(department.getManagerId());
        sourceDepartment.setIntroduce(department.getIntroduce());
        sourceDepartment.setManager(department.getManager());
        departmentDao.save(sourceDepartment);
    }

    /**
     * 根据ID获取部门信息
     *
     * @param id 部门ID
     * @return 部门信息
     */
    public Department findById(String id) {
        return departmentDao.findById(id).get();
    }

    /**
     * 删除部门
     *
     * @param id 部门ID
     */
    public void delete(String id) {
        departmentDao.deleteById(id);
    }

    /**
     * 获取部门列表
     */
    public List<Department> findAll(String companyId) {
        return departmentDao.findAll(getSpecification(companyId));
    }

    public Department findByCode(String code, String companyId) {
        return departmentDao.findByCodeAndCompanyId(code,companyId);
    }
}
