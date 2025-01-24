package com.labmanager.project.service.lock;

import com.labmanager.project.entity.lock.LockEntity;

public interface LockService {

    String createNewLock(String name, String modelId) throws Exception;

    LockEntity updateLockIsLocked(String modelId, boolean isLocked);

    LockEntity increaseLockRemainingOpen(String modelId);

    LockEntity resetRemainingOpen(String modelId);

    Boolean updateActiveAt(String modelId);

    LockEntity findByModelId(String modelId);
}
