package datastructure

import scala.collection.mutable.ListBuffer

/**
  * Created by duccao on 12/02/16.
  */
class Warehouse extends ProductContainer {
  override def toString = s"Warehouse(id $id, ($x, $y), products = $products)"

  /**
    * @param onDemandProducts
    * @return list supplied products and remaining products
    */
  def supply(onDemandProducts: Iterable[Product]): (List[Product], List[Product]) = {
    val remainingProducts = new ListBuffer[Product]
    val suppliedProducts = new ListBuffer[Product]
    onDemandProducts.foreach { product =>
      if (this.products.contains(product.id) && this.products(product.id).quantity > 0) {
        val inStockQuantity = this.products(product.id).quantity
        val onDemandQuantity = product.quantity

        // warehouse doesn't have enough quantity to supply
        if (inStockQuantity < onDemandQuantity) {
          remainingProducts += new Product(product.id, onDemandQuantity - inStockQuantity, product.weight)
          suppliedProducts += new Product(product.id, inStockQuantity, product.weight)
        } else {
          suppliedProducts += new Product(product.id, onDemandQuantity, product.weight)

          // update quantity of this product in stock
          this.products += (product.id -> new Product(product.id, inStockQuantity - onDemandQuantity, product.weight))
        }
      } else {
        remainingProducts += product
      }
    }

    (suppliedProducts.toList, remainingProducts.toList)
  }
}
