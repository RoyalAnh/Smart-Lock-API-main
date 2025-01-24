package com.labmanager.project.entity.authentication;


import com.labmanager.project.entity.lock.LockEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "authentication")
public class AuthenticationEntity {


    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "private_key")
    private String privateKey;

    @Column(name = "master_password")
    private String masterPassword;

    @Column(name = "duration_time")
    private int durationTime;

    @Column(name = "update_password_at")
    private LocalDateTime updatePasswordAt;

    @OneToOne
    @JoinColumn(name = "lock", referencedColumnName = "id")
    private LockEntity lock;

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getMasterPassword() {
        return masterPassword;
    }

    public void setMasterPassword(String masterPassword) {
        this.masterPassword = masterPassword;
    }

    public int getDurationTime() {
        return durationTime;
    }

    public void setDurationTime(int durationTime) {
        this.durationTime = durationTime;
    }

    public LocalDateTime getUpdatePasswordAt() {
        return updatePasswordAt;
    }

    public void setUpdatePasswordAt(LocalDateTime updatePasswordAt) {
        this.updatePasswordAt = updatePasswordAt;
    }

    public LockEntity getLock() {
        return lock;
    }

    public void setLock(LockEntity lock) {
        this.lock = lock;
    }

    @Override
    public String toString() {
        return STR."AuthenticationEntity{privateKey='\{privateKey}', masterPassword='\{masterPassword}', durationTime=\{durationTime}, updatePasswordAt=\{updatePasswordAt}, lock=\{lock}}";
    }
}
