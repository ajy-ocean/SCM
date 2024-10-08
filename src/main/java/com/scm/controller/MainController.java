package com.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.scm.dao.UserRepository;
import com.scm.entities.User;
import com.scm.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class MainController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserRepository userRepository;

	// Handler for home page
	@RequestMapping("/")
	public String homeHandler(Model model) {
		model.addAttribute("title", "Home - SCM");
		return "home";
	}

	// Handler for about page
	@RequestMapping("/about")
	public String aboutHandler(Model model) {
		model.addAttribute("title", "About - SCM");
		return "about";
	}

	// Handler for signup page
	@RequestMapping("/signup")
	public String signupHandler(Model model) {
		model.addAttribute("title", "Signup - SCM");
		model.addAttribute("user", new User());
		return "signup";
	}

	// Handler for signup user
	@RequestMapping(value = "/do_signup", method = RequestMethod.POST)
	public String signupUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,

			HttpSession session) {
		try {
			if (!agreement) {
				System.out.println("Please accept our terms & conditions");
				throw new Exception("Please accept our terms & conditions");
			}

			if (bindingResult.hasErrors()) {
				System.out.println("Error " + bindingResult.toString());
				model.addAttribute("user", user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			System.out.println("Agreement:- " + agreement);
			System.out.println("User:- " + user);
			User result = this.userRepository.save(user);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong " + e.getMessage(), "alert-danger"));
			return "signup";
		}
	}

	// Handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Signin page");
		return "signin";
	}

}
