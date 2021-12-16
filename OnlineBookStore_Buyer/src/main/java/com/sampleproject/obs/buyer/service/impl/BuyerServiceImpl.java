package com.sampleproject.obs.buyer.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sampleproject.obs.buyer.payload.request.BuyBookRequest;
import com.sampleproject.obs.buyer.payload.request.TransactionFeeDto;
import com.sampleproject.obs.buyer.service.BuyerService;
import com.sampleproject.obs.data.constant.OnlineBookStoreConstant;
import com.sampleproject.obs.data.model.BookInfo;
import com.sampleproject.obs.data.model.BuyerInfo;
import com.sampleproject.obs.data.model.ERole;
import com.sampleproject.obs.data.model.Role;
import com.sampleproject.obs.data.model.TokenIdInfo;
import com.sampleproject.obs.data.model.TransactionFee;
import com.sampleproject.obs.data.model.TransactionHistroy;
import com.sampleproject.obs.data.model.User;
import com.sampleproject.obs.data.model.UserSecurity;
import com.sampleproject.obs.data.payload.response.MessageResponse;
import com.sampleproject.obs.data.repository.BookInfoRepository;
import com.sampleproject.obs.data.repository.BuyerInfoRepository;
import com.sampleproject.obs.data.repository.RoleRepository;
import com.sampleproject.obs.data.repository.TokenIdInfoRepo;
import com.sampleproject.obs.data.repository.TransactionfeeRepository;
import com.sampleproject.obs.data.repository.TranscationHistoryRepo;
import com.sampleproject.obs.data.repository.UserRepository;
import com.sampleproject.obs.data.repository.UserSecurityRepository;
import com.sampleproject.obs.data.request.TokenInfoRequest;
import com.sampleproject.obs.data.request.TransactionHistroyRequest;
import com.sampleproject.obs.data.util.PublishInfo;

@Service
public class BuyerServiceImpl implements BuyerService {

	@Autowired
	BuyerInfoRepository buyerInfoRepository;

	@Autowired
	BookInfoRepository bookInfoRepository;

	@Autowired
	Environment env;

	@Autowired
	UserRepository userRepository;

	@Autowired
	UserSecurityRepository userSecurityRepository;

	@Autowired
	RoleRepository roleRepository;
	@Autowired
	TokenIdInfoRepo tokenIdInfoRepo;

	@Autowired
	PublishInfo publishInfo;

	@Value("${pound.per.token}")
	private int poundPerToken;

	@Value("${timezone}")
	private String timeZone;

	@Autowired
	TransactionfeeRepository transactionfeeRepository;

	@Autowired
	TranscationHistoryRepo transcationHistoryRepo;

	@Override
	public ResponseEntity<List<BookInfo>> viewBookInfo() {
		List<BookInfo> bookinfo = bookInfoRepository.findAll();
		return ResponseEntity.ok(bookinfo);
	}

	@Override
	public ResponseEntity<?> buyBook(BuyBookRequest buyBookRequest) {
		User user = userRepository.getById(buyBookRequest.getBuyer_id()).get();
		BookInfo book = bookInfoRepository.findByBookNameAndAuthor(buyBookRequest.getBook_name(),
				buyBookRequest.getAuthor());
		int bookCount = book.getNo_of_book_available() - buyBookRequest.getQuantity();
//		bookCount--;
		bookInfoRepository.updateBookCount(bookCount, buyBookRequest.getBook_name(), buyBookRequest.getAuthor());

		BuyerInfo buyerInfo = BuyerInfo.builder().buyer_id(buyBookRequest.getBuyer_id())
				.buyer_name(buyBookRequest.getBuyer_name()).email(buyBookRequest.getEmail())
				.mobile_number(buyBookRequest.getMobile_number()).address(buyBookRequest.getAddress())
				.book_name(buyBookRequest.getBook_name()).book_id(buyBookRequest.getBook_id())
				.author(buyBookRequest.getAuthor()).price(buyBookRequest.getPrice())
				.quantity(buyBookRequest.getQuantity()).createdTime(buyBookRequest.getCreatedTime()).user(user).build();

		return ResponseEntity.ok(buyerInfo);
	}

	@Override
	public ResponseEntity<?> getWallet(String buyer_id) {
		User user = userRepository.getById(buyer_id).get();
		try {
			if (user != null) {
				UserSecurity security = userSecurityRepository.findByUser(user).get();

				return ResponseEntity
						.ok(new MessageResponse(HttpStatus.OK.value(), env.getProperty("wallet.success"), security));
			}
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return ResponseEntity
					.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(), env.getProperty("wallet.fail")));
		}

		return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(), env.getProperty("wallet.fail")));
	}

	@Override
	public ResponseEntity<?> buyToken(String buyer_id, int amount, HttpServletRequest request) {
		try {

			User user = userRepository.getById(buyer_id).get();
			UserSecurity security = userSecurityRepository.findByUser(user).get();
			Optional<Role> roleOptional = roleRepository.findByName(ERole.ROLE_ADMIN);
			List<User> admin = userRepository.findAllByRoles(roleOptional.get());
			UserSecurity adminDetails = userSecurityRepository.findByUser(admin.get(0)).get();
			TokenIdInfo tokenValue = tokenIdInfoRepo.findByStatus(TokenIdInfo.Status.ACTIVE);
			if (Objects.isNull(tokenValue)) {
				return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value())
						.message(env.getProperty("token.value.not.found")).build());
			}

			String fromSenderId = adminDetails.getAccountShard() + "." + adminDetails.getAccountRealm() + "."
					+ adminDetails.getAccountNum();
			String toAccountId = security.getAccountShard() + "." + security.getAccountRealm() + "."
					+ security.getAccountNum();
			 double amountInCurrency = Double.valueOf(amount) /
			 Double.valueOf(poundPerToken);

			TokenInfoRequest token = TokenInfoRequest.builder().fromSenderId(fromSenderId)
					.fromSenderKey(adminDetails.getUserPrivateKey()).toAccountId(toAccountId)
					.toAccountKey(security.getUserPrivateKey()).amount(amount).tokenId(env.getProperty("token.value"))
					 .amountInCurrency(amountInCurrency)
					.operatorId(toAccountId).operatorKey(security.getUserPrivateKey())
					.payments(OnlineBookStoreConstant.PAID_IN).userId(user.getId()).build();
			String balance = "";

			MessageResponse response = publishInfo.transferTokenToMirrorNode(request, token);
			if (Objects.isNull(response)) {
				return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(),
						env.getProperty("fund.transfer.mirror.node.error")));
			}
			if (response.getStatus() == HttpStatus.OK.value()) {
				return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
						.message(env.getProperty("buy.token.success")).response(balance).build());

			} else {
				return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value())
						.message(env.getProperty("fund.transfer.mirror.node.error")).response(balance).build());
			}
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.getLocalizedMessage());
			return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(),
					env.getProperty("buy.token.fail"), e.getMessage()));
		}

	}

	@Override
	public ResponseEntity<?> withDrawToken(String buyer_id, int amount, HttpServletRequest request) {
		try {

			User user = userRepository.getById(buyer_id).get();
			UserSecurity security = userSecurityRepository.findByUser(user).get();
			Optional<Role> roleOptional = roleRepository.findByName(ERole.ROLE_ADMIN);
			List<User> admin = userRepository.findAllByRoles(roleOptional.get());
			UserSecurity adminDetails = userSecurityRepository.findByUser(admin.get(0)).get();
			TokenIdInfo tokenValue = tokenIdInfoRepo.findByStatus(TokenIdInfo.Status.ACTIVE);
			if (Objects.isNull(tokenValue)) {
				return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value())
						.message(env.getProperty("token.value.not.found")).build());
			}

			String fromSenderId = security.getAccountShard() + "." + security.getAccountRealm() + "."
					+ security.getAccountNum();
			String toAccountId = adminDetails.getAccountShard() + "." + adminDetails.getAccountRealm() + "."
					+ adminDetails.getAccountNum();
			// double amountInCurrency = Double.valueOf(amount) /
			// Double.valueOf(poundPerToken);

			// Check Balance
			String balance = null;
			MessageResponse response = null;
			// Check User Token Balance
			response = publishInfo.getBalance(request, fromSenderId);

			if (Objects.isNull(response)) {
				return ResponseEntity
						.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(), env.getProperty("balance.not.found")));
			}
			if (response.getStatus() != HttpStatus.OK.value()) {
				return ResponseEntity
						.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(), env.getProperty("balance.not.found")));
			}

			balance = (String) response.getResponse();

			if (Integer.parseInt(balance) <= 0 || (amount > Integer.parseInt(balance))) {
				return ResponseEntity
						.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(), env.getProperty("balance.not.enough")));
			}

			TokenInfoRequest token = TokenInfoRequest.builder().fromSenderId(fromSenderId)
					.fromSenderKey(security.getUserPrivateKey()).toAccountId(toAccountId).amount(amount)
					.operatorId(toAccountId).operatorKey(adminDetails.getUserPrivateKey())
					.tokenId(tokenValue.getTokenId())
					// .amountInCurrency(amountInCurrency)
					.payments(OnlineBookStoreConstant.PAID_OUT).userId(user.getId()).build();

			response = publishInfo.transferTokenToMirrorNode(request, token);
			if (Objects.isNull(response)) {
				return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(),
						env.getProperty("fund.transfer.mirror.node.error"), ""));
			}
			if (response.getStatus() == HttpStatus.OK.value()) {

				return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
						.message(env.getProperty("withdraw.token.success")).response(balance).build());
			} else {
				return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(),
						env.getProperty("fund.transfer.mirror.node.error"), ""));
			}
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(),
					env.getProperty("withdraw.token.fail"), e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> getTokeBalance(String buyer_id, HttpServletRequest request) {
		try {
			UserSecurity security = userSecurityRepository.findByUserId(buyer_id).get();
			String operatorId = security.getAccountShard() + "." + security.getAccountRealm() + "."
					+ security.getAccountNum();
			String balance = "0";
			MessageResponse response = publishInfo.getBalance(request, operatorId);
			Map<String, String> tokenDetails = new HashMap<String, String>();
			tokenDetails.put(OnlineBookStoreConstant.BALANCE_VALUE, balance);
			tokenDetails.put(OnlineBookStoreConstant.WALLET_ID,
					security.getAccountShard() + "." + security.getAccountRealm() + "." + security.getAccountNum());
			if (response.getStatus() == HttpStatus.OK.value()) {
				balance = (String) response.getResponse();
				tokenDetails.put(OnlineBookStoreConstant.BALANCE_VALUE, balance);

			}

			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
					.message(env.getProperty("token.retrieved.success")).response(tokenDetails).build());
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(),
					env.getProperty("failed.fetch.tokenbalance"), e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> tokenTransactionHistory(String buyer_id, HttpServletRequest request) {
		try {
			User user = userRepository.getById(buyer_id).get();
			List<TransactionHistroy> transaction = transcationHistoryRepo.findByUser(user);
			List<TransactionHistroyRequest> txnHistory = new ArrayList<TransactionHistroyRequest>();
			if (transaction != null) {
				transaction.stream().forEach(transactions -> {
					TransactionHistroyRequest txHistoryRequest = null;
					txHistoryRequest = TransactionHistroyRequest.builder().id(transactions.getId())
							.amountInCurrency(transactions.getAmountInCurrency())
							.balanceAmountInCurrency(transactions.getBalanceAmountInCurrency())
							.balanceStableCoin(transactions.getBalanceStableCoin())
							.dateTime(publishInfo.convertDateTimeToZoneTime(transactions.getDateTime(), timeZone)
									.toString("yyyy-MM-dd HH:mm:ss"))
							.netWorkFee(transactions.getNetWorkFee()).nodeFee(transactions.getNodeFee())
							.payments(transactions.getPayments()).stableCoin(transactions.getStableCoin())
							.security(transactions.getSecurity()).totalFee(transactions.getTotalFee())
							.transactionfee(transactions.getTransactionfee())
							.transcationType(transactions.getTranscationType()).user(user).build();
					txnHistory.add(txHistoryRequest);
				});

			}
			return ResponseEntity
					.ok(new MessageResponse(HttpStatus.OK.value(), env.getProperty("get.history.success"), txnHistory));
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage());
			return ResponseEntity.ok(new MessageResponse(HttpStatus.BAD_REQUEST.value(),
					env.getProperty("get.history.failed"), e.getMessage()));
		}
	}

	@Override
	public ResponseEntity<?> getTrasactionfee() {
		List<TransactionFee> transactionFee = transactionfeeRepository.findAll();
		List<TransactionFeeDto> transactionFeeDtoList = new ArrayList<TransactionFeeDto>();
		TransactionFeeDto transactionFeeDto = new TransactionFeeDto();
		for (TransactionFee transactionFee2 : transactionFee) {
			transactionFeeDto = TransactionFeeDto.builder().transactionFeeId(transactionFee2.getId())
					.amount(Integer.parseInt(transactionFee2.getFee())).build();
		}
//		transactionFee.stream().forEach( transactionFee2->{
//			TransactionFeeDto transactionFeeDto = new TransactionFeeDto();
//			transactionFeeDto = TransactionFeeDto.builder().transactionFeeId(transactionFee2.getId())
//					.amount(Integer.parseInt(transactionFee2.getFee())).build();
//			transactionFeeDtoList.add(transactionFeeDto);
//		});
		return ResponseEntity.ok(new MessageResponse(HttpStatus.OK.value(), env.getProperty("transaction.fee.success"),
				transactionFeeDto));

	}
}
/* */
