import org.scalatest.*
import org.scalatest.matchers.should
import org.scalatest.propspec.AnyPropSpec
import prop.*

import java.io.ByteArrayOutputStream
import Utils.{Dictionary, SpellCheckerService, SpellCheckerImpl}
import Chat.TokenizerService

class BotTenderTokenizerInputSuite extends AnyPropSpec with TableDrivenPropertyChecks with should.Matchers {
    val spellCheckerSvc: SpellCheckerService = new SpellCheckerImpl(Dictionary.dictionary)
    val tokenizerSvc: TokenizerService = new TokenizerService(spellCheckerSvc)
    
    val evaluateInput = MainTokenizer.evaluateInput(tokenizerSvc)

    // You can use this test to debug any input
    property("inputting") {
        evaluateInput("quitter")
    }

    property("inputting 'quitter'") {
        // capture output for testing therefore it is not shown in the terminal
        val outCapture = new ByteArrayOutputStream
        Console.withOut(outCapture) {
            evaluateInput("quitter") should equal(false)
        }
        outCapture.toString() should include ("Adieu.")
    }

    property("inputting 'santé !'") {
        evaluateInput("santé !") should equal(true)
    }

    // TESTS FOR stringDistance
    property("two identical strings") {
        spellCheckerSvc.stringDistance("test", "test") should equal(0)
    }

    property("two empty strings") {
        spellCheckerSvc.stringDistance("", "") should equal(0)
    }

    property("two completely different strings") {
        spellCheckerSvc.stringDistance("hello", "goodbye") should equal(7)
    }

    property("two strings with one letter difference") {
        spellCheckerSvc.stringDistance("hello", "hella") should equal(1)
    }

    property("two strings that are the same except one has extra characters") {
        spellCheckerSvc.stringDistance("apple", "applesauce") should equal(5)
    }

    property("two strings that are almost the same but with some missing characters:") {
        spellCheckerSvc.stringDistance("beach", "bach") should equal(1)
    }
}
