package net.javaguides.springboot.controller;

import net.javaguides.springboot.model.Site;
import net.javaguides.springboot.repository.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sites")
public class SiteController {

    @Autowired
    private SiteRepository siteRepository;

    @PostMapping
    public ResponseEntity<?> createSite(@RequestBody Site site) {
        return ResponseEntity.ok(siteRepository.save(site));
    }

    @GetMapping
    public ResponseEntity<List<Site>> getAllSites() {
        return ResponseEntity.ok(siteRepository.findByActiveTrue());
    }
}