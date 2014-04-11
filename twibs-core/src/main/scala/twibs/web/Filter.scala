/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.web

import javax.servlet._
import javax.servlet.http._
import twibs.util.{SystemSettings, RunMode}
import util.DynamicVariable

class Filter extends javax.servlet.Filter {
  private var servletContextVar: ServletContext = null

  def servletContext = servletContextVar

  private var combiningResponderVar: CombiningResponder = null

  def combiningResponder = combiningResponderVar

  def createCombiningResponder(): CombiningResponder = new FilterResponder(this)

  override def init(filterConfig: FilterConfig): Unit = {
    servletContextVar = filterConfig.getServletContext
    combiningResponderVar = createCombiningResponder()
  }

  override def destroy(): Unit = combiningResponderVar.destroy()

  override def doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain): Unit =
    Filter._servletRequest.withValue(request) {
      Filter._servletResponse.withValue(response) {
        (request, response) match {
          case (httpRequest: HttpServletRequest, httpResponse: HttpServletResponse) =>
            doFilter(httpRequest, httpResponse, filterChain)
          case _ => filterChain.doFilter(request, response)
        }
      }
    }

  def doFilter(httpRequest: HttpServletRequest, httpResponse: HttpServletResponse, filterChain: FilterChain): Unit = {
    httpRequest.setCharacterEncoding("UTF-8")
    httpResponse.setCharacterEncoding("UTF-8")
    httpResponse.setHeader("X-Twibs", if (RunMode.isProduction) SystemSettings.Twibs.version else SystemSettings.Twibs.version + " - " + RunMode.name)
    val request = createRequest(httpRequest)
    request.use {
      combiningResponderVar.respond(request) match {
        case Some(response) =>
          new HttpResponseRenderer(request, response, httpRequest, httpResponse).render()
        case None =>
          new ApplicationResponder(new Responder() {
            override def respond(request: Request): Option[Response] = {
              filterChain.doFilter(httpRequest, httpResponse)
              None
            }
          }).respond(request)
      }
    }
  }

  def createRequest(httpRequest: HttpServletRequest): HttpRequest = new HttpRequestWithCommonsFileUpload(httpRequest)
}

object Filter {
  val _servletRequest = new DynamicVariable[ServletRequest](null)
  val _servletResponse = new DynamicVariable[ServletResponse](null)

  def currentServletRequest = _servletRequest.value

  def currentServletResponse = _servletResponse.value
}