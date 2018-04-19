/*******************************************************************************
 * Copyright © Microsoft Open Technologies, Inc.
 * 
 * All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * THIS CODE IS PROVIDED *AS IS* BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION
 * ANY IMPLIED WARRANTIES OR CONDITIONS OF TITLE, FITNESS FOR A
 * PARTICULAR PURPOSE, MERCHANTABILITY OR NON-INFRINGEMENT.
 * 
 * See the Apache License, Version 2.0 for the specific language
 * governing permissions and limitations under the License.
 ******************************************************************************/
package com.microsoft.aad.oidcpoc;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.microsoft.aad.adal4j.AuthenticationResult;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;

public final class AuthHelper {

    public static final String PRINCIPAL_SESSION_NAME = "principal";
    public static final String USEROBJ_SESSION_NAME = "DBUser";
    public static final String ACCESS_TOKEN_LOCAL = "local";

    private AuthHelper() {
    }

    public static void SetLocalSession(HttpSession session, DBUser user) {
		//String accessTokenType, String accessToken, String refreshToken, long expiresIn, String idToken, UserInfo userInfo, boolean isMultipleResourceRefreshToken
		AuthenticationResult res = new AuthenticationResult(AuthHelper.ACCESS_TOKEN_LOCAL, null, null, 0, null, null, false);
        session.setAttribute(AuthHelper.PRINCIPAL_SESSION_NAME, res);
        session.setAttribute(AuthHelper.USEROBJ_SESSION_NAME, user);
    }
    public static DBUser AuthLocalUser(HttpServletRequest request, String userName, String password) throws ClassNotFoundException {
    	return AuthLocalUser(request, userName, password, true);
    }
    
    public static DBUser AuthLocalUser(HttpServletRequest request, String userName, String password, boolean setLocalSession) throws ClassNotFoundException {
        HttpSession session = request.getSession();
		db database = new db(getSetting(request, "db_host"), getSetting(request,"db_name"), getSetting(request,"db_user"), getSetting(request, "db_password"));
		DBUser user = database.ValidateUser(userName, password);
		if (user == null) {
			return null;
		} else {
			//need to create authenticated session
			if (setLocalSession)
				AuthHelper.SetLocalSession(session, user);
	        return user;
		}
    }

    public static boolean SetAADSession(HttpServletRequest request, String uniqueId) throws ClassNotFoundException {
        HttpSession session = request.getSession();
		db database = new db(getSetting(request, "db_host"), getSetting(request,"db_name"), getSetting(request,"db_user"), getSetting(request, "db_password"));
		DBUser user = database.GetUser(uniqueId);
		if (user == null) {
			//user not found, will need to load the legacy login form to match
			return false;
		} else {
	        session.setAttribute(AuthHelper.USEROBJ_SESSION_NAME, user);
	        return true;
		}
    }
    
    public static void LinkAccounts(HttpServletRequest request, String uniqueId, int userId) throws ClassNotFoundException, SQLException {
        HttpSession session = request.getSession();
		db database = new db(getSetting(request, "db_host"), getSetting(request,"db_name"), getSetting(request,"db_user"), getSetting(request, "db_password"));
		DBUser user = database.LinkAccounts(userId, uniqueId);
        session.setAttribute(AuthHelper.USEROBJ_SESSION_NAME, user);
    }
    
    public static DBUser GetSessionProfile(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object usrblob = session.getAttribute(AuthHelper.USEROBJ_SESSION_NAME);
        return (usrblob!=null) ? (DBUser)usrblob : null;
    }
    
    public static boolean isAuthenticated(HttpServletRequest request) {
        return request.getSession().getAttribute(PRINCIPAL_SESSION_NAME) != null;
    }

    public static AuthenticationResult getAuthSessionObject(
            HttpServletRequest request) {
        return (AuthenticationResult) request.getSession().getAttribute(
                PRINCIPAL_SESSION_NAME);
    }

    public static String getPrincipalName(HttpServletRequest request) {
    	DBUser user = GetSessionProfile(request);
    	return (user!=null) ? user.Email : "N/A";
    }
    
    public static String GetCurrentUri(HttpServletRequest request) {
    	return GetCurrentUri(request, false);
    }

    public static String getSetting(ServletContext ctx, String key) {
		return System.getProperty(key);
	}
    
    public static String getSetting(HttpServletRequest request, String key) {
		HttpSession session = request.getSession();
		ServletContext ctx = session.getServletContext();
		return getSetting(ctx, key);
	}

    private static SchemeDTO GetScheme(HttpServletRequest request) {
		boolean requireSsl = Boolean.parseBoolean(getSetting(request, "require_ssl"));
		SchemeDTO res = new SchemeDTO();
		res.Scheme = (requireSsl) ? "https" : request.getScheme();
		res.Port = (requireSsl) ? 443 : request.getServerPort();
		return res;
    }
    
    public static String GetCurrentUri(HttpServletRequest request, boolean FQDNOnly) {
    	SchemeDTO scheme = GetScheme(request);
        String currentUri = scheme.Scheme
                + "://"
                + request.getServerName()
                + ("http".equals(scheme.Scheme)
                        && scheme.Port == 80
                        || "https".equals(scheme.Scheme)
                        && scheme.Port == 443 ? "" : ":"
                        + scheme.Port);
        
        if (!FQDNOnly)
        	currentUri += request.getRequestURI();
    	
        return currentUri;
    }
    public static String GetReplyUri(HttpServletRequest request) {
    	SchemeDTO scheme = GetScheme(request);
        String uri = scheme.Scheme
                + "://"
                + request.getServerName()
                + ("http".equals(scheme.Scheme)
                        && scheme.Port == 80
                        || "https".equals(scheme.Scheme)
                        && scheme.Port == 443 ? "" : ":"
                        + scheme.Port)
                + "/";
        return uri;
    }
    
    public static Map<String, Object> GetClaims(String idTokenString) {
        Map<String, Object> claims = null;
    	try {
			claims = JWTParser.parse(idTokenString).getJWTClaimsSet().getAllClaims();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return claims;
    }
    
    public static boolean containsAuthenticationData(
            HttpServletRequest httpRequest) {
        
    	//Map<String, String[]> map = httpRequest.getParameterMap();
        return httpRequest.getMethod().equalsIgnoreCase("POST") && (httpRequest.getParameterMap().containsKey(
                        AuthParameterNames.ERROR)
                        || httpRequest.getParameterMap().containsKey(
                                AuthParameterNames.ID_TOKEN) || httpRequest
                        .getParameterMap().containsKey(AuthParameterNames.CODE));
    }

    public static boolean isAuthenticationSuccessful(
            AuthenticationResponse authResponse) {
        return authResponse instanceof AuthenticationSuccessResponse;
    }
}
