package datastructure

/**
  * Created by duccao on 12/02/16.
  */
class Warehouse(var id: Int = -1, var products: List[Product] = List.empty) extends Point {
  override def toString = s"Warehouse(id $id, ($x, $y), products = $products)"
}
