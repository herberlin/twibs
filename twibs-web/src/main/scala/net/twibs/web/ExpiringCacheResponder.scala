/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import com.google.common.cache.{CacheLoader, CacheBuilder, LoadingCache}
import java.util.concurrent.TimeUnit
import net.twibs.util.{ResponseRequest, Request}

import scala.concurrent.duration._

class ExpiringCacheResponder(delegate: Responder, duration: Duration = 1 second) extends CacheResponder {
  def respond(request: Request): Option[Response] =
    request.use {
      val requestCacheKey = request.responseRequest
      if (!request.useCache) {
        cache.invalidate(requestCacheKey)
      }
      respond(requestCacheKey)
    }

  protected val cache: LoadingCache[ResponseRequest, Option[Response]] =
    CacheBuilder.newBuilder().expireAfterWrite(duration.toMillis, TimeUnit.MILLISECONDS).build(loader)

  private def loader = new CacheLoader[ResponseRequest, Option[Response]]() {
    def load(requestCacheKey: ResponseRequest): Option[Response] = delegate.respond(Request)
  }
}
