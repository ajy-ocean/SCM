package com.scm.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.scm.dao.ContactRepository;
import com.scm.dao.UserRepository;
import com.scm.entities.Contact;
import com.scm.entities.User;
import com.scm.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ContactRepository contactRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	// method for add common information
	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {
		// This is principal is from spring security which helps to get user details
		String userName = principal.getName();
		System.out.println("Username:- " + userName);

		// getting username i.e email
		User user = userRepository.getUserByUserName(userName);
		System.out.println("user:- " + user);
		model.addAttribute("user", user);
	}

	// Handler for user dashboard
	@RequestMapping("/dashboard")
	public String userDashboard(Model model, Principal principal) {

		// If our page is inside some kind of folder then return like below
		model.addAttribute("title", "dashboard");
		return "normal_user/user_dashboard";
	}

	// Handler to open add contact form
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact Form");
		model.addAttribute("contact", new Contact());
		return "normal_user/add_contact_form";
	}

	// Handler for processing contact form
	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, BindingResult bindingResult, Principal principal,
			HttpSession session) {
		try {
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);

			// Processing and uploading file
			if (file.isEmpty()) {
				// If file is empty
				System.out.println("File is empty");
				contact.setImage("default_contact.png");
			} else {
				// If file is not empty upload it
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
			}

			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			System.out.println("Contact:- " + contact);
			System.out.println("Added to db");

			// Success Message
			session.setAttribute("message", new Message("Contact Addded", "success"));
		} catch (Exception e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();

			// Error Message
			session.setAttribute("message", new Message("Something Went Wrong, Try Again", "danger"));
		}

		return "normal_user/add_contact_form";
	}

	// Handler for showing contacts
	// Per page = 5[n]
	// Current Page = 0 [page]
	@GetMapping("/view-contacts/{page}")
	public String viewContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "View Contacts");

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		// pageable have two information currentPage and contactPerPage
		Pageable pageable = PageRequest.of(page, 3);
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "normal_user/view_contacts";
	}

	// Handler for showing specific contact details
	@RequestMapping("/{contact_id}/contact")
	public String showContactDetail(@PathVariable("contact_id") Integer contact_id, Model model, Principal principal) {
		System.out.println("contact_id:- " + contact_id);
		model.addAttribute("title", "Contact Details");
		Optional<Contact> contactFromOptional = this.contactRepository.findById(contact_id);
		Contact contact = contactFromOptional.get();

		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);

		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
		}

		return "normal_user/contact_details";
	}

	// Handler for deleting specific contacts
	@GetMapping("/delete/{contact_id}")
	public String deleteContact(@PathVariable("contact_id") Integer contact_id, Model model, HttpSession session,
			Principal principal) {

		Contact contact = this.contactRepository.findById(contact_id).get();
		// contact.setUser(null);
		// this.contactRepository.delete(contact);

		User user = this.userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		this.userRepository.save(user);

		session.setAttribute("message", new Message("Contact Deleted", "success"));
		return "redirect:/user/view-contacts/0";
	}

	// Handler for updating form
	@PostMapping("/update-contact/{contact_id}")
	public String updateForm(@PathVariable("contact_id") Integer contact_id, Model model) {

		model.addAttribute("title", "Update Contact");
		Contact contact = this.contactRepository.findById(contact_id).get();
		model.addAttribute("contact", contact);
		return "normal_user/form_update";
	}

	// Handler for processing update logic
	@RequestMapping(value = "/process-update", method = RequestMethod.POST)
	public String processUpdate(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile file,
			Model model, HttpSession session, Principal principal) {

		try {
			// image before updating
			Contact contactDetailsBeforeUpdate = this.contactRepository.findById(contact.getContact_id()).get();

			// image..
			if (!file.isEmpty()) {
				// file work.. rewrite

				// Deleting Image
				File updatingFile = new ClassPathResource("static/img").getFile();
				File deletedFile = new File(updatingFile, contactDetailsBeforeUpdate.getImage());
				deletedFile.delete();

				// Updating image
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(contactDetailsBeforeUpdate.getImage());
			}

			User user = this.userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Contact Updated", "success"));

		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Contact:- " + contact.getName());
		System.out.println("Contact id:- " + contact.getContact_id());
		return "redirect:/user/" + contact.getContact_id() + "/contact";
	}

	// Handler for profile
	@GetMapping("/view-profile")
	public String viewProfile(Model model) {
		model.addAttribute("title", "Profile");
		return "normal_user/profile";
	}

	// Handler for settings option
	@GetMapping("/settings")
	public String openSettings(Model model) {
		model.addAttribute("title", "Settings");
		return "normal_user/settings";
	}

	// Handler for change password process
	@PostMapping("change-password")
	public String changePassword(
									@RequestParam("oldPassword") String oldPassword,
									@RequestParam("newPassword") String newPassword, 
									Principal principal,
									HttpSession session
								) {
		String user = principal.getName();
		User currentUser = this.userRepository.getUserByUserName(user);
		System.out.println(currentUser.getPassword());
		if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currentUser);
			session.setAttribute("message", new Message("Your password is updated...", "success"));
		}else {
			session.setAttribute("message", new Message("Password Is Incorrect", "danger"));			
			return "redirect:/user/settings";
		}
		return "redirect:/user/dashboard";
	}
}





