/*******************************************************************************
 // Copyright (c) Microsoft Corporation.
 // All rights reserved.
 //
 // This code is licensed under the MIT License.
 //
 // Permission is hereby granted, free of charge, to any person obtaining a copy
 // of this software and associated documentation files(the "Software"), to deal
 // in the Software without restriction, including without limitation the rights
 // to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
 // copies of the Software, and to permit persons to whom the Software is
 // furnished to do so, subject to the following conditions :
 //
 // The above copyright notice and this permission notice shall be included in
 // all copies or substantial portions of the Software.
 //
 // THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 // IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 // FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
 // AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 // LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 // OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 // THE SOFTWARE.
 ******************************************************************************/
package com.microsoft.aad.oidcpoc;

import java.io.IOException;
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
import com.microsoft.aad.adal4j.AuthenticationResult;

public class BasicFilter implements Filter {

	private AuthFlow _flow;

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            try {
                String currentUri = AuthHelper.GetReplyUri((HttpServletRequest) request);
                String queryStr = httpRequest.getQueryString();
                String fullUrl = currentUri + (queryStr != null ? "?" + queryStr : "");

                //check if user has already authenticated locally
            	AuthenticationResult result = AuthHelper
                        .getAuthSessionObject(httpRequest);
            	
    			if (result != null && result.getAccessTokenType()==AuthHelper.ACCESS_TOKEN_LOCAL) {
    		        chain.doFilter(request, response);
    		        return;
    			}

    			//continue with Azure AD validation
                // check if user has a AuthData in the session
                if (!AuthHelper.isAuthenticated(httpRequest)) {
                    if (AuthHelper.containsAuthenticationData(httpRequest)) {
                        _flow.processAuthenticationData(httpRequest, httpResponse, currentUri, fullUrl);
                    } else {
                        // not authenticated
                    	_flow.sendAuthRedirect(httpRequest, httpResponse);
                        return;
                    }
                }
                if (_flow.isAuthDataExpired(httpRequest)) {
                	_flow.updateAuthDataUsingRefreshToken(httpRequest, httpResponse);
                }
            } catch (AuthenticationException authException) {
                // something went wrong (like expiration or revocation of token)
                // we should invalidate AuthData stored in session and redirect to Authorization server
            	_flow.removePrincipalFromSession(httpRequest);
            	_flow.sendAuthRedirect(httpRequest, httpResponse);
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
				AuthHelper.getSetting(ctx, "authority"),
				AuthHelper.getSetting(ctx,  "policy_susi"));
		_flow.is_b2c = (AuthHelper.getSetting(ctx,  "is_b2c").equals("true"));
    }
    public void destroy() {

    }
}