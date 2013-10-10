package com.github.arschles

/**
 * the type class for monotonically increasing types
 * @tparam T the type to belong to this class
 */
trait Monotonic[T] {
  /**
   * defines the monotonically increasing order on type T
   * @param existing the left-hand-side of the test
   * @param test the right hand side of the test
   * @return true if existing <= test, false otherwise
   */
	def increased(existing: T, test: T): Boolean
}

object Monotonic {
  /**
   * helper function to create a new {{{Monotonic[T]}}}
   */
  def apply[T](fn: (T, T) => Boolean) = new Monotonic[T] {
    override def increased(t1: T, t2: T) = fn(t1, t2)
  }
}