package com.payment.gateway.controller;

import com.payment.gateway.utils.JwtTokenGenerator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/token")
public class TokenGenController {
  private JwtTokenGenerator generator;

  public TokenGenController(JwtTokenGenerator tokenGenerator) {
    this.generator = tokenGenerator;
  }

  @GetMapping
  public ResponseEntity<String> getToken(
      @RequestParam("username") String username, @RequestParam("tenant") String tenant) {
    return ResponseEntity.ok(generator.generateToken(username, tenant));
  }
}
