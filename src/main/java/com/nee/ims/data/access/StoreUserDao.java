package com.nee.ims.data.access;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nee.ims.data.entities.StoreUser;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface StoreUserDao extends CrudRepository<StoreUser, String> {

    StoreUser findOneByStoreIdAndUserId(String storeId, String userId);

    StoreUser findOneByUserId(String userId);
}

