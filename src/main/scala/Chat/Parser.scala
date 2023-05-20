package Chat

class UnexpectedTokenException(msg: String) extends Exception(msg){}

class Parser(tokenized: Tokenized):
  import ExprTree._
  import Chat.Token._

  // Start the process by reading the first token.
  var curTuple: (String, Token) = tokenized.nextToken()

  def curValue: String = curTuple._1
  def curToken: Token = curTuple._2

  /** Reads the next token and assigns it into the global variable curTuple */
  def readToken(): Unit = curTuple = tokenized.nextToken()

  /** "Eats" the expected token and returns it value, or terminates with an error. */
  private def eat(token: Token): String =
    if token == curToken then
      val tmp = curValue
      readToken()
      tmp
    else expected(token)

  /** Complains that what was found was not expected. The method accepts arbitrarily many arguments of type Token */
  private def expected(token: Token, more: Token*): Nothing =
    expected(more.prepended(token))
  private def expected(tokens: Seq[Token]): Nothing =
    val expectedTokens = tokens.mkString(" or ")
    throw new UnexpectedTokenException(s"Expected: $expectedTokens, found: $curToken")

  /** the root method of the parser: parses an entry phrase */
  def parsePhrases() : ExprTree =
    if curToken == BONJOUR then readToken()
    if curToken == JE then
      eat(JE)
      if curToken == ETRE then
        eat(ETRE)
        curToken match
          case ASSOIFFE => eat(ASSOIFFE); Thirsty
          case AFFAME => eat(AFFAME); Hungry
          case PSEUDO => Identify(eat(PSEUDO))
          case _ => expected(ASSOIFFE, AFFAME, PSEUDO)
      else if curToken == VOULOIR then
        parseVouloir()
      else if curToken == ME then
        eat(ME)
        eat(APPELLER)
        Identify(eat(PSEUDO))
      else expected(ETRE, VOULOIR, ME)
    else if curToken == QUEL then
      readToken()
      eat(ETRE)
      eat(LE)
      eat(PRIX)
      eat(DE)
      val command = parseCommand()
      GetPrice(command)
    else if curToken == COMBIEN then
      eat(COMBIEN)
      eat(COUTER)
      val command = parseCommand()
      GetPrice(command)
    else expected(BONJOUR, JE, QUEL, COMBIEN)


  /** Intermediate method called when parsing the 'Vouloir' command
   *  Parses the following token : COMMANDER, CONNAITRE
   */
  def parseVouloir() : ExprTree = 
    eat(VOULOIR)
    if curToken == COMMANDER then
      eat(COMMANDER)
      val command = parseCommand()
      Order(command)
    else if curToken == CONNAITRE then
      readToken()
      eat(MON) 
      eat(SOLDE)
      GetBalance
    else expected(COMMANDER, CONNAITRE)
    

  /** method called for parsing a product. eats NUM, PRODUIT and MARQUE (if it exists)
   *  to give it as parameters in Product.
   */
  def parseProduct() : ExprTree =
    val quantity = eat(NUM).toInt
    val product = eat(PRODUIT)
    if curToken == MARQUE then
      val brand = eat(MARQUE)
      Product(product, brand, quantity)
    else Product(product, "", quantity)

  /** method called for parsing a 'command' 
   *  parses the following token : ET, OU, EOL.
   *  returns first if there is one product, or a tree of And or Or if there is more than one product.
   */
  def parseCommand() : ExprTree =
    val first = parseProduct()
    curToken match
      case ET => 
        eat(ET)
        val second = parseCommand()
        And(first, second)
      case OU => 
        eat(OU)
        val second = parseCommand()
        Or(first, second)
      case EOL => 
        eat(EOL)
        first
      case _ => expected(ET, OU, EOL)

end Parser


      



