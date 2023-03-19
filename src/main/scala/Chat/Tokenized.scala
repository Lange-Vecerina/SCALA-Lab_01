package Chat

import Chat.Token.*
import Utils.SpellCheckerService

trait Tokenized:
  /**
    * Get the next token of the user input, or EOL if there is no more token.
    * @return a tuple that contains the string value of the current token, and the identifier of the token
    */
  def nextToken(): (String, Token)

// TokenizedImpl is a class that implements the Tokenized trait.
// It is used to tokenize the user's input.
// It contains an array of tuples that contains the string value of the current token, and the identifier of the token.
class TokenizedImpl(val tokens: Array[(String, Token)]) extends Tokenized:
  var index = 0 // The index of the current token

  /**
    * Implementation of the nextToken method of the Tokenized trait.
    * @return a tuple that contains the string value of the current token, and the identifier of the token
    *        or EOL if there is no more token.
    * @see Tokenized.nextToken
    */
  def nextToken(): (String, Token) = {
    if (tokens.isEmpty || index >= tokens.length) { // If there is no more token
      ("EOL", EOL) // We return the EOL token
    } else {
      // Otherwise, we return the current token
      val (str, token) = tokens(index)
      index += 1
      (str, token)
    }
  } 
end TokenizedImpl
