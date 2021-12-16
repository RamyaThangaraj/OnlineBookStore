package com.sampleproject.obs.buyer.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.sampleproject.obs.buyer.payload.request.BuyBookRequest;
import com.sampleproject.obs.data.model.BookInfo;

@Service
public interface BuyerService {

	ResponseEntity<List<BookInfo>> viewBookInfo();

	// ResponseEntity<BookInfo> viewByAuthor(String author);

	ResponseEntity<?> buyBook(BuyBookRequest buyBookRequest);

	ResponseEntity<?> getWallet(String buyer_id);

	ResponseEntity<?> buyToken(String buyer_id, int amount, HttpServletRequest request);

	ResponseEntity<?> withDrawToken(String buyer_id, int amount, HttpServletRequest request);

	ResponseEntity<?> getTokeBalance(String buyer_id, HttpServletRequest request);

	ResponseEntity<?> tokenTransactionHistory(String buyer_id, HttpServletRequest request);

	ResponseEntity<?> getTrasactionfee();

	

}
