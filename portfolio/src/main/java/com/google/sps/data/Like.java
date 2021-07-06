package com.google.sps.data;

/**
 * Class representing a like.
 *
 * <p>Note: The private variables in this class are converted into JSON.
 */
public class Like {

  private long id;
  private int numLikes;

  public Like(long id, int numLikes) {
    this.id = id;
    this.numLikes = numLikes;
  }


}