package com.nee.ims.data.access;

import com.nee.ims.data.entities.ContactGroupRelation;
import com.nee.ims.data.entities.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by heikki on 17/8/20.
 */
@Repository
public interface ContactGroupRelationDao extends CrudRepository<ContactGroupRelation, String> {

    Iterable<ContactGroupRelation> findAllByGroupId(String groupId);

    @Modifying
    @Transactional
    @Query("update ContactGroupRelation set groupId = ?1, updateTime = now() where groupId = ?2 ")
    void update(String groupId, String oldGroupId);

    @Modifying
    @Transactional
    @Query("update ContactGroupRelation set groupId = ?1, updateTime = now() where groupId = ?2 and user.userId = ?3")
    void update(String groupId, String oldGroupId, String userId);
}

