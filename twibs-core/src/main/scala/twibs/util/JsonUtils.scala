/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.util

import scala.collection.Map
import scala.util.parsing.json.JSONFormat
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
    case string: String => stringToJsonString(string)
    case other => stringToJsonString(other.toString)
  }

  private def stringToJsonString(string: String) = "\"" + JSONFormat.quoteString(string) + "\""

  private def mapToJsonString(map: Map[_, _]): String =
    "{" + map.map(entry => stringToJsonString(entry._1.toString) + ":" + anyToJsonString(entry._2) + "").mkString(",") + "}"

  private def seqToJsonString(seq: scala.collection.Seq[Any]): String =
    if (seq.isEmpty || !seq.head.isInstanceOf[(_, _)]) {
      "[" + seq.map(entry => anyToJsonString(entry)).mkString(",") + "]"
    } else {
      "{" + seq.asInstanceOf[Seq[(_, _)]].map(entry => stringToJsonString(entry._1.toString) + ":" + anyToJsonString(entry._2) + "").mkString(",") + "}"
    }
}
