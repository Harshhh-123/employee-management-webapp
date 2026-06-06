package net.javaguides.springboot.controller;

import net.javaguides.springboot.model.Worker;
import net.javaguides.springboot.repository.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/workers")
public class WorkerController {

    @Autowired
    private WorkerRepository workerRepository;

    @PostMapping
    public ResponseEntity<?> createWorker(@RequestBody Worker worker) {
        return ResponseEntity.ok(workerRepository.save(worker));
    }

    @GetMapping
    public ResponseEntity<List<Worker>> getAllWorkers() {
        return ResponseEntity.ok(workerRepository.findByActiveTrue());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWorker(@PathVariable Long id) {
        return workerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}