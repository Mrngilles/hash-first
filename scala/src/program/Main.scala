package program

import java.io.{File, BufferedWriter, FileWriter}

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

  // main data structures
  val warehouses = ListBuffer[Warehouse]()
  val orders = ListBuffer[Order]()
  val drones = ListBuffer[Drone]()

  def parseInput(fileName: String): Unit = {
    // Initialize variables
    var numOfWarehouses = 0
    var numOfOrders = 0

    var index = 0
    val warehouseStartIndex = 3
    var orderStartIndex = 0
    var warehouseIndex = 0
    var orderIndex = 0

    var warehouse = new Warehouse()
    var order = new Order()

    warehouses.clear()
    orders.clear()
    drones.clear()

    // index = product id, value = product weight
    val productWeights = new ListBuffer[Int]()

    // Processing file
    for (line <- Source.fromFile(fileName).getLines) {
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
              val drone: Drone = new Drone(id = id)
              drone.x = warehouse.x
              drone.y = warehouse.y
              drones += drone
            }
          }
        } else {
          var productId = 0
          line.split(" ").foreach { quantity =>
            warehouse.products += (productId -> new Product(productId, quantity.toInt, productWeights(productId)))
            productId += 1
          }

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
          line.split(" ").foreach { productIdString =>
            val productId: Int = productIdString.toInt
            if (!products.contains(productId)) {
              order.products += (productId -> new Product(productId, 1, productWeights(productId)))
            } else {
              val existingProduct = order.products(productId)
              order.products += (productId -> new Product(productId, existingProduct.quantity + 1, productWeights(productId)))
            }
          }

          orders += order
        }

        orderIndex += 1
      }

      index += 1
    }
  }

  def generateOutput(fileName: String, commands: ListBuffer[String]): Unit = {
    val file = new File(fileName)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(commands.size + "\n")
    for (command <- commands) bw.write(command)
    bw.close()
  }

  /***
  queue = all drones
  when queue still has drones
    select first drone of the queue
      find nearest small order
      load products (update turns)
      deliver products (update turns)
    if this drone is still available
      append this drone to end of the queue
  ***/

  def generateCommands(): ListBuffer[String] = {
    val commands = new ListBuffer[String]()
    val smallOrders = orders.filter(order => order.isSmall).to[ListBuffer]
    val queue = drones
    var numOfCompletedOrders = 0

    orders.filter(order => order.numOfDrones > 1)
      .map(order => smallOrders ++= order.subOrders())

    while (smallOrders.nonEmpty && queue.nonEmpty && commands.size < numOfTurns) {
      val drone = queue.head
      queue -= drone

      val nearestSmallOrder = drone.findNearestOrder(smallOrders)
      if (nearestSmallOrder != null) {
        commands ++= drone.process(nearestSmallOrder, warehouses)

        smallOrders -= nearestSmallOrder

        numOfCompletedOrders += 1

        if (drone.isAvailable) queue += drone
      }
    }

    println(s"% turns used ${commands.size / numOfTurns.toFloat}")
    println(s"% completed orders ${numOfCompletedOrders / orders.size.toFloat}")

    commands
  }

  for (fileName <- Array("busy_day.in", "mother_of_all_warehouses.in", "redundancy.in")) {
    println("FILE " + fileName)

    parseInput("input/" + fileName)
    generateOutput("output/" + fileName.replace(".in", ".out"), generateCommands())

    var sumEfficiency = 0.0
    drones.foreach {drone =>
//      println(s"Drone ${drone.id} has efficiency ${drone.avgEfficiency}")
      sumEfficiency += drone.avgEfficiency
    }
    val avgEfficiency = sumEfficiency / drones.size
    println(s"[Average efficiency of all drones is $avgEfficiency]")

    println
  }
}
