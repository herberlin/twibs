package twibs.web

import com.google.common.cache.LoadingCache

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
