package com.alexischevalier.calendarToolkit

import io.circe.generic.auto._
import io.circe.parser._
import scopt.OParser
import zio.blocking.Blocking
import zio.console._
import zio.{IO, ZIO}

import java.io.File
import scala.io.Source

object CalendarToolkitApp extends App {
  zio.Runtime.default.unsafeRun(toolkitApp(args.toList).tapError(
    e => zio.console.putStrLn(e)).exitCode
  )

  def toolkitApp(args: List[String]): ZIO[Blocking with Console, String, Unit] = {
    OParser.parse(CalendarCommandParser.parser, args, CalendarCommand.Noop) match {
      case Some(command) =>
        command match {
          case CalendarCommand.Noop => ZIO.unit // done by scopt
          case CalendarCommand.MakeCalendarsFromConfig(configFile) =>
            for {
              config <- IO.fromEither(loadConfig(configFile))
              _ <- CalendarMaker.makeCalendars(config.batches, config.parallelism)
            } yield ()
          case CalendarCommand.MakeCalendars(batchesToGenerate, parallelism) =>
            CalendarMaker.makeCalendars(Seq(batchesToGenerate), parallelism)
        }
      case None =>
        ZIO.unit
    }
  }

  private def loadConfig(configFile: File): Either[String, Config] = {
    val src = Source.fromFile(configFile)
    println(s"Loading config from ${configFile.toPath}")
    for {
      json <- parse(src.getLines().mkString).left.map(_.message)
      _ = src.close()
      config <- json.as[Config].left.map(_.message)
    } yield config
  }

  final case class Config(batches: Seq[CalendarBatchToGenerate], parallelism: Int)

  final case class CalendarBatchToGenerate(
    templateFile: String,
    inputFolder: String,
    outputFolder: String,
    frameWidth: Int,
    frameHeight: Int,
    frameOffsetX: Int,
    frameOffsetY: Int
  )
}
