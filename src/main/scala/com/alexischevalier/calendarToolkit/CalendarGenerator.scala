package com.alexischevalier.calendarToolkit

import java.io.File
import java.net.URI

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.JpegWriter

class CalendarGenerator(config: Config) {
  import CalendarGenerator._

  def generateCalendarForImage(image: File): Unit = {
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
      file.getName.replaceAll("\\s", "")

    Image.fromFile(fileFromPath(config.templateFile.get))
      .overlay(buildOverlayImage(image),config.frameOffsetX.get, config.frameOffsetY.get)
      .output(fileFromPath(getOutputFileName(image)))(JpegWriter())

    println(s"Calendar ${standardizeName(image)} done.")
  }
}

object CalendarGenerator {
  case class Dimensions(width: Int, height: Int)
}
