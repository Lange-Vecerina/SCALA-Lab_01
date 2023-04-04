package Data

trait ProductService:
  type BrandName = String
  type ProductName = String

  def getPrice(product: ProductName, brand: BrandName): Double
  def getDefaultBrand(product: ProductName): BrandName

class ProductImpl extends ProductService:
  // TODO - Part 2 Step 
  private val products : Map[ProductName, Map[BrandName, Double]] = Map(
    "biere" -> Map(
      "boxer" -> 1.00,
      "farmer" -> 1.00,
      "wittekop" -> 2.00,
      "punkipa" -> 3.00,
      "jackhammer" -> 3.00,
      "tenebreuse" -> 4.00,
    ),
    "croissant" -> Map(
      "maison" -> 2.00,
      "cailler" -> 2.00,
    )
  )

  def getPrice(product: ProductName, brand: String): Double = 
    products.get(product).flatMap(_.get(brand)).getOrElse(0.0)
  def getDefaultBrand(product: ProductName): BrandName = 
    products.get(product).map(_.head._1).getOrElse("")
end ProductImpl


// test
@main def testProductService(): Unit =
  val productService = ProductImpl()
  println(productService.getPrice("biere", "boxer"))
  println(productService.getPrice("biere", "farmer"))
  println(productService.getPrice("biere", "wittekop"))
  println(productService.getPrice("biere", "punkipa"))
  println(productService.getPrice("biere", "jackhammer"))
  println(productService.getPrice("biere", "tenebreuse"))
  println(productService.getPrice("croissant", "maison"))
  println(productService.getPrice("croissant", "cai