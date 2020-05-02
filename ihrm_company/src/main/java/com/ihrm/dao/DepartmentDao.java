package com.ihrm.dao;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/19 20:34
 */

import com.ihrm.domain.company.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 企业数据访问接口
 */
public interface DepartmentDao extends JpaRepository<Department,String>,
        JpaSpecificationExecutor<Department>{
    Department findByCodeAndCompanyId(String code, String companyId);
}
