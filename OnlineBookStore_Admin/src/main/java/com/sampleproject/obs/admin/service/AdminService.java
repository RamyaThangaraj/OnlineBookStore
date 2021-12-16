package com.sampleproject.obs.admin.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sampleproject.obs.admin.model.BookInfoRequest;
import com.sampleproject.obs.admin.model.TransactionFeeDto;
import com.sampleproject.obs.data.exception.GetBalanceMirrorNodeException;
import com.sampleproject.obs.data.exception.HTSException;
import com.sampleproject.obs.data.exception.PublishingMirrorNodeException;
import com.sampleproject.obs.data.model.BookInfo;
import com.sampleproject.obs.data.request.CreateTokenRequest;


@Service
public interface AdminService {

	ResponseEntity<?> addBook(BookInfoRequest bookInfo);

	ResponseEntity<?> updateBook(BookInfoRequest bookinforeq);

	ResponseEntity<?> deleteBook(String id);

	ResponseEntity<List<BookInfo>> showBookInfo();

	ResponseEntity<?> createToken(CreateTokenRequest createTokenRequest, HttpServletRequest request) throws HTSException, PublishingMirrorNodeException;
	
	ResponseEntity<?> getTokeBalance(String adminId, HttpServletRequest request) throws GetBalanceMirrorNodeException;
	
	ResponseEntity<?> tokenTransactionHistory(String adminId, HttpServletRequest request) throws JSONException;
	
	ResponseEntity<?> getTrasactionfee();

	ResponseEntity<?> updateTransactionFee(TransactionFeeDto transactionFeeDto, HttpServletRequest request);

	ResponseEntity<?> mintToken(String userId, int amount, HttpServletRequest request) throws PublishingMirrorNodeException, HTSException;
//	ResponseEntity<?> mintToken(String userId,int amount,HttpServletRequest request) throws PublishingMirrorNodeException, HTSException;
	
}
