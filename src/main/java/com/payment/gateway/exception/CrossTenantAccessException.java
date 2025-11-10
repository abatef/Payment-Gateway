package com.payment.gateway.exception;

public class CrossTenantAccessException extends RuntimeException {
  public CrossTenantAccessException(String message) {
    super(message);
  }
}
