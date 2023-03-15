package Chat

import Chat.Token.*
import Utils.SpellCheckerService

class TokenizerService(spellCheckerSvc: SpellCheckerService):
  /**
    * Separate the user's input into tokens
    * @param input The user's input
    * @return A Tokenizer which allows iteration over the tokens of the input
    */
  // TODO - Part 1 Step 3
  def tokenize(input: String): Tokenized = {
    val tokens = input.split(" ")
    val tokenized = tokens.map(token => {
      token match {
        case "bonjour" => (token, BONJOUR)
        case "je" => (token, JE)
        case "svp" => (token, SVP)
        case "assoiffe" => (token, ASSOIFFE)
        case "affame" => (token, AFFAME)
        case "etre" => (token, ETRE)
        case "vouloir" => (token, VOULOIR)
        case "et" => (token, ET)
        case "ou" => (token, OU)
        case "produit" => (token, PRODUIT)
        case _ => {
          if (token.forall(_.isDigit)) {
            (token, NUM)
          } else if (spellCheckerSvc.isCorrect(token)) {
            (token, PSEUDO)
          } else {
            (token, UNKNOWN)
          }
        }
      }
    })
    
  }
end TokenizerService
