package com.sampleproject.obs.buyer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sampleproject.obs.buyer.payload.request.BuyBookRequest;
import com.sampleproject.obs.buyer.service.BuyerService;
import com.sampleproject.obs.data.exception.FundTransferMirrorNodeException;
import com.sampleproject.obs.data.exception.GetBalanceMirrorNodeException;
import com.sampleproject.obs.data.exception.PublishingMirrorNodeException;
import com.sampleproject.obs.data.model.BookInfo;

@CrossOrigin
@RestController
@RequestMapping(value = "/obs")
public class BuyerController {

	// search book from bookinfo✔
	// buy book✔
	// search book by name
	// search book by author✔

	@Autowired
	BuyerService buyerService;

	@GetMapping("/viewBook")
	public ResponseEntity<List<BookInfo>> viewBookInfo() {
		return buyerService.viewBookInfo();
	}

//	@GetMapping("/viewBookByAuthor")
//	public ResponseEntity<BookInfo> viewByAuthor(@PathVariable(value = "author") String author) {
//		return buyerService.viewByAuthor(author);
//
	/*
	 * @PostMapping("/saveBook") public ResponseEntity<?> addBookInfo(@RequestBody
	 * BookInfoRequest bookInfo) { return adminService.addBook(bookInfo); }
	 */
//	}

	@PostMapping("/buyBook")
	public ResponseEntity<?> buyBook(@RequestBody BuyBookRequest buyBookRequest) {
		return buyerService.buyBook(buyBookRequest);
	}
	
	/* This API helps to get wallet details for user */
	@GetMapping("/get/wallet/details/{buyer_id}")
	public ResponseEntity<?> getWallet(@PathVariable(value = "buyer_id") String buyer_id) {
		return buyerService.getWallet(buyer_id);
	}
	
	/* This API helps to buy token */
	@PostMapping("/token/buy/{buyer_id}/{amount}")
	public ResponseEntity<?> buyToken(@PathVariable("buyer_id") String buyer_id, @PathVariable("amount") int amount,
			HttpServletRequest request) throws PublishingMirrorNodeException, FundTransferMirrorNodeException {
		return buyerService.buyToken(buyer_id, amount, request);
	}
	/* This API helps to withdraw token */
	@PostMapping("/token/withdraw/{buyer_id}/{amount}")
	public ResponseEntity<?> withdrawToken(@PathVariable("buyer_id") String buyer_id,
			@PathVariable("amount") int amount, HttpServletRequest request)
			throws PublishingMirrorNodeException, FundTransferMirrorNodeException, GetBalanceMirrorNodeException {
		return buyerService.withDrawToken(buyer_id, amount, request);
	}
	
	/* This API helps to get token balance */
	@GetMapping("/get/token/balance/{buyer_id}")
	public ResponseEntity<?> tokenBalance(@PathVariable("buyer_id") String buyer_id, HttpServletRequest request)
			throws GetBalanceMirrorNodeException {
		return buyerService.getTokeBalance(buyer_id, request);
	}

	/* This API helps to get transaction history */
	@GetMapping("/get/token/transaction/history/{buyer_id}")
	public ResponseEntity<?> tokenTransactionHistory(@PathVariable("buyer_id") String buyer_id,
			HttpServletRequest request) {
		return buyerService.tokenTransactionHistory(buyer_id, request);
	}
	
	/* This API helps to get transaction fee */
	@GetMapping("/get/transactionfee")
	public ResponseEntity<?> getTrasactionfee() {
		return buyerService.getTrasactionfee();
	}


}
