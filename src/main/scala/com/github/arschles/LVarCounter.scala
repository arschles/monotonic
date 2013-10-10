package com.github.arschles

/**
 * a counter that has a global integer which will only grow.
 * it allows multiple threads to increment it, and only the largest increment
 * will occur at once.
 * multiple threads can increment it, but a single {{{incr}}} call
 * will only apply if it results in the internal integer will grow
 */
class LVarCounter(initialValue: Int = 0) {
  private val lVar = new LVar(initialValue)
  /**
   * increment the counter only if it will result in the internal value to increase
   * @param incrValue the value to increment by
   * @return the resulting value
   */
  def incr(incrValue: Int = 1): Int = {
    val cur = lVar.get(initialValue)
    val proposed = cur + incrValue
    val wasSet = lVar.set(proposed)
    if(wasSet) {
      proposed
    } else {
      cur
    }
  }

  /**
   * block until the value of the counter is >= minValue.
   * note that since the internal counter can only increase, calling this method with the default
   * value of minValue is guaranteed to return immediately.
   * a common use case for this method is to implement a barrier in a thread
   * @param minValue the minimum value that the counter must be before returning
   * @return the value of the counter once it's >= minValue
   */
  def get(minValue: Int = initialValue): Int = {
    lVar.get(minValue)
  }
}
