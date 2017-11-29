package com.nee.ims.data.access;

import com.nee.ims.data.entities.Color;
import com.nee.ims.data.entities.Product;
import com.nee.ims.data.entities.ProductPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface ProductPermissionDao extends CrudRepository<ProductPermission, String> {

    Page<ProductPermission> findAll(Specification<ProductPermission> specification, Pageable pageable);

    @Modifying
    @Transactional
    @Query("delete from ProductPermission p where p.id in :ids")
    void deleteProductPermissions(@Param("ids") List<String> ids);

    ProductPermission findOneByProductAndUserId(Product product, String userId);

    @Modifying
    @Transactional
    @Query("delete from ProductPermission p where p.product.productId = ?1 and p.userId = ?2")
    void deleteByProductIdAndUserId(String productId, String userId);
}

