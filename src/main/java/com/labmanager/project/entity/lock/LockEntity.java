package com.labmanager.project.entity.lock;

import com.labmanager.project.entity.authentication.AuthenticationEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "lock")
public class LockEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "lock_name")
    private String name;


    @Column(name = "is_locked")
    private boolean isLocked;

    @Column(name = "id_model")
    private String modelId;

    @Column(name = "create_at")
    private LocalDateTime createdAt;

    @Column(name = "active_at")
    private LocalDateTime updatedAt;

    @Column(name = "remaining_open")
    private int remainingOpen;

    @OneToOne(mappedBy = "lock", cascade = CascadeType.ALL)
    private AuthenticationEntity authen;

    public AuthenticationEntity getAuthentication() {
        return authen;
    }

    public int getRemainingOpen() {
        return remainingOpen;
    }

    public void setRemainingOpen(int remainingOpen) {
        this.remainingOpen = remainingOpen;
    }

    public void setAuthentication(AuthenticationEntity authen) {
        this.authen = authen;
    }


    @Override
    public String toString() {
        return "LockEntity{" +
                "name='" + name + '\'' +
                ", authen=" + authen +
                ", updatedAt=" + updatedAt +
                ", createdAt=" + createdAt +
                ", isLocked=" + isLocked +
                ", idModel=" + modelId +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
