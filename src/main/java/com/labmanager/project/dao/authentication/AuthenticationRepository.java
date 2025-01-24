package com.labmanager.project.dao.authentication;

import com.labmanager.project.entity.authentication.AuthenticationEntity;

public interface AuthenticationRepository {

    void save(AuthenticationEntity authenticationEntity);

    AuthenticationEntity changePassword(String privateKey, String newPassword);

    AuthenticationEntity findByPrivateKey(String privateKey);

    void deleteByPrivateKey(String privateKey);

    AuthenticationEntity updateDurationTime(String privateKey, int durationTime);

}
