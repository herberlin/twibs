/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

var parseLessCss = function (source, path, compress, optimization) {
    var error = null;
    var result = "";

    window.less.Parser.fileLoader = function (originalHref, currentFileInfo, callback, env, modifyVars) {
        if (currentFileInfo && currentFileInfo.currentDirectory && !/^([a-z-]+:)?\//.test(originalHref)) {
            originalHref = currentFileInfo.currentDirectory + originalHref;
        }

        var newFileInfo = {
            currentDirectory: originalHref.replace(/^(.*[\/\\])[^\/\\]*$/, '$1'),
            filename: originalHref,
            entryPath: currentFileInfo.entryPath,
            rootpath: currentFileInfo.rootpath,
            rootFilename: currentFileInfo.rootFilename,
            relativeUrls: currentFileInfo.relativeUrls
        };

        try {
            callback(null, String(twibs.load(originalHref)), originalHref, newFileInfo);
        }
        catch (e) {
            error = e;
        }
    };

    var additionalData = {globalVars:{"context-path": ""}};

    new (window.less.Parser)({ paths: ["/"], optimization: optimization}).parse(source,
        function (e, root) {
            if (e != null) error = e;
            else result = compress ? exports.compressor.cssmin(root.toCSS()) : root.toCSS();
        }, additionalData);

    if (error) throw error;
    return result;
};
