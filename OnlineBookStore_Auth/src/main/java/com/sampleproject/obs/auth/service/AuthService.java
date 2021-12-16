package com.sampleproject.obs.auth.service;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.sampleproject.obs.auth.payload.request.LoginRequest;
import com.sampleproject.obs.auth.payload.request.SignupRequest;
import com.sampleproject.obs.data.exception.PublishingMirrorNodeException;
import com.sampleproject.obs.data.exception.TokenInfoException;

@Service
public interface AuthService {

	ResponseEntity<?> singUpReq(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request) throws TokenInfoException, PublishingMirrorNodeException;

	ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest);


}
