package com.github

/**
 * Created by IntelliJ IDEA.
 *
 * com.github.arschles
 *
 * User: aaron
 * Date: 10/10/13
 * Time: 2:48 PM
 */
package object arschles {

  implicit val intMonotonic = Monotonic { (i1: Int, i2: Int) =>
    i2 >= i1
  }
}
