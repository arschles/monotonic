package com.github.arschles

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._

/**
 * a counter that can be incremented and merged in case they diverge.
 * this is an implementation of https://github.com/aphyr/meangirls#g-counter
 *
 * semantics of this GCounter:
 * - it subsumes LVarCounter
 * - each actor/thread/process/etc... maintains an individual GCounter
 * - incr and get operate on the individual GCounter for that actor/thread/process
 * - it provides a merge function which takes the current value of each GCounter in the list and adds it together to form a new GCounter
 */

class GCounter {

  private lazy val counters = new ConcurrentHashMap[String, LVarCounter]()

  /**
   * ensure a counter exists for an actor
   * @param actorName the actor for which the counter should exist
   * @param onMiss the counter to put into the map if one didn't exist
   * @return the counter that exists in the map
   */
  private def ensureCounter(actorName: String,
                            onMiss: LVarCounter): LVarCounter = {
    Option(counters.putIfAbsent(actorName, onMiss)).getOrElse(onMiss)
  }

  /**
   * increment the value for the given actor if the result is greater than or equal than the current.
   * if there's no current value for the given actor, then a new one is created with incrValue
   * @param actorName the actor whose value to increment
   * @param incrValue the value to increment by
   * @return the new value for the given actor
   */
  def incr(actorName: String, incrValue: Int = 1): Int = {
    val lVarCounter = ensureCounter(actorName, new LVarCounter(incrValue))
    lVarCounter.incr(incrValue)
  }

  /**
   * block until the current value for the given actor is >= minValue.
   * if there's no counter for the given actor, one is created with minValue and this function operates on it
   * @param actorName the name of the actor whose value to get
   * @param minValue the value that the counter for {{{actorName}}} must be before this function returns
   * @return the value for the given actor
   */
  def get(actorName: String, minValue: Int = Int.MinValue): Int = {
    val lVarCounter = ensureCounter(actorName, new LVarCounter(minValue))
    lVarCounter.get(minValue)
  }
  
  /**
   * merge all of the values for each actor together and return the result.
   * after this method is called, each actor still exists with its original value.
   * TODO: implement a compact method to erase all actors
   * the returned result will be a lower bound because increments can happen in the meantime
   * @return the sum of all the values of each actor
   */
  def merge: Int = {
    counters.asScala.foldLeft(0) { (cur, counterTup) =>
      val (_, counter) = counterTup
      cur + counter.get(Int.MinValue)
    }
  }
}

