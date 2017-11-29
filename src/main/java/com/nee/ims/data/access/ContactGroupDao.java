package com.nee.ims.data.access;

import com.nee.ims.data.entities.ContactGroup;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface ContactGroupDao extends CrudRepository<ContactGroup, String> {

    Iterable<ContactGroup> findAllByStoreId(String storeId, Sort sort);
}

