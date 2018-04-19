package com.microsoft.aad.oidcpoc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.UserInfo;

@Controller
public class LoginController {

	@RequestMapping(value = "/login", method = RequestMethod.GET )
	public String GetLoginForm(ModelMap model, HttpServletRequest httpRequest) {
		
        return "/login";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST )
	public String Login(ModelMap model, HttpServletRequest httpRequest) throws ClassNotFoundException {

		String userName = httpRequest.getParameter("j_username");
		String password = httpRequest.getParameter("j_password");
		
		if (AuthHelper.AuthLocalUser(httpRequest, userName, password) != null) {
			return "redirect:/";
		} else {
			model.addAttribute("error", "Sorry, you are unauthorized. If you have previously linked your account to your work login, you will have to use that account going forward.");
			return "/login";
		}
	}

	@RequestMapping(value="/logout", method = RequestMethod.GET )
	public String logout(ModelMap model, HttpServletRequest httpRequest) throws Exception {
	
		AuthenticationResult res = AuthHelper.getAuthSessionObject(httpRequest);
		HttpSession session = httpRequest.getSession();
        String tenant = AuthHelper.getSetting(session.getServletContext(), "tenant");
		session.invalidate();
		String logoutUrl = "/";
        if (res.getAccessTokenType() != AuthHelper.ACCESS_TOKEN_LOCAL) {
            String replyUri = AuthHelper.GetReplyUri(httpRequest);
			logoutUrl = String.format("https://login.microsoftonline.com/%s/oauth2/logout?post_logout_redirect_uri=%s", tenant, replyUri);
		}
		return "redirect:" + logoutUrl;
	}
	
	@RequestMapping(value="/secure/linkaccounts", method = RequestMethod.GET )
	public String linkAccounts(ModelMap model, HttpServletRequest httpRequest) {
		
		return "/secure/linkaccounts";
	}

	@RequestMapping(value="/secure/linkaccounts", method = RequestMethod.POST )
	public String establishLink(ModelMap model, HttpServletRequest httpRequest) throws ClassNotFoundException, Exception {
		String userName = httpRequest.getParameter("j_username");
		String password = httpRequest.getParameter("j_password");
		
		DBUser user = AuthHelper.AuthLocalUser(httpRequest, userName, password, false); 
		if (user != null) {
			AuthenticationResult res = AuthHelper.getAuthSessionObject(httpRequest);
			UserInfo info = res.getUserInfo();
			String uniqueId = info.getUniqueId();
			int userId = user.UserId;
			AuthHelper.LinkAccounts(httpRequest, uniqueId, userId);
			
			return "redirect:/";
		} else {
			model.addAttribute("error", "Sorry, we could not validate your account. Please try again.");
			return "/secure/linkaccounts";
		}
	}
}
