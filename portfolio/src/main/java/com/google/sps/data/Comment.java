package com.google.sps.data;

/**
 * Class representing a comment.
 *
 * <p>Note: The private variables in this class are converted into JSON.
 */
public class Comment {

  private long timestamp;
  private long id;
  private String content;
  private String email;
  private String username;
  private String likers;

  public Comment(long timestamp, long id, String content, String email, String username, String likers) {
    this.timestamp = timestamp;
    this.id = id;
    this.content = content;
    this.email = email;
    this.username = username;
    this.likers = likers;
  }


}