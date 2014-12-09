/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import javax.servlet.FilterChain
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.google.common.base.Charsets
import net.twibs.util.{CollectionUtils, Parameters, Request, Upload}
import org.apache.sling.api.request.RequestParameter
import org.apache.sling.api.{SlingHttpServletRequest, SlingHttpServletResponse}

import scala.collection.JavaConverters._
import scala.util.DynamicVariable

class SlingFilter extends Filter {
  override def doFilter(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, filterChain: FilterChain): Unit =
    (httpRequest, httpResponse) match {
      case (slingRequest: SlingHttpServletRequest, slingResponse: SlingHttpServletResponse) =>
        CurrentSlingHttpServletRequest.withValue(slingRequest) {
          CurrentSlingHttpServletResponse.withValue(slingResponse) {
            super.doFilter(httpRequest, httpResponse, filterChain)
          }
        }
      case _ => super.doFilter(httpRequest, httpResponse, filterChain)
    }

  override def createRequest(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse): Request =
    (httpRequest, httpResponse) match {
      case (slingRequest: SlingHttpServletRequest, slingResponse: SlingHttpServletResponse) =>
        HttpServletRequestWithSlingUpload(slingRequest, slingResponse)
      case _ => super.createRequest(httpRequest, httpResponse)
    }
}

object HttpServletRequestWithSlingUpload extends HttpServletUtils {
  def apply(httpServletRequest: SlingHttpServletRequest, httpServletResponse: HttpServletResponse) = {
    lazy val allRequestParameters: Seq[(String, RequestParameter)] =
      httpServletRequest.getRequestParameterMap.asScala.map(e => e._2.map(e._1 ->).toList).flatten.toSeq

    def parameters: Parameters = removeUnderscoreParameterSetByJQuery(urlParameters ++ multiPartParameters)

    def urlParameters: Map[String, Seq[String]] = httpServletRequest.asInstanceOf[HttpServletRequest].getParameterMap.asScala.map(entry => (entry._1, entry._2.toSeq)).toMap

    def multiPartParameters: Map[String, Seq[String]] = CollectionUtils.zipToMap(formFieldsFromMultipartRequest.map(e => (e._1, e._2.getString(Charsets.UTF_8.name))))

    def uploads: Map[String, Seq[Upload]] = CollectionUtils.zipToMap(fileItemsFromMultipartRequest.map(e => (e._1, toUpload(e._1, e._2))))

    def toUpload(nameArg: String, requestParameterArg: RequestParameter) = new Upload() {
      val requestParameter = requestParameterArg

      def name = new String(requestParameter.getFileName.getBytes(Charsets.ISO_8859_1.name), Charsets.UTF_8.name)

      def size = requestParameter.getSize

      def stream = requestParameter.getInputStream
    }

    def formFieldsFromMultipartRequest: Seq[(String, RequestParameter)] = allRequestParameters.filter(_._2.isFormField)

    def fileItemsFromMultipartRequest: Seq[(String, RequestParameter)] = allRequestParameters.filter(e => isValidFileItem(e._2))

    def isValidFileItem(requestParameter: RequestParameter) = !requestParameter.isFormField && (requestParameter.getSize > 0 || !requestParameter.getFileName.isEmpty)

    HttpServletRequestBase(httpServletRequest, httpServletResponse).copy(parameters = parameters, uploads = uploads)
  }
}

object CurrentSlingHttpServletRequest extends DynamicVariable[SlingHttpServletRequest](null) {
  implicit def unwrap(companion: CurrentSlingHttpServletRequest.type): SlingHttpServletRequest = value
}

object CurrentSlingHttpServletResponse extends DynamicVariable[SlingHttpServletResponse](null) {
  implicit def unwrap(companion: CurrentSlingHttpServletResponse.type): SlingHttpServletResponse = value
}

