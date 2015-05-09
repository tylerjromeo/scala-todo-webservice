package com.example

import akka.actor.{ActorLogging, Actor}
import com.example.jdbc.TaskDAO
import com.example.jdbc.TaskDAO.Task
import spray.http.HttpHeaders.Location
import spray.routing._
import spray.http._
import MediaTypes._
import spray.httpx.SprayJsonSupport._
import spray.util.LoggingContext
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Success, Failure}


// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class TaskServiceActor extends Actor with TaskService with ActorLogging {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(taskRoute)

}


// this trait defines our service behavior independently from the service actor
trait TaskService extends HttpService {


  implicit def taskExceptionHandler(implicit log: LoggingContext) =
    ExceptionHandler {
      case e: Exception =>
        requestUri {
          uri =>
            log.error(e, "Request to {} could not be handled normally", uri)
            complete(StatusCodes.InternalServerError, s"An error occurred: ${e.getMessage}")
        }
    }

  val taskRoute =
    pathPrefix("tasks") {
      pathEndOrSingleSlash {
        get {
          respondWithMediaType(`application/json`) {
            onSuccess(TaskDAO.getTasks) { value =>
              complete(value)
            }
          }
        }
      } ~
        post {
          entity(as[Task]) { task =>
            onSuccess(TaskDAO.addTask(task)) { value =>
              requestUri { uri =>
                respondWithHeader(Location(uri + s"/${value}")) {
                  complete(StatusCodes.Created, task.copy(id=value))
                }
              }
            }
          }
        } ~
        path(IntNumber) {
          param =>
            get {
              respondWithMediaType(`application/json`) {
                onSuccess(TaskDAO.getTaskById(param)) {
                  value =>
                    complete(value)
                }
              }
            } ~
              delete {
                onSuccess(TaskDAO.deleteTask(param)) {
                  value =>
                    complete(StatusCodes.NoContent)
                }
              } ~
              put {
                entity(as[Task]) {
                  task =>
                    //if the task doesn't have an id, fail. If it does make sure it matches the path param
                    validate(task.id.exists(_ == param), "Task id does not match path") {
                      onSuccess(TaskDAO.updateTask(task)) {
                        value =>
                          requestUri {
                            uri =>
                              respondWithHeader(Location(uri)) {
                                complete(StatusCodes.Created, task)
                              }
                          }
                      }
                    }
                }
              }
        }
    } ~ path("setup") {
      post {
        entity(as[Seq[Task]]) { tasks =>
          onSuccess(TaskDAO.setup(tasks)) {
            value =>
              complete(StatusCodes.Created)
          }
        }
      }
    }

}
