package com.sampleproject.obs.auth.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.sampleproject.obs.auth.payload.request.LoginRequest;
import com.sampleproject.obs.auth.payload.request.SignupRequest;
import com.sampleproject.obs.auth.payload.response.JwtResponse;
import com.sampleproject.obs.auth.service.AuthService;
import com.sampleproject.obs.data.exception.RoleNotFoundException;
import com.sampleproject.obs.data.exception.UserNotFoundException;
import com.sampleproject.obs.data.model.ERole;
import com.sampleproject.obs.data.model.Role;
import com.sampleproject.obs.data.model.User;
import com.sampleproject.obs.data.model.UserInfo;
import com.sampleproject.obs.data.payload.response.MessageResponse;
import com.sampleproject.obs.data.repository.RoleRepository;
import com.sampleproject.obs.data.repository.UserInfoRepository;
import com.sampleproject.obs.data.repository.UserRepository;
import com.sampleproject.obs.data.security.jwt.JwtUtils;
import com.sampleproject.obs.data.services.UserDetailsImpl;

@Component
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	private Environment env;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserInfoRepository userInfoRepository;

	@Override
	public ResponseEntity<?> singUpReq(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request) {
		// create user account
		User user = User.builder().mobileNum(signUpRequest.getMobileNumber()).email(signUpRequest.getEmail())
				.password(encoder.encode(signUpRequest.getPassword()))
				.username(String.valueOf(signUpRequest.getEmail())).build();

//				if (userRepository.existsByEmail(signUpRequest.getEmail())) {
//					return ResponseEntity.badRequest()
//							.body(new MessageResponse(HttpStatus.BAD_REQUEST.value(), env.getProperty("email.exist")));
//				}

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		user.setRoles(getRole(strRoles, roles));

		user = userRepository.save(user);

		return ResponseEntity
				.ok(new MessageResponse(HttpStatus.OK.value(), env.getProperty("registered.success"), user));

	}
/*  GET ROLE METHOD FOR ONLINE BOOK STORE*/
	private Set<Role> getRole(Set<String> strRoles, Set<Role> roles) {
		if (Objects.isNull(strRoles)) {
			if (!roleRepository.existsByName(ERole.ROLE_BUYER)) {
				throw new RoleNotFoundException();
			}
			Role userRole = roleRepository.findByName(ERole.ROLE_BUYER).get();
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "ADMIN":
					if (!roleRepository.existsByName(ERole.ROLE_ADMIN)) {
						throw new RoleNotFoundException();
					}
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).get();
					roles.add(adminRole);
					break;
				case "COMPANY":
					if (!roleRepository.existsByName(ERole.ROLE_BUYER)) {
						throw new RoleNotFoundException();
					}
					Role userRole = roleRepository.findByName(ERole.ROLE_BUYER).get();
					roles.add(userRole);
					break;

				default:
					if (!roleRepository.existsByName(ERole.ROLE_BUYER)) {
						throw new RoleNotFoundException();
					}
					userRole = roleRepository.findByName(ERole.ROLE_BUYER).get();
					roles.add(userRole);
				}
			});
		}

		return roles;
	}

	@Override
	public ResponseEntity<?> loginUser(@Valid LoginRequest loginRequest) {
		if (!userRepository.existsByUsername(loginRequest.getUsername())) {
			throw new UserNotFoundException();
		}
		User user = userRepository.findByEmail(loginRequest.getUsername());

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			SecurityContextHolder.getContext().setAuthentication(authentication);
			String jwt = jwtUtils.generateJwtToken(authentication);

			UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
			if (Objects.isNull(userDetails)) {
				throw new UserNotFoundException();
			}

			List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
					.collect(Collectors.toList());

			UserInfo info = userInfoRepository.getByUserId(user);
			if (user != null) {
				userRepository.save(user);
			}
			
			return ResponseEntity
					.ok(new MessageResponse(HttpStatus.OK.value(), env.getProperty("login.sucess"), new JwtResponse(jwt,
							userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles, info)));
		} catch (Exception e) {
			return ResponseEntity.ok(new MessageResponse(HttpStatus.UNAUTHORIZED.value(),
					env.getProperty("bad.credentials"), e.getMessage()));
		}

	}
}