package Web

import Chat.{AnalyzerService, TokenizerService, ExprTree}
import Data.{MessageService, AccountService, SessionService, Session}

import castor.Context.Simple.global // Cannot find an implicit ExecutionContext. Added this line to fix it.

import scalatags.Text.all._
import Chat.Parser
/**
  * Assembles the routes dealing with the message board:
  * - One route to display the home page
  * - One route to send the new messages as JSON
  * - One route to subscribe with websocket to new messages
  *
  * @param log
  */
class MessagesRoutes(tokenizerSvc: TokenizerService,
                     analyzerSvc: AnalyzerService,
                     msgSvc: MessageService,
                     accountSvc: AccountService,
                     sessionSvc: SessionService)(implicit val log: cask.Logger) extends cask.Routes:
    import Decorators.getSession

    // Variable to store the WebSocket connections to notify when a new message is sent
    var connections = Set.empty[cask.WsChannelActor]

    // Method to notify all the connections to update the message board
    def notifyConnections() = connections.foreach(_.send(cask.Ws.Text(Layouts.messageBoard(msgSvc.getLatestMessages(20)).render)))

    // TODO - Part 3 Step 2: Display the home page (with the message board and the form to send new messages)
    @getSession(sessionSvc) // This decorator fills the `(session: Session)` part of the `index` method.
    @cask.get("/")
    def index()(session: Session) =
        val messages = msgSvc.getLatestMessages(20)
        Layouts.homePage(session.getCurrentUser, messages)
        
    
    // TODO - Part 3 Step 4b: Process the new messages sent as JSON object to `/send`. The JSON looks
    //      like this: `{ "msg" : "The content of the message" }`.
    //
    //      A JSON object is returned. If an error occurred, it looks like this:
    //      `{ "success" : false, "err" : "An error message that will be displayed" }`.
    //      Otherwise (no error), it looks like this:
    //      `{ "success" : true, "err" : "" }`
    //
    //      The following are treated as error:
    //      - No user is logged in
    //      - The message is empty
    //
    //      If no error occurred, every other user is notified with the last 20 messages
    // TODO - Part 3 Step 5: Modify the code of step 4b to process the messages sent to the bot (message
    //      starts with `@bot `). This message and its reply from the bot will be added to the message
    //      store together.
    //
    //      The exceptions raised by the `Parser` will be treated as an error (same as in step 4b)
    
    @getSession(sessionSvc)
    @cask.postJson("/send")
    def sendMessage(msg: ujson.Value)(session: Session): ujson.Obj = {
      val currenUser = session.getCurrentUser

      if (currenUser.isEmpty) {
        ujson.Obj("success" -> false, "err" -> "No user is logged in")
      } 
      if (msg.str.isEmpty()) {
        ujson.Obj("success" -> false, "err" -> "The message is empty")
      } 
      
      val user = currenUser.get
      // get mention from message if it starts with @, can be any user or bot
      val mention = if (msg.str.startsWith("@")) Some(msg.str.substring(1, msg.str.indexOf(" "))) else None

      // if mention is bot, get the expression and evaluate it
      if (mention == Some("Bot")) {
        try {
          // get the reply of the bot
          val tokenized = tokenizerSvc.tokenize(msg.str.substring(msg.str.indexOf(" ") + 1))
          val parser = new Parser(tokenized)
          val expr = parser.parsePhrases()
          val reply = analyzerSvc.reply(session)(expr)

          val id = msgSvc.add(user, msg.str, mention, Some(expr))

          // notify for first message
          notifyConnections()

          // process the reply
          msgSvc.add("BotTender", reply, None, None, Some(id))

          // notify for second message
          notifyConnections()

          return ujson.Obj("success" -> true, "err" -> "")

        } catch {
          case e: Exception => return ujson.Obj("success" -> false, "err" -> e.getMessage)
        }
      } 

      msgSvc.add(user, msg.str, mention)
      notifyConnections()
      ujson.Obj("success" -> true, "err" -> "")
    
    }


    // TODO - Part 3 Step 4c: Process and store the new websocket connection made to `/subscribe`

    @cask.websocket("/subscribe")
    def subscribe(): cask.WebsocketResult = {
      cask.WsHandler { channel =>
        connections += channel
        cask.WsActor {
          case cask.Ws.Close(_, _) => connections -= channel
        }
      }
    }
    
    // TODO - Part 3 Step 4d: Delete the message history when a GET is made to `/clearHistory`

    @getSession(sessionSvc)
    @cask.get("/clearHistory")
    def clearHistory(): Unit = {
      msgSvc.deleteHistory()
      notifyConnections()
    }

    

    initialize()
end MessagesRoutes
