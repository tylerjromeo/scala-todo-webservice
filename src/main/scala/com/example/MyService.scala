package com.example

import akka.actor.{ActorLogging, Actor}
import spray.routing._
import spray.http._
import MediaTypes._
import spray.util.LoggingContext


// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService with ActorLogging {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)

}


// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {


  implicit def taskExceptionHandler(implicit log: LoggingContext) =
    ExceptionHandler {
      case e: Exception =>
        requestUri {
          uri =>
            log.error(e, "Request to {} could not be handled normally", uri)
            complete(StatusCodes.InternalServerError, s"An error occurred: ${e.getMessage}")
        }
    }

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>Say hello to
                  <i>spray-routing</i>
                  on
                  <i>spray-can</i>
                  !</h1>
              </body>
            </html>
          }
        }
      }
    }
}
