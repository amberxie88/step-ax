package com.google.sps.servlets;

import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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
import com.google.sps.data.Like;

/** Servlet that takes care of commenting. */
@WebServlet("/like-comment")
public final class LikeServlet extends HttpServlet {

  private static ArrayList<String> msgHistory = new ArrayList<String>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<Like> msg = new ArrayList<Like>();
    Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
        long id = entity.getKey().getId();
        String likers = (String) entity.getProperty("likers");
        int numLikes = getNumLikes(likers);
        Like l = new Like(id, numLikes);
        msg.add(l);
    }

    response.setContentType("application/json");
    String returnString = new Gson().toJson(msg);
    response.getWriter().println(returnString);
  }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get some data
    long id = Long.parseLong(getParameter(request, "id", "-1")); // which comment is being liked

    UserService userService = UserServiceFactory.getUserService();
    String email = userService.getCurrentUser().getEmail(); // which user is liking the comment

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key idKey = KeyFactory.createKey("Comment", id);
    try {
        Entity entity = datastore.get(idKey);
        String likers = (String) entity.getProperty("likers");
        if (likers.contains(email)) {
            response.sendRedirect("/random.html");
        } else {
            likers = likers + "," + email;
            update(datastore, entity, idKey, likers);
        }
    } catch (Exception e) {
        System.out.println("Error getting the comment :(");
    }
    
    // Redirect back to the HTML page.
    response.sendRedirect("/random.html");
  }

  private int getNumLikes(String likers) {
    String[] tokens = likers.split(",");
    int numLikes = Math.max(0, tokens.length - 1);
    return numLikes;
  }

  private void update(DatastoreService datastore, Entity entity, Key idKey, String likers) {
    long timestamp = (long) entity.getProperty("timestamp");
    long id = entity.getKey().getId();
    String content = (String) entity.getProperty("comment");
    String email = (String) entity.getProperty("email");
    String username = (String) entity.getProperty("name");

    entity.setProperty("comment", content);
    entity.setProperty("timestamp", timestamp);
    entity.setProperty("name", username);
    entity.setProperty("email", email);
    entity.setProperty("likers", likers); 
    datastore.put(entity);
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
}
