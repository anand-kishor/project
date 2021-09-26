package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

@RestController
public class SearchController {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ContactRepository contactRepository;

	@GetMapping("/search/{query}")
	public ResponseEntity<?> searchContact(@PathVariable("query") String query,Principal principal)
	{
		System.out.println(query);
		User user=this.userRepo.getUserByUserName(principal.getName());
		List<Contact> contacts=this.contactRepository.findByNameContainingAndUsers(query, user);
		return ResponseEntity.ok(contacts);
	}

}
