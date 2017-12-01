package com.alexischevalier.calendarToolkit

import java.io.File
import javax.activation.MimetypesFileTypeMap

class BulkCalendarManager(config: Config) {
  val calendarGenerator = new CalendarGenerator(config)

  def generateCalendars(): Unit = {
    val directory = new File(config.inputFolder.get)

    def generateForFiles(files: List[File]): Unit = {
      val imageFiles = files.filter(isFileAnImage)

      println(s"Creating ${imageFiles.length} calendars...")

      imageFiles.foreach(
        image => calendarGenerator.generateCalendarForImage(image)
      )
    }

    def isFileAnImage(file: File): Boolean = {
      val mimetype = new MimetypesFileTypeMap().getContentType(file)
      val typeGroup = mimetype.split("/")(0)

      typeGroup == "image"
    }

    if (!directory.exists() || !directory.isDirectory)
      printf("Invalid input directory")
    else
      generateForFiles(directory.listFiles.filter(_.isFile).toList)
  }
}
