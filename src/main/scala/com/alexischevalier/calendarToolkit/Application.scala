package com.alexischevalier.calendarToolkit

import org.joda.time.DateTime
import scopt.OptionParser

object Application extends App {

  override def main(args: Array[String]): Unit = {
    val programConfig = parser.parse(args, Config()) match {
      case Some(config) => config
      case _ => return
    }

    if (programConfig.mode == "layout") {
      TemplatePrinter.printTabbedTemplates(programConfig.year)
    }
  }

  lazy val parser: OptionParser[Config] = new scopt.OptionParser[Config]("calendarToolkit") {
    head("Calendar Toolkit")

    cmd("layout").action((_, config) => config.copy(mode = "layout"))
      .text("Prints the calendar tabbed layout for indesign")
      .children(
        opt[Int]("year")
          .abbr("y")
          .text("Year to generate the layout for")
          .action((givenFirstYear, config) => config.copy(year = givenFirstYear))
      )

    cmd("generate").action((_, config) => config.copy(mode = "generate"))
      .text("Generates the calendar files")

    checkConfig(config =>
      if (config.mode == "noop") failure("You must choose a command")
      else success
    )
  }
}

case class Config(
                 mode: String = "noop",
                 year: Int = DateTime.now.year().get()
                 )