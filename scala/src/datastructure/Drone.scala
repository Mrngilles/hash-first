package datastructure

import program.Main

import scala.collection.mutable.ListBuffer

/**
  * Created by duccao on 12/02/16.
  */
class Drone (var id: Int = -1, var products: List[Product] = List.empty) extends Point {
  override def toString = s"Drone(id $id, ($x, $y), products = $products)"

  var turns = 0

  def isAvailable = turns <= Main.numOfTurns

  /**
    * @param orders
    * @return the order that is nearest to drone's location
    */
  def findNearestOrder(orders: ListBuffer[Order]): Order = {
    var nearestOrder: Order = null
    var nearestDistance = Int.MaxValue
    orders.filter(order => !order.isProcessing).foreach { order =>
      val distance: Int = order.distanceTo(this)
      if (distance < nearestDistance) {
        nearestDistance = distance
        nearestOrder = order
      }
    }

    if (nearestOrder != null) {
      // we gonna process this order now
      nearestOrder.isProcessing = true
    }

    nearestOrder
  }

  /**
    * @param warehouses
    * @param smallOrder
    * @return The closest warehouse from drone and can serve this small order
    */
  def fineNearestWarehouse(warehouses: ListBuffer[Warehouse], smallOrder: Order): Warehouse = {
    var nearestWarehouse: Warehouse = null
    var nearestDistance = Int.MaxValue
    warehouses.filter(warehouse => warehouse.canServe(smallOrder)).foreach { warehouse =>
      val distance: Int = warehouse.distanceTo(this)
      if (distance < nearestDistance) {
        nearestDistance = distance
        nearestWarehouse = warehouse
      }
    }

    nearestWarehouse
  }



  /**
    * @param order
    * @return list commands we use to load all products of this order
    */
  def process(order: Order, warehouse: Warehouse, commandCode: String): List[String] = {
    val commands = new ListBuffer[String]
    order.products.foreach(product => commands += s"$id $commandCode ${warehouse.id} ${product.id} ${product.quantity}\n")

    turns += distanceTo(warehouse) + 1

    commands.toList
  }

  def load(order: Order, warehouse: Warehouse): List[String] = {
    order.products.foreach { onDroneProduct =>
      warehouse.products = warehouse.products
        .filter(product => product.equals(onDroneProduct))
        .map(product => product.update(onDroneProduct.quantity))
    }

    process(order, warehouse, "L")
  }

  def unload(order: Order, warehouse: Warehouse): List[String] = {
    process(order, warehouse, "U")
  }

  def deliver(order: Order): List[String] = {
    val commands = new ListBuffer[String]
    order.products.foreach(product => commands += s"$id D ${order.id} ${product.id} ${product.quantity}\n")

    turns += distanceTo(order) + 1

    commands.toList
  }
}
