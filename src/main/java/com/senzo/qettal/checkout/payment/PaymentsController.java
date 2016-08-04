package com.senzo.qettal.checkout.payment;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.senzo.qettal.checkout.moip.MoipApiWrapper;
import com.senzo.qettal.checkout.purchase.Purchase;
import com.senzo.qettal.checkout.purchase.Purchases;
import com.senzo.qettal.checkout.security.LoggedUser;

@RestController
@RequestMapping("/payments")
public class PaymentsController {

	@Autowired
	private LoggedUser loggedUser;
	@Autowired
	private Purchases purchases;
	@Autowired
	private MoipApiWrapper api;
	@Autowired
	private PaymentFactory factory;
	@Autowired
	private Payments payments;
	
	@RequestMapping(method = POST)
	public ResponseEntity<String> create(@Valid @RequestBody PaymentDTO paymentDTO) {
		Optional<Purchase> optionalPurchase = purchases.find(paymentDTO.getPurchaseId());
		if(!optionalPurchase.isPresent())
			return new ResponseEntity<>(NOT_FOUND);

		Purchase purchase = optionalPurchase.get();
		if(!purchase.isOwnedBy(loggedUser.getUser().get()))
			return new ResponseEntity<>(FORBIDDEN);
		
		Payment payment = factory.create(paymentDTO, purchase);
		payments.save(payment);
		
		return new ResponseEntity<>(OK);
	}

}
