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

// TODO - Part 2 Step 1
  def tokenize(input: String): Tokenized = {

    // Cleaning the input
    val cleanedInput = input.replaceAll("[.,;:!?*]", "").replaceAll("\\'", " ").trim.replaceAll("\\s+", " ").toLowerCase()
    
    // Splitting the cleaned input into strings to tokenize
    val tokens = cleanedInput.split(" ")

    // Converting the tokens into a tuple of (String, Token)
    val tokenized = tokens.map(token => {
      if(stringToTokenTuple(token)._2 == UNKNOWN) {
        // If the token is not in the dictionary, we correct it with the spell checker
        val corrected = spellCheckerSvc.getClosestWordInDictionary(token) 
        stringToTokenTuple(corrected) // We return the corrected string's tuple
      } else {
        // If the token is in the dictionary, we return its tuple
        stringToTokenTuple(token)
      }
    })

    // Adding the EOL token at the end of the array
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
      case "quel" => ("quel", QUEL)
      case "le" => ("le", LE)
      case "prix" => ("prix", PRIX)
      case "de" => ("de", DE)
      case "combien" => ("combien", COMBIEN)
      case "je" => ("je", JE)
      case "me" => ("me", ME)
      case "mon" => ("mon", MON)
      case "solde" => ("solde", SOLDE)
      case "svp" => ("svp", SVP)
      case "assoiffe" => ("assoiffe", ASSOIFFE)
      case "affame" => ("affame", AFFAME)
      //Actions
      case "etre" => ("etre", ETRE)
      case "vouloir" => ("vouloir", VOULOIR)
      case "commander" => ("commander", COMMANDER)
      case "connaitre" => ("connaitre", CONNAITRE)
      case "couter" => ("coûter", COUTER)
      case "appeler" => ("appeler", APPELLER)
      // Logic Operators
      case "et" => ("et", ET)
      case "ou" => ("ou", OU)
      // Products
      case "produit" => ("produit", PRODUIT)
      case "marque" => ("marque", MARQUE)
      case "croissant" => ("croissant", PRODUIT)
      case "biere" => ("biere", PRODUIT)
      case "punkipa" => ("punkipa", MARQUE)
      case "boxer" => ("boxer", MARQUE)
      case "farmer" => ("farmer", MARQUE)
      case "tenebreuse" => ("tenebreuse", MARQUE)
      case "jackhammer" => ("jackhammer", MARQUE)
      case "wittekop" => ("wittekop", MARQUE)
      case "maison" => ("maison", MARQUE)
      case "cailler" => ("cailler", MARQUE)

      case number if number.matches("[0-9]+") => (number, NUM)
      case pseudo if pseudo.matches("_[a-zA-Z]+") => (pseudo, PSEUDO)
      case _ => (token, UNKNOWN)
    }
  }
end TokenizerService
