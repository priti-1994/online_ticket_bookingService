/*
 * package com.example.interview_demo.service;
 * 
 * import java.time.Duration;
 * 
 * import org.springframework.data.redis.core.StringRedisTemplate; import
 * org.springframework.stereotype.Service;
 * 
 * @Service public class SeatLockService {
 * 
 * private final StringRedisTemplate redis;
 * 
 * public SeatLockService(StringRedisTemplate redis) { this.redis = redis; }
 * 
 * public boolean lockSeat(Long showId, String seat) { String key =
 * buildKey(showId, seat);
 * 
 * Boolean locked = redis.opsForValue() .setIfAbsent(key, "LOCKED",
 * Duration.ofMinutes(5));
 * 
 * return Boolean.TRUE.equals(locked); }
 * 
 * public void releaseSeat(Long showId, String seat) {
 * 
 * redis.delete(buildKey(showId, seat)); }
 * 
 * private String buildKey(Long showId, String seat) {
 * 
 * return "seat_lock:" + showId + ":" + seat; }
 * 
 * }
 */