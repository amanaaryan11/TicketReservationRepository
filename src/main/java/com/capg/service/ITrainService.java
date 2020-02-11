package com.capg.service;

import java.sql.Date;
import java.util.List;

import com.capg.beans.BookingDetails;
import com.capg.beans.TicketDetails;
import com.capg.beans.TrainDetails;
import com.capg.beans.User;
import com.capg.exception.InvalidPnrException;
import com.capg.exception.InvalidTrainNoException;
import com.capg.exception.NoTrainsFoundException;
import com.capg.exception.UserNotFoundException;


public interface ITrainService {

	TrainDetails addNewTrainDetails(TrainDetails trainobj);

	TrainDetails deleteTrainDetails(Integer trainNo, String departureStation, String arrivalStation);

	User registerUserDetails(User userObj);

	User getUserDetails(Long phoneNumber) throws UserNotFoundException;

	List<User> checkUserExists(Long phoneNumber);

	List<TrainDetails> getTrainsList(String departureStation, String arrivalStation,Date departureDate)throws NoTrainsFoundException;

	

	BookingDetails bookTrain(Integer trainNo, Date departureDate, Date arrivalDate,Long phoneNumber) throws InvalidTrainNoException;

	TicketDetails getBookingDetails(Long pnr)throws InvalidPnrException;

	Integer checkWalletBalanceExists(Long phoneNumber);

	/*
	 * public Register addRegisterDetails(Register r); 
	 *public AccountDetails
	 * addAccountDetails(AccountDetails a); public Optional<Register>
	 * getPassword(String email); public Integer deposit(String email,Long
	 * accno,Double balance); public Integer withdraw(String email,Long accno,Double
	 * amount); public Integer getValidAccount(String email,Long accno); public
	 * Double showBalance( String email,Long accno); public Integer deposit(Long
	 * accno,Double balance); public Transaction addTransactionDetails(Transaction
	 * t); public List<Transaction> getTransactionDetails(Long accno); public
	 * Integer checkAccExists(String email); public Integer checkAccountExists(Long
	 * accountNo);
	 */
	





}
