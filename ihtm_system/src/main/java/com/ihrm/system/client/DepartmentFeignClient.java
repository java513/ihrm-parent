package com.ihrm.system.client;

import com.ihrm.common.entity.Result;
import com.ihrm.domain.company.Department;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/28 11:00
 */
@FeignClient(value = "ihrm-company")
public interface DepartmentFeignClient {

    //@RequestMapping注解用于对被调用的微服务进行地址映射
    @RequestMapping(value = "/company/departments/{id}",method = RequestMethod.GET)
    public Result findById(@PathVariable("id") String id)throws Exception;

    //excel导入，查询部门信息
    @RequestMapping(value = "/company/department/search",method = RequestMethod.POST)
    public Department findByCode(@RequestParam(value = "code") String code, @RequestParam(value = "companyId")String companyId);
}
