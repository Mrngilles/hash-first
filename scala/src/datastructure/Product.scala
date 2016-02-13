package datastructure

/**
  * Created by duccao on 12/02/16.
  */
class Product(var id: Int = -1, var quantity: Int = 0, var weight: Int = 0) {
  override def toString = s"Product(id $id, quantity $quantity, weight $weight)"
}
