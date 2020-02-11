package com.capg.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.capg.beans.BookingDetails;
import com.capg.beans.TicketDetails;
import com.capg.beans.TrainDetails;
import com.capg.beans.User;
import com.capg.exception.InvalidPhoneNumberException;
import com.capg.exception.InvalidPnrException;
import com.capg.exception.InvalidTrainNoException;
import com.capg.exception.NoTrainsFoundException;
import com.capg.exception.UserNotFoundException;
import com.capg.service.IAccountService;
import com.capg.service.ITrainService;
import com.capg.springSecurity.JwtAuthenticationResponse;
import com.capg.springSecurity.JwtTokenProvider;

@CrossOrigin()
@RestController
public class Controller {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	IAccountService account;

	@Autowired
	ITrainService trainService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	JwtTokenProvider tokenProvider;

	@PostMapping(path = "/irctc/admin/addnewTrain")
	public TrainDetails addNewTrainDetails(@RequestBody TrainDetails trainobj) {

		return trainService.addNewTrainDetails(trainobj);

	}

	@PostMapping(path = "/irctc/admin/deleteTrain/{trainNo}/{departureStation}/{arrivalStation}")
	public TrainDetails deleteTrainDetails(@PathVariable(value = "trainNo") Integer trainNo,
			@PathVariable(value = "departureStation") String departureStation,
			@PathVariable(value = "departureStation") String arrivalStation) {
		return trainService.deleteTrainDetails(trainNo, departureStation, arrivalStation);

	}

	@PostMapping(path = "/irctc/user/registerUser")
	public User registerUserDetails(@RequestBody User userObj) {
		List<User> flag = checkAccExists(userObj.getPhoneNumber());
		
		if (flag.isEmpty()) {

			User user = new User();

			userObj.setPassword(passwordEncoder.encode(userObj.getPassword()));

			userObj.setAuthority("USER");

			User result = trainService.registerUserDetails(userObj);

			return result;
		} else {
			throw new UserNotFoundException("User with phone Number Exists");
		}

	}

	@PostMapping(path = "/irctc/user/validateUser")
	public ResponseEntity<?> logIn(@RequestBody User userObj) throws UserNotFoundException {

		long phoneNumber = userObj.getPhoneNumber();

		String password = userObj.getPassword();

		User user = trainService.getUserDetails(phoneNumber);
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(user.getPhoneNumber(), password));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = tokenProvider.generateToken(authentication);
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));

	}

	@GetMapping(path = "/irctc/user/check/{phoneNumber}")
	public List<User> checkAccExists(@PathVariable(value = "phoneNumber") Long phoneNumber) {
		return trainService.checkUserExists(phoneNumber);
	}

	@GetMapping(path = "/irctc/user/trainList/{departureStation}/{arrivalStation}/{departureDate}")
	public List<TrainDetails> getTrainsList(@PathVariable(value = "departureStation") String departureStation,
			@PathVariable(value = "arrivalStation") String arrivalStation,
			@PathVariable(value = "departureDate") Date departureDate) throws NoTrainsFoundException {
		System.out.println(departureDate);
		System.out.println(departureStation);
		System.out.println(arrivalStation);
		return trainService.getTrainsList(departureStation, arrivalStation, departureDate);
	}

	@PostMapping(path = "/irctc/user/bookTrain/{trainNo}/{departureDate}/{arrivalDate}/{phoneNumber}")
	public BookingDetails bookTrain(@PathVariable(value = "trainNo") Integer trainNo,
			@PathVariable(value = "departureDate") Date departureDate,
			@PathVariable(value = "arrivalDate") Date arrivalDate,
			@PathVariable(value = "phoneNumber") Long phoneNumber) throws InvalidTrainNoException {

		return trainService.bookTrain(trainNo, departureDate, arrivalDate, phoneNumber);

	}

	@PostMapping(path = "/irctc/user/addMoney/{amount}/{phoneNumber}/{accountNumber}")
	public Integer addMoneyToWallet(@PathVariable(value = "amount") Integer amount,
			@PathVariable(value = "phoneNumber") Long phoneNumber,
			@PathVariable(value = "accountNumber") Long accountNumber) throws InvalidPhoneNumberException {
		return account.addMoneyToWallet(amount, phoneNumber, accountNumber);

	}

	@GetMapping(path = "/irctc/user/checkWalletBalance/{phoneNumber}")
	public Integer checkWalletBalanceExists(@PathVariable(value = "phoneNumber") Long phoneNumber) {
		return trainService.checkWalletBalanceExists(phoneNumber);
	}
//
//	@PostMapping(path = "/irctc/user/completePayment/{amount}/{phoneNumber}")
//	public Integer completePayment(@PathVariable(value = "amount") Integer amount,
//			@PathVariable(value = "phoneNumber") Long phoneNumber) {
//		return account.completePayment(amount, phoneNumber);
//
//	}

	@GetMapping(path = "/irctc/user/trainList/{pnr}")
	public TicketDetails getBookingDetails(@PathVariable(value = "pnr") Long pnr) throws InvalidPnrException {
		return trainService.getBookingDetails(pnr);
	}

}
