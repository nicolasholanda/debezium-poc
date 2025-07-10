package com.github.nicolasholanda.debezium_poc.service;

import com.github.nicolasholanda.debezium_poc.User;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserCacheService {

    private final StringRedisTemplate redis;

    public UserCacheService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void updateCache(User user) {
        if (user == null) return;

        String key = "user:" + user.id;
        redis.opsForHash().put(key, "name", user.name);
        redis.opsForHash().put(key, "email", user.email);
    }

    public void deleteFromCache(Integer userId) {
        redis.delete("user:" + userId);
    }
}
