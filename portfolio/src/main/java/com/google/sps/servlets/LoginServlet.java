// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      String responseJSON = convertToJSON(userService, true, userEmail);
      response.getWriter().println(responseJSON);
      //response.getWriter().println("<p>Hello " + userEmail + "!</p>");
      //response.getWriter().println("<p>Logout <a href=\"" + logoutUrl + "\">here</a>.</p>");
    } else {
      String urlToRedirectToAfterUserLogsIn = "/random.html";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      String responseJSON = convertToJSON(userService, false, "notloggedin@notloggedin.com");
      response.getWriter().println(responseJSON);
      //response.getWriter().println("<p>Hello stranger.</p>");
      //response.getWriter().println("<p>Login <a href=\"" + loginUrl + "\">here</a>.</p>");
    }
  }

  private String convertToJSON(UserService userService, boolean status, String userEmail) {
      String responseJSON = "{" + "\"email\": \"" + userEmail + "\"," + formatLoginStatus(status) + ", " + formatLoginHTML(userService, status) + "}";
      return responseJSON;
  }

  private String formatLoginStatus(boolean status) {
      String loginJSON = "\"loginStatus\": \"" + String.valueOf(status) +"\"";
      return loginJSON;
  }

  private String formatLoginHTML(UserService userService, boolean status) {
      String loginHTML = "\"loginHTML\": \"";
      
      if (status) { 
        // If you are logged in and want a logout URL
        String userEmail = userService.getCurrentUser().getEmail();
        String urlToRedirectToAfterUserLogsOut = "/random.html";
        String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);
        loginHTML += "<p>Hello " + userEmail + "!</p>";
        loginHTML += "<p>Logout <a href=\\\"" + logoutUrl + "\\\">here</a>.</p>";
      } else { 
        // If you are logged out and want a login URL
        String urlToRedirectToAfterUserLogsIn = "/random.html";
        String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);
        loginHTML += "<p>Hello stranger.</p>";
        loginHTML += "<p>Login <a href=\\\"" + loginUrl + "\\\">here</a>.</p>";
      }
      loginHTML += "\"";
      return loginHTML;
  }


}
