package com.scm.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ForgotPasswordController {

	Random random = new Random(1000);

	// Handler for opening form for email in case of password forgot
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forget_email_form";
	}

	// Handler for OTP
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email) {
		System.out.println("Email:- " + email);

		int otp = random.nextInt(999999);
		System.out.println("OTP:- " + otp);
		return "verify_otp";
	}
}
