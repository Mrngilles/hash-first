package datastructure

import program.Main

import scala.collection.mutable.ListBuffer

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

  def emptyOrder: Order = {
    val order = new Order()
    order.id = this.id

    order
  }

  /**
    * @return list of small orders
    */
  def subOrders(): ListBuffer[Order] = {
    val orders = new ListBuffer[Order]

    var order = emptyOrder
    this.products.foreach { case(productId, product) =>
      order.products += (productId -> product)

      if (!order.isSmall) {
        // remove the heavy product
        order.products -= productId

        // add new sub order to list
        orders += order

        // create new sub order
        order = emptyOrder
        order.products += (productId -> product)
      }
    }

    // add the last small order
    if (order.isSmall) orders += order

    orders
  }

  def distanceTo(drone: Drone): Int = {
    super.distanceTo(drone)
  }
}
