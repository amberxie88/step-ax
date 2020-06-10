package com.google.sps.data;

/**
 * Class representing a comment.
 *
 * <p>Note: The private variables in this class are converted into JSON.
 */
public class Comment {

  private long timestamp;
  private String content;
  private String email;
  private String username;

  public Comment(long timestamp, String content, String email, String username) {
    this.timestamp = timestamp;
    this.content = content;
    this.email = email;
    this.username = username;
  }


}