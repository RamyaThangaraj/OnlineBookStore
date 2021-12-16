package com.sampleproject.obs.admin.service.impl;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.sampleproject.obs.admin.model.BookInfoRequest;
import com.sampleproject.obs.admin.model.TransactionFeeDto;
import com.sampleproject.obs.admin.service.AdminService;
import com.sampleproject.obs.data.constant.OnlineBookStoreConstant;
import com.sampleproject.obs.data.exception.GetBalanceMirrorNodeException;
import com.sampleproject.obs.data.exception.HTSException;
import com.sampleproject.obs.data.exception.PublishingMirrorNodeException;
import com.sampleproject.obs.data.model.BookInfo;
import com.sampleproject.obs.data.model.ERole;
import com.sampleproject.obs.data.model.MintTokenDto;
import com.sampleproject.obs.data.model.Role;
import com.sampleproject.obs.data.model.TokenIdInfo;
import com.sampleproject.obs.data.model.TokenIdInfo.Status;
import com.sampleproject.obs.data.model.TransactionFee;
import com.sampleproject.obs.data.model.TransactionHistroy;
import com.sampleproject.obs.data.model.User;
import com.sampleproject.obs.data.model.UserInfo;
import com.sampleproject.obs.data.model.UserSecurity;
import com.sampleproject.obs.data.payload.response.MessageResponse;
import com.sampleproject.obs.data.repository.BookInfoRepository;
import com.sampleproject.obs.data.repository.RoleRepository;
import com.sampleproject.obs.data.repository.TokenIdInfoRepo;
import com.sampleproject.obs.data.repository.TransactionfeeRepository;
import com.sampleproject.obs.data.repository.TranscationHistoryRepo;
import com.sampleproject.obs.data.repository.UserRepository;
import com.sampleproject.obs.data.repository.UserSecurityRepository;
import com.sampleproject.obs.data.request.CreateTokenDto;
import com.sampleproject.obs.data.request.CreateTokenRequest;
import com.sampleproject.obs.data.services.RestTemplateService;
import com.sampleproject.obs.data.util.PublishInfo;


@Service
public class AdminServiceImpl implements AdminService{
	
	@Autowired
	BookInfoRepository bookInfoRepo;
	
	@Autowired
	Environment env;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	RoleRepository roleRepository;

	@Autowired
	UserSecurityRepository userSecurityRepository;
	
	@Autowired
	TranscationHistoryRepo transcationHistoryRepo;

//	@Value("${pound.per.token}")
//	private int poundPerToken;
	@Autowired
	TokenIdInfoRepo tokenIdInfoRepo;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	RestTemplateService restTemplateService;

	@Value("${hcs.url}")
	private String hcsBaseUrl;
	@Value("${hts.url}")
	private String htsBaseUrl;
	
	@Autowired
	TransactionfeeRepository transactionfeeRepository;

	@Value("${timezone}")
	private String timeZone;

	@Autowired
	PasswordEncoder encoder;
	
	@Autowired
	PublishInfo publishInfo;

	

	@Override
	public ResponseEntity<?> addBook(BookInfoRequest bookInfo) {
		BookInfo bookinfo = BookInfo.builder().bookname(bookInfo.getBookname()).author(bookInfo.getAuthor()).price(bookInfo.getPrice()).no_of_book_available(bookInfo.getNo_of_book_available()).build();
		bookinfo= bookInfoRepo.save(bookinfo);
		return ResponseEntity.ok(bookinfo);
	}

	@Override
	public ResponseEntity<?> updateBook(BookInfoRequest bookinforeq) {
		BookInfo bookinfo = bookInfoRepo.findById(bookinforeq.getId()).get();
		bookinfo= BookInfo.builder().id(bookinforeq.getId()).bookname(bookinforeq.getBookname()).author(bookinforeq.getAuthor()).price(bookinforeq.getPrice()).no_of_book_available(bookinforeq.getNo_of_book_available()).build();
		bookInfoRepo.save(bookinfo);
				return ResponseEntity.ok(bookinfo);
	}

	@Override
	public ResponseEntity<?> deleteBook(String id) {
		bookInfoRepo.findById(id);
		bookInfoRepo.deleteById(id);
		return ResponseEntity.ok("BOOK INFO DELETED");
	}

	@Override
	public ResponseEntity<List<BookInfo>> showBookInfo() {
		List<BookInfo> bookinformation=bookInfoRepo.findAll();
		return ResponseEntity.ok(bookinformation);
	}


	@Override
	public ResponseEntity<?> getTokeBalance(String adminId, HttpServletRequest request)
			throws GetBalanceMirrorNodeException {
		try {
			UserSecurity security = userSecurityRepository.findByUserId(adminId).get();
			String operatorId = security.getAccountShard() + "." + security.getAccountRealm() + "."
					+ security.getAccountNum();
			String balance = "0";
			Map<String, String> tokenDetails = new HashMap<String, String>();
			tokenDetails.put(OnlineBookStoreConstant.BALANCE_VALUE, balance);
			tokenDetails.put(OnlineBookStoreConstant.WALLET_ID,
					security.getAccountShard() + "." + security.getAccountRealm() + "." + security.getAccountNum());
			MessageResponse response = getBalance(request, operatorId);
			if (response.getStatus() == HttpStatus.OK.value()) {
				balance = (String) response.getResponse();
				tokenDetails.put(OnlineBookStoreConstant.BALANCE_VALUE, balance);
			}
			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
					.message(env.getProperty("token.retrieved.success")).response(security).build());
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value())
					.message(env.getProperty("token.retrieved.failed")));
		}
	}

	@Override
	public ResponseEntity<?> tokenTransactionHistory(String adminId, HttpServletRequest request) throws JSONException {
		try {
			JSONObject json = null;
			JSONArray jsonArray = new JSONArray();
			DecimalFormat df = new DecimalFormat("0.00");
			List<TransactionHistroy> transactionList = transcationHistoryRepo.findByUser();
			if (transactionList.size() > 0) {
				for (int i = 0; i < transactionList.size(); i++) {
					json = new JSONObject();
					Set<Role> role2 = transactionList.get(i).getUser().getRoles();
					String name = role2.iterator().next().getName().toString();
					String dateTime = publishInfo
							.convertDateTimeToZoneTime(transactionList.get(i).getDateTime(), timeZone)
							.toString("yyyy-MM-dd HH:mm:ss");
					if (name != OnlineBookStoreConstant.ROLE_ADMIN) {
						Optional<Role> roleOptional = roleRepository.findByName(ERole.ROLE_ADMIN);
						List<User> admin = userRepository.findAllByRoles(roleOptional.get());
						if (transactionList.get(i).getTranscationType().equals(OnlineBookStoreConstant.CREDIT_WITHDRAWAL)) {
							json.put(OnlineBookStoreConstant.BUYER_ID, admin.get(0).getEmail());
							json.put(OnlineBookStoreConstant.SELLER_ID, transactionList.get(i).getUser().getEmail());
							json.put(OnlineBookStoreConstant.TRANSCATION_TYPE_VALUE, OnlineBookStoreConstant.CREDIT_DEPOSIT);
							json.put(OnlineBookStoreConstant.PAYMENTS, OnlineBookStoreConstant.PAID_IN);

						} else {
							json.put(OnlineBookStoreConstant.BUYER_ID, transactionList.get(i).getUser().getEmail());
							json.put(OnlineBookStoreConstant.SELLER_ID, admin.get(0).getEmail());
							json.put(OnlineBookStoreConstant.TRANSCATION_TYPE_VALUE, OnlineBookStoreConstant.CREDIT_WITHDRAWAL);
							json.put(OnlineBookStoreConstant.PAYMENTS, OnlineBookStoreConstant.PAID_OUT);
						}

						json.put(OnlineBookStoreConstant.AMOUNT_IN_CURRENCY,
								df.format(Double.parseDouble(transactionList.get(i).getAmountInCurrency())));
						json.put(OnlineBookStoreConstant.STABLE_COIN,
								df.format(Double.parseDouble(transactionList.get(i).getStableCoin()) / 100));
						json.put(OnlineBookStoreConstant.BALANCE_STABLE_COIN,
								df.format(Double.parseDouble(transactionList.get(i).getBalanceStableCoin()) / 100));
						json.put(OnlineBookStoreConstant.BALANCE_STABLE_COIN_IN_CURRENCY,
								df.format(Double.parseDouble(transactionList.get(i).getBalanceAmountInCurrency())));
						json.put(OnlineBookStoreConstant.BALANCE_HBARS,
								transactionList.get(i).getSecurity().getAccountBalance());

						json.put(OnlineBookStoreConstant.NODE_FEE,
								(transactionList.get(i).getNodeFee() != null) ? transactionList.get(i).getNodeFee()
										: "-");
						json.put(OnlineBookStoreConstant.NET_WORK_FEE,
								(transactionList.get(i).getNetWorkFee() != null)
										? transactionList.get(i).getNetWorkFee()
										: "-");
						json.put(OnlineBookStoreConstant.TRANSACTIONFEE,
								(transactionList.get(i).getTransactionfee() != null)
										? transactionList.get(i).getTransactionfee()
										: "-");
						json.put(OnlineBookStoreConstant.SMART_CONTRACT_FEE,
								(transactionList.get(i).getSmartContractFee() != null)
										? transactionList.get(i).getSmartContractFee()
										: "-");
						json.put(OnlineBookStoreConstant.TOTAL_FEE,
								(transactionList.get(i).getTotalFee() != null) ? transactionList.get(i).getTotalFee()
										: "-");
						json.put(OnlineBookStoreConstant.BALANCE_HBARS,
								(transactionList.get(i).getBalanceHbars() != null)
										? transactionList.get(i).getBalanceHbars()
										: "-");
						json.put(OnlineBookStoreConstant.USER_EMAIL, transactionList.get(i).getUser().getEmail());
						json.put(OnlineBookStoreConstant.DATE_TIME, dateTime);

						jsonArray.put(json);

					}

				}
			}
			return ResponseEntity.ok(new MessageResponse(HttpStatus.OK.value(), env.getProperty("get.history.success"),
					jsonArray.toList()));
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value())
					.message(env.getProperty("get.history.failed")));
		}
	}

	@Override
	public ResponseEntity<?> getTrasactionfee() {
		List<TransactionFee> transactionFee = transactionfeeRepository.findAll();
		TransactionFeeDto transactionFeeDto = new TransactionFeeDto();
		
		for (TransactionFee transactionFee2 : transactionFee) {
			transactionFeeDto = TransactionFeeDto.builder().transactionFeeId(transactionFee2.getId())
					.amount(Integer.parseInt(transactionFee2.getFee())).build();
		}
		return ResponseEntity.ok(new MessageResponse(HttpStatus.OK.value(), env.getProperty("transaction.fee.success"),
				transactionFeeDto));
	}

	@Override
	public ResponseEntity<?> updateTransactionFee(TransactionFeeDto transactionFeeDto, HttpServletRequest request) {
		try {
			// TODO Auto-generated method stub
			TransactionFee transactionFee = transactionfeeRepository.findById(transactionFeeDto.getTransactionFeeId())
					.get();
			transactionFee.setFee(String.valueOf(transactionFeeDto.getAmount()));
			TransactionFee transactionFeeResult = transactionfeeRepository.save(transactionFee);
			return ResponseEntity.ok(new MessageResponse(HttpStatus.OK.value(),
					env.getProperty("transaction.fee.update"), transactionFeeResult));
		} catch (Exception e) {
			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value())
					.message(env.getProperty("transaction.fee.failed")));
		}
	}
	
	/*
	 * getBalance method is used to get balance from Hedera Token Service
	 */
	private MessageResponse getBalance(HttpServletRequest request, String operatorId)
			throws GetBalanceMirrorNodeException {
		MessageResponse response = null;
		try {
			String url = htsBaseUrl + String.format(OnlineBookStoreConstant.GET_BALANCE_API);
			RequestEntity<?> secEntity = new RequestEntity<>(operatorId, restTemplateService.headers(request),
					HttpMethod.POST, restTemplateService.uri(url));
			response = restTemplate.exchange(secEntity, MessageResponse.class).getBody();
		} catch (Exception e) {
			// TODO: handle exception
			throw new GetBalanceMirrorNodeException();
		}
		return response;
	}
	
	/*
	 * createToken is used to create token.It takes input parameters as
	 * tokenName,tokenSymbol,initialSupply and userId
	 */
	@Override
	@Transactional(rollbackFor = { HTSException.class, PublishingMirrorNodeException.class })
	public ResponseEntity<?> createToken(CreateTokenRequest createTokenRequest, HttpServletRequest request)
			throws HTSException, PublishingMirrorNodeException {
		// TODO Auto-generated method stub
		User user = userRepository.getById(createTokenRequest.getUserId()).get();
		UserSecurity userSecurity = userSecurityRepository.findByUserId(user.getId()).get();
		if (Objects.isNull(userSecurity)) {
			return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(),
					env.getProperty("user.security.not.found"), ""));
		}
		String operatorId = userSecurity.getAccountShard() + "." + userSecurity.getAccountRealm() + "."
				+ userSecurity.getAccountNum();
		TokenIdInfo token = new TokenIdInfo();
		CreateTokenDto createTokenDto = CreateTokenDto.builder().tokenName(createTokenRequest.getTokenName())
				.tokenSymbol(createTokenRequest.getTokenSymbol()).initialSupply(createTokenRequest.getInitialSupply())
				.operatorId(operatorId).operatorkey(userSecurity.getUserPrivateKey()).build();
		MessageResponse response = publishToMirrorNoideForToken(createTokenDto, request);
		String tokenId = (String) response.getResponse();
		if (Objects.nonNull(tokenId)) {
			List<TokenIdInfo> existingToken = tokenIdInfoRepo.findAll();
			for (TokenIdInfo tokenIdInfo : existingToken) {
				tokenIdInfo.setStatus(Status.IN_ACTIVE);
				tokenIdInfoRepo.save(tokenIdInfo);
			}
			token.setTokenId(tokenId);
			token.setSymbol(createTokenDto.getTokenSymbol());
			token.setStatus(Status.ACTIVE);
			tokenIdInfoRepo.save(token);
			publishMessageToMirrorNode("createToken", token);
			return ResponseEntity
					.ok(new MessageResponse(HttpStatus.OK.value(), env.getProperty("token.create.success"), tokenId));
		} else {
			return ResponseEntity.ok(
					new MessageResponse(HttpStatus.BAD_REQUEST.value(), env.getProperty("create.toekn.failed"), ""));
		}

	}
	
	/*
	 * publishToMirrorNoideForToken method is used to create token in Hedera Token
	 * Service
	 */
	private MessageResponse publishToMirrorNoideForToken(Object object, HttpServletRequest request)
			throws HTSException {
		String url1 = htsBaseUrl + String.format(OnlineBookStoreConstant.TOKEN_CREATE);
		MessageResponse response = null;
		try {
//			RequestEntity<?> secEntity = new RequestEntity<>(object, restTemplateService.headers(request),
//					HttpMethod.POST, restTemplateService.uri(url1),content);
			RequestEntity<?> secEntity = RequestEntity.post(restTemplateService.uri(url1))
					.accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).body(object);
			response = restTemplate.exchange(secEntity, MessageResponse.class).getBody();

		} catch (Exception e) {
			// TODO: handle exception
			throw new HTSException();
		}

		return response;
	}

	/*
	 * publishMessageToMirrorNode method is used to publish message into mirror
	 * node.
	 */
	private MessageResponse publishMessageToMirrorNode(String methodName, Object object)
			throws PublishingMirrorNodeException {
		String url = hcsBaseUrl + String.format(OnlineBookStoreConstant.PUBLISH_MIRROR_NODE_USER);
		MessageResponse response = null;
		try {
			switch (methodName) {

			case "userInfo":
				UserInfo userInfo = (UserInfo) object;
				userInfo.setMethodName("userInfo");
				RequestEntity<UserInfo> entityPersonalInfo = new RequestEntity<>(userInfo, HttpMethod.POST,
						restTemplateService.uri(url));
				response = restTemplate.exchange(entityPersonalInfo, MessageResponse.class).getBody();
				break;
			
			case "createToken":
				TokenIdInfo tokenInfo = (TokenIdInfo) object;
				// info.setMethodName("orgainizationUpdate");
				RequestEntity<TokenIdInfo> entityTokenInfo = new RequestEntity<>(tokenInfo, HttpMethod.POST,
						restTemplateService.uri(url));
				response = restTemplate.exchange(entityTokenInfo, MessageResponse.class).getBody();
				break;
//			case "mintToken":
//				MintTokenDto mintTokenDto = (MintTokenDto) object;
//				// info.setMethodName("orgainizationUpdate");
//				RequestEntity<MintTokenDto> entityMintTokenDto = new RequestEntity<>(mintTokenDto, HttpMethod.POST,
//						restTemplateService.uri(url));
//				response = restTemplate.exchange(entityMintTokenDto, MessageResponse.class).getBody();
//				break;
			
			}
			return response;
		} catch (Exception e) {
			
			throw new PublishingMirrorNodeException();
		}
	}

//	@Override
//	public ResponseEntity<?> mintToken(String userId, int amount, HttpServletRequest request) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	/*
	 * mintToken method is used to mint token.It takes input parameters as user id
	 * and amount.Finally mint token message published into mirror node
	 */
	@Override
	public ResponseEntity<?> mintToken(String userId, int amount, HttpServletRequest request)
			throws PublishingMirrorNodeException, HTSException {
		// TODO Auto-generated method stub
		User user = userRepository.getById(userId).get();

		UserSecurity useseSecurity = userSecurityRepository.findByUser(user).get();
		if (Objects.isNull(useseSecurity)) {
			return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(),
					env.getProperty("user.security.not.found"), ""));
		}
		TokenIdInfo tokenIdInfo = tokenIdInfoRepo.findByStatus(Status.ACTIVE);
		String tokenId = "";
		if (Objects.nonNull(tokenIdInfo)) {
			tokenId = tokenIdInfo.getTokenId();
		} else {
			return ResponseEntity.ok(
					new MessageResponse(HttpStatus.BAD_REQUEST.value(), env.getProperty("active.token.not.found"), ""));
		}
		MintTokenDto mintTokenDto = new MintTokenDto();
		if (Objects.nonNull(useseSecurity)) {
			String operatorId = useseSecurity.getAccountShard() + "." + useseSecurity.getAccountRealm() + "."
					+ useseSecurity.getAccountNum();

			mintTokenDto = MintTokenDto.builder().operatorId(operatorId).operatorKey(useseSecurity.getUserPrivateKey())
					.amount(amount).tokenId(tokenId).userId(userId).build();
			MessageResponse response = mintTokenInHts(mintTokenDto, request);
			publishMessageToMirrorNode("mintToken", mintTokenDto);
			if (response.getStatus() == HttpStatus.OK.value()) {
				return ResponseEntity
						.ok(new MessageResponse(HttpStatus.OK.value(), env.getProperty("amount.added.success"), ""));
			} else {
				return ResponseEntity
						.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(), env.getProperty("amount.failed"), ""));
			}

		} else {
			return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(),
					env.getProperty("user.security.not.found"), ""));
		}

	}

		
	/*
	 * mintTokenInHts method is used to mint token in Hedera Token Service
	 */
	private MessageResponse mintTokenInHts(Object object, HttpServletRequest request) throws HTSException {
		String url1 = htsBaseUrl + String.format(OnlineBookStoreConstant.MINT_TOEKN);
		MessageResponse response = null;
		try {
//			RequestEntity<?> secEntity = new RequestEntity<>(object, restTemplateService.headers(request),
//					HttpMethod.POST, restTemplateService.uri(url1));
			RequestEntity<?> secEntity= RequestEntity.post(restTemplateService.uri(url1)).accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).body(object);
			response = restTemplate.exchange(secEntity, MessageResponse.class).getBody();

		} catch (Exception e) {
			// TODO: handle exception
			throw new HTSException();
		}

		return response;
	}

}
