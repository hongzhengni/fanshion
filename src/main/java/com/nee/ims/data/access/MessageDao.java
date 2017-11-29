package com.nee.ims.data.access;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.nee.ims.data.entities.Message;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface MessageDao extends CrudRepository<Message, String> {

    Page<Message> findAllByUserId(String userId, Pageable pageable);

    Page<Message> findAllByStoreId(String storeId, Pageable pageable);
}

