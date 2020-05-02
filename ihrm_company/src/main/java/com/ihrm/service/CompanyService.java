package com.ihrm.service;

import com.ihrm.common.utils.IdWorker;
import com.ihrm.dao.CompanyDao;
import com.ihrm.domain.company.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/19 20:37
 */
@Service
public class CompanyService {
    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private IdWorker idWorker;


    public Company add(Company company){
        company.setId(idWorker.nextId()+"");
        company.setCreateTime(new Date());
        company.setState(1);
        company.setAuditState("0");//待审核
        company.setBalance(0d);
        return companyDao.save(company);
    }

    public void deleteById(String id) {
        companyDao.deleteById(id);
    }

    public Company update(Company company){
        return companyDao.save(company);
    }

    public Company findById(String id){
        return companyDao.findById(id).get();
    }

    public List<Company> findAll(){
        return companyDao.findAll();
    }
}
