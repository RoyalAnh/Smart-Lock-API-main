package com.labmanager.project.rest.lock;


import com.labmanager.project.entity.lock.LockEntity;
import com.labmanager.project.service.lock.LockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LockRestController {

    private LockService lockService;


    @Autowired
    public LockRestController(LockService lockService) {
        this.lockService = lockService;
    }


    @GetMapping("/lock/{modelId}")
    public ResponseEntity<LockEntity> getLockByModelId(@PathVariable String modelId) {
        return ResponseEntity.ok(lockService.findByModelId(modelId));
    }

    @PostMapping("/lock")
    public ResponseEntity<String> createNewLock(@RequestParam String name, @RequestParam String modelId) {
        try {
            return ResponseEntity.ok(lockService.createNewLock(name, modelId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/lock/{modelId}/updateActiveAt")
    public ResponseEntity<Boolean> updateActiveAt(@PathVariable String modelId) {
        return ResponseEntity.ok(lockService.updateActiveAt(modelId));
    }

    @PostMapping("/lock/{modelId}/resetRemainingOpen")
    public ResponseEntity<LockEntity> resetRemainingOpen(@PathVariable String modelId) {
        return ResponseEntity.ok(lockService.resetRemainingOpen(modelId));
    }
}
