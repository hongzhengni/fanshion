package com.nee.ims.data.access;

import com.nee.ims.data.entities.StoreFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface StoreFollowDao extends CrudRepository<StoreFollow, String> {

    Long countByStoreId(String storeId);

    Page<StoreFollow> findAllByUserId(String userId, Pageable pageable);

    StoreFollow findOneByUserIdAndStoreId(String userId, String storeId);
}

