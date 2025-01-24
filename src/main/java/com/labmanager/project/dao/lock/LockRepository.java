package com.labmanager.project.dao.lock;

import com.labmanager.project.entity.lock.LockEntity;

public interface LockRepository {

    void save(LockEntity lockEntity);

    void deleteById(String modelId);

    LockEntity increaseLockRemainingOpen(String modelId);

    LockEntity resetRemainingOpen(String modelId);

    LockEntity updateLockIsLocked(String modelId, boolean isLocked);

    LockEntity updateActiveAt(String modelId);

    LockEntity findByModelId(String modelId);
}
