package com.alexischevalier.calendarToolkit

import scopt.OParser

import java.io.File

object CalendarCommandParser {
  private val builder = OParser.builder[CalendarCommand]

  val parser: OParser[Unit, CalendarCommand] = {
    import builder._
    OParser.sequence(
      programName("calendarToolkit"),
      head("Calendar Toolkit"),
      cmd("generateFromConfig").action((_, _) => CalendarCommand.MakeCalendarsFromConfig())
        .text("Generates the calendar batches from the configuration file")
        .children(
          opt[File]("configurationFile")
            .abbr("c")
            .text("Calendar batches configuration file")
            .action((givenConfigurationFile, c) =>
              c.asInstanceOf[CalendarCommand.MakeCalendarsFromConfig].copy(configFile = givenConfigurationFile)
            )
        ),
      cmd("generateFromArgs").action((_, _) => CalendarCommand.MakeCalendars.default)
        .text("Generates the calendar batch from the command line arguments")
        .children(
          opt[String]("templateFile").required()
            .abbr("tf")
            .text("Calendar template file")
            .action((givenTemplateFile, c) => c.asInstanceOf[CalendarCommand.MakeCalendars].copy(
              batch = c.asInstanceOf[CalendarCommand.MakeCalendars].batch.copy(templateFile = givenTemplateFile)
            )),
          opt[String]("inputFolder").required()
            .abbr("if")
            .text("Folder containing the images to process")
            .action((givenInputFolder, c) => c.asInstanceOf[CalendarCommand.MakeCalendars].copy(
              batch = c.asInstanceOf[CalendarCommand.MakeCalendars].batch.copy(inputFolder = givenInputFolder)
            )),
          opt[String]("outputFolder").required()
            .abbr("of")
            .text("Folder to write the calendars to")
            .action((givenOutputFolder, c) => c.asInstanceOf[CalendarCommand.MakeCalendars].copy(
              batch = c.asInstanceOf[CalendarCommand.MakeCalendars].batch.copy(outputFolder = givenOutputFolder)
            )),
          opt[Int]("frameWidth").required()
            .abbr("fw")
            .text("Picture frame width")
            .action((givenFrameWidth, c) => c.asInstanceOf[CalendarCommand.MakeCalendars].copy(
              batch = c.asInstanceOf[CalendarCommand.MakeCalendars].batch.copy(frameWidth = givenFrameWidth)
            )),
          opt[Int]("frameHeight").required()
            .abbr("fh")
            .text("Picture frame height")
            .action((givenFrameHeight, c) => c.asInstanceOf[CalendarCommand.MakeCalendars].copy(
              batch = c.asInstanceOf[CalendarCommand.MakeCalendars].batch.copy(frameHeight = givenFrameHeight)
            )),
          opt[Int]("frameOffsetX").required()
            .abbr("fox")
            .text("Picture frame offset on X axis")
            .action((givenFrameOffsetX, c) => c.asInstanceOf[CalendarCommand.MakeCalendars].copy(
              batch = c.asInstanceOf[CalendarCommand.MakeCalendars].batch.copy(frameOffsetX = givenFrameOffsetX)
            )),
          opt[Int]("frameOffsetY").required()
            .abbr("foy")
            .text("Picture frame offset on Y axis")
            .action((givenFrameOffsetY, c) => c.asInstanceOf[CalendarCommand.MakeCalendars].copy(
              batch = c.asInstanceOf[CalendarCommand.MakeCalendars].batch.copy(frameOffsetY = givenFrameOffsetY)
            )),
          opt[Int]("parallelism").required()
            .abbr("p")
            .text("Number of calendars generated in parallel")
            .action((parallelism, c) => c.asInstanceOf[CalendarCommand.MakeCalendars].copy(
              parallelism = parallelism
            )),
        ),
      checkConfig {
        case CalendarCommand.Noop =>
          failure("You must provide a valid command")
        case _: CalendarCommand =>
          success
        case _ =>
          failure("Unexpected command")
      },
    )
  }
}
