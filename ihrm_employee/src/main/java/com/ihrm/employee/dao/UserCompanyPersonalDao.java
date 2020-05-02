package com.ihrm.employee.dao;

import com.ihrm.domain.employee.UserCompanyPersonal;
import com.ihrm.domain.employee.response.EmployeeReportResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 数据访问接口
 */
public interface UserCompanyPersonalDao extends JpaRepository<UserCompanyPersonal, String>, JpaSpecificationExecutor<UserCompanyPersonal> {
    UserCompanyPersonal findByUserId(String userId);

    //SELECT * FROM em_user_company_personal u LEFT JOIN em_resignation r ON
    // u.user_id=r.user_id WHERE u.time_of_entry LIKE '2018-02%' OR (r.resignation_time LIKE '2018-02%')
    @Query(value = "SELECT new com.ihrm.domain.employee.response.EmployeeReportResult(u,e) FROM UserCompanyPersonal u " +
            "LEFT JOIN EmployeeResignation e ON u.userId=e.userId WHERE u.companyId=?1 and u.timeOfEntry LIKE ?2 OR (e.resignationTime LIKE ?2)")
    List<EmployeeReportResult> findByExport(String companyId, String month);

    /*@Query(value="select new com.ihrm.domain.employee.response.EmployeeReportResult(a,b) from UserCompanyPersonal a " +
            "LEFT JOIN EmployeeResignation b on a.userId=b.userId where a.companyId=?1 and a.timeOfEntry like?2 or (" +
            "b.resignationTime like ?2)")
    List<EmployeeReportResult> findByExport(String companyId, String month);*/
}