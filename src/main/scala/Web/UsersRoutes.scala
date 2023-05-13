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


    /**
      * The main route for login, redirects to the login/register page.
      */
    @getSession(sessionSvc)
    @cask.get("/login")
    def connect()(session: Session) =
        Layouts.loginPage()

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
        accountSvc.isAccountExisting(username.value) match
            case true =>
                session.setCurrentUser(username.value)
                Layouts.loginAndRegisterSuccessPage("Logged In successfully", username.value)
            case false =>
                Layouts.loginPage(Some("The specified user does not exists"))

    /**
      * The user register post route. Checks if the register input is an account taht already exists.
      * If this is the case shows an error message, else redirects to the success register page.
      */
    @getSession(sessionSvc)
    @cask.postForm("/register")
    def register(username: cask.FormValue)(session: Session) =
        accountSvc.isAccountExisting(username.value) match
            case true =>
                Layouts.loginPage(Some("The specified user already exists"))
            case false =>
                accountSvc.addAccount(username.value, 0.0)
                session.setCurrentUser(username.value)
                Layouts.loginAndRegisterSuccessPage("Registered successfully", username.value)   

    initialize()
end UsersRoutes
