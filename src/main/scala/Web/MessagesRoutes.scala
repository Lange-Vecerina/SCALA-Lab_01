package Web

import Chat.{AnalyzerService, TokenizerService, ExprTree}
import Data.{MessageService, AccountService, SessionService, Session}

import scalatags.Text.all._
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

    @getSession(sessionSvc) // This decorator fills the `(session: Session)` part of the `index` method.
    @cask.get("/")
    def index()(session: Session) =
        // TODO - Part 3 Step 2: Display the home page (with the message board and the form to send new messages)
        
    session.getCurrentUser match
      case Some(user) => Layouts.welcomePage(Some(user))
      case None => Layouts.welcomePage()
    
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
    //
    // TODO - Part 3 Step 4c: Process and store the new websocket connection made to `/subscribe`
    //
    // TODO - Part 3 Step 4d: Delete the message history when a GET is made to `/clearHistory`
    //
    // TODO - Part 3 Step 5: Modify the code of step 4b to process the messages sent to the bot (message
    //      starts with `@bot `). This message and its reply from the bot will be added to the message
    //      store together.
    //
    //      The exceptions raised by the `Parser` will be treated as an error (same as in step 4b)


    @getSession(sessionSvc)
    @cask.postJson("/send")
    def sendMessage(msg: String)(session: Session) = {
      val currenUser = session.getCurrentUser

      if (currenUser.isEmpty) {
        ujson.Obj("success" -> false, "err" -> "No user is logged in")
      } else if (msg.isEmpty()) {
        ujson.Obj("success" -> false, "err" -> "The message is empty")
      } else {
        val user = currenUser.get
        val mention = if (msg.startsWith("@bot ")) Some("bot") else None
        val replyToId = None
        val id = msgSvc.add(user, msg, mention, None, replyToId)
        val json = ujson.Obj("success" -> true, "err" -> "", "messages" -> msg)
        json
        
      }
    }

    /*@cask.websocket("/subscribe")
    def subscribe(userName: String)(): cask.WebsocketResult = {
         if (userName != "haoyi") cask.Response("", statusCode = 403)
        else cask.WsHandler { channel =>
        cask.WsActor {
        case cask.Ws.Text("") => channel.send(cask.Ws.Close())
        case cask.Ws.Text(data) =>
          channel.send(cask.Ws.Text(userName + " " + data))
        }
      } 
    }*/
    


    @getSession(sessionSvc)
    @cask.get("/clearHistory")
    def clearHistory()(session: Session) = {
      val currenUser = session.getCurrentUser

      if (currenUser.isEmpty) {
        ujson.Obj("success" -> false, "err" -> "No user is logged in")
      } else {
        msgSvc.deleteHistory()
        ujson.Obj("success" -> true, "err" -> "")
      }
    }

    initialize()
end MessagesRoutes
