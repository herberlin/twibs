package twibs.util

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
        def toJsonString: String = JsonUtils.toJsonAny(any)
      }
    }

  private def toJsonAny(any: Any): String = any match {
    case withToJson: Json => withToJson.toJsonString
    case null => "null"
    case v: Boolean => v.toString
    case v: Float => v.toString
    case v: Double => v.toString
    case v: Byte => v.toString
    case v: Short => v.toString
    case v: Int => v.toString
    case v: Long => v.toString
    case map: Map[_, _] => toJsonMap(map)
    case nb: NodeBuffer =>
      val ns: NodeSeq = nb
      toJsonString(ns.toString())
    case seq: NodeSeq => toJsonString(seq.toString())
    case seq: Seq[_] => toJsonSeq(seq)
    case string: String => toJsonString(string)
    case other => toJsonString(other.toString)
  }

  private def toJsonString(string: String) =
    "\"" + string.replace("\"", "\\\"").replace("\r\n", "\\n").replace("\r", "\\n").replace("\n", "\\n") + "\""

  private def toJsonMap(map: Map[_, _]): String =
    "{" + map.asInstanceOf[Map[Any, Any]].map(entry => "\"" + entry._1.toString + "\":" + toJsonAny(entry._2) + "").mkString(",") + "}"

  private def toJsonSeq(seq: scala.collection.Seq[Any]): String =
    if (seq.isEmpty || !seq.head.isInstanceOf[(_, _)]) {
      "[" + seq.map(entry => toJsonAny(entry)).mkString(",") + "]"
    } else {
      "{" + seq.asInstanceOf[Seq[(_, _)]].map(entry => "\"" + entry._1.toString + "\":" + toJsonAny(entry._2) + "").mkString(",") + "}"
    }
}
