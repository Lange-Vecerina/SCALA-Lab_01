package Chat

import Chat.Token.*
import Utils.SpellCheckerService

trait Tokenized:
  /**
    * Get the next token of the user input, or EOL if there is no more token.
    * @return a tuple that contains the string value of the current token, and the identifier of the token
    */
  def nextToken(): (String, Token)

  private var index = 0

object Tokenized {
  private var counter = 0
  def increment = {
    counter += 1
    counter
  }
  def reset = {
    counter = 0
  }
}
class TokenizedImpl(val tokens: Array[(String, Token)]) extends Tokenized:
  // TODO - Part 1 Step 3
   
   
   def nextToken(): (String, Token) = {
    var index = Tokenized.increment -1
    if (tokens.isEmpty || index >= tokens.length) {
      Tokenized.reset
      ("EOL", EOL)
    } else {
      val (str, token) = tokens(index)
      (str, token)
    }
  } 
end TokenizedImpl
