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

  /**
    * prepare the different products in parallel, and return the result of the preparation.
    * This means it will parcours the tree, and for each different product, it will prepare it in parallel. 
    *
    * @param t the tree to prepare.
    * @return a future tree with the products that could be prepared.
    */
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

  /**
    * prepare a product sequentially, and return the result of the preparation. Called by prepareProducts.
    *
    * @param name the name of the product
    * @param brand the brand of the product
    * @param quantity the quantity of the product
    * @return A product with the name, brand and quantity given in parameter that succeeded to be prepared.
    */
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
      .flatMap(qty => qty match {
        case 0 => Future.failed(new Exception("Product not available"))
        case _ => Future.successful(Product(name, brand, qty))
      })
  }


  /**
    * Return the output text of the current node, in order to write it in console.
    * @return the output text of the current node
    */
  def reply(session: Session, callback: () => Unit = () => {})(t: ExprTree): String =
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
          case Some(user) => s"Le montant actuel de votre solde est de CHF ${accountSvc.getAccountBalance(user)}."
        }
      }

      // Product cases : get price and order.
      case GetPrice(products) => s"Cela coûte CHF ${computePrice(products)}."
      case Order(products) => {
        session.getCurrentUser match {
          case None => "Veuillez d'abord vous identifier."
          case Some(user) => {
            // If user is too poor, return error message
            if accountSvc.getAccountBalance(user) < computePrice(products) then
              s"Vous n'avez pas assez d'argent sur votre compte pour effectuer cette commande."

            // Prepare products. Wait of the preparation to be done to get the result.
            val prepared = prepareProducts(products)

            // If prepared successfully, get the price and purchase
            prepared.transform {
              case Success(preparedProducts) => {
                val price = computePrice(preparedProducts)
                // If prepared successfully, get the price and purchase
                if preparedProducts == products then 
                  messageSvc.add("BotTender", 
                    s"La commande de ${reply(session)(products)} est prête ! Cela coûte CHF ${computePrice(preparedProducts)} et votre nouveau solde est de CHF ${accountSvc.purchase(user, price)}.",
                    Some(user))
                  callback() //callback used to update the UI
                else 
                  // If not all products are available, return the message with the available products
                  messageSvc.add("BotTender", 
                    s"La commande de ${reply(session)(products)} est partiellement prête! Voici ${reply(session)(preparedProducts)}. Cela coûte CHF ${computePrice(preparedProducts)} et votre nouveau solde est de CHF ${accountSvc.purchase(user, price)}.",
                    Some(user)) 
                  callback() //callback used to update the UI
                Success(preparedProducts)
              }
              case Failure(_) => {
                messageSvc.add("BotTender", 
                  s"La commande de ${reply(session)(products)} ne peut pas être délivrée.",
                  Some(user))
                callback() //callback used to update the UI
                Failure(new Exception("Error while preparing products"))
              }
            }
            // returned directly in opposition of the the commands. The commands are only returned if the future is completed.
            s"Votre commande est en cours de préparation."
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
