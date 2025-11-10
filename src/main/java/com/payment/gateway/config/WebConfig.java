package com.payment.gateway.config;

import com.payment.gateway.security.TenantInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final TenantInterceptor tenantInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(tenantInterceptor)
        .addPathPatterns("/api/**")
        .excludePathPatterns("/actuator/**", "/error");
  }
}
