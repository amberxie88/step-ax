package com.google.sps.data;

/**
 * Class representing a reply.
 *
 * Future: combine this with Comment to avoid redundancy. 
 */
public class Reply {

  private long timestamp;
  private long id;
  private String content;
  private String email;
  private String username;
  private long parentID;

  public Reply(long timestamp, long id, String content, String email, String username, long parentID) {
    this.timestamp = timestamp;
    this.id = id;
    this.content = content;
    this.email = email;
    this.username = username;
    this.parentID = parentID;
  }


}