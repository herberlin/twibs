/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util.{Request, RequestCacheKey}

abstract class LoadingCacheResponder(delegate: Responder) extends CacheResponder {
  def respond(request: Request): Option[Response] =
    request.use {
      val requestCacheKey = request.cacheKey
      if (!request.useCache) {
        cache.invalidate(requestCacheKey)
      }
      getIfPresentAndNotModified(requestCacheKey) getOrElse respond(requestCacheKey)
    }

  def getIfPresentAndNotModified(requestCacheKey: RequestCacheKey) =
    Option(cache.getIfPresent(requestCacheKey)).flatMap {
      case Some(response) if !response.isModified =>
        Some(Some(response))
      case any =>
        cache.invalidate(requestCacheKey)
        None
    }
}
