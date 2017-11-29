package com.nee.ims.data.access;

import com.nee.ims.data.entities.ProductPicture;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * Created by heikki on 17/8/20.
 */
public interface ProductPictureDao extends CrudRepository<ProductPicture, String> {


    Iterable<ProductPicture> findAllByProductId(String productId);

    @Modifying //说明该操作是修改类型操作，删除或者修改
    @Transactional //因为默认是readOnly=true的，这里必须自己进行声明
    @Query("delete from ProductPicture where productId = ?1") //删除的语句
    void deleteByProductId(String productId);

    ProductPicture findFirstByProductId(String productId);
}

