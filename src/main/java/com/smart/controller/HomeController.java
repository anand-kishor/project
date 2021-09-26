package com.smart.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.entity.User;
import com.smart.helper.Message;
import com.smart.repository.UserRepository;

@Controller
public class HomeController {
	@Autowired
	private
	BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepo;
	@GetMapping("/test")
	@ResponseBody
	public String getWork()
	{
		User user=new User();
		user.setName("anand");
		user.setEmail("anandkishorjava@gmail.com");
		user.setPassword("anand@kishor");
		userRepo.save(user);
		return "working";
	}
	@RequestMapping("/")
	public String home(Model model)
	{
		model.addAttribute("title", "Home -smart contact manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model)
	{
		model.addAttribute("title", "About -smart contact manager");
		return "about";
	}
	@RequestMapping("/signup")
	public String signUp(Model model)
	{
		model.addAttribute("title", "Register -smart contact manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	//user register
	@RequestMapping(value="/do_register",method=RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,@RequestParam(value="agreement",defaultValue="false") boolean agreement,Model model,
			HttpSession session)
	{
		try {
		if(!agreement)
		{
			System.out.println("you are not agrre terms and conditions");
			throw new Exception("you are not agrre terms and conditions");
		}
		if(bindingResult.hasErrors())
		{
			System.out.println("ERROR"+bindingResult.toString());
			model.addAttribute("user",user);
			return "signup";
		}
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		user.setImageUrl("default.png");
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		System.out.println("Agreement "+agreement);
		System.out.println("User"+user);
		User result=this.userRepo.save(user);
		model.addAttribute("user"+result);
		model.addAttribute("user",new User());
		session.setAttribute("message", new Message("successfully registerd","alert-success"));
		return "signup";
	}
		catch(Exception e)
		{
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message", new Message("Something went wrong"+e.getMessage(),"alert-dangers"));
			return "signup";
			
		}
		}
	@GetMapping("/signin")
	public String getLoginPage(Model model)
	{
		model.addAttribute("title", "Login -smart contact manager");
		return "login";
	}
   
}
