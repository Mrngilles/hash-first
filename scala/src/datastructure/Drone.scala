package datastructure

import program.Main

import scala.collection.mutable.ListBuffer

/**
  * Created by duccao on 12/02/16.
  */
class Drone (var id: Int = -1, var products: List[Product] = List.empty) extends Point {
  override def toString = s"Drone(id $id, ($x, $y), products = $products)"

  var turns = 0
  var numOfCompletedOrders = 0
  var sumEfficiency = 0.0

  def avgEfficiency = sumEfficiency / numOfCompletedOrders

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
    * Check several nearest warehouses until we can supply this order
    * @param order
    * @param warehouses
    */
  def process(order: Order, warehouses: ListBuffer[Warehouse]): List[String] = {
    val commands = new ListBuffer[String]

    val nearestWarehouses = warehouses.sortBy(warehouse => this.distanceTo(warehouse))
    var onDemandProducts = order.products.values.toList
    for (warehouse <- nearestWarehouses) {
      val supply: (List[Product], List[Product]) = warehouse.supply(onDemandProducts)

      val suppliedProducts = supply._1
      if (suppliedProducts.nonEmpty) {
        commands ++= load(suppliedProducts, warehouse)

        onDemandProducts = supply._2
        if (onDemandProducts.isEmpty) {
          commands ++= deliver(order)

          sumEfficiency += order.totalWeight / Main.maxPayLoad.toFloat
          numOfCompletedOrders += 1

          return commands.toList
        }
      }
    }

    List.empty
  }

  /**
    * @param products
    * @return list commands we use to load all products of this order
    */
  def generateCommands(products: List[Product], warehouse: Warehouse, commandCode: String): List[String] = {
    val commands = new ListBuffer[String]
    products.foreach(product => commands += s"${this.id} $commandCode ${warehouse.id} ${product.id} ${product.quantity}\n")

    turns += distanceTo(warehouse) + 1

    commands.toList
  }

  def load(products: List[Product], warehouse: Warehouse): List[String] = {
    generateCommands(products, warehouse, "L")
  }

  def unload(products: List[Product], warehouse: Warehouse): List[String] = {
    generateCommands(products, warehouse, "U")
  }

  def deliver(order: Order): List[String] = {
    val commands = new ListBuffer[String]
    order.products.foreach{case (_, product) => commands += s"$id D ${order.id} ${product.id} ${product.quantity}\n"}

    turns += distanceTo(order) + 1

    commands.toList
  }
}
