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
  // TODO - Part 2 Step 4
  def parsePhrases() : ExprTree =
    println(curToken)
    if curToken == BONJOUR then readToken()
    println(curToken)
    if curToken == JE then
      readToken()
      if curToken == ETRE then
        readToken()
        if curToken == ASSOIFFE then
          readToken()
          Thirsty
        else if curToken == AFFAME then
          readToken()
          Hungry  
        else if curToken == PSEUDO then
          Identify(eat(PSEUDO))
        else expected(ASSOIFFE, AFFAME, PSEUDO)
      else if curToken == VOULOIR then
        println(curToken)
        parseVouloir()
      else if curToken == ME then
        readToken()
        eat(APPELLER)
        Identify(eat(PSEUDO))
      else expected(ETRE, VOULOIR, ME)
    else if curToken == QUEL then
      eat(ETRE)
      eat(LE)
      eat(PRIX)
      eat(DE)
      parseCommand()
    else if curToken == COMBIEN then
      eat(COMBIEN)
      eat(COUTER)
      parseCommand()
    else expected(BONJOUR, JE, QUEL, COMBIEN)



  def parseVouloir() : ExprTree = 
    println("parseVouloir")
    println(curToken)
    eat(VOULOIR)
    if curToken == COMMANDER then
      eat(COMMANDER)
      parseCommand()
    else if curToken == CONNAITRE then
      readToken()
      eat(MON) 
      eat(SOLDE)
      GetBalance
    else expected(COMMANDER, CONNAITRE)
    
  //def parse
  def parseCommand() : ExprTree =
    println("parseCommand")
    println(curToken)
    if curToken == NUM then
      println(curToken)
      var quantity = eat(NUM).toInt
      println(curToken)
      var product = eat(PRODUIT)
      var brand = curToken match
        case MARQUE => eat(MARQUE)
        case _ => ""
      println(curToken)
      if curToken == ET then
        readToken()
        quantity += eat(NUM).toInt
        product = eat(PRODUIT)
        brand = eat(MARQUE)
        parseCommand()
      else if curToken == OU then
        readToken()
        quantity += eat(NUM).toInt
        product = eat(PRODUIT)
        brand = eat(MARQUE)
        parseCommand()
      else if curToken == EOL then
        println("end of line")
        readToken()
        GetPrice(Product(product, brand, quantity))
        Order(Product(product, brand, quantity))
      else expected(ET, OU, EOL)
    else expected(NUM)


      



