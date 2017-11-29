package com.nee.ims.data.access;

import com.nee.ims.data.entities.StockOrderItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface StockOrderItemDao extends CrudRepository<StockOrderItem, String> {

    Page<StockOrderItem> findByOrderId(String orderId, Pageable pageable);

    Page<StockOrderItem> findAllByUserId(String userId, Pageable pageable);

    @Query("select distinct storeId, storeName, storeLogo from StockOrderItem where userId = :userId order by createTime")
    Iterable<Object> findAll(@Param("userId") String userId);

    Iterable<StockOrderItem> findAllByUserIdAndStoreId(String userId, String storeId);

    Iterable<StockOrderItem> findAllByUserId(String userId);

    Iterable<StockOrderItem> findAllByStoreIdAndUserId(String storeId, String userId);

    Iterable<StockOrderItem> findAllByOrderId(String orderId);
}

