/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import com.google.common.cache.LoadingCache
import net.twibs.util.RequestCacheKey

trait CacheResponder extends Responder {
  protected def cache: LoadingCache[RequestCacheKey, Option[Response]]

  def respond(requestCacheKey: RequestCacheKey) =
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
