package com.sampleproject.obs.data.util;

import javax.servlet.http.HttpServletRequest;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sampleproject.obs.data.constant.OnlineBookStoreConstant;
import com.sampleproject.obs.data.exception.FundTransferMirrorNodeException;
import com.sampleproject.obs.data.exception.GetBalanceMirrorNodeException;
import com.sampleproject.obs.data.exception.PublishingMirrorNodeException;
import com.sampleproject.obs.data.model.TokenIdInfo;
import com.sampleproject.obs.data.model.User;
import com.sampleproject.obs.data.model.UserInfo;
import com.sampleproject.obs.data.model.UserSecurity;
import com.sampleproject.obs.data.payload.response.MessageResponse;
import com.sampleproject.obs.data.request.TokenInfoRequest;
import com.sampleproject.obs.data.services.RestTemplateService;

@Service
public class PublishInfo {
	
	@Value("${hcs.url}")
	public String hcsBaseUrl;
	@Value("${hts.url}")
	private String htsBaseUrl;
	@Autowired
	RestTemplateService restTemplateService;
	@Autowired
	 RestTemplate restTemplate;
//	@Autowired
//	MongoDBLoggerService mongoDBLoggerService;
	@Autowired
	Environment env;

	/*
	 * 	// publishMessageToMirrorNode 
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
			return response;
		} catch (Exception e) {
			throw new PublishingMirrorNodeException();
		}
	}
	 * 
	 */
	public MessageResponse publishMessageToMirrorNode(String str, Object object)
			throws PublishingMirrorNodeException {
		String url = str + String.format(OnlineBookStoreConstant.PUBLISH_MIRROR_NODE_USER);
		MessageResponse response = null;
		try {
			switch (str) {
			case "user":
				User user = (User) object;
				user.setMethodName("users");

				RequestEntity<User> entityUser = new RequestEntity<>(user, HttpMethod.POST,
						restTemplateService.uri(url));
				response = restTemplate.exchange(entityUser, MessageResponse.class).getBody();
						//restTemplate.exchange(entityUser, MessageResponse.class).getBody();
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
			case "createToken":
				TokenIdInfo tokenInfo = (TokenIdInfo) object;
				// info.setMethodName("orgainizationUpdate");
				RequestEntity<TokenIdInfo> entityTokenInfo = new RequestEntity<>(tokenInfo, HttpMethod.POST,
						restTemplateService.uri(url));
				response = restTemplate.exchange(entityTokenInfo, MessageResponse.class).getBody();
				break;
			}
			return response;
		} catch (Exception e) {
					throw new PublishingMirrorNodeException();
		}
	}
	
	public MessageResponse transferTokenToMirrorNode(HttpServletRequest request, TokenInfoRequest token)
			throws FundTransferMirrorNodeException {
		MessageResponse response = null;
		try {
			String url = htsBaseUrl + String.format(OnlineBookStoreConstant.TRANSFER);
			RequestEntity<TokenInfoRequest> secEntity = new RequestEntity<>(token, restTemplateService.headers(request),
					HttpMethod.POST, restTemplateService.uri(url));
			response = restTemplate.exchange(secEntity, MessageResponse.class).getBody();
		} catch (Exception e) {
			// TODO: handle exception
			throw new FundTransferMirrorNodeException();
		}
		return response;
	}
	
	/*
	 * getBalance method is used to get balance from Hedera Token Service
	 */
	public MessageResponse getBalance(HttpServletRequest request, String operatorId)
			throws GetBalanceMirrorNodeException {
		MessageResponse response = null;
		try {
			String url = htsBaseUrl + String.format(OnlineBookStoreConstant.GET_BALANCE_API);
			RequestEntity<?> secEntity = new RequestEntity<>(operatorId, restTemplateService.headers(request),
					HttpMethod.POST, restTemplateService.uri(url));
			response = restTemplate.exchange(secEntity, MessageResponse.class).getBody();
		} catch (Exception e) {
			// TODO: handle exception
			e.fillInStackTrace();
			throw new GetBalanceMirrorNodeException();
		}
		return response;
	}
	
	public static DateTime convertDateTimeToZoneTime(DateTime dt, String zone) {
		DateTimeZone timeZone = DateTimeZone.forID(zone);
		return timeZone == null ? dt : dt.toDateTime(timeZone);
	}

}
