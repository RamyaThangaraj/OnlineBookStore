package com.sampleproject.obs.hts.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hedera.hashgraph.sdk.AccountBalanceQuery;
import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Hbar;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.Status;
import com.hedera.hashgraph.sdk.TokenAssociateTransaction;
import com.hedera.hashgraph.sdk.TokenCreateTransaction;
import com.hedera.hashgraph.sdk.TokenGrantKycTransaction;
import com.hedera.hashgraph.sdk.TokenId;
import com.hedera.hashgraph.sdk.TokenMintTransaction;
import com.hedera.hashgraph.sdk.TransactionReceipt;
import com.hedera.hashgraph.sdk.TransactionResponse;
import com.hedera.hashgraph.sdk.Transfer;
import com.hedera.hashgraph.sdk.TransferTransaction;
import com.sampleproject.obs.data.constant.OnlineBookStoreConstant;
import com.sampleproject.obs.data.exception.BalanceTokenNotFoundException;
import com.sampleproject.obs.data.model.ERole;
import com.sampleproject.obs.data.model.MintTokenDto;
import com.sampleproject.obs.data.model.Role;
import com.sampleproject.obs.data.model.TokenIdInfo;
import com.sampleproject.obs.data.model.TransactionHistroy;
import com.sampleproject.obs.data.model.User;
import com.sampleproject.obs.data.model.UserSecurity;
import com.sampleproject.obs.data.payload.response.MessageResponse;
import com.sampleproject.obs.data.repository.RoleRepository;
import com.sampleproject.obs.data.repository.TokenIdInfoRepo;
import com.sampleproject.obs.data.repository.TranscationHistoryRepo;
import com.sampleproject.obs.data.repository.UserRepository;
import com.sampleproject.obs.data.repository.UserSecurityRepository;
import com.sampleproject.obs.hts.request.CreateTokenDto;
import com.sampleproject.obs.hts.request.TokenBean;
import com.sampleproject.obs.hts.response.TokenTransferResponse;
import com.sampleproject.obs.hts.service.HederaTokenService;
import com.sampleproject.obs.hts.service.TokenHelperService;

@Service(value = "HederaTokenServiceImpl")
public class HederaTokenServiceImpl implements HederaTokenService {

	@Autowired
	TokenHelperService tokenHelper;

	@Autowired
	UserSecurityRepository userSecurityRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	TokenIdInfoRepo tokenIdInfoRepo;

	@Autowired
	TranscationHistoryRepo transcationHistoryRepo;
	@Autowired
	UserRepository userRepository;

	Client client = null;

//	@Value("${mobile.app.tokenid}")
//	private String tokenId;

	private Status transactionStatus = null;

	@Autowired
	private Environment env;

	@Override
	public ResponseEntity<MessageResponse> balanceToken(String operatorId) {
		client = tokenHelper.getAdminClient();
		Map tokenBalance = null;
		try {
			tokenBalance = new AccountBalanceQuery().setAccountId(tokenHelper.getCustomOperatorId(operatorId))
					.execute(client).token;
			TokenIdInfo tokenIdInfo = tokenIdInfoRepo.findByStatus(TokenIdInfo.Status.ACTIVE);
			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
					.message(env.getProperty("balance.token.success"))
					.response(tokenBalance.get(TokenId.fromString(tokenIdInfo.getTokenId())).toString()).build());

		} catch (Exception e) {
			e.getStackTrace();
			return ResponseEntity
					.ok(new MessageResponse(HttpStatus.PARTIAL_CONTENT.value(), e.getMessage(), tokenBalance));
		}
	}

	@Override
	public ResponseEntity<?> transferToken(TokenBean tokenBean) {
		client = tokenHelper.getTreasuryClient();
		TransactionResponse txResponse = null;
		try {
			txResponse = new TransferTransaction()
					.addTokenTransfer(TokenId.fromString(tokenBean.getTokenId()),
							tokenHelper.getCustomFromOperatorId(tokenBean), -(tokenBean.getAmount()))

					.addTokenTransfer(TokenId.fromString(tokenBean.getTokenId()),
							tokenHelper.getCustomToOperatorId(tokenBean), tokenBean.getAmount())

					.freezeWith(client).sign(tokenHelper.getCustomFromOperatorKey(tokenBean)).execute(client);
			List<Transfer> transactionChargeList = txResponse.getRecord(client).transfers;
			TokenTransferResponse tokenTransferResponse = new TokenTransferResponse();
			tokenTransferResponse.setNetWorkFee(transactionChargeList.get(1).amount.getValue());
			tokenTransferResponse.setNodeFee(transactionChargeList.get(0).amount.getValue());
			tokenTransferResponse.setTransactionFee(transactionChargeList.get(2).amount.getValue());
			if (tokenBean.getPayments().equals(OnlineBookStoreConstant.PAID_OUT)) {
				tokenBean.setOperatorId(tokenBean.getFromSenderId());
				tokenBean.setOperatorKey(tokenBean.getFromSenderKey());
			}

			tokenTransferResponse.setBalanceRemain(Double.parseDouble(initialBalanceToken(tokenBean)));

			// add entry in transaction history record for user
			TransactionHistroy transcationHistroy = new TransactionHistroy();
			if (tokenBean.getPayments().equals("PAID IN")) {
				transcationHistroy.setTranscationType("Credit Deposit");
				transcationHistroy.setPayments("PAID IN");
			} else {
				transcationHistroy.setTranscationType("Credit Withdrawal");
				transcationHistroy.setPayments("PAID OUT");
			}

			transcationHistroy.setDateTime(new DateTime());

			transcationHistroy.setAmountInCurrency(tokenBean.getAmountInCurrency());
			transcationHistroy.setStableCoin(String.valueOf(tokenBean.getAmount()));

			String balanceToken = initialBalanceToken(tokenBean);

			transcationHistroy.setBalanceStableCoin(balanceToken);
			transcationHistroy.setBalanceAmountInCurrency(String.valueOf(Double.parseDouble(balanceToken) / 10));

			UserSecurity security = userSecurityRepository.findByUserId(tokenBean.getUserId()).get();
			transcationHistroy.setUser(security.getUser());

			// transaction fee
			transcationHistroy.setNodeFee(transactionChargeList.get(0).amount.getValue().toString());
			transcationHistroy.setNetWorkFee(transactionChargeList.get(1).amount.getValue().toString());
			transcationHistroy
					.setTransactionfee(transactionChargeList.get(2).amount.getValue().toString().replace("-", ""));
			transcationHistroy.setSmartContractFee("   -");
			transcationHistroy.setTotalFee(transactionChargeList.get(2).amount.getValue().toString().replace("-", ""));
			transcationHistroy.setSecurity(security);

			transcationHistoryRepo.save(transcationHistroy);

			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
					.message(env.getProperty("transfer.token.success"))
					.response(tokenTransferResponse.getBalanceRemain()).build());
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok(
					MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value()).message(e.getMessage()).build());
		}
	}

	/*
	 * initialBalanceToken method is used to get balance
	 */
	private String initialBalanceToken(TokenBean tokenBean) throws BalanceTokenNotFoundException {
		client = tokenHelper.getAdminClient();
		Map tokenBalance = null;
		try {
			tokenBalance = new AccountBalanceQuery()
					.setAccountId(tokenHelper.getCustomOperatorId(tokenBean.getOperatorId())).execute(client).token;
			long balanceLong = (long) tokenBalance.get(TokenId.fromString(tokenBean.getTokenId()));
			return String.valueOf(balanceLong);
		} catch (Exception e) {
			e.printStackTrace();
			throw new BalanceTokenNotFoundException();
		}
	}

	/*
	 * createToken is used to create token.It takes input parameters as
	 * tokenName,tokenSymbol,initialSupply and admin operator id, admin operator
	 * key,operator id and operator key
	 */
	@Override
	public ResponseEntity<?> createToken(CreateTokenDto tokenBean) throws Exception {
		// TODO Auto-generated method stub
		try {

			final AccountId ADMIN_OPERATOR_ID = AccountId
					.fromString(Objects.requireNonNull(env.getProperty("ADMIN_OPERATOR_ID")));
			final AccountId OPERATOR_ID_FROM = AccountId.fromString(Objects.requireNonNull(tokenBean.getOperatorId()));
			final PrivateKey ADMIN_OPERATOR_KEY = PrivateKey
					.fromString(Objects.requireNonNull(env.getProperty("ADMIN_OPERATOR_KEY")));
			final PrivateKey OPERATOR_KEY_FROM = PrivateKey
					.fromString(Objects.requireNonNull(tokenBean.getOperatorkey()));

			Client client = Client.forTestnet();
			client.setOperator(ADMIN_OPERATOR_ID, ADMIN_OPERATOR_KEY);
			TokenCreateTransaction transaction = new TokenCreateTransaction().setTokenName(tokenBean.getTokenName())
					.setTokenSymbol(tokenBean.getTokenSymbol()).setTreasuryAccountId(OPERATOR_ID_FROM)
					.setInitialSupply(5000).setMaxTransactionFee(new Hbar(100)).setDecimals(2)
					.setAdminKey(ADMIN_OPERATOR_KEY.getPublicKey()).setKycKey(OPERATOR_KEY_FROM)
					.setFreezeKey(OPERATOR_KEY_FROM).setWipeKey(OPERATOR_KEY_FROM).setSupplyKey(OPERATOR_KEY_FROM);

			// Build the unsigned transaction, sign with admin private key of the token,
			// sign with the token treasury private key, submit the transaction to a Hedera
			// network
			TransactionResponse txResponse = transaction.freezeWith(client).sign(ADMIN_OPERATOR_KEY)
					.sign(OPERATOR_KEY_FROM).execute(client);

			// Request the receipt of the transaction
			TransactionReceipt receipt = txResponse.getReceipt(client);

			// Get the token ID from the receipt
			TokenId tokenId = receipt.tokenId;

			// System.out.println("The new token ID is " + tokenId);
			String tokenIdString = tokenId.toString();

			// once token created we automatically enable kyc and also associate token
			List<UserSecurity> securityList = userSecurityRepository.findAll();
			securityList.stream().forEach(secDetails -> {
				TokenBean tokenInputBean = new TokenBean();
				tokenInputBean.setOperatorId(secDetails.getAccountShard() + "." + secDetails.getAccountRealm() + "."
						+ secDetails.getAccountNum());
				tokenInputBean.setOperatorKey(secDetails.getUserPrivateKey());
				tokenInputBean.setTokenId(tokenIdString);
				try {
					associateToken(tokenInputBean);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			});

			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
					.message(env.getProperty("token.create.success")).response(tokenIdString).build());
		} catch (Exception e) {

			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
					.message(env.getProperty("token.create.failed")).build());
		}

	}

	/*
	 * associateToken method is used to associate user with available active token
	 * for this application
	 */
	private void associateToken(TokenBean tokenBean) throws Exception {
		client = tokenHelper.getAdminClient();

		try {
			transactionStatus = new TokenAssociateTransaction()
					.setAccountId(tokenHelper.getCustomOperatorId(tokenBean.getOperatorId()))
					.setMaxTransactionFee(new Hbar(100)).setTokenIds(TokenId.fromString(tokenBean.getTokenId()))
					.freezeWith(client).sign(tokenHelper.getCustomOperatorKey(tokenBean.getOperatorKey()))
					.execute(client).getReceipt(client).status;
		} catch (Exception e) {

			throw e;
		}
	}

	/*
	 * associateUser method is used to associate newly created user with active
	 * token.It takes input parameters as token id,operator id,operator key and
	 * sender id, sender key
	 */
	@Override
	public ResponseEntity<MessageResponse> associatKycUser(TokenBean tokenBean) {

		String token = "";
		try {
			associateToken(tokenBean);
			enableKYC(tokenBean);
			tokenBean.setFromSenderId(tokenHelper.getTreasuryOperatorIdString());
			tokenBean.setFromSenderKey(tokenHelper.getTreasuryOperatorKeyString());
			tokenBean.setToAccountId(tokenBean.getOperatorId());
			tokenBean.setToAccountKey(tokenBean.getOperatorKey());
			initialTransferToken(tokenBean);
			token = initialBalanceToken(tokenBean);

		} catch (Exception e) {

			// TODO: handle exception
			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value())
					.message(env.getProperty("token.associate.failed")).response(token).build());
		}
		return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
				.message(env.getProperty("token.associate.success")).response(token).build());

	}

	/*
	 * enableKYC method is used to create token grant KYC transaction
	 */
	private void enableKYC(TokenBean tokenBean) throws Exception {
		client = tokenHelper.getAdminClient();

		try {
			transactionStatus = new TokenGrantKycTransaction()
					.setAccountId(tokenHelper.getCustomOperatorId(tokenBean.getOperatorId()))
					.setTokenId(TokenId.fromString(tokenBean.getTokenId())).freezeWith(client)
					.sign(tokenHelper.getTreasuryOperatorKey()).execute(client).getReceipt(client).status;
		} catch (Exception e) {

			throw e;
		}
	}

	/*
	 * initialTransferToken method is used to transfer transaction
	 */
	private void initialTransferToken(TokenBean tokenBean) {
		client = tokenHelper.getTreasuryClient();
		TransactionResponse txResponse = null;
		try {
			txResponse = new TransferTransaction()
					.addTokenTransfer(TokenId.fromString(tokenBean.getTokenId()),
							tokenHelper.getCustomFromOperatorId(tokenBean), -(tokenBean.getAmount() * 100))
					.addTokenTransfer(TokenId.fromString(tokenBean.getTokenId()),
							tokenHelper.getCustomToOperatorId(tokenBean), tokenBean.getAmount() * 100)
					.freezeWith(client).sign(tokenHelper.getCustomFromOperatorKey(tokenBean)).execute(client);
			List<Transfer> transactionChargeList = txResponse.getRecord(client).transfers;
			TokenTransferResponse tokenTransferResponse = new TokenTransferResponse();
			tokenTransferResponse.setNetWorkFee(transactionChargeList.get(1).amount.getValue());
			tokenTransferResponse.setNodeFee(transactionChargeList.get(0).amount.getValue());
			tokenTransferResponse.setTransactionFee(transactionChargeList.get(2).amount.getValue());
		} catch (Exception e) {

		}
	}

	@Override
	public ResponseEntity<?> mintToken(MintTokenDto mintTokenDto) throws Exception {
		try {
			Client client = Client.forTestnet();
			AccountId accountId = AccountId.fromString(Objects.requireNonNull(mintTokenDto.getOperatorId()));
			PrivateKey operatorId = PrivateKey.fromString(Objects.requireNonNull(mintTokenDto.getOperatorKey()));
			client.setOperator(accountId, operatorId);
			String tokenId = mintTokenDto.getTokenId();
			String num = tokenId.substring(4);

			TokenMintTransaction transaction = new TokenMintTransaction()
					.setTokenId(new TokenId(0, 0, Long.parseLong(num))).setAmount(mintTokenDto.getAmount());

			// Freeze the unsigned transaction, sign with the supply private key of the
			// token, submit the transaction to a Hedera network
			TransactionResponse txResponse = transaction.freezeWith(client).execute(client);

			// Request the receipt of the transaction
			TransactionReceipt receipt = txResponse.getReceipt(client);
			// Obtain the transaction consensus status
			Status transactionStatus = receipt.status;

			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.OK.value())
					.message(env.getProperty("token.create.success")).response(transactionStatus).build());
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.ok(MessageResponse.builder().status(HttpStatus.BAD_REQUEST.value())
					.message(env.getProperty("mint.failed")).build());
		}

	}

}
