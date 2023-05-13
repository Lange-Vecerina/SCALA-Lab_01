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
    private def navigationBar(ref : String, str : String) = { 
        nav(
            a(cls := "nav-brand")("Bot-tender"),
            div(cls := "nav-item")(a(href := ref, str))
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
            inputForm("/send", "messageInput", "Your message:", "Write your message")
        )
    }
    
    /**
      * This method generates the input form.
      *
      * @param url The redirection url once the form submitted.
      * @param labelTag The label of the form.
      * @param labelMessage The text shown before the input. 
      * @param placeholderStr The placeholder inside the input area.
      * @return
      */
    def inputForm(url: String, labelTag: String, labelMessage: String, placeholderStr: String) = {
        form(id := "msgForm", action := url, method := "post")(
            div(id := "errorDiv", cls := ".errorMsg"),
            label(`for` := labelTag)(labelMessage),
            input(id := labelTag, `type` := "text", name := labelTag, placeholder := placeholderStr),
            input(id := "send", `type` := "submit", value := "Envoyer")
        )
    }

    /**
      * This method generates the complete page with all other functions above and loads and links the css and 
      * js files to the html page.
      *  
      */
    def welcomePage(username: Option[String] = None) = {
        val navBarInfo = username match
            case Some(value) => ("/logout", "Hello "+ value + " ! ", "Log Out")
            case None => ("/login", "", "Log In")

        html(
            head(
                tags2.title("Bot-tender"),
                link(rel := "stylesheet", href := "/static/resource/css/main.css"),
                script(src := "/static/resource/js/main.js")
            ),
            body(
                navigationBar(navBarInfo._1, navBarInfo._2 + navBarInfo._3),
                contentPage()
            )
        )     
    }
        
    /**
      * This method generates the login/register page.
      * 
      */
    def loginPage(errorMessage: Option[String] = None) = {
        val errorTag = errorMessage match
            case Some(msg) => div(color.red, id := "errorDiv", cls := ".errorMsg", msg)
            case None => div(id := "errorDiv", cls := ".errorMsg")()
        html(
            head(
                tags2.title("Bot-tender"),
                link(rel := "stylesheet", href := "/static/resource/css/main.css"),
                script(src := "/static/resource/js/main.js")
            ),
            body(
                navigationBar("/", "Go to message board"),
                h1("Login"),

                errorTag,
                inputForm("/login", "username", "Username:", "Write your username"),
      
                h1("Register"),
                inputForm("/register", "username", "Username:", "Write your username")
            )
        )
    }

    /**
      * This method generates the login or register success page with the success message of login or register and the username.
      * 
      */
    def loginAndRegisterSuccessPage(successMessage: String, username: String) = {
        html(
            head(
                tags2.title("Bot-tender"),
                link(rel := "stylesheet", href := "/static/resource/css/main.css"),
                script(src := "/static/resource/js/main.js")
            ),
            body(
                navigationBar("/", "Go to message board"),
                h1(successMessage),
                h2("Welcome " + username)
            )
        )
    }

    /**
      * This method generates the logout page
      *
      */
    def logoutSuccessPage() = {
        html(
            head(
                tags2.title("Bot-tender"),
                link(rel := "stylesheet", href := "/static/resource/css/main.css"),
                script(src := "/static/resource/js/main.js")
            ),
            body(
                navigationBar("/", "Go to message board"),
                h1("Logged out")
            )
        )
    }

    def loginFailedPage() = {
        html(
            head(
                tags2.title("Bot-tender"),
                link(rel := "stylesheet", href := "/static/resource/css/main.css"),
                script(src := "/static/resource/js/main.js")
            ),
            body(
                navigationBar("/", "Go to message board"),
                h1("Login Failed")
            )
        )
    }

       
end Layouts
