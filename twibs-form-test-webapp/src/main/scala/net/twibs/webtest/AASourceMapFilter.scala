/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.webtest

import javax.servlet.annotation.WebFilter
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import javax.servlet.{FilterChain, FilterConfig, ServletRequest, ServletResponse}

import net.twibs.util.RunMode

@WebFilter(urlPatterns=Array("*.js", "*.css"))
class AASourceMapFilter extends javax.servlet.Filter {
  override def init(filterConfig: FilterConfig): Unit = ()

  override def doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain): Unit = {
    if (RunMode.isDevelopment)
      (request, response) match {
        case (req: HttpServletRequest, resp: HttpServletResponse) => resp.setHeader("SourceMap", req.getRequestURI + ".map")
        case _ => ()
      }
    chain.doFilter(request, response)
  }

  override def destroy(): Unit = ()
}
