package datastructure

import scala.util.control.Breaks._

/**
  * Created by duccao on 12/02/16.
  */
class Warehouse extends ProductContainer {
  override def toString = s"Warehouse(id $id, ($x, $y), products = $products)"

  def canServe(order: Order): Boolean = {
    var canServe = true
    breakable {
      order.products.foreach { onDemandProduct =>
        canServe = this.products
          .count(product => product.equals(onDemandProduct) && product.quantity >= onDemandProduct.quantity) > 0
        if (!canServe) break
      }
    }

    canServe
  }
}
