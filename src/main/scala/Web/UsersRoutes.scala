package Web

import Data.{AccountService, SessionService, Session}
import Web.Decorators.getSession

/**
  * Assembles the routes dealing with the users:
  * - One route to display the login form and register form page
  * - One route to process the login form and display the login success page
  * - One route to process the register form and display the register success page
  * - One route to logout and display the logout success page
  * 
  * The username of the current session user is stored inside a cookie called `username`.
  */
class UsersRoutes(accountSvc: AccountService,
                  sessionSvc: SessionService)(implicit val log: cask.Logger) extends cask.Routes:
    // TODO - Part 3 Step 3a: Display a login form and register form page for the following URL: `/login`.
    // TODO - Part 3 Step 3b: Process the login information sent by the form with POST to `/login`,
    //      set the user in the provided session (if the user exists) and display a successful or
    //      failed login page.
    //
    // TODO - Part 3 Step 3c: Process the register information sent by the form with POST to `/register`,
    //      create the user, set the user in the provided session and display a successful
    //      register page.
    //
    // TODO - Part 3 Step 3d: Reset the current session and display a successful logout page.

    @getSession(sessionSvc)
    @cask.get("/login")
    def connect()(session: Session) =
        println("====> ln")
        Layouts.loginPage()

    /*@cask.get("/logged")
    def logged(username: String) =
        println("====> logged")
        Layouts.loginAndRegisterSuccessPage("Logged in successfully", username)

    @cask.get("/registered")
    def registered(username: String) =
        println("====> registered")
        Layouts.loginAndRegisterSuccessPage("Registered successfully", username)*/

   
    @getSession(sessionSvc)
    @cask.get("/logout")
    def logout()(session: Session) =
        println("====> logout")
        session.reset()
        Layouts.logoutSuccessPage()

    @getSession(sessionSvc)
    @cask.postForm("/login")
    def login(username : cask.FormValue)(session: Session) =
        println("====> login")
  
        accountSvc.isAccountExisting(username.value) match
            case true =>
                println("====> login success")
                session.setCurrentUser(username.value)
                Layouts.loginAndRegisterSuccessPage("Logged In successfully", username.value)
                //cask.Redirect("/logged")

            case false =>
                println("====> login failed")
                Layouts.loginPage(Some("The specified user does not exists"))

    @getSession(sessionSvc)
    @cask.postForm("/register")
    def register(username: cask.FormValue)(session: Session) =
        accountSvc.isAccountExisting(username.value) match
            case true =>
                Layouts.loginPage(Some("The specified user already exists"))
            case false =>
                accountSvc.addAccount(username.value, 0.0)
                //val session = sessionSvc.create()
                session.setCurrentUser(username.value)
                //val currentsession = Decorators.getSession(sessionSvc)
                //println(currentsession)
                //val cookie = cask.Cookie("username", username.value)
                Layouts.loginAndRegisterSuccessPage("Registered successfully", username.value)   

    initialize()
end UsersRoutes
