package com.omar.vendingmachine.controller;

import com.omar.vendingmachine.exceptions.InvalidDepositAmountException;
import com.omar.vendingmachine.service.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    CustomUserDetailService customUserDetailService;

    @PutMapping("/user/deposit/{amount}")
    public ResponseEntity<?> deposit(@PathVariable int amount) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        try {
            customUserDetailService.depoist(username, amount);
        } catch (InvalidDepositAmountException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().body(String.format("Amount %s was deposit to user %s successfully", amount, username));
    }

    @PutMapping("/user/deposit/reset")
    public ResponseEntity<?> resetDeposit() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        customUserDetailService.resetDeposit(username);
        return ResponseEntity.ok().body("Successfullu reset deposit");
    }
}
