package com.github.arschles

/**
 * a counter that can be incremented and decremented, by maintaining a counter for increments and decrements.
 * this is an implementation of https://github.com/aphyr/meangirls#pn-counter
 */
class PNCounter {
  private val positive = new GCounter
  private val negative = new GCounter

  /**
   * increment the counter for a given actor if the result is >= than the existing value
   * @param actorName the actor to increment
   * @param incrBy the amount by which to increment
   * @return the current value for the given actor
   */
  def incr(actorName: String, incrBy: Int = 1): Int = {
    positive.incr(actorName, incrBy)
    positive.get(actorName) - negative.get(actorName)
  }

  /**
   * decrement the counter for a given actor if the result is >= than the existing value
   * @param actorName the actor to increment
   * @param decrBy the amount by which to increment
   * @return the current value for the given actor
   */
  def decr(actorName: String, decrBy: Int = 1) {
    negative.incr(actorName, decrBy)
    positive.get(actorName) - negative.get(actorName)
  }

  /**
   * merge all existing actors together and return the value
   * @return the total value after merging all actors together
   */
  def merge: Int = {
    positive.merge - negative.merge
  }

}
