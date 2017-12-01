package com.alexischevalier.calendarToolkit

import akka.actor.ActorSystem
import com.alexischevalier.calendarToolkit.CalendarGeneratorOrchestrator.{GenerateCalendars, GenerationCompleted}
import com.alexischevalier.calendarToolkit.IndesignTemplatePrinter.{PrintCompleted, PrintLayout}
import org.joda.time.DateTime
import akka.pattern.ask
import akka.util.Timeout
import scopt.OptionParser
import scala.concurrent.duration._
import scala.language.postfixOps

import scala.concurrent.ExecutionContextExecutor

object Application extends App {

  lazy val parser: OptionParser[Config] = new scopt.OptionParser[Config]("calendarToolkit") {
    head("Calendar Toolkit")

    cmd("layout").action((_, config) => config.copy(mode = Some("layout")))
      .text("Prints the calendar tabbed layout for indesign")
      .children(
        opt[Int]("year")
          .abbr("y")
          .text("Year to generate the layout for")
          .action((givenFirstYear, config) => config.copy(year = Some(givenFirstYear)))
      )

    cmd("generate").action((_, config) => config.copy(mode = Some("generate")))
      .text("Generates the calendar files")
      .children(
        opt[String]("templateFile").required()
          .abbr("tf")
          .text("Calendar template file")
          .action((givenTemplateFile, config) => config.copy(templateFile = Some(givenTemplateFile))),

        opt[String]("inputFolder").required()
          .abbr("if")
          .text("Folder containing the images to process")
          .action((givenInputFolder, config) => config.copy(inputFolder = Some(givenInputFolder))),

        opt[String]("outputFolder").required()
          .abbr("of")
          .text("Folder to write the calendars to")
          .action((givenOutputFolder, config) => config.copy(outputFolder = Some(givenOutputFolder))),

        opt[Int]("frameWidth").required()
          .abbr("fw")
          .text("Picture frame width")
          .action((givenFrameWidth, config) => config.copy(frameWidth = Some(givenFrameWidth))),

        opt[Int]("frameHeight").required()
          .abbr("fh")
          .text("Picture frame height")
          .action((givenFrameHeight, config) => config.copy(frameHeight = Some(givenFrameHeight))),

        opt[Int]("frameOffsetX").required()
          .abbr("fox")
          .text("Picture frame offset on X axis")
          .action((givenFrameOffsetX, config) => config.copy(frameOffsetX = Some(givenFrameOffsetX))),

        opt[Int]("frameOffsetY").required()
          .abbr("foy")
          .text("Picture frame offset on Y axis")
          .action((givenFrameOffsetY, config) => config.copy(frameOffsetY = Some(givenFrameOffsetY)))
      )

    checkConfig(config =>
      config.mode match {
        case Some("layout") => success
        case Some("generate") => success
        case _ =>
          failure("You must choose a command")
      }
    )
  }

  override def main(args: Array[String]): Unit = {
    val programConfig = parser.parse(args, Config()) match {
      case Some(config) => config
      case _ => return
    }

    val actorSystem = ActorSystem("calendar-toolkit")
    implicit val executionContext: ExecutionContextExecutor = actorSystem.dispatcher
    implicit val timeout: Timeout = Timeout(10 minutes)

    programConfig.mode match {
      case Some("layout") =>
        actorSystem.actorOf(IndesignTemplatePrinter.props(), "templatePrinter") ? PrintLayout(programConfig) map {
          case PrintCompleted =>
            actorSystem.terminate()
        }
      case Some("generate") =>
        actorSystem.actorOf(CalendarGeneratorOrchestrator.props(), "calendarGeneratorOrchestrator") ? GenerateCalendars(programConfig) map {
          case GenerationCompleted =>
            actorSystem.terminate()
        }
      case _ => println("Unknown command")
    }
  }
}

case class Config(
                   mode: Option[String] = None,
                   year: Option[Int] = Some(DateTime.now.year().get()),
                   templateFile: Option[String] = None,
                   inputFolder: Option[String] = None,
                   outputFolder: Option[String] = None,
                   frameWidth: Option[Int] = None,
                   frameHeight: Option[Int] = None,
                   frameOffsetX: Option[Int] = None,
                   frameOffsetY: Option[Int] = None,
                 )