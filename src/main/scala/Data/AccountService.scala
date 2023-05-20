package Data

import scala.collection.mutable

trait AccountService:
  /**
    * Retrieve the balance of a given account
    * @param user the name of the user whose account will be retrieve
    * @return the current balance of the user
    */
  def getAccountBalance(user: String): Double

  /**
    * Add an account to the existing accounts
    * @param user the name of the user
    * @param balance the initial balance value
    */
  def addAccount(user: String, balance: Double): Unit

  /**
    * Indicate is an account exist
    * @param user the name of the user whose account is checked to exist
    * @return whether the account exists or not
    */
  def isAccountExisting(user: String): Boolean

  /**
    * Update an account by decreasing its balance.
    * @param user the name of the user whose account will be updated
    * @param amount the amount to decrease
    * @return the new balance
    */
  def purchase(user: String, amount: Double): Double

class AccountImpl extends AccountService:
  // Store in a map the balance of each account
  // If we have a lot of accounts, we should use a database
  private val accounts: mutable.Map[String, Double] = mutable.Map.empty

  def getAccountBalance(user: String): Double = 
    if !isAccountExisting(user) then
      throw new IllegalArgumentException(s"Account $user does not exist")
    accounts(user)
  def addAccount(user: String, balance: Double): Unit = 
    if isAccountExisting(user) then
      throw new IllegalArgumentException(s"Account $user already exists")
    accounts.put(user, balance)
  def isAccountExisting(user: String): Boolean = accounts.contains(user)
  def purchase(user: String, amount: Double): Double = 
    val balance = getAccountBalance(user)
    val newBalance = balance - amount // check if balance is enough is done by the caller
    accounts.put(user, newBalance)
    newBalance

end AccountImpl
