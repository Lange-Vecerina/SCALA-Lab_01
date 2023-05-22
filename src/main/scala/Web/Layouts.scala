package Web

import scalatags.Text.all._
import scalatags.Text.tags2
import scalatags.Text.tags2.nav
import Data.MessageService.{MsgContent, Username}

type STag = scalatags.Text.TypedTag[String] // alias for scalatags.Text.TypedTag[String] type

/**
 * Assembles the method used to layout ScalaTags
 */
object Layouts:
    // You can use it to store your methods to generate ScalaTags.

    // --------------------------------
    // Pages
    // --------------------------------

    /**
      * This method generates the complete page with all other functions above and loads and links the css and 
      * js files to the html page.
      *  
      */
    def homePage(username: Option[String] = None, messages: Seq[(Username, MsgContent)] = Seq.empty): STag = {
        baseLayout(
            authNavBar(username),
            Seq(
                div(id := "boardMessage")(messageBoardContent(messages)),
                makeForm(
                    labelTag = "messageInput",
                    labelMessage = "Your message:",
                    placeholderTxt = "Write your message",
                    doAction = "submitMessageForm(); return false"
                )
            )
        )    
    }
        
    /**
      * This method generates the login/register page.
      * 
      */
    def authPage(loginError: Option[String] = None, registerError: Option[String] = None): STag = {
        baseLayout(
            defaultNavBar(),
            Seq(
                h1("Login"),
                makeForm( 
                    postUrl = "/login",
                    labelTag = "username", 
                    labelMessage = "Username:", 
                    placeholderTxt = "Write your username", 
                    errorMessage = loginError
                ),

                h1("Register"),
                makeForm(
                    postUrl = "/register",
                    labelTag = "username",
                    labelMessage = "Username:",
                    placeholderTxt = "Write your username",
                    errorMessage = registerError
                )
            )
        )
    }

    /**
      * This method generates the login or register success page with the success message of login or register and the username.
      * 
      */
    def authSuccessPage(successMessage: String, username: String): STag = {
        baseLayout(
            defaultNavBar(),
            Seq(
                h1(successMessage),
                h2("Welcome " + username)
            )
        )
    }

    /**
      * This method generates the logout page
      *
      */
    def logoutSuccessPage(): STag = {
        baseLayout(
            defaultNavBar(),
            Seq(
                h1("Logout Success"),
                h2("You have been logged out")
            )
        )
    }

    // --------------------------------
    // Reusable pieces of HTML code and elements
    // --------------------------------

    // Head
    private def header(): STag = 
        head (
            tags2.title("Bot-tender"),
            link(rel := "stylesheet", href := "/static/resource/css/main.css"),
            script(src := "/static/resource/js/main.js")
        )

    // Navigation bar
    private def navBar(ref : String, refText : String, msg : String = ""): STag = {
        nav(
            a(cls := "nav-brand")("Bot-tender"),
            div(cls := "nav-item")(msg + " ", a(href := ref, refText))
        )         
    }

    // Default Navigation bar
    private def defaultNavBar(): STag = {
        navBar("/", "Go to message board")        
    }

    // Auth Navigation bar
    private def authNavBar(username: Option[String] = None): STag = {
        username match
            case Some(value) => navBar("/logout", "Log Out", "Hello "+ value + " !")
            case None => navBar("/login", "Log In")   
    }

    // Base layout
    private def baseLayout(nav: STag, content: Seq[STag]): STag = {
        html(
            header(),
            body(
                nav,
                div(cls := "content", id := "content")(content)
            )
        )
    }

    // Error div
    private def errorDiv(errorMessage: Option[String] = None): STag = {
        errorMessage match
            case Some(msg) => div(color.red, id := "errorDiv", cls := "errorMsg", msg)
            case _ => div(id := "errorDiv", cls := "errorMsg")()
    }

    // Message board
     def messageBoardContent(messages: Seq[(Username, MsgContent)]): Seq[STag] = {
        // If messages is empty, then return a div with a message
        if messages.isEmpty then
            Seq(div(cls := "msg")("No messages have been sent yet!", textAlign.center))
        else
            messages.map(msg => message(msg))
    }

    // Message
    private def message(m: (Username, MsgContent)): STag =
        div(cls := "msg")(
            span(cls := "author")(m._1),
            span(cls := "msg-content")(
                if m._2.toString().startsWith("@") then {
                    span(cls := "mention")(m._2.toString().takeWhile(_ != ' '))
                    m._2.toString().dropWhile(_ != ' ')
                } else {
                    m._2
                }
            ),
        )

    // Form
    private def makeForm(
        labelTag: String,
        labelMessage: String,
        placeholderTxt: String, 
        errorMessage: Option[String] = None, 
        postUrl: String = null, 
        doAction: String = null) : STag = {

            val content = Seq(
                errorDiv(errorMessage),
                label(`for` := labelTag)(labelMessage),
                input(id := labelTag, `type` := "text", name := labelTag, placeholder := placeholderTxt),
                input(id := "send", `type` := "submit", value := "Envoyer")
            )

            if postUrl != null then
                form(id := "msgForm", action := postUrl, method := "post")(content)
            else
                form(id := "msgForm", onsubmit := doAction)(content)
    }


       
end Layouts
