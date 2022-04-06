package com.omar.vendingmachine.controller;

import com.omar.vendingmachine.constants.UserContants;
import com.omar.vendingmachine.model.user.ERole;
import com.omar.vendingmachine.model.user.Role;
import com.omar.vendingmachine.service.CustomUserDetailService;
import com.omar.vendingmachine.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Api(value = "AuthController", description = "REST APIs related to user registration and authentication flows")
@RestController
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    CustomUserDetailService customUserDetailService;
    @Autowired
    RoleService roleService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    SessionRegistry sessionRegistry;

    @ApiOperation(value = "Register a new user, accepts the username, password and role as input", tags = "signup")
    @PostMapping("/user/signup")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> signUpDetails) {
        String username = signUpDetails.getOrDefault(UserContants.USERNAME, "");
        String password = signUpDetails.getOrDefault(UserContants.PASSWORD, "");
        String strRole = signUpDetails.getOrDefault(UserContants.ROLE, "");
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) || StringUtils.isBlank(strRole)) {
            return ResponseEntity.badRequest().body(String.format("Username, password and role should be filled at sign up"));
        }
        if (customUserDetailService.findByUsername(username) != null) {
            return ResponseEntity.badRequest().body(String.format("User with %s username already exists", username));
        }
        Role role = null;
        if (strRole.equalsIgnoreCase(UserContants.SELLER)) {
            role = roleService.findRoleByName(ERole.SELLER);
        } else if (strRole.equalsIgnoreCase(UserContants.BUYER)) {
            role = roleService.findRoleByName(ERole.BUYER);
        } else {
            return ResponseEntity.badRequest().body(String.format("Role %s can not be found", strRole));
        }
        customUserDetailService.createUser(username, passwordEncoder.encode(password), role, 0);
        return ResponseEntity.ok().body("User Registered Successfully!");
    }

    @GetMapping("user/getAllSessions")
    public ResponseEntity<?> getAllSessions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok().body(sessionRegistry.getAllSessions(authentication.getPrincipal(), true));
    }

    @GetMapping("user/logout/all")
    public ResponseEntity<?> logoutAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(authentication.getPrincipal(), false);
        for (SessionInformation sessionInformation : sessions) {
            sessionInformation.expireNow();
        }
        return ResponseEntity.ok().body("Successfully logged out of all sessions");
    }

    @PostMapping("user/login")
    public ResponseEntity<?> login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(authentication.getPrincipal(), false);
        if (sessions.size() > 1) {
            return ResponseEntity.ok().body(String.format("There is already an active session using your account"));
        }
        return ResponseEntity.ok().body("success");
    }
}
