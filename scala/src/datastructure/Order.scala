package datastructure

import program.Main

/**
  * Created by duccao on 12/02/16.
  */
class Order extends ProductContainer {
  override def toString = s"Order(id $id, ($x, $y), products = $products)"

  var isProcessing = false

  /**
    * @return true if this order can be server by only 1 drone
    */
  def isSmall: Boolean = {
    totalWeight <= Main.maxPayLoad
  }

  def numOfDrones = Math.ceil(totalWeight / Main.maxPayLoad.toFloat).toInt

  def distanceTo(drone: Drone): Int = {
    super.distanceTo(drone)
  }
}
