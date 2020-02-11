package com.capg.service;

public interface IAccountService {

	Integer addMoneyToWallet(Integer amount, Long phoneNumber, Long accountNumber);

	Integer completePayment(Integer amount, Long phoneNumber);

}
