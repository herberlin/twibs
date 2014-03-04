package twibs.util

import com.google.common.base.Charsets
import com.google.common.hash.Hashing
import com.google.common.io.BaseEncoding
import java.lang.StringBuilder

object StringUtils {
  /**
   * Returns the input string without whitespace converted to lower case replacing umlauts with corresponding double
   * character equivalents. All unknown characters are removed.
   *
   * Returns an empty string of the string was <code>null</code> or blank.
   *
   * @param input
	 * string to convert to computer label
   * @return an ASCII string without white spaces
   */
  def convertToComputerLabel(input: String): String =
    if (input == null || input.isEmpty) ""
    else convertToComputerLabelSecure(input)

  private def convertToComputerLabelSecure(input: String): String = {
    var wasSpace: Boolean = false
    val inputAsLowerCase: String = input.toLowerCase.trim
    val sb: StringBuilder = new StringBuilder()

    var i: Int = 0
    while (i < inputAsLowerCase.length) {
      val c = inputAsLowerCase.charAt(i)
      if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9') {
        wasSpace = false
        sb.append(c)
      }
      else c match {
        case 'ä' =>
          wasSpace = false
          sb.append("ae")
        case 'ö' =>
          wasSpace = false
          sb.append("oe")
        case 'ü' =>
          wasSpace = false
          sb.append("ue")
        case 'ß' =>
          wasSpace = false
          sb.append("ss")
        case '-' | ' ' | '\n' | '\t' =>
          if (!wasSpace) {
            sb.append("-")
            wasSpace = true
          }
        case '_' | '.' =>
          wasSpace = false
          sb.append(c)
        case _ => // Nothing
      }
      i += 1
    }
    sb.toString
  }

  def encryptPassword(salt: String, unencryptedPassword: String) = {
    var ba = Hashing.sha256().hashString(salt + unencryptedPassword, Charsets.UTF_8)
    for (i <- 0 to 997) {
      ba = Hashing.sha256().hashBytes(ba.asBytes())
    }
    BaseEncoding.base64().encode(ba.asBytes)
  }

  def encodeFilenameForContentDisposition(filename: String) = UrlUtils.encodeUrl(filename).replace("%3F", "?")
}
