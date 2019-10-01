package com.microsoft.aad.oidcpoc;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController {

	@RequestMapping(value="/", method = RequestMethod.GET )
	public String GetIndex(ModelMap model, HttpServletRequest httpRequest) {
		
        return "/index";
	}

	@RequestMapping(value="/about", method = RequestMethod.GET )
	public String GetAbout(ModelMap model, HttpServletRequest httpRequest) {
		
        return "/about";
	}
	
	@RequestMapping(value="/contact", method = RequestMethod.GET )
	public String GetContact(ModelMap model, HttpServletRequest httpRequest) {
		
		return "/contact";
	}
}
