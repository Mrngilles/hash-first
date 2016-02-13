package datastructure

import scala.collection.mutable.Map

/**
  * Created by duccao on 12/02/16.
  */
class ProductContainer(var id: Int = -1, var products: Map[Int, Product] = Map.empty) extends Point {
  override def toString = s"Order(id $id, ($x, $y), products = $products)"

  def totalWeight: Int = {
    var sum = 0
    products.foreach {case (id, product) => sum += product.weight}

    sum
  }

  def totalQuantity(): Int = {
    var total = 0
    products.foreach {case (id, product) => total += product.quantity}

    total
  }
}
