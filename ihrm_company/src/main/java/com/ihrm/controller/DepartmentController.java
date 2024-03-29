package com.ihrm.controller;

import com.ihrm.common.controller.BaseController;
import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.company.Company;
import com.ihrm.domain.company.Department;
import com.ihrm.domain.company.response.DeptListResult;
import com.ihrm.service.CompanyService;
import com.ihrm.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/21 15:20
 */
@RestController
@RequestMapping("/company")
public class DepartmentController extends BaseController {
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private CompanyService companyService;

    /**
     * 添加部门
     */
    @RequestMapping(value = "/addDepartment", method = RequestMethod.POST)
    public Result add(@RequestBody Department department) throws Exception {
        department.setCompanyId(companyId);
        departmentService.save(department);
        return Result.SUCCESS();
    }
    /**
     * 修改部门信息
     */
    @RequestMapping(value = "/departments/{id}", method = RequestMethod.PUT)
    public Result update(@PathVariable(name = "id") String id, @RequestBody Department
            department) throws Exception {
        department.setCompanyId(companyId);
        department.setId(id);
        departmentService.update(department);
        return Result.SUCCESS();
    }

    /**
     * 删除部门
     */
    @RequestMapping(value = "/departments/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable(name = "id") String id) throws Exception {
        departmentService.delete(id);
        return Result.SUCCESS();
    }
    /**
     * 根据id查询
     */
    @RequestMapping(value = "/departments/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable(name = "id") String id) throws Exception {
        Department department = departmentService.findById(id);
        return new Result(ResultCode.SUCCESS,department);
    }

    /**
     * 组织架构列表
     */
    @RequestMapping(value = "/departments", method = RequestMethod.GET)
    public Result findAll() throws Exception {
        System.out.println(companyId);
        System.out.println(StringUtils.isEmpty(companyId));
        Company company = companyService.findById(companyId);
        List<Department> list = departmentService.findAll(companyId);
        return new Result(ResultCode.SUCCESS,new DeptListResult(company,list));
    }

    /**
     * excel导入，查询部门信息
     * @param code
     * @param companyId
     * @return
     */
    @RequestMapping(value = "/department/search",method = RequestMethod.POST)
    public Department findByCode(@RequestParam(value = "code") String code,@RequestParam(value = "companyId")String companyId){
       return departmentService.findByCode(code,companyId);
    }
}
