package com.nee.ims.data.access;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nee.ims.data.entities.User;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface UserDao extends CrudRepository<User, String> {


    User findFirstByMobileAndPassword(String mobile, String password);

    User findFirstByMobileAndPasswordAndUserType(String mobile, String password, Integer userType);

    User findFirstByMobile(String mobile);


    User findOneByMobileAndUserType(String mobile, Integer userType);

    User findFirstByWeixinAndUserType(String weixin, int i);

    Iterable<User> findAllByUserType(Integer userType);
}

