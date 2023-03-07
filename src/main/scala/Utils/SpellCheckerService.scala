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
  // TODO - Part 1 Step 2
  def stringDistance(s1: String, s2: String): Int = 
    val memo = collection.mutable.Map[(Int, Int), Int]()
    def stringDistanceRec(i: Int, j: Int): Int =
      if memo.contains((i, j)) then memo((i, j))
      else
        val res = (i, j) match
          case (0, _) => j
          case (_, 0) => i
          case _ =>
            val cost = if s1(i - 1) == s2(j - 1) then 0 else 1
            val deletion = stringDistanceRec(i - 1, j) + 1
            val insertion = stringDistanceRec(i, j - 1) + 1
            val substitution = stringDistanceRec(i - 1, j - 1) + cost
            deletion min insertion min substitution
        memo((i, j)) = res
        res
    end stringDistanceRec
    stringDistanceRec(s1.length, s2.length)
  end stringDistance

  // TODO - Part 1 Step 2
  def getClosestWordInDictionary(misspelledWord: String): String = 
    if misspelledWord.forall(_.isDigit) then misspelledWord
    else if misspelledWord.startsWith("_") then misspelledWord
    else
      val closestWord = dictionary.keys.minBy(word => stringDistance(word, misspelledWord))
      dictionary(closestWord)
  end getClosestWordInDictionary
end SpellCheckerImpl
