/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

includeFile("modernizr.js");
includeFile("ie8/html5shiv-printshiv.js");
includeFile("ie8/respond.js");
includeFile("ie8/jquery-1.11.1.js");
includeFile("twibs-base.js");
Modernizr.load({
    test: Modernizr.input.placeholder,
    nope: [
        '/inc/polyfills/jquery.placeholder.js'
    ]
});
