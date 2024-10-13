package com.scm.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.dao.UserRepository;
import com.scm.entities.User;
import com.scm.services.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotPasswordController {

	Random random = new Random(1000);
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

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
		//String message = "OTP = " + otp;
		String message = "<div style='max-width: 600px; margin: 20px auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);'>"
			    + "<div style='background-color: #007bff; color: #ffffff; text-align: center; padding: 15px 0; border-radius: 8px 8px 0 0;'>"
			    + "<h1 style='margin: 0; font-size: 24px;'>Your OTP Code</h1>"
			    + "</div>"
			    + "<div style='padding: 20px; text-align: center;'>"
			    + "<p style='font-size: 18px; color: #333333; margin-bottom: 20px;'>Hello,</p>"
			    + "<p style='font-size: 18px; color: #333333;'>Your OTP (One-Time Password) for verification is:</p>"
			    + "<div style='font-size: 32px; font-weight: bold; color: #007bff; letter-spacing: 4px; padding: 10px 20px; background-color: #f4f8ff; display: inline-block; border-radius: 4px; margin-bottom: 20px;'>"
			    + otp + "</div>"
			    + "<p style='font-size: 18px; color: #333333;'>This OTP is valid for 10 minutes. Please do not share this code with anyone.</p>"
			    + "</div>"
			    + "<div style='font-size: 14px; color: #777777; text-align: center; padding: 10px; border-top: 1px solid #dddddd; margin-top: 20px;'>"
			    + "<p>If you didn’t request this, please ignore this email or <a href='#' style='color: #007bff; text-decoration: none;'>contact support</a>.</p>"
			    + "<p>Thank you!</p>"
			    + "</div>"
			    + "</div>";

		String to = email;
		boolean flag = this.emailService.sendEmailUsingRediffPro(subject, message, to);
		if(flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}else {		
			session.setAttribute("message", "Check Your Mail");
			return "forget_email_form";
		}
	}
	
	// Verify OTP
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session) {
		int myOtp = (int)session.getAttribute("myotp");
		String email = (String)session.getAttribute("email");
		if(myOtp == otp) {
			User user = this.userRepository.getUserByUserName(email);
			if(user == null) {
				session.setAttribute("message", "User Does Not Exits With This Email !!! ");
				return "forget_email_form";
			}else {				
				return "password_change_form";
			} 
			
		}else {
			session.setAttribute("message", "Wrong OTP");
			return "verify_otp";
		}
	}
	
	// Handler For Change Password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword, HttpSession session) {
		String email = (String)session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userRepository.save(user);
		return "redirect:/signin?change-password changed successfully";
	}
}




