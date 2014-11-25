/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import com.google.common.cache.{CacheLoader, CacheBuilder, LoadingCache}
import net.twibs.util.{Request, RequestCacheKey}
import concurrent.duration.Duration

class MemoryCachingResponder(delegate: Responder, cacheSizeInBytes: Long = 100000000, maxFileSizeInBytes: Long = 1000000) extends LoadingCacheResponder(delegate) {
  protected val cache: LoadingCache[RequestCacheKey, Option[Response]] = CacheBuilder.newBuilder().maximumSize(cacheSizeInBytes).build(loader)

  private def loader = new CacheLoader[RequestCacheKey, Option[Response]]() {
    def load(request: RequestCacheKey): Option[Response] = {
      delegate.respond(Request).map(response =>
        if (response.isInMemory || !response.isCacheable || response.length > maxFileSizeInBytes) response
        else new ByteArrayResponse {
          def isModified: Boolean = response.isModified

          def isCacheable: Boolean = response.isCacheable

          def lastModified: Long = response.lastModified

          def asBytes: Array[Byte] = response.asBytes

          def expiresOnClientAfter: Duration = response.expiresOnClientAfter

          def mimeType: String = response.mimeType
        })
    }
  }
}
