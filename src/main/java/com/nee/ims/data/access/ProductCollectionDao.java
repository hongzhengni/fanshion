package com.nee.ims.data.access;

import com.nee.ims.data.entities.ProductCollection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface ProductCollectionDao extends CrudRepository<ProductCollection, String> {

    Long countByUserId(String userId);

    Page<ProductCollection> findAllByUserId(String userId, Pageable pageable);

    ProductCollection findOneByUserIdAndProductId(String userId, String productId);
}

