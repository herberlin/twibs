/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

abstract class LoadingCacheResponder(delegate: Responder) extends CacheResponder {
  def respond(request: Request): Option[Response] =
    request.use {
      val requestCacheKey = request.cacheKey
      if (!Request.useCache) {
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
