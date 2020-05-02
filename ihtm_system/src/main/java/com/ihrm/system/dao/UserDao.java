package com.ihrm.system.dao;

import com.ihrm.domain.system.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * @author liuhao
 * @Desc //
 * @DATE 2019/12/21 21:41
 */
public interface UserDao extends JpaRepository<User,String>,
        JpaSpecificationExecutor<User>{
    public User getUserByMobile(String moblie);
}
