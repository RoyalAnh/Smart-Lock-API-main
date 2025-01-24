package com.labmanager.project.service.authentication;

import com.labmanager.project.dao.authentication.AuthenticationRepository;
import com.labmanager.project.dao.lock.LockRepository;
import com.labmanager.project.entity.authentication.AuthenticationEntity;
import com.labmanager.project.entity.lock.LockEntity;
import com.labmanager.project.utils.AESUtil;
import com.labmanager.project.utils.UUIDUtil;
import dev.samstevens.totp.code.*;
import dev.samstevens.totp.time.NtpTimeProvider;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import dev.samstevens.totp.qr.QrData;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private LockRepository lockRepository;
    private AuthenticationRepository authenticationRepository;

    @Autowired
    public AuthenticationServiceImpl(LockRepository lockRepository, AuthenticationRepository authenticationRepository) {
        this.lockRepository = lockRepository;
        this.authenticationRepository = authenticationRepository;
    }


    @Override
    public Boolean changeMasterPassword (String modelId, String oldMasterPassword, String newMasterPassword) {

        LockEntity lockEntity = lockRepository.findByModelId(modelId);
        AuthenticationEntity authenticationEntity = lockEntity.getAuthentication();
        String privateKey = authenticationEntity.getPrivateKey();
        String oldPassword = authenticationEntity.getMasterPassword();

        if (!oldPassword.equals(oldMasterPassword)) {
            throw new IllegalArgumentException("Old master password is not correct");
        }

        authenticationRepository.changePassword(privateKey, newMasterPassword);

        return true;
    }

    @Override
    public Boolean updateDurationTime(String modelId, int durationTime) {
        LockEntity lockEntity = lockRepository.findByModelId(modelId);
        AuthenticationEntity authenticationEntity = lockEntity.getAuthentication();
        String privateKey = authenticationEntity.getPrivateKey();

        authenticationRepository.updateDurationTime(privateKey, durationTime);

        return true;
    }

    @Override
    public String resetMasterPassword(String PrivateKey) throws Exception {
        String secretKey = "b'*\\xe7\\x0e![io\\x9b]\\xcf";
        String privateKeyEncrypted = AESUtil.encrypt(PrivateKey, secretKey);
        AuthenticationEntity authenticationEntity = authenticationRepository.findByPrivateKey(privateKeyEncrypted);
        String privateKey = authenticationEntity.getPrivateKey();

        authenticationEntity.setUpdatePasswordAt(LocalDateTime.now());
        String newMasterPassword = UUIDUtil.generateBase32UUID();

        authenticationRepository.changePassword(privateKey, newMasterPassword);

        return newMasterPassword;
    }

    @Override
    public String getPrivateKey(String modelId, String masterPassword) throws Exception {
        LockEntity lockEntity = lockRepository.findByModelId(modelId);
        AuthenticationEntity authenticationEntity = lockEntity.getAuthentication();
        String privateKey = authenticationEntity.getPrivateKey();
        String password = authenticationEntity.getMasterPassword();

        if (!password.equals(masterPassword)) {
            throw new Exception("Master password is not correct");
        }

        String secretKey = "b'*\\xe7\\x0e![io\\x9b]\\xcf";;

        return AESUtil.decrypt(privateKey, secretKey);
    }

    @Override
    public String createCodeQrUri(String modelId, String masterPassword) throws Exception {
        LockEntity lockEntity = lockRepository.findByModelId(modelId);
        AuthenticationEntity authenticationEntity = lockEntity.getAuthentication();
        String privateKeyEncrypted = authenticationEntity.getPrivateKey();
        String password = authenticationEntity.getMasterPassword();

        if (!password.equals(masterPassword)) {
            throw new Exception("Master password is not correct");
        }

        String secretKey = "b'*\\xe7\\x0e![io\\x9b]\\xcf";;

        String privateKey = AESUtil.decrypt(privateKeyEncrypted, secretKey);
        String secret = AESUtil.encrypt(authenticationEntity.getMasterPassword(), privateKey);

        QrData data = new QrData.Builder()
                .label(modelId)
                .secret(AESUtil.encodeBase32(secret.getBytes()))
                .issuer("HUST")
                .algorithm(HashingAlgorithm.SHA1)
                .digits(6)
                .period(authenticationEntity.getDurationTime())
                .build();

        return data.getUri();
    }

    @Override
    public Boolean VerifyOTP(String modelId, String otp) throws Exception {
        TimeProvider timeProvider = new SystemTimeProvider();
        CodeGenerator codeGenerator = new DefaultCodeGenerator();
        CodeVerifier verifier = new DefaultCodeVerifier(codeGenerator, timeProvider);
        LockEntity lockEntity = lockRepository.findByModelId(modelId);

        AuthenticationEntity authenticationEntity = lockEntity.getAuthentication();
        String privateKeyEncrypted = authenticationEntity.getPrivateKey();
        String secretKey = "b'*\\xe7\\x0e![io\\x9b]\\xcf";;

        String privateKey = AESUtil.decrypt(privateKeyEncrypted, secretKey);
        String secret = AESUtil.encrypt(authenticationEntity.getMasterPassword(), privateKey);
        System.out.println(AESUtil.encodeBase32(secret.getBytes()) + " " + otp);
        boolean isSuccess =  verifier.isValidCode(AESUtil.encodeBase32(secret.getBytes()), otp);
        System.out.println("run213123123");

        if (isSuccess) {
            lockRepository.resetRemainingOpen(modelId);
            lockRepository.updateLockIsLocked(modelId, false);
        } else {
            lockRepository.increaseLockRemainingOpen(modelId);
        }

        return isSuccess;
    }
}
