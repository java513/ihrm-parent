package com.ihrm.controller;

import com.ihrm.common.entity.Result;
import com.ihrm.common.entity.ResultCode;
import com.ihrm.domain.company.Company;
import com.ihrm.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/19 20:43
 */
@RestController
@RequestMapping("/company")
public class CompanyController {
    @Autowired
    private CompanyService companyService;

    @RequestMapping(value = "addCompany",method = RequestMethod.POST)
    public Result add(@RequestBody Company company)throws Exception{
        companyService.add(company);
        return Result.SUCCESS();
    }

    @RequestMapping(value = "update/{id}",method = RequestMethod.PUT)
    public Result update(@PathVariable(name = "id")String id, @RequestBody Company company)throws Exception{
        Company one = companyService.findById(id);
        one.setName(company.getName());
        one.setRemarks(company.getRemarks());
        one.setState(company.getState());
        one.setAuditState(company.getAuditState());
        companyService.update(one);
        return Result.SUCCESS();
    }

    @RequestMapping(value = "delete/{id}",method = RequestMethod.DELETE)
    public Result delete(@PathVariable(name = "id")String id)throws Exception{
        companyService.deleteById(id);
        return Result.SUCCESS();
    }

    @RequestMapping(value = "findById/{id}",method = RequestMethod.GET)
    public Result findById(@PathVariable(name = "id")String id)throws Exception{
        Company company = companyService.findById(id);
        Result result = new Result(ResultCode.SUCCESS);
        result.setData(company);
        return result;
    }

    @RequestMapping(value = "/companies",method = RequestMethod.GET)
    public Result findAll()throws Exception{
        List<Company> companies =   companyService.findAll();
        Result result = new Result(ResultCode.SUCCESS);
        result.setData(companies);
        return result;
    }
}
