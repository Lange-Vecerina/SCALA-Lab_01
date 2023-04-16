package Chat

/**
  * This sealed trait represents a node of the tree.
  */
sealed trait ExprTree

/**
  * Declarations of the nodes' types.
  */
object ExprTree:
  // TODO - Part 2 Step 3  
  // Example cases
  case object Hello extends ExprTree
  case object Thirsty extends ExprTree
  case object Hungry extends ExprTree
  case object GetBalance extends ExprTree
  case class GetPrice(products: ExprTree) extends ExprTree
  case class Order(products: ExprTree) extends ExprTree
  case class Identify(pseudo: String) extends ExprTree
  case class And(left: ExprTree, right: ExprTree) extends ExprTree
  case class Or(left: ExprTree, right: ExprTree) extends ExprTree
  case class Product(name: String, brand: String, quantity: Int) extends ExprTree


end ExprTree
