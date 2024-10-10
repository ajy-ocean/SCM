package com.scm.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotPasswordController {

	Random random = new Random(1000);
	
	@Autowired
	private EmailService emailService;

	// Handler for opening form for email in case of password forgot
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forget_email_form";
	}

	// Handler for OTP
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session) {
		System.out.println("Email:- " + email);

		int otp = random.nextInt(999999);
		System.out.println("OTP:- " + otp);
		
		// Logic for email verification
		String subject = "OTP from SCM";
		String message = "OTP = " + otp;
		String to = email;
		boolean flag = this.emailService.sendEmailUsingRediffPro(subject, message, to);
		if(flag) {
			session.setAttribute("otp", otp);
			return "verify_otp";
		}else {		
			session.setAttribute("message", "Check Your Mail");
			return "forget_email_form";
		}
	}
}
