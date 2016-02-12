import scala.collection.mutable.ListBuffer
import scala.io.Source

/**
  * Created by duccao on 12/02/16.
  */
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

  // Parse input
  val filename = "input/sample.in"
  var index = 0
  var warehouseStartIndex = 3
  var orderStartIndex = 0
  var warehouseIndex = 0
  var orderIndex = 0
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
        val array = line.split(" ").map(_.toInt)
        val x = array(0)
        val y = array(1)
      } else {
        val quantities = line.split(" ").map(_.toInt)
      }

      warehouseIndex += 1
    }
    // orders
    else if (index == orderStartIndex) {
      numOfOrders = line.toInt
    }
    else if (orderStartIndex + 1 <= index && index <= orderStartIndex + numOfOrders * 3) {
      if (orderIndex % 3 == 0) {
        val array = line.split(" ").map(_.toInt)
        val x = array(0)
        val y = array(1)
      } else if (orderIndex % 3 == 2) {
        val quantities = line.split(" ").map(_.toInt)
      }

      orderIndex += 1
    }

    index += 1
  }

}
