package Web

/**
  * Assembles the routes dealing with static files.
  */
class StaticRoutes()(implicit val log: cask.Logger) extends cask.Routes:

    @cask.staticResources("/static/resource/css")
    def staticResourceRoutesCss() = "./css"

    @cask.staticResources("/static/resource/js")
    def staticResourceRoutesJs() = "./js"

    initialize()
end StaticRoutes
