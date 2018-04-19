package com.microsoft.aad.oidcpoc;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.aad.adal4j.AuthenticationException;

public class CoreFilter implements Filter {

	private AuthFlow _flow;

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            try {
            	//see if call is a response from Azure AD with claims

                if (AuthHelper.isAuthenticated(httpRequest) || (!AuthHelper.containsAuthenticationData(httpRequest))) {
    		        chain.doFilter(request, response);
    		        return;
                }
            	
    			//continue with Azure AD validation
                // check if user has a AuthData in the session
                String currentUri = AuthHelper.GetReplyUri((HttpServletRequest) request);
                String queryStr = httpRequest.getQueryString();
                String fullUrl = currentUri + (queryStr != null ? "?" + queryStr : "");

                _flow.processAuthenticationData(httpRequest, httpResponse, currentUri, fullUrl);
                if (_flow.isAuthDataExpired(httpRequest)) {
                    _flow.updateAuthDataUsingRefreshToken(httpRequest, httpResponse);
                }
            } catch (AuthenticationException authException) {
                // something went wrong (like expiration or revocation of token)
                // we should invalidate AuthData stored in session and redirect to Authorization server
                _flow.removePrincipalFromSession(httpRequest);
                return;
            } catch (Throwable exc) {
                httpResponse.setStatus(500);
                request.setAttribute("error", exc.getMessage());
                request.getRequestDispatcher("/error.jsp").forward(request, response);
            }
        }
        chain.doFilter(request, response);
    }

    public void init(FilterConfig config) throws ServletException {
    	ServletContext ctx = config.getServletContext();
		_flow = new AuthFlow(
				AuthHelper.getSetting(ctx, "client_id"), 
				AuthHelper.getSetting(ctx, "secret_key"),
				AuthHelper.getSetting(ctx, "tenant"),
				AuthHelper.getSetting(ctx, "authority"));
    }

	public void destroy() {

    }
}
