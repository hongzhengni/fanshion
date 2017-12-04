package com.nee.ims.data.access;

import com.nee.ims.data.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface ProductDao extends PagingAndSortingRepository<Product, String> {

    Page<Product> findAll(Specification<Product> specification, Pageable pageable);

    @Modifying
    @Transactional
    @Query("update Product p set p.status = :status where p.id in :ids")
    int updateProductStatus(@Param("status") Integer status, @Param("ids") List<String> ids);

    @Modifying
    @Transactional
    @Query("update Product p set p.businessLineId = :businessLineId where p.storeId = :storeId")
    int updateProductBusinessLineId(@Param("businessLineId") Integer businessLineId,
                                    @Param("storeId") String id);

    @Modifying
    @Transactional
    @Query("delete from Product p where p.id in :ids")
    void deleteProducts(@Param("ids") List<String> ids);

    Product findFirstByStoreId(String storeId, Sort sort);

    Product findFirstByProductCode(String productCode);

    Iterable<Product> findAllByCategory(String categoryId);
}

