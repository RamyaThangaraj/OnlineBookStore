package com.sampleproject.obs.admin.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sampleproject.obs.admin.model.BookInfoRequest;
import com.sampleproject.obs.admin.model.TransactionFeeDto;
import com.sampleproject.obs.admin.service.AdminService;
import com.sampleproject.obs.data.exception.GetBalanceMirrorNodeException;
import com.sampleproject.obs.data.exception.HTSException;
import com.sampleproject.obs.data.exception.PublishingMirrorNodeException;
import com.sampleproject.obs.data.model.BookInfo;
import com.sampleproject.obs.data.request.CreateTokenRequest;

@CrossOrigin
@RestController
@RequestMapping(value = "/obs")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@Autowired
	AdminService adminService;

	@PostMapping("/saveBook")
	public ResponseEntity<?> addBookInfo(@RequestBody BookInfoRequest bookInfo) {
		return adminService.addBook(bookInfo);
	}
	
	@PutMapping("/updateBook")
	public ResponseEntity<?> updatBookInfo(@RequestBody BookInfoRequest bookinforeq){
		return adminService.updateBook(bookinforeq);
	}
	
	@DeleteMapping("/deleteBook/{id}")
	public ResponseEntity<?> deleteBookInfo(@PathVariable(value = "id") String id){
		return adminService.deleteBook(id);
	}
	
	@GetMapping("/showBook")
	public ResponseEntity<List<BookInfo>>showBookInfo(){
		return adminService.showBookInfo();
	}
	
	//search by author
	//search by bookname
	
//	/* This API helps to create one token for entire application and make it created token as active */
	@PostMapping(value = "/token/create", consumes = "application/json",produces = "application/json")
	public ResponseEntity<?> createToken(@RequestBody CreateTokenRequest createTokenRequest,
			HttpServletRequest request) throws HTSException, PublishingMirrorNodeException {
		return adminService.createToken(createTokenRequest, request);
	} 
	
	/* This API helps to mint token */
	@PostMapping(value = "/token/mint")
	public ResponseEntity<?> mintToken(@RequestParam(value="userId")String userId, @RequestParam(value="amount")int amount, HttpServletRequest request) throws PublishingMirrorNodeException, HTSException {
		return adminService.mintToken(userId,amount, request);
	}
	
	/* This API helps to get token balance */
	@GetMapping("/get/token/balance/{adminId}")
	public ResponseEntity<?> tokenBalance(@PathVariable("adminId") String adminId, HttpServletRequest request)
			throws GetBalanceMirrorNodeException {
		return adminService.getTokeBalance(adminId, request);
	}

	/* This API helps to get token transaction history */
	@GetMapping("/get/token/transaction/history/{adminId}")
	public ResponseEntity<?> tokenTransactionHistory(@PathVariable("adminId") String adminId,
			HttpServletRequest request) throws JSONException {
		return adminService.tokenTransactionHistory(adminId, request);
	}
	
	/* This API helps to get transaction fee */
	@GetMapping("/get/transactionfee")
	public ResponseEntity<?> getTrasactionfee() {
		return adminService.getTrasactionfee();
	}

	/* This API helps to update transaction fee */
	@PostMapping(value = "/update/transactionfee")
	public ResponseEntity<?> updateTransactionFee(@RequestBody TransactionFeeDto transactionFeeDto,
			HttpServletRequest request) {
		return adminService.updateTransactionFee(transactionFeeDto, request);
	}

}
