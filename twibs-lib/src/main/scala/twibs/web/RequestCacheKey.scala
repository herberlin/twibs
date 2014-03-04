package twibs.web

import twibs.util.Parameters

case class RequestCacheKey(path: String, method: RequestMethod, domain: String, parameters: Parameters)