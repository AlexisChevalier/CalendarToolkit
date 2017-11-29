package com.alexischevalier.calendarToolkit

import org.joda.time.DateTime

object TemplatePrinter {
  private val monthNameMaps = Map(
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
  private val MaximumDisplayedWeeks = 6
  private val DaysPerWeek = 7
  private val offsetEnd: Int = DaysPerWeek * MaximumDisplayedWeeks

  def printTabbedTemplates(config: Config): Unit = {
    (1 to 12).foreach(month => {
      printMonthName(month)
      printDaysHeader()
      printMonth(month, config.year.get)
    })
  }

  def printMonthName(month: Int): Unit = {
    println(monthNameMaps.getOrElse(month, ""))
  }

  def printDaysHeader(): Unit = {
    println("L\tM\tM\tJ\tV\tS\tD")
  }

  def printMonth(month: Int, year: Int): Unit = {
    val date = new DateTime(year, month, 1, 0, 0)
    val firstDayOfWeek = date.dayOfWeek.get
    val daysInMonth = date.dayOfMonth.getMaximumValue
    printDaysRecursively(1, 1, firstDayOfWeek, daysInMonth)
    print("\n\n")
  }

  def printDaysRecursively(offsetDay: Int, nextDayNumber: Int, firstDayOfWeek: Int, daysInMonth: Int): Unit = {
    if (firstDayOfWeek > offsetDay) { // Clears additional previously existing entries in InDesign before the end
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
    print(s"$nextDayNumber")
    if (offsetDay % 7 == 0) print("\n")
    else print("\t")
  }
}
