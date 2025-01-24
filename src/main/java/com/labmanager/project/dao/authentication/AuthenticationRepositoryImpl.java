package com.labmanager.project.dao.authentication;


import com.labmanager.project.entity.authentication.AuthenticationEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class AuthenticationRepositoryImpl implements AuthenticationRepository{

    private EntityManager entityManager;

    @Autowired
    public AuthenticationRepositoryImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }



    @Override
    @Transactional
    public void save(AuthenticationEntity authenticationEntity) {
        entityManager.persist(authenticationEntity);
    }

    @Override
    @Transactional
    public AuthenticationEntity changePassword(String privateKey, String newPassword) {
        AuthenticationEntity authenticationEntity = findByPrivateKey(privateKey);
        authenticationEntity.setMasterPassword(newPassword);
        authenticationEntity.setUpdatePasswordAt(LocalDateTime.now());

        return entityManager.merge(authenticationEntity);
    }

    @Override
    public AuthenticationEntity findByPrivateKey(String privateKey) {
        TypedQuery<AuthenticationEntity> result =
                entityManager.createQuery("from AuthenticationEntity where privateKey like: pk", AuthenticationEntity.class);
        result.setParameter("pk", "%" + privateKey + "%");

        try {
            return result.getSingleResult();
        }catch (NoResultException | NullPointerException e) {
            return null;
        }
    }

    @Override
    @Transactional
    public void deleteByPrivateKey(String privateKey) {
        AuthenticationEntity authenticationEntity = findByPrivateKey(privateKey);
        entityManager.remove(authenticationEntity);
    }

    @Override
    @Transactional
    public AuthenticationEntity updateDurationTime(String privateKey, int durationTime) {
        AuthenticationEntity authenticationEntity = findByPrivateKey(privateKey);
        authenticationEntity.setDurationTime(durationTime);

        return entityManager.merge(authenticationEntity);
    }
}
