package com.labmanager.project.service.authentication;

import com.labmanager.project.entity.authentication.AuthenticationEntity;

public interface AuthenticationService {
    Boolean changeMasterPassword(String modelId, String oldMasterPassword, String newMasterPassword);

    Boolean updateDurationTime(String modelId, int durationTime);

    public String resetMasterPassword(String PrivateKey) throws Exception;

    String getPrivateKey(String modelId, String masterPassword) throws Exception;

    String createCodeQrUri(String modelId, String masterPassword) throws Exception;

    Boolean VerifyOTP(String modelId, String otp) throws Exception;
}
