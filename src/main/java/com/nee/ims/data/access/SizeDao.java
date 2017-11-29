package com.nee.ims.data.access;

import com.nee.ims.data.entities.Size;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface SizeDao extends CrudRepository<Size, String> {

    Iterable<Size> findByStoreId(String storeId, Sort sort);

    Size findOneByStoreIdAndSizeNameAndGroupName(String storeId, String sizeName, String groupName);
}

