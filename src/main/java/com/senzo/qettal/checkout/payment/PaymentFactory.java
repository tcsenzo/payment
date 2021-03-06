package com.senzo.qettal.checkout.payment;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.senzo.qettal.checkout.moip.MoipApiWrapper;
import com.senzo.qettal.checkout.purchase.Purchase;

@Component
public class PaymentFactory {

	@Autowired
	private MoipApiWrapper moip;
	@Autowired
	private Payments payments;

	@Transactional
	public Payment create(PaymentDTO paymentDTO, Purchase purchase) {
		Payment payment = purchase.pay(payments);
		moip.pay(paymentDTO, purchase.getReferenceId());		
		return payment;
	}
	
	
}
