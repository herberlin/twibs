/*
 * Copyright (C) 2013-2015 by Michael Hombre Brinkmann
 */

package net.twibs.web

import com.google.common.cache.LoadingCache
import net.twibs.util.ContentRequest

trait CacheResponder extends Responder {
  protected def cache: LoadingCache[ContentRequest, Option[Response]]

  def respond(requestCacheKey: ContentRequest) =
    cache.get(requestCacheKey) match {
      case Some(response) =>
        if (!response.isCacheable)
          cache.invalidate(requestCacheKey)
        Some(response)
      case None =>
        cache.invalidate(requestCacheKey)
        None
    }
}
