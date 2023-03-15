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
    val inputWithoutPunctuationLowerCase = input.replaceAll("[.,;:!?*]", "").replaceAll("[\\']", " ").toLowerCase()
 
    println(inputWithoutPunctuationLowerCase)
    val tokens = inputWithoutPunctuationLowerCase.split(" ")

    //tokens.foreach(println)

    val tokenized = tokens.map(token => {
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
        case _ => {
          
          val closestToken = spellCheckerSvc.getClosestWordInDictionary(token)
          //println("correcting: " + token + " -> " + closestToken)
          if (closestToken.matches("[0-9]+")) {
            (token, NUM)
          } else if (closestToken.matches("_[a-zA-Z]+")) {
            (closestToken, PSEUDO)
          } else {
            closestToken match {
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
            case _ => (closestToken, UNKNOWN)
            }     
          }
        }
      }
    })
    new TokenizedImpl(tokenized)
  }
end TokenizerService
