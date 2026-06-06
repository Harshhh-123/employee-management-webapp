package net.javaguides.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

@Service
public class ActiveWorkerService {

    private static final String ACTIVE_WORKERS_KEY = "active:workers";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void addActiveWorker(Long workerId, Long siteId, String clockInTime) {
        try {
            Map<String, String> workerData = new HashMap<>();
            workerData.put("workerId", workerId.toString());
            workerData.put("siteId", siteId.toString());
            workerData.put("clockInTime", clockInTime);
            String key = ACTIVE_WORKERS_KEY + ":" + workerId;
            redisTemplate.opsForHash().putAll(key, workerData);
            redisTemplate.expire(key, Duration.ofHours(16));
        } catch (Exception e) {
            System.err.println("Redis error: " + e.getMessage());
        }
    }

    public void removeActiveWorker(Long workerId) {
        try {
            String key = ACTIVE_WORKERS_KEY + ":" + workerId;
            redisTemplate.delete(key);
        } catch (Exception e) {
            System.err.println("Redis error: " + e.getMessage());
        }
    }

    public Object getActiveWorkers() {
        try {
            Set<String> keys = redisTemplate.keys(ACTIVE_WORKERS_KEY + ":*");
            if (keys == null || keys.isEmpty()) return Map.of("activeWorkers", java.util.List.of());
            java.util.List<Map<Object, Object>> workers = new java.util.ArrayList<>();
            for (String key : keys) {
                Map<Object, Object> data = redisTemplate.opsForHash().entries(key);
                workers.add(data);
            }
            return Map.of("activeWorkers", workers);
        } catch (Exception e) {
            return Map.of("error", "Redis unavailable", "activeWorkers", java.util.List.of());
        }
    }
}