package com.sampleproject.obs.auth.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
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
import org.springframework.web.client.RestTemplate;

import com.sampleproject.obs.auth.payload.request.AssociateRequest;
import com.sampleproject.obs.auth.payload.request.LoginRequest;
import com.sampleproject.obs.auth.payload.request.SignupRequest;
import com.sampleproject.obs.auth.payload.response.JwtResponse;
import com.sampleproject.obs.auth.service.AuthService;
import com.sampleproject.obs.data.constant.OnlineBookStoreConstant;
import com.sampleproject.obs.data.exception.CreateWalletMirrorNodeException;
import com.sampleproject.obs.data.exception.PublishingMirrorNodeException;
import com.sampleproject.obs.data.exception.RoleNotFoundException;
import com.sampleproject.obs.data.exception.TokenInfoException;
import com.sampleproject.obs.data.exception.UserNotFoundException;
import com.sampleproject.obs.data.model.ERole;
import com.sampleproject.obs.data.model.Role;
import com.sampleproject.obs.data.model.TokenIdInfo;
import com.sampleproject.obs.data.model.User;
import com.sampleproject.obs.data.model.UserInfo;
import com.sampleproject.obs.data.model.UserSecurity;
import com.sampleproject.obs.data.payload.response.MessageResponse;
import com.sampleproject.obs.data.repository.RoleRepository;
import com.sampleproject.obs.data.repository.TokenIdInfoRepo;
import com.sampleproject.obs.data.repository.UserInfoRepository;
import com.sampleproject.obs.data.repository.UserRepository;
import com.sampleproject.obs.data.repository.UserSecurityRepository;
import com.sampleproject.obs.data.security.jwt.JwtUtils;
import com.sampleproject.obs.data.services.RestTemplateService;
import com.sampleproject.obs.data.services.UserDetailsImpl;
import com.sampleproject.obs.data.util.PublishInfo;

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

	@Autowired
	UserSecurityRepository userSecurityRepository;

	@Autowired
	TokenIdInfoRepo tokenIdInfoRepo;

	@Value("${hcs.url}")
	private String hcsBaseUrl;
	@Value("${hts.url}")
	private String htsBaseUrl;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	RestTemplateService restTemplateService;

	@Autowired
	PublishInfo publishInfo;

	@Override
	public ResponseEntity<?> singUpReq(@Valid @RequestBody SignupRequest signUpRequest, HttpServletRequest request)
			throws TokenInfoException, PublishingMirrorNodeException {
		// create user account
		User user = User.builder().mobileNum(signUpRequest.getMobileNumber()).email(signUpRequest.getEmail())
				.password(encoder.encode(signUpRequest.getPassword()))
				.username(String.valueOf(signUpRequest.getEmail())).build();

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		user.setRoles(getRole(strRoles, roles));

		user = userRepository.save(user);

		UserInfo userInfo = insertUserInfo(signUpRequest, user);

		// Create Wallet
		UserSecurity userSec = saveSecurity(userInfo, user);
		
		UserSecurity security = userSecurityRepository.findByUser(user).get();

//		TokenIdInfo tokenValue = tokenIdInfoRepo.findByStatus();
//		if (Objects.isNull(tokenValue)) {
//			throw new TokenInfoException();
//		}

		String toAccountId = security.getAccountShard() + "." + security.getAccountRealm() + "."
				+ security.getAccountNum();
		
		AssociateRequest token = AssociateRequest.builder().tokenId(env.getProperty("token.value")).operatorId(toAccountId)
				.operatorKey(security.getUserPrivateKey()).userId(user.getId()).build();

		associateUser(request, token);

		publishMessageToMirrorNode("user", user);
		publishMessageToMirrorNode("userInfo", userInfo);
		publishMessageToMirrorNode("userSecurity", userSec);

		return ResponseEntity
				.ok(new MessageResponse(HttpStatus.OK.value(), env.getProperty("registered.success"), user));

	}

	/* publishMessageToMirrorNode is used to publish message into mirror node */
	private MessageResponse publishMessageToMirrorNode(String methodName, Object object)
			throws PublishingMirrorNodeException {
		String url = hcsBaseUrl + String.format(OnlineBookStoreConstant.PUBLISH_MIRROR_NODE_USER);
		MessageResponse response = null;
		try {
			switch (methodName) {
			case "user":
				User user = (User) object;
				user.setMethodName("users");

				RequestEntity<User> entityUser = new RequestEntity<>(user, HttpMethod.POST,
						restTemplateService.uri(url));
				response = restTemplate.exchange(entityUser, MessageResponse.class).getBody();

				break;

			case "userInfo":
				UserInfo userInfo = (UserInfo) object;
				userInfo.setMethodName("userInfo");
				RequestEntity<UserInfo> entityPersonalInfo = new RequestEntity<>(userInfo, HttpMethod.POST,
						restTemplateService.uri(url));
				response = restTemplate.exchange(entityPersonalInfo, MessageResponse.class).getBody();
				break;

			case "userSecurity":
				UserSecurity userSec = (UserSecurity) object;
				userSec.setMethodName("userSecurity");
				RequestEntity<UserSecurity> entitySimulator = new RequestEntity<>(userSec, HttpMethod.POST,
						restTemplateService.uri(url));
				response = restTemplate.exchange(entitySimulator, MessageResponse.class).getBody();
				break;
			}

		} catch (Exception e) {
			System.out.print(e.getLocalizedMessage());
			throw new PublishingMirrorNodeException();
		}
		return response;
	}

	/* associate User */
	private MessageResponse associateUser(HttpServletRequest request, AssociateRequest token) {
		MessageResponse response = null;

		String url = htsBaseUrl + String.format(OnlineBookStoreConstant.INITIAL_TOKEN);
		RequestEntity<AssociateRequest> secEntity = new RequestEntity<>(token, restTemplateService.headers(request),
				HttpMethod.POST, restTemplateService.uri(url));
		response = restTemplate.exchange(secEntity, MessageResponse.class).getBody();

		return response;
	}

//saveSecurity method
	private UserSecurity saveSecurity(UserInfo userInfo, User user) {

		StringEntity entity1 = new StringEntity(userInfo.getUser().getId(), ContentType.APPLICATION_JSON);
		HttpClient httpClient = HttpClientBuilder.create().build();
		HttpPost request = new HttpPost(
				env.getProperty("createaccount.url") + OnlineBookStoreConstant.CREATE_WALLET_API);
		request.setEntity(entity1);
		HttpResponse response;
		String content = null;
		try {
			response = httpClient.execute(request);
			HttpEntity entity = response.getEntity();
			content = EntityUtils.toString(entity);
			if (Objects.isNull(content)) {
				throw new CreateWalletMirrorNodeException();
			}
		} catch (Exception e1) {
			System.out.print(e1.getLocalizedMessage());

		}
		JSONObject inputJsonResponse = new JSONObject(content);
		JSONObject inputJson = (JSONObject) inputJsonResponse.get("response");
		String accrr = inputJson.getString("account");
		String acc[] = accrr.split("\\.");
		UserSecurity security = new UserSecurity();
		security.setUser(user);
		security.setUserPubKey(inputJson.getString("publickey"));
		security.setUserPrivateKey(inputJson.getString("privatekey"));
		security.setAccountShard(Long.parseLong(acc[0]));
		security.setAccountRealm(Long.parseLong(acc[1]));
		security.setAccountNum(Long.parseLong(acc[2]));
		security.setAccountBalance("0");
		return userSecurityRepository.save(security);

	}

//insert user info into userinfo table 
	private UserInfo insertUserInfo(@Valid SignupRequest signUpRequest, User user) {
		UserInfo userInfo = UserInfo.builder().user(user).name(signUpRequest.getName()).email(signUpRequest.getEmail())
				.mobileNumber(signUpRequest.getMobileNumber()).address(signUpRequest.getAddress()).build();

		return userInfoRepository.save(userInfo);
	}

	/* GET ROLE METHOD FOR ONLINE BOOK STORE */
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
				case "ROLE_ADMIN":
					if (!roleRepository.existsByName(ERole.ROLE_ADMIN)) {
						throw new RoleNotFoundException();
					}
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).get();
					roles.add(adminRole);
					break;
				case "ROLE_BUYER":
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