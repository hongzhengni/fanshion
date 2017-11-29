package com.nee.ims.data.access;

import com.nee.ims.data.entities.StockOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface StockOrderDao extends CrudRepository<StockOrder, String> {

    Page<StockOrder> findByUserId(String userId, Pageable pageable);

    Long countByUserIdAndStatus(String userId, int status);

    Long countByCustomerIdAndStatus(String userId, int status);

    Page<StockOrder> findAllByCustomerIdAndStatus(String userId, Integer status, Pageable pageable);

    Page<StockOrder> findAllByUserIdAndStatus(String userId, Integer status, Pageable pageable);

    Page<StockOrder> findAllByCustomerIdAndStatusAndStoreId(String userId, Integer status,
                                                            String storeId, Pageable pageable);

    Iterable<StockOrder> findAllByStoreId(String storeId);

    Iterable<StockOrder> findAllByStoreIdAndCreateTimeGreaterThanEqual(String storeId, Date time);
}

