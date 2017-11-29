package com.nee.ims.data.access;

import com.nee.ims.data.entities.Address;
import com.nee.ims.data.entities.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface AddressDao extends CrudRepository<Address, String> {


    Iterable<Address> findAllByUserId(String userId, Sort sort);

    @Modifying //说明该操作是修改类型操作，删除或者修改
    @Transactional //因为默认是readOnly=true的，这里必须自己进行声明
    @Query("update Address set isDefault = 0 where userId = ?1") //删除的语句
    void updateByUserId(String userId);

    Address findOneByUserId(String userId);
}

