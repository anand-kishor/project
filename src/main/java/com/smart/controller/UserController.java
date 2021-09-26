package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Map;
//import java.util.Optional;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Optionals;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.smart.entity.Contact;
import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private ContactRepository contactRepository;


	@ModelAttribute
	public void addCommonData(Model model, Principal pri) {

		String userName = pri.getName();
		System.out.println("USERNAME" + userName);
		User user = this.userRepo.getUserByUserName(userName);
		System.out.println("user find by principal");
		System.out.println("user" + user);
		System.out.println("user not found by principal");
		model.addAttribute("user", user);
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	// @RequestMapping("/index")

	public String dashboard(Model model, Principal pri) {

		String name = pri.getName();
		System.out.println("find out username " + name);

		User user = this.userRepo.getUserByUserName(name);
		System.out.println("finally get username :" + user);
		model.addAttribute("user" + user);

		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (principal instanceof UserDetails) {
			String username = ((UserDetails) principal).getUsername();
			User userFind = this.userRepo.getUserByUserName(username);
			model.addAttribute("user", userFind);
			System.out.println("username " + userFind.getName());
			System.out.println("user " + userFind);

		} else {

			System.out.println("user name not found");
		}

		// model.addAttribute("user", user2);

		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String addContactForm(Model model) {

		model.addAttribute("title", "Add contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact_form";
	}

	// processing add contact form
	
	//@RequestMapping(value="/process-contact",method=RequestMethod.POST)
	@PostMapping("/process-contact")
	public String addContactForm(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile file, Principal principal,HttpSession session) {
		try {
			String userName = principal.getName();
			User user = this.userRepo.getUserByUserName(userName);
			// file uploading
			if (file.isEmpty()) {
				// file not upload
				//contact.setImageUrl("contact.png");
				System.out.println("file is empty");
				contact.setImageUrl("contact.png");
			} else {
				// file is upload
				contact.setImageUrl(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("image is upload");
			}
			contact.setUsers(user);
			user.getContacts().add(contact);
			this.userRepo.save(user);
			System.out.println(contact);
			System.out.println("add to database");
			session.setAttribute("message", new Message("add contact successfully !! add new more contact","success"));
		} catch (Exception e) {
			System.out.println("error :" + e.getMessage());
			session.setAttribute("message", new Message("some went wrong !! try again more!!"," error"));

		}
		return "normal/add_contact_form";
	}
	
	//show contacts
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable ("page") Integer page,Model model,Principal principal)
	{
		model.addAttribute("title","show contacts");
		String userName=principal.getName();
		User user=this.userRepo.getUserByUserName(userName);
		//current page 
		//contact of perpage 5
		PageRequest pageable=PageRequest.of(page, 3);
		
		Page<Contact> contacts=this.contactRepository.findContactsByUser(user.getId(),pageable);
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",contacts.getTotalPages());
		
		/*
		 * //send contacts list Authentication authentication =
		 * SecurityContextHolder.getContext().getAuthentication(); String userName =
		 * authentication.getName(); //String userName=principal.getName(); User
		 * user=this.userRepo.getUserByUserName(userName); //List<Contact>
		 * contacts=this.contactRepo.findContactsByUser(user.getId());
		 * //model.addAttribute("contacts",contacts);
		 */		return "normal/show_contacts";
	}
	//show particular details contact
	@RequestMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal)
	{
		System.out.println("CID"+cId);
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);
		Contact contact=contactOptional.get();
		
		String userName=principal.getName();
		User user=this.userRepo.getUserByUserName(userName);
		if(user.getId()==contact.getUsers().getId())
		{
			model.addAttribute("contact", contact);
			model.addAttribute("title"+contact.getName());
		}
		//model.addAttribute("contact"+contact);
		
		return "normal/contact_detail";
	}
	//delete contact handler
	@GetMapping("/delete/{cId}")
	@Transactional
	public String deleteContact(@PathVariable("cId") Integer cId,Model model,HttpSession session,Principal principal)
	{
	Optional<Contact> contactOptional=this.contactRepository.findById(cId);
	Contact contact=contactOptional.get();
	//contact.setUsers(null);
	//this.contactRepository.delete(contact);
	User user=this.userRepo.getUserByUserName(principal.getName());
	user.getContacts().remove(contact);
	this.userRepo.save(user);
	System.out.println("DELETE");
	session.setAttribute("message", new Message("contact is deleted successfully","success"));
	
	return "redirect:/user/show-contacts/0";
	}
	
	//update contact
	@PostMapping("/update-contact/{cId}")
	public String updateContact(@PathVariable("cId") Integer cId,Model model)
	{
		Contact contact=this.contactRepository.findById(cId).get();
		model.addAttribute("title", "update contact");
		model.addAttribute("contact",contact);
		return "normal/update_contact";
	}
	// update contact handler
	@RequestMapping(value="/update-contact",method=RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,
			Model model,HttpSession session,Principal principal)
	{
		try {
			Contact oldcontactDetails=this.contactRepository.findById(contact.getcId()).get();
			
			if(!file.isEmpty())
			{
				//delete old photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file2=new File(deleteFile,oldcontactDetails.getImageUrl());
				file2.delete();
				//update old photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImageUrl(file.getOriginalFilename());
			}
			else
			{
				contact.setImageUrl(oldcontactDetails.getImageUrl());
			}
			User user=this.userRepo.getUserByUserName(principal.getName());
			contact.setUsers(user);
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("your contact is updated !!","success"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("contact name :"+contact.getName());
		System.out.println("contact id :"+contact.getcId());
		return "redirect:/user/"+contact.getcId()+"/contact";
	}
	//profile handler
	@GetMapping("/profile")
	public String profilePage(Model model)
	{
		model.addAttribute("title","profile page");
		return "normal/profile";
	}
	//setting handller
	@GetMapping("/settings")
	
	public String settingPassword()
	{
		return "normal/settings";
	}
	//change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,
			Principal principal,HttpSession session)
	{
		System.out.println("old password "+oldPassword);
		System.out.println("new password "+newPassword);
		String userName=principal.getName();
		User currentUser=this.userRepo.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());
		
		if(this.bcryptPasswordEncoder.matches(oldPassword, currentUser.getPassword()))
		{
			//change password
			currentUser.setPassword(this.bcryptPasswordEncoder.encode(newPassword));
			this.userRepo.save(currentUser);
			session.setAttribute("message", new Message("password is successfully changed","success"));
		}
		else {
			session.setAttribute("message", new Message("please enter correct old password","danger"));
			return "redirect:/user/settings";
		}
		
		return "redirect:/user/index";
	}
	@PostMapping("/create_order")
	@ResponseBody
	public String createOrder(@RequestBody Map<String,Object> data) throws RazorpayException
	{
		int amt=Integer.parseInt(data.get("amount").toString());
		var client=new RazorpayClient("","");
		JSONObject ob=new JSONObject();
		ob.put("amount", amt*100);
		ob.put("currency", "INR");
		ob.put("receipt", "txn_12346");
		Order order=client.Orders.create(ob);
		System.out.println("order is success:"+order);
		System.out.println(data);
		return order.toString();
	}

}
