package Web

import scalatags.Text.all._
import scalatags.Text.tags2
import scalatags.Text.tags2.nav

/**
 * Assembles the method used to layout ScalaTags
 */
object Layouts:
    // You can use it to store your methods to generate ScalaTags.


    /**
      * This method generates the navigation bar.
      * 
      */
    private def navigationBar() = { 
        nav(
            a(cls := "nav-brand")("Bot-tender"),
            div(cls := "nav-item")(a(href := "/login", "Log In"))
        )
            
    }


    /**
      * This method generates the message information such as author, mention and content of the message.
      * 
      */
    private def messageItem(author: String, content: String, mention: String) = {
        div(cls := "msg")(
            span(cls := "author")(author),
            span(cls := "msg-content")(
                span(cls := "mention")(mention),
                content
            ),
        )
    }

    /**
      * This method generates generates a default message when there is no message.
      * Else, it generates the message information with the method messageItem.
      * 
      */
    private def messageBoard(messages: Seq[(String, String, String)]) = {
        div(id := "boardMessage")(
            // If messages is empty, then return a div with a message
            if messages.isEmpty then
                div(cls := "msg")("Please wait! the messages are loading !", textAlign.center)
            else
                messages.map(msg => messageItem(msg._1, msg._2, msg._3))
        )
    }

    /**
      * This method generates bottom part of the page (the input for the message and a send button to send it).
      * 
      */
    private def contentPage() = {
        div(cls := "content", id := "content")(
            messageBoard(Seq.empty),

            form(id := "msgForm", action := "/send", method := "post")(
                div(id := "errorDiv", cls := ".errorMsg"),
                label(`for` := "messageInput")("Your message:"),
                input(id := "messageInput", `type` := "text", placeholder := "Write your message"),
                input(id := "send", `type` := "submit", value := "Envoyer")
            )
        )

    
    }
    
    /**
      * This method generates the complete page with all other functions above and loads and links the css and 
      * js files to the html page.
      * 
      * 
      */
    def welcomePage() = {
        html(
            head(
                tags2.title("Bot-tender"),
                link(rel := "stylesheet", href := "/static/resource/css/main.css"),
                script(src := "/static/resource/js/main.js")
            ),
            body(
                navigationBar(),
                contentPage()
            )
        )

        
        
    }
        
       
end Layouts
