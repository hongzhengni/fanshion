package com.nee.ims.data.access;

import com.nee.ims.data.entities.UserToken;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UserTokenDao extends CrudRepository<UserToken, String> {

    @Modifying //说明该操作是修改类型操作，删除或者修改
    @Transactional //因为默认是readOnly=true的，这里必须自己进行声明
    @Query("delete from UserToken where token = ?1") //删除的语句
    void deleteByToken(String token);

    UserToken findOneByToken(String token);

    UserToken findOneByUserId(String userId);
}

