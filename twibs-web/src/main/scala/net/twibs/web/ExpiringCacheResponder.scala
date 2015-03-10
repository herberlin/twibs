/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import com.google.common.cache.{CacheLoader, CacheBuilder, LoadingCache}
import java.util.concurrent.TimeUnit
import net.twibs.util.{ContentRequest, Request}

import scala.concurrent.duration._

class ExpiringCacheResponder(delegate: Responder, duration: Duration = 1 second) extends CacheResponder {
  def respond(request: Request): Option[Response] =
    request.use {
      val requestCacheKey = request.contentRequest
      if (!request.useCache) {
        cache.invalidate(requestCacheKey)
      }
      respond(requestCacheKey)
    }

  protected val cache: LoadingCache[ContentRequest, Option[Response]] =
    CacheBuilder.newBuilder().expireAfterWrite(duration.toMillis, TimeUnit.MILLISECONDS).build(loader)

  private def loader = new CacheLoader[ContentRequest, Option[Response]]() {
    def load(requestCacheKey: ContentRequest): Option[Response] = delegate.respond(Request)
  }
}
