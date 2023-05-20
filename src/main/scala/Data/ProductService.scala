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

    // if the brand in not in the map, we throw an exception
    if !products.contains((product, brandName)) then
      throw new IllegalArgumentException(s"Product $product with brand $brandName does not exist")

    products((product, brandName))

  def getDefaultBrand(product: ProductName): BrandName = 
    if !defaultBrands.contains(product) then
      throw new IllegalArgumentException(s"Product $product does not exist")

    defaultBrands(product)

end ProductImpl
