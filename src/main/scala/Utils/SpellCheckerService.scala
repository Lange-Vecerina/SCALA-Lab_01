package Utils

trait SpellCheckerService:
  /**
    * This dictionary is a Map object that contains valid words as keys and their normalized equivalents as values (e.g.
    * we want to normalize the words "veux" and "aimerais" in one unique term: "vouloir").
    */
  val dictionary: Map[String, String]

  /**
    * Calculate the Levenstein distance between two words.
    * @param s1 the first word
    * @param s2 the second word
    * @return an integer value, which indicates the Levenstein distance between "s1" and "s2"
    */
  def stringDistance(s1: String, s2: String): Int

  /**
    * Get the syntactically closest word in the dictionary from the given misspelled word, using the "stringDistance"
    * function. If the word is a number or a pseudonym, this function just returns it.
    * @param misspelledWord the mispelled word to correct
    * @return the closest normalized word from "mispelledWord"
    */
  def getClosestWordInDictionary(misspelledWord: String): String
end SpellCheckerService

class SpellCheckerImpl(val dictionary: Map[String, String]) extends SpellCheckerService:
  // Levenstein distance algorithm : This algorithm is used to calculate the distance between two words.
  def stringDistance(s1: String, s2: String): Int = 
    // Map to store the results of the recursive function "stringDistanceRec" to avoid recalculating the same substrings
    val memo = collection.mutable.Map[(Int, Int), Int]()

    // Recursive function to calculate the Levenstein distance between two words
    def stringDistanceRec(i: Int, j: Int): Int =
      // If the substring has already been calculated, we just return the result
      if memo.contains((i, j)) then memo((i, j))
      else
        // Otherwise, we calculate the distance between the two words
        val res = (i, j) match
          case (0, _) => j // If one of the words is empty, the distance is the length of the other word
          case (_, 0) => i // If one of the words is empty, the distance is the length of the other word
          case _ =>  // Otherwise, we calculate the distance between the two words
            val cost = if s1(i - 1) == s2(j - 1) then 0 else 1 // If the last characters are the same, the cost is 0, otherwise it is 1
            val deletion = stringDistanceRec(i - 1, j) + 1 // We calculate the distance between the two words by removing the last character of the first word
            val insertion = stringDistanceRec(i, j - 1) + 1 // We calculate the distance between the two words by removing the last character of the second word
            val substitution = stringDistanceRec(i - 1, j - 1) + cost // We calculate the distance between the two words by removing the last character of both words
            deletion min insertion min substitution // We return the minimum distance between the two words between the three possibilities
        memo((i, j)) = res // We store the result in the map to avoid recalculating the same substrings
        res // We return the result
    end stringDistanceRec
    stringDistanceRec(s1.length, s2.length) // We call the recursive function
  end stringDistance

  // Get the closest word in the dictionary from the given misspelled word
  def getClosestWordInDictionary(misspelledWord: String): String = 
    // If the word is a number or a pseudonym, we just return it
    if misspelledWord.forall(_.isDigit) then misspelledWord
    else if misspelledWord.startsWith("_") then misspelledWord
    else
      // Otherwise, we get the closest word in the dictionary from the given misspelled word
      val closestWord = dictionary.keys.minBy(word => stringDistance(word, misspelledWord))
      // If the distance between the closest word and the misspelled word is greater than 2, we return the misspelled word
      dictionary(closestWord)
  end getClosestWordInDictionary
end SpellCheckerImpl
