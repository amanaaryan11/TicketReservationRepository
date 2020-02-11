package com.capg.service;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.capg.beans.BookingDetails;
import com.capg.beans.TicketDetails;
import com.capg.beans.TrainDetails;
import com.capg.beans.User;
import com.capg.dao.IBookingRepository;
import com.capg.dao.IRegisterRepository;
import com.capg.dao.ITrainRepository;
import com.capg.exception.InvalidPhoneNumberException;
import com.capg.exception.InvalidPnrException;
import com.capg.exception.InvalidTrainNoException;
import com.capg.exception.NoTrainsFoundException;
import com.capg.exception.SeatUnAvailabiltyException;
import com.capg.exception.UserExistsException;
import com.capg.exception.UserNotFoundException;

@Service
public class ITrainServiceImpl implements ITrainService {

	@Autowired
	IRegisterRepository userRepo;

	@Autowired
	ITrainRepository trainRepo;

	@Autowired
	IBookingRepository bookRepo;

	@Autowired
	IAccountService accService;

	@Override
	public TrainDetails addNewTrainDetails(TrainDetails trainobj) {

		return trainRepo.save(trainobj);
	}

	@Override
	public TrainDetails deleteTrainDetails(Integer trainNo, String departureStation, String arrivalStation) {

		return trainRepo.deleteTrain(trainNo, departureStation, arrivalStation);
	}

	@Override
	public User registerUserDetails(User userObj) {

		userObj.setWalletBalance(0);
		return userRepo.save(userObj);

	}

	@Override
	public User getUserDetails(Long phoneNumber) throws UserNotFoundException {

		User userDetails;

		userDetails = userRepo.getUserDetails(phoneNumber);
		System.out.println(userDetails);
		if (userDetails == null) {

			throw new UserNotFoundException("PLEASE ENTER CORRECT USERNAME AND PASSWORD");

		}
		return userDetails;
	}

	@Override
	public List<User> checkUserExists(Long phoneNumber) throws UserExistsException {

		List<User> listFlag = userRepo.checkUserExists(phoneNumber);
		if (listFlag.isEmpty()) {
			throw new UserExistsException("No User Exists with phone Number");
		} else {
			throw new UserExistsException("User Exists with phone Number");
		}
	}

	@Override
	public List<TrainDetails> getTrainsList(String departureStation, String arrivalStation, Date departureDate)
			throws NoTrainsFoundException {
		List<TrainDetails> list;

		list = trainRepo.findTrainDetails(departureStation, arrivalStation, departureDate);

		if (list.isEmpty()) {

			throw new NoTrainsFoundException("NO TRAINS FOUND ON THAT DATE");
		}

		return list;
	}

//Exception handling done
	@Override
	public BookingDetails bookTrain(Integer trainNo, Date departureDate, Date arrivalDate, Long phoneNumber)
			throws InvalidTrainNoException {

		int seats;
		try {
			seats = trainRepo.findSeatAvaiable(trainNo, departureDate, arrivalDate);

		} catch (Exception e) {
			throw new InvalidTrainNoException("PLEASE ENTER CORRECT TRAIN NUMBER");
		}

		if (seats == 0) {
			throw new SeatUnAvailabiltyException("NO SEATS AVAILABLE");
		}

		int trainId = trainRepo.findTrainId(trainNo, departureDate, arrivalDate);

		int fare = trainRepo.findFareById(trainId);

		List<User> listFlag = userRepo.checkUserExists(phoneNumber);
		if (!listFlag.isEmpty()) {

			int checkPayment = accService.completePayment(fare, phoneNumber);

			if (checkPayment == 1) {
				seats--;

				trainRepo.seatUpdate(trainNo, departureDate, arrivalDate, seats);

				BookingDetails book = new BookingDetails();
				book.setPhoneNumber(phoneNumber);
				book.setTrainId(trainId);

				return bookRepo.save(book);
			} else
				return null;
		} else {
			throw new UserExistsException("Phone Number doesnt exists");
		}
	}

	// DONE EXCEPTION HANDLING

	@Override
	public TicketDetails getBookingDetails(Long pnr) throws InvalidPnrException,InvalidPhoneNumberException {

		TicketDetails ticketdetails=new TicketDetails();
		Integer trainId;
		Long phoneNumber;

		trainId = bookRepo.findTrainIdByPnr(pnr);
		System.out.println(trainId);

		if (trainId == null)
			throw new InvalidPnrException("PLEASE CHECK YOUR PNR NO");

		phoneNumber = bookRepo.findPhoneNumberByPnr(pnr);

		
		List<User> user=userRepo.checkUserExists(phoneNumber);
		if(!user.isEmpty()) {
		System.out.println(user);
		TrainDetails details= trainRepo.findByTrainId(trainId);
		ticketdetails.setFirstName(user.get(0).getFirstname());
		ticketdetails.setLastName(user.get(0).getLastName());
		ticketdetails.setArrivalDate(details.getArrivalDate());
		ticketdetails.setArrivalStation(details.getArrivalStation());
		ticketdetails.setArrivalTime(details.getArrivalTime());
		ticketdetails.setDeparturedate(details.getDeparturedate());
		ticketdetails.setDepartureStation(details.getDepartureStation());
		ticketdetails.setDepartureTime(details.getDepartureTime());
		ticketdetails.setFare(details.getFare());
		ticketdetails.setTrainNo(details.getTrainNo());
		ticketdetails.setTrainname(details.getTrainname());
		
	
		
		
		return ticketdetails;
		}
		else
			throw new InvalidPhoneNumberException("Invalid Phone Number");
	}

	@Override
	public Integer checkWalletBalanceExists(Long phoneNumber) throws InvalidPhoneNumberException {

		Integer balance = userRepo.findWalletbalance(phoneNumber);
		System.out.println(balance);

		if (balance == null) {
			throw new InvalidPhoneNumberException("Phone number doesn't exists");
		}
		return balance;
	}

}
