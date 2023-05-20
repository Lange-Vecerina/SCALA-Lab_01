package Utils

/**
  * Contains the function necessary to calculate the number of *clinks* when n people want to cheers.
  */
object ClinksCalculator:
  /**
    * Calculate the factorial of a given number
    * @param n the number to compute
    * @return n!
    */
  def factorial(n: Int): BigInt = {
    // if n is smaller than 0, throw an exception
    if n < 0 then
      throw new IllegalArgumentException("n must be greater than 0")
    
    n match
      case 0 => 1
      case _ => n * factorial(n-1) 
  }

  /**
    * Calculate the combination of two given numbers
    * @param n the first number
    * @param k the second number
    * @return n choose k
    */
  def calculateCombination(n: Int, k: Int): Int = {
    if k > n then
      return 0

    val result = (factorial(n) / (factorial(k) * factorial(n -k))).toInt
    result
  }
end ClinksCalculator
