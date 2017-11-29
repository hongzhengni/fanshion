package com.nee.ims.data.access;

import com.nee.ims.data.entities.StockOrderItemDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface StockOrderItemDetailDao extends CrudRepository<StockOrderItemDetail, String> {

    Page<StockOrderItemDetail> findByItemId(String itemId, Pageable pageable);

    Iterable<StockOrderItemDetail> findByItemId(String itemId);
}

