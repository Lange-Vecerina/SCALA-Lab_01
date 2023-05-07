package Web

import scalatags.Text.all._
import scalatags.Text.tags2
import scalatags.Text.tags2.nav

/**
 * Assembles the method used to layout ScalaTags
 */
object Layouts:
    // You can use it to store your methods to generate ScalaTags.


   
    def navigationBar() = { 
        nav(
            a(cls := "nav-brand")("Bot-tender"),
            div(cls := "nav-item")(a(href := "/login", "Log In"))
        )
            
    }
    def contentPage() = {
        div(cls := "content", id := "content")(
            div(id := "boardMessage")(
                div(cls := "msg", "Please wait! the messages are loading !", textAlign.center)(
                    span(cls := "msg-cotent")(
                        span(cls := "author", "Bot-tender")(
                            span(cls := "author::after", "lol", content := "lol")
                        ),
                        span(cls := "mention")),
                )   
            ),

            form(id := "msgForm", action := "/send", method := "post")(
                div(id := "errorDiv", cls := ".errorMsg"),
                label(`for` := "messageInput")("Your message:"),
                input(id := "messageInput", `type` := "text", placeholder := "Write your message"),
                input(id := "send", `type` := "submit", value := "Envoyer")
            )
        )

    
    }
    
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
