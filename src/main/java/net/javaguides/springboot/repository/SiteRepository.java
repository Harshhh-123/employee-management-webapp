package net.javaguides.springboot.repository;

import net.javaguides.springboot.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteRepository extends JpaRepository<Site, Long> {
    java.util.List<Site> findByActiveTrue();
}