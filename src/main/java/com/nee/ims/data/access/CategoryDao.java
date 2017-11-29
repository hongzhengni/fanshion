package com.nee.ims.data.access;

import com.nee.ims.data.entities.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface CategoryDao extends CrudRepository<Category, String> {

    Iterable<Category> findByStoreId(String storeId, Sort sort);
}

