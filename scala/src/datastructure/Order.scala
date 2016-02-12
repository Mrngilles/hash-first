package datastructure

/**
  * Created by duccao on 12/02/16.
  */
class Order(var x: Int = 0, var y: Int = 0, var products: List[Product] = List.empty) extends Point(x, y) {
  override def toString = s"Warehouse(($x, $y), products = $products)"
}
