package com.nee.ims.data.access;

import com.nee.ims.data.entities.Color;
import com.nee.ims.data.entities.Size;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface ColorDao extends CrudRepository<Color, String> {

    Iterable<Color> findByStoreId(String storeId, Sort sort);

    Color findOneByStoreIdAndColorNameAndGroupName(String storeId, String colorName, String groupName);

    List<Color> findOneByStoreIdAndGroupName(String storeId, String groupName, Sort sort);
}

