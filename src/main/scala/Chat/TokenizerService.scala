package Chat

import Chat.Token.*
import Utils.SpellCheckerService
import Utils.Dictionary.dictionary

class TokenizerService(spellCheckerSvc: SpellCheckerService):
  /**
    * Separate the user's input into tokens
    * @param input The user's input
    * @return A Tokenizer which allows iteration over the tokens of the input
    */
  // TODO - Part 1 Step 3
  def tokenize(input: String): Tokenized = {

    //removing punctuation and lowercasing
    val inputWithoutPunctuationLowerCase = input.replaceAll("[.,;:!?*]", "").replaceAll("[\\']", " ").toLowerCase()
 
    //splitting the input on whitespaceS
    val tokens = inputWithoutPunctuationLowerCase.split(" ")

    //mapping the tokens to their corresponding token, if the token is not in the dictionary, it is replaced by the closest word in the dictionary
    val tokenized = tokens.map(token => {
      if(stringToTokenTuple(token)._2 == UNKNOWN) {
        val corrected = spellCheckerSvc.getClosestWordInDictionary(token) 
            stringToTokenTuple(corrected)
        } else {
          stringToTokenTuple(token)
        }
    })
    new TokenizedImpl(tokenized)
  }

  /**
    * Convert a string to a token
    * @param token The string to convert
    * @return A tuple that contains the string value of the current token, and the identifier of the token
    */
  def stringToTokenTuple(token: String): (String, Token) = {
    token match {
      case "bonjour" => ("bonjour", BONJOUR)
      case "je" => ("je", JE)
      case "svp" => ("svp", SVP)
      case "assoiffe" => ("assoiffe", ASSOIFFE)
      case "affame" => ("affame", AFFAME)
      case "etre" => ("etre", ETRE)
      case "vouloir" => ("vouloir", VOULOIR)
      case "et" => ("et", ET)
      case "ou" => ("ou", OU)
      case "croissant" => ("croissant", PRODUIT)
      case "biere" => ("biere", PRODUIT)
      case number if number.matches("[0-9]+") => (number, NUM)
      case pseudo if pseudo.matches("_[a-zA-Z]+") => (pseudo, PSEUDO)
      case _ => (token, UNKNOWN)
    }
  }
end TokenizerService
