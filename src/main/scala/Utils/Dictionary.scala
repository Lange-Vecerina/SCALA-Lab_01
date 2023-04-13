package Utils

/**
* Contains the dictionary of the application, which is used to validate, correct and normalize words entered by the
* user.
*/
object Dictionary:
  // This dictionary is a Map object that contains valid words as keys and their normalized equivalents as values (e.g.
  // we want to normalize the words "veux" and "aimerais" in one unique term: "vouloir").
  val dictionary: Map[String, String] = Map(
    "bonjour" -> "bonjour",
    "hello" -> "bonjour",
    "yo" -> "bonjour",
    "je" -> "je",
    "j" -> "je",
    "suis" -> "etre",
    "veux" -> "vouloir",
    "aimerais" -> "vouloir",
    "assoiffé" -> "assoiffe",
    "assoiffée" -> "assoiffe",
    "affamé" -> "affame",
    "affamée" -> "affame",
    "bière" -> "biere",
    "bières" -> "biere",
    "croissant" -> "croissant",
    "croissants" -> "croissant",
    "et" -> "et",
    "ou" -> "ou",
    "svp" -> "svp",
    "stp" -> "svp",
    // TODO - Part 2 Step 1
    "quel" -> "quel",
    "quelle" -> "quel",
    "quels" -> "quel",
    "quelles" -> "quel",
    "le" -> "le",
    "la" -> "le",
    "les" -> "le",
    "l" -> "le",
    "prix" -> "prix",
    "de" -> "de",
    "d" -> "de",
    "combien" -> "combien",
    "coûte" -> "couter",
    "coûter" -> "couter",
    "coûtent" -> "couter",
    "mon" -> "mon",
    "ma" -> "mon",
    "me" -> "me",
    "m" -> "me",
    "solde" -> "solde",
    "connaître" -> "connaitre",
    "commander" -> "commander",
    "commandé" -> "commander",
    "commandée" -> "commander",
    "appeler" -> "appeler",
    "appelé" -> "appeler",
    "appelle" -> "appeler"
  )
end Dictionary
