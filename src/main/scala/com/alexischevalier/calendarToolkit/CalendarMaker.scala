package com.alexischevalier.calendarToolkit

import java.io.File

import akka.actor.{Actor, Props}
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.JpegWriter

class CalendarMaker extends Actor {
  import CalendarMaker._

  override def receive: Receive = {
    case GenerateCalendar(config, filePath) =>
      generateCalendarForImage(new File(filePath), config)
      sender() ! CalendarDone(filePath)
  }
}

object CalendarMaker {
  case class GenerateCalendar(config: Config, filePath: String)
  case class CalendarDone(filePath: String)

  def props() = Props(new CalendarMaker)

  case class Dimensions(width: Int, height: Int)

  def generateCalendarForImage(image: File, config: Config): Unit = {
    def fileFromPath(path: String): File = {
      new File(path)
    }

    def buildOverlayImage(imageFile: File): Image = {
      val image = Image.fromFile(imageFile)
      val resizeDimensions = getResizeDimensions(image, config.frameWidth.get, config.frameHeight.get)
      val resizedImage = if (resizeDimensions.height > resizeDimensions.width) {
        image.scaleToWidth(resizeDimensions.width)
      } else {
        image.scaleToHeight(resizeDimensions.height)
      }

      resizedImage.resizeTo(config.frameWidth.get, config.frameHeight.get)
    }

    def getResizeDimensions(image: Image, frameWidth: Int, frameHeight: Int): Dimensions = {
      val imageHeight = image.height
      val imageWidth = image.width

      val widthResizePercent = (imageWidth.toDouble - frameWidth.toDouble) / imageWidth.toDouble
      val heightResizePercent = (imageHeight.toDouble - frameHeight.toDouble) / imageHeight.toDouble

      //We resize to the smallest percent in order not to loose content
      val idealResizeCoefficient = if (heightResizePercent > widthResizePercent)
        widthResizePercent
      else
        heightResizePercent

      val resizeToWidth = imageWidth - (imageWidth * idealResizeCoefficient)
      val resizeToHeight = imageHeight - (imageHeight * idealResizeCoefficient)

      Dimensions(resizeToWidth.round.toInt, resizeToHeight.round.toInt)
    }

    def getOutputFileName(file: File) =
      s"${config.outputFolder.get}\\${standardizeName(file)}"

    def standardizeName(file: File): String =
      file.getName
        .replaceAll("\\s", "_")
        .replaceAll(",", "_")

    Image.fromFile(fileFromPath(config.templateFile.get))
      .overlay(buildOverlayImage(image),config.frameOffsetX.get, config.frameOffsetY.get)
      .output(fileFromPath(getOutputFileName(image)))(JpegWriter())

    println(s"Calendar ${standardizeName(image)} done.")
  }
}
