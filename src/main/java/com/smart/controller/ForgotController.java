package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.emailconfig.OtpVerification;
import com.smart.entity.User;
import com.smart.repository.UserRepository;
import com.smart.service.EmailService;

@Controller
public class ForgotController {
	@Autowired
	private OtpVerification eotpVerification;
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private BCryptPasswordEncoder bcryptPasswordEncoder;
	Random random=new Random(1000);
	
	//email id form open handler
	@RequestMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
	}
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email,HttpSession session)
	{
		System.out.println("your email"+email);
		//generating 4 digit otp
		
		//Random random=new Random(1000);
		int otp=random.nextInt(999999);
		System.out.println("your otp:"+otp);
		String subject="OTP FROM SCM";
		String message="<h1> OTP="+otp+"<h1>";
		String mHtml=""
		+""
				+"<div style='border:1px padding:20px solid #e2e2e2'>"
				+ "<h1>"
				+"otp is: "
				+"<b>"+otp
				+"</b>"
				+"</h1>"
				+"</div>";
		String to=email;
		boolean flag=this.eotpVerification.sendEmailOtp(subject, message, to);
		if(flag)
		{
			session.setAttribute("myotp:", otp);
			session.setAttribute("email ", email);
			return "verify_otp";
		}
		else
		{
			session.setAttribute("message", "check your email id");
			return "forgot_email_form";
		}
		
	}
	//verify otp
	@PostMapping("/verify-otp")
	public String verifyOtpEmail(@RequestParam("otp") int otp,HttpSession session)
	{
		int myOtp=(Integer)session.getAttribute("myotp");
		String email=(String)session.getAttribute("email");
		if(myOtp==otp)
		{
			//password change form
			User user=this.userRepo.getUserByUserName(email);
			if(user==null)
			{
				//send error messgae
				session.setAttribute("message", "user does not exists with email");
				return "forgot_email_form";
			}
			else
			{
				//send change password form
			}
			return "password_change_form";
		}
		session.setAttribute("message", "you have enter wrong otp please check!!");
		return "verify_otp";
	}
	//change password handller
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword")String newpassword,HttpSession session)
	{
		String email=(String)session.getAttribute("email");
		User user=this.userRepo.getUserByUserName(email);
		user.setPassword(this.bcryptPasswordEncoder.encode(newpassword));
		this.userRepo.save(user);
		return "redirect:/signin?change=change password successfully!!!";
	}

}
