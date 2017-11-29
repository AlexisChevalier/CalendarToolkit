package com.alexischevalier.calendarToolkit

import java.io.File

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.JpegWriter

object CalendarGenerator {
  def fileFromPath(path: String): File = {
    new File(path)
  }

  def test(config: Config): Unit = {
    val templateImage = Image.fromFile(fileFromPath(config.templateFile.get))

    templateImage
      .overlay(buildOverlayImage(config),config.frameOffsetX.get, config.frameOffsetY.get)
      .output(fileFromPath(config.outputFolder.get))(JpegWriter())
  }

  def buildOverlayImage(config: Config): Image = {
    val image = Image.fromFile(fileFromPath(config.inputFolder.get))
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

  case class Dimensions(width: Int, height: Int)
}
