package datastructure

import program.Main

/**
  * Created by duccao on 12/02/16.
  */
class Order(var x: Int = 0, var y: Int = 0, var products: List[Product] = List.empty) extends Point(x, y) {
  override def toString = s"Warehouse(($x, $y), products = $products)"

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
}
