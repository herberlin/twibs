/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.util

import scala.collection.Map
import xml.{NodeSeq, NodeBuffer}

trait Json {
  def toJsonString: String
}

object JsonUtils {
  implicit def asJson(any: Any) =
    any match {
      case json: Json => json
      case _ => new Json {
        def toJsonString: String = JsonUtils.anyToJsonString(any)
      }
    }

  private def anyToJsonString(any: Any): String = any match {
    case json: Json => json.toJsonString
    case null => "null"
    case _: Boolean | _: Float | _: Double | _: Byte | _: Short | _: Int | _: Long => any.toString
    case map: Map[_, _] => mapToJsonString(map)
    case nb: NodeBuffer => stringToJsonString(NodeSeq.seqToNodeSeq(nb).toString())
    case seq: NodeSeq => stringToJsonString(seq.toString())
    case seq: Seq[_] => seqToJsonString(seq)
    case arr: Array[_] => seqToJsonString(arr.toSeq)
    case string: String => stringToJsonString(string)
    case other => stringToJsonString(other.toString)
  }

  private def stringToJsonString(string: String) = "\"" + quoteString(string) + "\""

  private def mapToJsonString(map: Map[_, _]): String =
    "{" + map.map(entry => stringToJsonString(entry._1.toString) + ":" + anyToJsonString(entry._2) + "").mkString(",") + "}"

  private def seqToJsonString(seq: scala.collection.Seq[Any]): String =
    if (seq.isEmpty || !seq.head.isInstanceOf[(_, _)]) {
      "[" + seq.map(entry => anyToJsonString(entry)).mkString(",") + "]"
    } else {
      "{" + seq.asInstanceOf[Seq[(_, _)]].map(entry => stringToJsonString(entry._1.toString) + ":" + anyToJsonString(entry._2) + "").mkString(",") + "}"
    }

  // From Scala 2.10 JSONFormat wich is deprecated in 2.11 (for what ever reason)
  private def quoteString(s: String): String =
    s.map {
      case '"' => "\\\""
      case '\\' => "\\\\"
      case '/' => "\\/"
      case '\b' => "\\b"
      case '\f' => "\\f"
      case '\n' => "\\n"
      case '\r' => "\\r"
      case '\t' => "\\t"
      /* We'll unicode escape any control characters. These include:
       * 0x0 -> 0x1f  : ASCII Control (C0 Control Codes)
       * 0x7f         : ASCII DELETE
       * 0x80 -> 0x9f : C1 Control Codes
       *
       * Per RFC4627, section 2.5, we're not technically required to
       * encode the C1 codes, but we do to be safe.
       */
      case c if (c >= '\u0000' && c <= '\u001f') || (c >= '\u007f' && c <= '\u009f') => f"\\u${c.toInt}%04x"
      case c => c
    }.mkString
}
