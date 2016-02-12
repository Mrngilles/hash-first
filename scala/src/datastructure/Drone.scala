package datastructure

import scala.collection.mutable.ListBuffer

/**
  * Created by duccao on 12/02/16.
  */
class Drone (var id: Int = -1, var x: Int = 0, var y: Int = 0,
             var products: List[Product] = List.empty) extends Point(x, y) {
  override def toString = s"Drone(id $id, ($x, $y), products = $products)"

  def nearestOrder(orders: ListBuffer[Order]): Order = {
    var nearestOrder: Order = null
    var nearestDistance = Int.MaxValue
    orders.filter(order => !order.isProcessing).foreach { order =>
      val distance: Int = order.distanceTo(this)
      if (distance < nearestDistance) {
        nearestDistance = distance
        nearestOrder = order
      }
    }

    // we gonna process this order now
    nearestOrder.isProcessing = true

    nearestOrder
  }
}
