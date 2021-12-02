package com.sampleproject.obs.auth.service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.sampleproject.obs.auth.payload.request.LoginRequest;
import com.sampleproject.obs.auth.payload.request.SignupRequest;

@Service
public interface AuthService {

	ResponseEntity<?> singUpReq(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request);

	ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest);


}
