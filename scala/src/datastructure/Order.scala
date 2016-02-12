package datastructure

import program.Main

/**
  * Created by duccao on 12/02/16.
  */
class Order(var id: Int = -1, var x: Int = 0, var y: Int = 0,
            var products: List[Product] = List.empty) extends Point(x, y) {
  override def toString = s"Order(id $id, ($x, $y), products = $products)"

  var isProcessing = false

  def totalWeight: Int = {
    var sum = 0
    products.foreach(product => sum += product.weight)

    sum
  }

  /**
    * @return true if this order can be server by only 1 drone
    */
  def isSmall: Boolean = {
    totalWeight <= Main.maxPayLoad
  }

  def distanceTo(drone: Drone): Int = {
    Math.ceil( Math.sqrt(Math.pow(drone.x - this.x, 2) + Math.pow(drone.y - this.y, 2)) )
      .toInt
  }
}
