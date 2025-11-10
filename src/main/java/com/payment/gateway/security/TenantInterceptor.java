package com.payment.gateway.security;

import com.payment.gateway.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class TenantInterceptor implements HandlerInterceptor {

  private static final String TENANT_HEADER = "X-Tenant-Id";

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    String tenantId = request.getHeader(TENANT_HEADER);

    if (tenantId == null || tenantId.trim().isEmpty()) {
      log.error("Missing X-Tenant-Id header in request to: {}", request.getRequestURI());
      throw new BadRequestException("X-Tenant-Id header is required");
    }

    TenantContext.setTenantId(tenantId);
    log.debug("Set tenant context: {}", tenantId);

    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    TenantContext.clear();
  }
}
