package com.google.sps.servlets;

import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.gson.Gson;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.data.Reply;

/** Servlet that takes care of commenting. */
@WebServlet("/reply-section")
public final class ReplyServlet extends HttpServlet {

  private static ArrayList<String> msgHistory = new ArrayList<String>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<Reply> msg = new ArrayList<Reply>();
    Query query = new Query("Reply").addSort("timestamp", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
        long timestamp = (long) entity.getProperty("timestamp");
        long id = entity.getKey().getId();
        String content = (String) entity.getProperty("comment");
        String email = (String) entity.getProperty("email");
        String username = (String) entity.getProperty("name");
        long parentID = (long) entity.getProperty("parentID");
        Reply c = new Reply(timestamp, id, content, email, username, parentID);
        msg.add(c);
    }

    response.setContentType("application/json");
    //String returnString = convertToJson(msg);
    String returnString = new Gson().toJson(msg);
    response.getWriter().println(returnString);

  }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get some data
    long timestamp = System.currentTimeMillis();
    long parentID = Long.parseLong(getParameter(request, "id", "-1"));
    String commentString = getParameter(request, "comment-input", "");
    String name = getParameter(request, "name-input", "");

    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail();
    
    // Create entity and store in Datastore
    Entity commentEntity = new Entity("Reply");
    commentEntity.setProperty("comment", commentString);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("email", email);
    commentEntity.setProperty("parentID", parentID);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/random.html");
  }

  /**
   * @return the request parameter, or the default value if the parameter
   *         was not specified by the client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  /**
   * Converts an ArrayList to JSON
   */
  private String convertToJson(ArrayList<String> input) {
      String json = "{ \"comments\": [";
      int index=0;
    for (int i = 0; i < input.size(); i++) {
        String item = input.get(i);
        json+= "\"" + item + "\"";
        if (i + 1 != input.size()) {
            json+=", ";
        }
        index+=1;
    }
    json +="]}";
    return json;
  }
}
