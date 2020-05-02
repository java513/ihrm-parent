package com.ihrm.system.dao;

import com.ihrm.domain.system.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/22 10:23
 */
public interface RoleDao extends JpaRepository<Role,String>,JpaSpecificationExecutor<Role>{
}
