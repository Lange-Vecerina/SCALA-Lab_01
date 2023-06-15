package Chat
import Data.{AccountService, ProductService, Session}
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import Utils.FutureOps.*
import concurrent.duration.*
import Data.MessageService
import scalatags.Text.all.stringFrag

implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

class AnalyzerService(productSvc: ProductService,
                      accountSvc: AccountService,
                      messageSvc: MessageService):
  import ExprTree._
  /**
    * Compute the price of the current node, then returns it. If the node is not a computational node, the method
    * returns 0.0.
    * For example if we had a "+" node, we would add the values of its two children, then return the result.
    * @return the result of the computation
    */
  def computePrice(t: ExprTree): Double = t match {
    case GetPrice(products) => computePrice(products)
    case Order(products) => computePrice(products)
    case And(left, right) => computePrice(left) + computePrice(right)
    case Or(left, right) => computePrice(left).min(computePrice(right))
    case Product(name, brand, quantity) => productSvc.getPrice(name, brand) * quantity
    case _ => 0.0
  }

  def prepareProducts(t: ExprTree): Future[ExprTree] = t match {
  case And(left, right) =>
    val preparedLeftFuture = prepareProducts(left)
    val preparedRightFuture = prepareProducts(right)
    preparedLeftFuture
      .flatMap(pLeft => preparedRightFuture
        .map(pRight => And(pLeft, pRight))
        .recover(_ => pLeft))
      .recoverWith(_ => preparedRightFuture)
  case Or(left, right) =>
    val preparedLeftFuture = prepareProducts(left)
    val preparedRightFuture = prepareProducts(right)
    preparedLeftFuture
      .flatMap(pLeft => preparedRightFuture
        .map(pRight => And(pLeft, pRight))
        .recover(_ => pLeft))
      .recoverWith(_ => preparedRightFuture)
  case Product(name, brand, quantity) =>
    prepareProductSequentially(name, brand, quantity)
  case _ => Future.successful(t)
  }

  def prepareProductSequentially(name: String, brand: String, quantity: Int): Future[ExprTree] = {
    // function to prepare a product
    def prepareOne(qtyDone : Int, qtyLeft : Int): Future[Int] = {
      if qtyLeft == 0 then Future.successful(qtyDone)
      else 
        randomSchedule(1.second, 0.5.second, 0.8)
          .flatMap(_ => prepareOne(qtyDone + 1, qtyLeft - 1))
          .recoverWith{case _ => prepareOne(qtyDone, qtyLeft - 1)}
    }
    // call the function
    prepareOne(0, quantity)
      .map(_ => Product(name, brand, quantity))
  }


  /**
    * Return the output text of the current node, in order to write it in console.
    * @return the output text of the current node
    */
  def reply(session: Session)(t: ExprTree): String =
    // you can use this to avoid having to pass the session when doing recursion
    val inner: ExprTree => String = reply(session)
    t match
      // Entry cases
      case Thirsty => "Eh bien, la chance est de votre côté, car nous offrons les meilleures bières de la région !"
      case Hungry => "Pas de soucis, nous pouvons notamment vous offrir des croissants faits maisons !"
      case Hello => "Bonjour !"

      // User cases : Identification and balance
      case Identify(pseudo) => {
        /* Commented to prevent bot command from impacting session user
        if !accountSvc.isAccountExisting(pseudo) then
          accountSvc.addAccount(pseudo, 30.0)
        session.setCurrentUser(pseudo)*/
        s"Bonjour, ${pseudo.tail} !"
      }
      case GetBalance => {
        session.getCurrentUser match {
          case None => "Veuillez d'abord vous identifier."
          case Some(user) => s"Le montant acutel de votre solde est de CHF ${accountSvc.getAccountBalance(user)}."
        }
      }

      // Product cases : get price and order.
      case GetPrice(products) => s"Cela coûte CHF ${computePrice(products)}."
      case Order(products) => {
        session.getCurrentUser match {
          case None => "Veuillez d'abord vous identifier."
          case Some(user) => {
            // Add message saying that order is being prepared
            messageSvc.add("BotTender", "Votre commande est en cours de préparation.", Some(user))

            // Prepare products. If successful, compute price of the value and purchase. If not, return error message.
            val prepared = prepareProducts(products).recover()


            // val price = computePrice(products)
            // if accountSvc.getAccountBalance(user) < price then
            //   s"Vous n'avez pas assez d'argent sur votre compte pour effectuer cette commande."
            // else {
            //   s"Voici donc ${reply(session)(products)} ! Cela coûte CHF ${computePrice(products)} et votre nouveau solde est de CHF ${accountSvc.purchase(user, price)}."
            // }
          }
        }
      }
      
      // Operator cases for more than one product.
      case And(left, right) => s"${reply(session)(left)} et ${reply(session)(right)}"
      case Or(left, right) => if computePrice(left) < computePrice(right) then reply(session)(left) else reply(session)(right)

      // Product cases. Different types since the example given does not show 'biere' when there is a brand but 'croissant' when there is a type of croissant
      case Product("biere", "", quantity) => s"$quantity ${productSvc.getDefaultBrand("biere")}"
      case Product("biere", brand, quantity) => s"$quantity $brand"
      case Product(name, "", quantity) => s"$quantity $name ${productSvc.getDefaultBrand(name)}"
      case Product(name, brand, quantity) => s"$quantity $name $brand"
      case _ => "Je n'ai pas compris votre demande, pouvez-vous reformuler ?"
end AnalyzerService
