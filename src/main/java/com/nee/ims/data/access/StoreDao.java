package com.nee.ims.data.access;

import com.nee.ims.data.entities.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface StoreDao extends CrudRepository<Store, String> {

    Store findOneByUserId(String userId);

    Page<Store> findAll(Specification<Store> specification, Pageable pageable);

    /*@Query("SELECT sum(i.price * d.num)\n" +
            "FROM StockOrder o INNER JOIN StockOrderItem i\n" +
            "    ON i.orderId = o.orderId AND o.storeId = ?1 and o.createTime > ?2 \n" +
            "  LEFT JOIN StockOrderItemDetail d ON i.itemId = d.itemId")
    Double queryTodayEarnings(String storeId, Integer date);*/

    @Query("SELECT count(o.orderId) FROM StockOrder o where o.storeId = ?1 and o.createTime > ?2 ")
    Long queryTodayOrderNum(String storeId, Date time);

    @Query("SELECT count(storeId) FROM StoreFollow WHERE storeId = ?1")
    Long queryFanNum(String storeId);

    @Query("SELECT count(storeId) FROM StoreFollow WHERE storeId = ?1 and createTime > ?2")
    Long queryTodayFanNum(String storeId, Date date);

    @Query("SELECT count(storeId) FROM StoreFollow WHERE storeId = ?1 and createTime > ?2 and createTime < ?3")
    Long queryTodayFanNum(String storeId, Date startDate, Date endDate);
}

