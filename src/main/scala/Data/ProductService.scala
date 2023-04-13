package Data

import scala.collection.immutable.SortedMap

trait ProductService:
  type BrandName = String
  type ProductName = String

  def getPrice(product: ProductName, brand: BrandName): Double
  def getDefaultBrand(product: ProductName): BrandName

class ProductImpl extends ProductService:
  // Store in a map the price of each product for each brand
  // If we have a lot of products, we should use a database
  private val products : Map[(ProductName, BrandName), Double] = Map(
    ("biere", "boxer") -> 1.00,
    ("biere", "farmer") -> 1.00,
    ("biere", "wittekop") -> 2.00,
    ("biere", "punkipa") -> 3.00,
    ("biere", "jackhammer") -> 3.00,
    ("biere", "tenebreuse") -> 4.00,
    ("croissant", "maison") -> 2.00,
    ("croissant", "cailler") -> 2.00,
  )

  private val defaultBrands : Map[ProductName, BrandName] = Map(
    "biere" -> "boxer",
    "croissant" -> "maison"
  )

  def getPrice(product: ProductName, brand: String): Double = 
    // if the brand is not specified, we use the default brand
    val brandName = if brand.isEmpty then getDefaultBrand(product) else brand
    products.get((product, brandName)).getOrElse(0.0)

  def getDefaultBrand(product: ProductName): BrandName = 
    defaultBrands.get(product).getOrElse("")

end ProductImpl
