package program

import datastructure.{Drone, Order, Product, Warehouse}

import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by duccao on 12/02/16.
  */
class Main {
  val maxPayLoad = Main.maxPayLoad
}

object Main extends App {

  var numOfRows = 0
  var numOfColumns = 0
  var numOfDrones = 0
  var numOfTurns = 0
  var maxPayLoad = 0

  // index = product id, value = product weight
  val productWeights = ListBuffer[Int]()

  var numOfWarehouses = 0
  var numOfOrders = 0

  // main data structures
  val warehouses = ListBuffer[Warehouse]()
  val orders = ListBuffer[Order]()
  val drones = ListBuffer[Drone]()

  // Parse input
  val filename = "input/busy_day.in"
  var index = 0
  var warehouseStartIndex = 3
  var orderStartIndex = 0
  var warehouseIndex = 0
  var orderIndex = 0
  var warehouse = new Warehouse()
  var order = new Order()
  for (line <- Source.fromFile(filename).getLines) {
    // main parameters
    if (index == 0) {
      val array = line.split(" ").map(_.toInt)
      numOfRows = array(0)
      numOfColumns = array(1)
      numOfDrones = array(2)
      numOfTurns = array(3)
      maxPayLoad = array(4)
    }
    // weight of each product
    else if (index == 2) {
      val weights = line.split(" ").map(_.toInt)
      weights.foreach(weight => productWeights += weight)
    }
    // warehouses
    else if (index == warehouseStartIndex) {
      numOfWarehouses = line.toInt
      orderStartIndex = warehouseStartIndex + 2 * numOfWarehouses + 1
    }
    else if (warehouseStartIndex + 1 <= index && index <= warehouseStartIndex + numOfWarehouses * 2) {
      if (warehouseIndex % 2 == 0) {
        warehouse = new Warehouse
        warehouse.id = warehouseIndex / 2
        val array = line.split(" ").map(_.toInt)
        warehouse.x = array(0)
        warehouse.y = array(1)

        // at the beginning all drones stay at warehouse 0
        if (warehouse.id == 0) {
          for (id <- 0 until numOfDrones) {
            drones += new Drone(id = id, x = warehouse.x, y = warehouse.y)
          }
        }
      } else {
        var productId = 0
        var products = new ListBuffer[Product]
        line.split(" ").foreach { quantity =>
          products += new Product(productId, quantity.toInt, productWeights(productId))
          productId += 1
        }
        warehouse.products = products.toList

        warehouses += warehouse
      }

      warehouseIndex += 1
    }
    // orders
    else if (index == orderStartIndex) {
      numOfOrders = line.toInt
    }
    else if (orderStartIndex + 1 <= index && index <= orderStartIndex + numOfOrders * 3) {
      if (orderIndex % 3 == 0) {
        order = new Order
        order.id = orderIndex / 3
        val array = line.split(" ").map(_.toInt)
        order.x = array(0)
        order.y = array(1)
      } else if (orderIndex % 3 == 2) {
        val products = new ListBuffer[Product]
        var product: Product = null
        line.split(" ").foreach { productId =>
          product = new Product(productId.toInt, 1, productWeights(productId.toInt))
          if (!products.contains(product)) {
            products += product
          } else {
            product.quantity += 1
          }
        }
        order.products = products.toList

        orders += order
      }

      orderIndex += 1
    }

    index += 1
  }

//  warehouses.foreach(println)
//  orders.foreach(println)
  val smallOrders = orders.filter(order => order.isSmall)
  drones.foreach(drone => println(drone.nearestOrder(smallOrders).id))

  /***
  for each drone
    find nearest small order
    load products (update turns)
    deliver products (update turns)
  ***/
}
