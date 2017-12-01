package com.alexischevalier.calendarToolkit

import java.io.File
import javax.activation.MimetypesFileTypeMap

import akka.actor.{Actor, ActorRef, Props}
import akka.routing.FromConfig
import com.alexischevalier.calendarToolkit.CalendarMaker.{CalendarDone, GenerateCalendar}

import scala.util.{Failure, Success, Try}

class CalendarGeneratorOrchestrator extends Actor {
  import CalendarGeneratorOrchestrator._

  val calendarMakerRouter: ActorRef =
    context.actorOf(FromConfig.props(CalendarMaker.props()), "calendarMakerRouter")

  var jobs: Set[String] = Set[String]()
  val owner: ActorRef = sender()

  override def receive: Receive = {
    case GenerateCalendars(config) =>
      generateCalendarTasks(config) match {
        case Success(tasks) =>
          jobs = tasks.map(t => t.filePath).toSet
          println(s"Creating ${tasks.length} calendars...")
          tasks.foreach(task => calendarMakerRouter ! task)
        case Failure(reason) =>
          println(s"Calendar generation failure. Reason: $reason")
      }
    case CalendarDone(jobPath) =>
      jobs = jobs - jobPath
      if (jobs.isEmpty) {
        println(s"Calendar generation completed, exiting")
        owner ! GenerationCompleted
      }
  }
}

object CalendarGeneratorOrchestrator {
  case class GenerateCalendars(config: Config)
  object GenerationCompleted

  def props() = Props(new CalendarGeneratorOrchestrator)

  def generateCalendarTasks(config: Config): Try[List[GenerateCalendar]] = {
    val directory = new File(config.inputFolder.get)

    def generateForFiles(files: List[File]): List[GenerateCalendar] = {
      val imageFiles = files.filter(isFileAnImage)

      imageFiles.map(
        image => GenerateCalendar(config, image.getAbsolutePath)
      )
    }

    def isFileAnImage(file: File): Boolean = {
      val mimetype = new MimetypesFileTypeMap().getContentType(file)
      val typeGroup = mimetype.split("/")(0)

      typeGroup == "image"
    }
    Try(generateForFiles(directory.listFiles.filter(_.isFile).toList))
  }
}
