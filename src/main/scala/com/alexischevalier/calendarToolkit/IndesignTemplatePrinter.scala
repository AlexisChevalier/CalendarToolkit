package com.alexischevalier.calendarToolkit

import akka.actor.{Actor, Props}
import org.joda.time.DateTime

class IndesignTemplatePrinter() extends Actor {
  import IndesignTemplatePrinter._

  override def receive: Receive = {
    case PrintLayout(config) =>
      printTabbedTemplates(config)
      sender() ! PrintCompleted
  }
}

object IndesignTemplatePrinter {
  case class PrintLayout(config: Config)
  case class PrintCompleted()

  def props() = Props(new IndesignTemplatePrinter)

  private val monthNamesMap = Map(
    1 -> "Janvier",
    2 -> "Février",
    3 -> "Mars",
    4 -> "Avril",
    5 -> "Mai",
    6 -> "Juin",
    7 -> "Juillet",
    8 -> "Août",
    9 -> "Septembre",
    10 -> "Octobre",
    11 -> "Novembre",
    12 -> "Décembre"
  )
  private val monthInitials = List("J","F","M","A","M","J","J","A","S","O","N","D")
  private val MaximumDisplayedWeeks = 6
  private val DaysPerWeek = 7
  private val offsetEnd: Int = DaysPerWeek * MaximumDisplayedWeeks

  def printTabbedTemplates(config: Config): Unit = {
    def printMonthName(month: Int): Unit =
      println(monthNamesMap.getOrElse(month, ""))

    def printDaysHeader(): Unit =
      println(monthInitials.mkString("\t"))

    def printMonth(month: Int, year: Int): Unit = {
      val date = new DateTime(year, month, 1, 0, 0)
      val firstDayOfWeek = date.dayOfWeek.get
      val daysInMonth = date.dayOfMonth.getMaximumValue
      printDaysRecursively(1, 1, firstDayOfWeek, daysInMonth)
      print("\n\n")
    }

    def printDaysRecursively(offsetDay: Int, nextDayNumber: Int, firstDayOfWeek: Int, daysInMonth: Int): Unit = {
      if (firstDayOfWeek > offsetDay) { // Clears additional previously existing entries in InDesign before the beginning
        printPlaceHolderTab(offsetDay)
        printDaysRecursively(offsetDay + 1, nextDayNumber, firstDayOfWeek, daysInMonth)
      } else if (nextDayNumber <= daysInMonth) { // Prints the actual days of the month
        printDay(nextDayNumber, offsetDay)
        printDaysRecursively(offsetDay + 1, nextDayNumber + 1, firstDayOfWeek, daysInMonth)
      } else if (offsetDay <= offsetEnd) { // Clears additional previously existing entries in InDesign after the end
        printPlaceHolderTab(offsetDay)
        printDaysRecursively(offsetDay + 1, nextDayNumber, firstDayOfWeek, daysInMonth)
      }
    }

    def printPlaceHolderTab(offsetDay: Int): Unit = {
      if (offsetDay % 7 == 0) print("\n")
      else print("\t")
    }

    def printDay(nextDayNumber: Int, offsetDay: Int): Unit = {
      if (offsetDay % 7 == 0) print("\n")
      else print("\t")
    }

    (1 to 12).foreach(month => {
      printMonthName(month)
      printDaysHeader()
      printMonth(month, config.year.get)
    })
  }
}
