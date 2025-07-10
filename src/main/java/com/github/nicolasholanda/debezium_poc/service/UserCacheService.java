package com.github.nicolasholanda.debezium_poc.service;

import com.github.nicolasholanda.debezium_poc.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class UserCacheService {

    @Autowired
    private StringRedisTemplate redis;

    public Optional<User> findUserById(Integer id) {
        String key = "user:" + id;
        Map<Object, Object> fields = redis.opsForHash().entries(key);

        if (fields.isEmpty()) {
            return Optional.empty();
        }

        User user = new User();
        user.id = id;
        user.name = (String) fields.get("name");
        user.email = (String) fields.get("email");

        String updatedAtStr = (String) fields.get("updated_at");
        if (updatedAtStr != null) {
            user.updatedAt = Long.valueOf(updatedAtStr);
        }

        return Optional.of(user);
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
