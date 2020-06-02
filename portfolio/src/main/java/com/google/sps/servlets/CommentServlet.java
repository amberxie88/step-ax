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

/** Servlet that takes care of commenting. */
@WebServlet("/comment-section")
public final class CommentServlet extends HttpServlet {

  private static ArrayList<String> msgHistory = new ArrayList<String>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<String> msg = new ArrayList<String>();
    Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
        String txt = (String) entity.getProperty("comment");
        msg.add(txt);
    }

    response.setContentType("application/json");
    String returnString = convertToJson(msg);
    response.getWriter().println(returnString);

  }


  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Join the username and message to create a String for the appropriate comment
    long timestamp = System.currentTimeMillis();
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
    Date resultdate = new Date(timestamp);

    String commentString = getParameter(request, "comment-input", "");
    String name = getParameter(request, "name-input", "");
    if (commentString.length() == 0) {
        return;
    }
    if (name.length() > 0) {
        commentString = name + ": " + commentString;
    } else {
        commentString = "Anonymous: " + commentString;
    }

    commentString = "[" + sdf.format(resultdate) +"] " + commentString;

    // Create entity and store in Datastore
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("comment", commentString);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
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
