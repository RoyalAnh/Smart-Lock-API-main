package com.labmanager.project.service.lock;


import com.labmanager.project.dao.authentication.AuthenticationRepository;
import com.labmanager.project.dao.lock.LockRepository;
import com.labmanager.project.entity.authentication.AuthenticationEntity;
import com.labmanager.project.entity.lock.LockEntity;
import com.labmanager.project.utils.AESUtil;
import com.labmanager.project.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LockServiceImpl implements LockService {

    private LockRepository lockRepository;
    private AuthenticationRepository authenticationRepository;


    @Autowired
    public LockServiceImpl(LockRepository lockRepository, AuthenticationRepository authenticationRepository) {
        this.lockRepository = lockRepository;
        this.authenticationRepository = authenticationRepository;
    }


    @Override
    public String createNewLock(String name, String modelId) throws Exception{
        LockEntity lockEntity = new LockEntity();
        lockEntity.setName(name);
        lockEntity.setModelId(modelId);
        lockEntity.setLocked(false);
        lockEntity.setRemainingOpen(0);
        lockEntity.setCreatedAt(LocalDateTime.now());

        String privateKey = UUIDUtil.generateBase32UUID();
        String secretKey = "b'*\\xe7\\x0e![io\\x9b]\\xcf";;


        String privateKeyEncrypted = AESUtil.encrypt(privateKey, secretKey);
        AuthenticationEntity authenticationEntity = new AuthenticationEntity();
        authenticationEntity.setPrivateKey(privateKeyEncrypted);
        authenticationEntity.setDurationTime(30);
        authenticationEntity.setLock(lockEntity);

        lockEntity.setAuthentication(authenticationEntity);

        lockRepository.save(lockEntity);
        return privateKey;
    }

    @Override
    public LockEntity updateLockIsLocked(String modelId, boolean isLocked) {
        return lockRepository.updateLockIsLocked(modelId, isLocked);
    }

    @Override
    public LockEntity increaseLockRemainingOpen(String modelId) {
        return lockRepository.increaseLockRemainingOpen(modelId);
    }

    @Override
    public LockEntity resetRemainingOpen(String modelId) {
        return lockRepository.resetRemainingOpen(modelId);
    }

    @Override
    public Boolean updateActiveAt(String modelId) {
         lockRepository.updateActiveAt(modelId);

         return true;
    }

    @Override
    public LockEntity findByModelId(String modelId) {
        return lockRepository.findByModelId(modelId);
    }
}
