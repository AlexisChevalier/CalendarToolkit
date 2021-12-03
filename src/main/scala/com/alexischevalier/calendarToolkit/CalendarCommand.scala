package com.alexischevalier.calendarToolkit

import com.alexischevalier.calendarToolkit.CalendarToolkitApp.CalendarBatchToGenerate

import java.io.File

sealed trait CalendarCommand

object CalendarCommand {
  case object Noop extends CalendarCommand

  final case class MakeCalendarsFromConfig(configFile: File = new File("./config.json")) extends CalendarCommand

  final case class MakeCalendars(batch: CalendarBatchToGenerate, parallelism: Int) extends CalendarCommand

  object MakeCalendars {
    val default: MakeCalendars = MakeCalendars(CalendarBatchToGenerate("", "", "", 0, 0, 0, 0), 0)
  }
}
