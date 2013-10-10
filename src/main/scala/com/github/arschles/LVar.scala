package com.github.arschles

import scala.concurrent._

/**
 * an LVar. see http://www.cs.indiana.edu/pub/techreports/TR702.pdf for a complete description
 * @param initialValue the initial value of the LVar. must not be null
 * @tparam T the type of this LVar. must be a member of the {{{Monotonic}}} typeclass
 */
class LVar[T: Monotonic](initialValue: T) {

  private var value = initialValue

  private val mono = implicitly[Monotonic[T]]
  private val lock = new Lock()

  /**
   * set the value to the proposed value if it's monotonically greater than the existing value, else do nothing
   * @param proposed the proposed new value
   * @return true if the proposed value was set, false otherwise
   */
  def set(proposed: T): Boolean = {
    lock.acquire()
    val wasSet = if(mono.increased(value, proposed)) {
      value = proposed
      true
    } else {
      false
    }
    lock.release()
    wasSet
  }

  /**
   * block until the value of this LVar is >= {{{lowerBound}}}
   * @param lowerBound the value to test against
   * @return the value of this LVar after it's >= the lower bound value
   */
  def get(lowerBound: T): T = {
    while(!mono.increased(lowerBound, value)) {}
    value
  }
}
