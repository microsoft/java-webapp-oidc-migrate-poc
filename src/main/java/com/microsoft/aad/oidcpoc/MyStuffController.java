package com.microsoft.aad.oidcpoc;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class MyStuffController {
	@RequestMapping(value="/secure/stuff", method = RequestMethod.GET )
	public String GetIndex(ModelMap model, HttpServletRequest httpRequest) {
		
        return "/secure/stuff";
	}
}
