package com.labmanager.project.dao.lock;

import com.labmanager.project.entity.lock.LockEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;

@Repository
public class LockRepositoryImpl implements LockRepository{

    private EntityManager entityManager;

    public LockRepositoryImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }


    @Override
    @Transactional
    public void save(LockEntity lockEntity) {
        entityManager.persist(lockEntity);
    }


    @Override
    public LockEntity findByModelId(String modelId) {
        TypedQuery<LockEntity> result =
                entityManager.createQuery("from LockEntity where modelId like: id", LockEntity.class);
        result.setParameter("id", "%" + modelId + "%");

        try {
            return result.getSingleResult();
        }catch (NoResultException | NullPointerException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public void deleteById(String modelId) {
        LockEntity lockEntity = findByModelId(modelId);

        entityManager.remove(lockEntity);
    }

    @Override
    @Transactional
    public LockEntity increaseLockRemainingOpen(String modelId) {
        LockEntity lockEntity = findByModelId(modelId);
        int remainingOpen = lockEntity.getRemainingOpen();
        lockEntity.setRemainingOpen(++remainingOpen);

        if (remainingOpen > 3) {
            lockEntity.setLocked(true);
        }

        return entityManager.merge(lockEntity);
    }

    @Override
    public LockEntity resetRemainingOpen(String modelId) {
        LockEntity lockEntity = findByModelId(modelId);
        lockEntity.setRemainingOpen(0);

        return entityManager.merge(lockEntity);
    }

    @Override
    @Transactional
    public LockEntity updateLockIsLocked(String modelId, boolean isLocked) {
        LockEntity lockEntity = findByModelId(modelId);
        lockEntity.setLocked(isLocked);

        return entityManager.merge(lockEntity);
    }

    @Override
    @Transactional
    public LockEntity updateActiveAt(String modelId) {
        LockEntity lockEntity = findByModelId(modelId);
        lockEntity.setUpdatedAt(LocalDateTime.now());

        return entityManager.merge(lockEntity);
    }

}
