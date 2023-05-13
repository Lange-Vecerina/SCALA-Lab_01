package Web

import Data.{AccountService, SessionService, Session}

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

    @cask.get("/login")
    def connect() =
        println("====> ln")
        Layouts.loginPage()

    @cask.postForm("/login")
    def login(username : cask.FormValue) =
        println("====> login")
        println(Decorators.getSession(sessionSvc))  
        println(sessionSvc.get("username"))
        println(username)
        println(username.value)
        accountSvc.isAccountExisting(username.value) match
            case true =>
                println("====> login success")
                val session = sessionSvc.create()
                Layouts.loginSuccessPage(username.value)
                //println(Decorators.getSession(sessionSvc).toString())
            case false =>
                println("====> login failed")
                //Layouts.loginFailedPage()
                //cask.Response(Layouts.loginPage(Some("The specified user does not exists")))
                Layouts.loginPage(Some("The specified user does not exists"))

    @cask.postForm("/register")
    def register(username: cask.FormValue) =
        accountSvc.isAccountExisting(username.value) match
            case true =>
                Layouts.loginPage(Some("The specified user already exists"))
            case false =>
                accountSvc.addAccount(username.value, 0.0)
                val session = sessionSvc.create()
                session.setCurrentUser(username.value)
                val cookie = cask.Cookie("username", username.value)
                Layouts.loginSuccessPage(username.value)
                
    initialize()
end UsersRoutes
