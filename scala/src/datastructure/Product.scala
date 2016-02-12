package datastructure

/**
  * Created by duccao on 12/02/16.
  */
class Product(var id: Int = -1, var quantity: Int = 0) {
  override def toString = s"Product(id $id, quantity $quantity)"


  def canEqual(other: Any): Boolean = other.isInstanceOf[Product]

  override def equals(other: Any): Boolean = other match {
    case that: Product =>
      (that canEqual this) &&
        id == that.id
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(id)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}
