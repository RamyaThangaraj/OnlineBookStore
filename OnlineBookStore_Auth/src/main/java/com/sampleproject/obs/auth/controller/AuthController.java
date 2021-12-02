package com.sampleproject.obs.auth.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sampleproject.obs.auth.payload.request.LoginRequest;
import com.sampleproject.obs.auth.payload.request.SignupRequest;
import com.sampleproject.obs.auth.service.AuthService;
@CrossOrigin
@RestController
@RequestMapping("/obs")
public class AuthController {
	
	@Autowired
	AuthService authService ;
		
	///api/obs/auth/signup

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest , HttpServletRequest request) {
		return authService.singUpReq(signUpRequest, request);
	}
	
	
	@PostMapping("/signin")
	public ResponseEntity<?> loginAuthenticateUser(@Valid @RequestBody LoginRequest loginRequest){
		return authService.loginUser(loginRequest);
		
	}
}
