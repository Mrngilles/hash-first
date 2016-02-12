package datastructure

/**
  * Created by duccao on 12/02/16.
  */
class Point {
  var x: Int = -1
  var y: Int = -1

  override def toString = s"Point(x $x y $y)"

  def distanceTo(anotherPoint: Point): Int = {
    Math.ceil( Math.sqrt(Math.pow(anotherPoint.x - this.x, 2) + Math.pow(anotherPoint.y - this.y, 2)) )
      .toInt
  }
}
