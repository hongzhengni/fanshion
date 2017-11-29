package com.nee.ims.data.access;

import com.nee.ims.data.entities.SendProduct;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.nee.ims.data.entities.Address;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface SendProductDao extends CrudRepository<SendProduct, String> {

}

