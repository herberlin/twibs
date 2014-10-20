/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

package net.twibs.web

import net.twibs.util.Parameters

case class RequestCacheKey(path: String, method: RequestMethod, domain: String, parameters: Parameters)