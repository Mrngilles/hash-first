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
          var products = new ListBuffer[Product]
          line.split(" ").foreach { quantity =>
            products += new Product(productId, quantity.toInt, productWeights(productId))
            productId += 1
          }
          warehouse.products = products

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
    val smallOrders = orders.filter(order => order.isSmall)
    val queue = drones

    def processOrder(drone: Drone, nearestSmallOrder: Order, nearestWarehouse: Warehouse, queueAfter : Boolean = true): Any = {
      val load: List[String] = drone.load(nearestSmallOrder, nearestWarehouse)
      commands ++= load
      val deliver: List[String] = drone.deliver(nearestSmallOrder)
      commands ++= deliver

      if (drone.isAvailable && queueAfter) queue += drone
    }

    while (smallOrders.nonEmpty && queue.nonEmpty) {
      println(s"small orders ${smallOrders.size}, queue ${queue.size}")

      val drone = queue.head
      queue -= drone

      val nearestSmallOrder = drone.findNearestOrder(smallOrders)
      if (nearestSmallOrder != null) {
        val nearestWarehouse = drone.fineNearestWarehouse(warehouses, nearestSmallOrder)
        if (nearestWarehouse != null) {
          processOrder(drone, nearestSmallOrder, nearestWarehouse)
        } else {
          // this order needs products from at least 2 warehouses
          // TODO find list of warehouses for this order
          // theses warehouses should near to the drone
          println("several warehouses")

          val nearestWarehouses: List[Warehouse] = drone.findNearestWarehouses(warehouses, nearestSmallOrder)
          nearestWarehouses.foreach(warehouse => processOrder(drone, nearestSmallOrder, warehouse, queueAfter = false))

          if (drone.isAvailable) queue += drone
        }

        smallOrders -= nearestSmallOrder
      }
    }

    commands
  }

  for (fileName <- Array("busy_day.in"/*, "mother_of_all_warehouses.in", "redundancy.in"*/)) {
    println("FILE " + fileName)
    Main.parseInput("input/" + fileName)
    Main.generateOutput("output/" + fileName.replace(".in", ".out"), Main.generateCommands())
  }
}
