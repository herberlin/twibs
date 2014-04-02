/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package twibs.web

import twibs.util.Parameters

case class RequestCacheKey(path: String, method: RequestMethod, domain: String, parameters: Parameters)