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

    /**
      * The main route for login, redirects to the login/register page.
      */
    @getSession(sessionSvc)
    @cask.get("/login")
    def connect()(session: Session) =
        Layouts.authPage()

    /**
      * The logout route. Resets the session and redirects to the logout page.
      *
      */
    @getSession(sessionSvc)
    @cask.get("/logout")
    def logout()(session: Session) =
        session.reset()
        Layouts.logoutSuccessPage()

    /**
      * The user login post route. Checks if the login input is an account that already exists.
      * If this is the case redirects to the success login page, else shows an error message.
      */
    @getSession(sessionSvc)
    @cask.postForm("/login")
    def login(username : cask.FormValue)(session: Session) =
        try {
            accountSvc.getAccountBalance(username.value)
            session.setCurrentUser(username.value)
            Layouts.authSuccessPage("Logged In successfully", username.value)
        } catch {
            case e: Exception =>
                Layouts.authPage(loginError = Some(e.getMessage))
        }

    /**
      * The user register post route. Checks if the register input is an account taht already exists.
      * If this is the case shows an error message, else redirects to the success register page.
      */
    @getSession(sessionSvc)
    @cask.postForm("/register")
    def register(username: cask.FormValue)(session: Session) =
        try {
            accountSvc.addAccount(username.value, 30.0)
            session.setCurrentUser(username.value)
            Layouts.authSuccessPage("Registered successfully", username.value)
        } catch {
            case e: Exception =>
                Layouts.authPage(registerError = Some(e.getMessage))
        } 

    initialize()
end UsersRoutes
