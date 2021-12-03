package com.alexischevalier.calendarToolkit

import com.alexischevalier.calendarToolkit.CalendarToolkitApp.CalendarBatchToGenerate
import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.JpegWriter
import zio.blocking.{Blocking, effectBlocking}
import zio.console.Console
import zio.{IO, Task, ZIO}

import java.io.File
import java.nio.file.{Files, Path, Paths}


object CalendarMaker {

  def makeCalendars(batches: Seq[CalendarBatchToGenerate], parallelism: Int): ZIO[Console with Blocking, String, Unit] =
    for {
      images <- prepareBatches(batches)
      _ <- ZIO.foreachParN_(parallelism)(images) { case (image, calendarMetadata) =>
        for {
          _ <- generateCalendarForImage(image, calendarMetadata)
          _ <- zio.console.putStrLn(s"Calendar ${standardizeName(image)} done.").orElseFail("Failed to print to console")
        } yield ()
      }
      _ <- zio.console.putStrLn(s"All calendars have been generated.").orElseFail("Failed to print to console")
    } yield ()

  private def prepareBatches(batches: Seq[CalendarBatchToGenerate])=
    ZIO.foreach(batches) { batch =>
      for {
        inputFolder <- Task(Path.of(batch.inputFolder).toFile)
          .orElseFail(s"Failed to open input folder ${batch.inputFolder}")
        outputFolder <- Task(Path.of(batch.outputFolder).toFile)
          .orElseFail(s"Failed to open output folder ${batch.outputFolder}")
        templateFile <- Task(Path.of(batch.templateFile).toFile)
          .orElseFail(s"Failed to open template file ${batch.templateFile}")
        _ <- ZIO.fail(s"Invalid template file ${batch.templateFile}")
          .unless(templateFile.isFile)
        _ <- ZIO.fail(s"Template file ${batch.templateFile} is not an image")
          .unlessM(isFileAnImage(templateFile))
        inputFolderContent <- Task(inputFolder.listFiles().toSeq)
          .orElseFail(s"Failed to load input folder files ${batch.inputFolder}")
        files = inputFolderContent.filter(_.isFile)
        images <- ZIO.filter(files)(f => isFileAnImage(f)).map(_.map(i => (i, CalendarMetadata(inputFolder,
          outputFolder, templateFile, batch.frameWidth, batch.frameHeight, batch.frameOffsetX, batch.frameOffsetY))))
      } yield images
    }.map(_.flatten)

  private def isFileAnImage(file: File): IO[String, Boolean] =
    for {
      mimeType <- Task(Files.probeContentType(file.toPath))
        .orElseFail(s"Failed to determine mimeType of file ${file.toPath}")
      typeGroup = mimeType.split("/")(0)
    } yield typeGroup == "image"


  private def generateCalendarForImage(imageFile: File, calendarMetadata: CalendarMetadata): ZIO[Blocking, String, Unit] = {
    for {
      overlay <- buildOverlayImage(imageFile, calendarMetadata)
      outputFile = getOutputFileName(imageFile, calendarMetadata)
      _ <- effectBlocking {
        Image.fromFile(calendarMetadata.templateFile)
          .overlay(overlay, calendarMetadata.frameOffsetX, calendarMetadata.frameOffsetY)
          .output(outputFile)(JpegWriter())
      }.mapError(e => s"Failed to generate calendar for input image ${imageFile.toPath}, reason: $e")
    } yield ()

  }

  private def buildOverlayImage(imageFile: File, calendarMetadata: CalendarMetadata): ZIO[Blocking, String, Image] = {
    effectBlocking {
      val image = Image.fromFile(imageFile)
      val resizeDimensions = getResizeDimensions(image, calendarMetadata.frameWidth, calendarMetadata.frameHeight)
      val resizedImage = if (resizeDimensions.height > resizeDimensions.width) {
        image.scaleToWidth(resizeDimensions.width)
      } else {
        image.scaleToHeight(resizeDimensions.height)
      }

      resizedImage.resizeTo(calendarMetadata.frameWidth, calendarMetadata.frameHeight)
    }.mapError(e => s"Failed to convert input image ${imageFile.toPath}, reason: $e")
  }

  private def getResizeDimensions(image: Image, frameWidth: Int, frameHeight: Int): Dimensions = {
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

  private def getOutputFileName(file: File, calendarMetadata: CalendarMetadata) = {
    Paths.get(calendarMetadata.outputFolder.toPath.toString, standardizeName(file))
  }

  private def standardizeName(file: File): String =
    file.getName
      .replaceAll("\\s", "_")
      .replaceAll(",", "_")

  private final case class Dimensions(width: Int, height: Int)
  private final case class CalendarMetadata(
    inputFolder: File,
    outputFolder: File,
    templateFile: File,
    frameWidth: Int,
    frameHeight: Int,
    frameOffsetX: Int,
    frameOffsetY: Int
  )
}
