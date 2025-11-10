package com.payment.gateway.service;

import com.payment.gateway.model.PaymentTransaction;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotencyService {

  private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:";
  private static final long CACHE_EXPIRATION_HOURS = 24;
  private final RedisTemplate<String, Object> redisTemplate;

  public void saveIdempotentTransaction(String idempotencyKey, PaymentTransaction transaction) {
    String key = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
    try {
      redisTemplate
          .opsForValue()
          .set(key, transaction.getId().toString(), CACHE_EXPIRATION_HOURS, TimeUnit.HOURS);
      log.debug(
          "Saved idempotency key: {} for transaction: {}", idempotencyKey, transaction.getId());
    } catch (Exception e) {
      log.error("Failed to save idempotency key: {}", idempotencyKey, e);
    }
  }

  public UUID getTransactionIdByIdempotencyKey(String idempotencyKey) {
    String key = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
    try {
      Object value = redisTemplate.opsForValue().get(key);
      if (value != null) {
        log.debug("Found existing transaction for idempotency key: {}", idempotencyKey);
        return UUID.fromString(value.toString());
      }
    } catch (Exception e) {
      log.error("Failed to retrieve idempotency key: {}", idempotencyKey, e);
    }
    return null;
  }

  public boolean exists(String idempotencyKey) {
    String key = IDEMPOTENCY_KEY_PREFIX + idempotencyKey;
    return redisTemplate.hasKey(key);
  }
}
