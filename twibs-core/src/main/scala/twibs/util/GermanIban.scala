/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import com.google.common.io.ByteStreams
import io.Source
import java.io._
import java.net.URL
import java.util.zip.ZipFile
import twibs.util.Predef._

case class GermanIban(str: String) {
  val string = str.toUpperCase.replace(" ", "")

  def valid = string.length == 22 && country == "DE" && GermanIban.blzToBicAndName.contains(blz) && validateChecksum

  def checksumString = (string.substring(4) + string.substring(0, 4)).map {
    case c@('0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9') => c.toString
    case c => (c - 'A' + 10).toString
  }.mkString

  def checksum = BigInt(checksumString)

  def validateChecksum = checksum % 97 == BigInt(1)

  def country = string.substring(0, 2)

  def checkDigits = string.substring(2, 2)

  def blz = string.substring(4, 12)

  def bic = GermanIban.blzToBicAndName(blz)._1

  def bankName = GermanIban.blzToBicAndName(blz)._2

  def kto = string.substring(12)
}

object GermanIban {
  private def inUrl = new URL("http://www.bundesbank.de/Redaktion/DE/Downloads/Aufgaben/Unbarer_Zahlungsverkehr/Bankleitzahlen/2014_09_07/blz_2014_06_09_txt_zip.zip?__blob=publicationFile")

  private def outfile = new File("src/main/resources/twibs/util/de_blz.ser")

  def isValidBlz(blzString: String) = blzToBicAndName.contains(blzString)

  lazy val blzToBicAndName = Option(getClass.getResourceAsStream("de_blz.ser")) match {
    case Some(stream) => new ObjectInputStream(stream) useAndClose {_.readObject.asInstanceOf[Map[String, (String, String)]]}
    case None => loadAndCache()
  }

  private def loadAndCache() = {
    val ret = blzToNameFromUrl
    if (outfile.getParentFile.canWrite) {
      new ObjectOutputStream(new FileOutputStream(outfile)) useAndClose {_.writeObject(ret)}
    }
    ret
  }

  private def blzToNameFromUrl = {
    val infile = File.createTempFile("blz", "zip")
    infile.deleteOnExit()

    new FileOutputStream(infile) useAndClose {
      os =>
        inUrl.openStream useAndClose {
          is => {}
            ByteStreams.copy(is, os)
        }
    }

    val zipFile = new ZipFile(infile)
    zipFile.getInputStream(zipFile.entries().nextElement()) useAndClose {
      is =>
        (for (line <- Source.fromInputStream(is, "ISO-8859-15").getLines() if line.charAt(8) == '1') yield
          line.substring(0, 8) ->(line.substring(139, 150), line.substring(9, 60).trim)).toMap
    }
  }
}
