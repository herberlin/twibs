window.Modernizr = function(window, document, undefined) {
    var version = "2.8.3", Modernizr = {}, enableClasses = true, docElement = document.documentElement, mod = "modernizr", modElem = document.createElement(mod), mStyle = modElem.style, inputElem = document.createElement("input"), toString = {}.toString, prefixes = " -webkit- -moz- -o- -ms- ".split(" "), omPrefixes = "Webkit Moz O ms", cssomPrefixes = omPrefixes.split(" "), domPrefixes = omPrefixes.toLowerCase().split(" "), tests = {}, inputs = {}, attrs = {}, classes = [], slice = classes.slice, featureName, isEventSupported = function() {
        var TAGNAMES = {
            select: "input",
            change: "input",
            submit: "form",
            reset: "form",
            error: "img",
            load: "img",
            abort: "img"
        };
        function isEventSupported(eventName, element) {
            element = element || document.createElement(TAGNAMES[eventName] || "div");
            eventName = "on" + eventName;
            var isSupported = eventName in element;
            if (!isSupported) {
                if (!element.setAttribute) {
                    element = document.createElement("div");
                }
                if (element.setAttribute && element.removeAttribute) {
                    element.setAttribute(eventName, "");
                    isSupported = is(element[eventName], "function");
                    if (!is(element[eventName], "undefined")) {
                        element[eventName] = undefined;
                    }
                    element.removeAttribute(eventName);
                }
            }
            element = null;
            return isSupported;
        }
        return isEventSupported;
    }(), _hasOwnProperty = {}.hasOwnProperty, hasOwnProp;
    if (!is(_hasOwnProperty, "undefined") && !is(_hasOwnProperty.call, "undefined")) {
        hasOwnProp = function(object, property) {
            return _hasOwnProperty.call(object, property);
        };
    } else {
        hasOwnProp = function(object, property) {
            return property in object && is(object.constructor.prototype[property], "undefined");
        };
    }
    if (!Function.prototype.bind) {
        Function.prototype.bind = function bind(that) {
            var target = this;
            if (typeof target != "function") {
                throw new TypeError();
            }
            var args = slice.call(arguments, 1), bound = function() {
                if (this instanceof bound) {
                    var F = function() {};
                    F.prototype = target.prototype;
                    var self = new F();
                    var result = target.apply(self, args.concat(slice.call(arguments)));
                    if (Object(result) === result) {
                        return result;
                    }
                    return self;
                } else {
                    return target.apply(that, args.concat(slice.call(arguments)));
                }
            };
            return bound;
        };
    }
    function setCss(str) {
        mStyle.cssText = str;
    }
    function setCssAll(str1, str2) {
        return setCss(prefixes.join(str1 + ";") + (str2 || ""));
    }
    function is(obj, type) {
        return typeof obj === type;
    }
    function contains(str, substr) {
        return !!~("" + str).indexOf(substr);
    }
    function testProps(props, prefixed) {
        for (var i in props) {
            var prop = props[i];
            if (!contains(prop, "-") && mStyle[prop] !== undefined) {
                return prefixed == "pfx" ? prop : true;
            }
        }
        return false;
    }
    function testDOMProps(props, obj, elem) {
        for (var i in props) {
            var item = obj[props[i]];
            if (item !== undefined) {
                if (elem === false) return props[i];
                if (is(item, "function")) {
                    return item.bind(elem || obj);
                }
                return item;
            }
        }
        return false;
    }
    function testPropsAll(prop, prefixed, elem) {
        var ucProp = prop.charAt(0).toUpperCase() + prop.slice(1), props = (prop + " " + cssomPrefixes.join(ucProp + " ") + ucProp).split(" ");
        if (is(prefixed, "string") || is(prefixed, "undefined")) {
            return testProps(props, prefixed);
        } else {
            props = (prop + " " + domPrefixes.join(ucProp + " ") + ucProp).split(" ");
            return testDOMProps(props, prefixed, elem);
        }
    }
    tests["history"] = function() {
        return !!(window.history && history.pushState);
    };
    tests["borderradius"] = function() {
        return testPropsAll("borderRadius");
    };
    tests["boxshadow"] = function() {
        return testPropsAll("boxShadow");
    };
    function webforms() {
        Modernizr["input"] = function(props) {
            for (var i = 0, len = props.length; i < len; i++) {
                attrs[props[i]] = !!(props[i] in inputElem);
            }
            if (attrs.list) {
                attrs.list = !!(document.createElement("datalist") && window.HTMLDataListElement);
            }
            return attrs;
        }("autocomplete autofocus list placeholder max min multiple pattern required step".split(" "));
    }
    for (var feature in tests) {
        if (hasOwnProp(tests, feature)) {
            featureName = feature.toLowerCase();
            Modernizr[featureName] = tests[feature]();
            classes.push((Modernizr[featureName] ? "" : "no-") + featureName);
        }
    }
    Modernizr.input || webforms();
    Modernizr.addTest = function(feature, test) {
        if (typeof feature == "object") {
            for (var key in feature) {
                if (hasOwnProp(feature, key)) {
                    Modernizr.addTest(key, feature[key]);
                }
            }
        } else {
            feature = feature.toLowerCase();
            if (Modernizr[feature] !== undefined) {
                return Modernizr;
            }
            test = typeof test == "function" ? test() : test;
            if (typeof enableClasses !== "undefined" && enableClasses) {
                docElement.className += " " + (test ? "" : "no-") + feature;
            }
            Modernizr[feature] = test;
        }
        return Modernizr;
    };
    setCss("");
    modElem = inputElem = null;
    Modernizr._version = version;
    Modernizr._prefixes = prefixes;
    Modernizr._domPrefixes = domPrefixes;
    Modernizr._cssomPrefixes = cssomPrefixes;
    Modernizr.hasEvent = isEventSupported;
    Modernizr.testProp = function(prop) {
        return testProps([ prop ]);
    };
    Modernizr.testAllProps = testPropsAll;
    docElement.className = docElement.className.replace(/(^|\s)no-js(\s|$)/, "$1$2") + (enableClasses ? " js " + classes.join(" ") : "");
    return Modernizr;
}(this, this.document);

(function(a, b, c) {
    function d(a) {
        return "[object Function]" == o.call(a);
    }
    function e(a) {
        return "string" == typeof a;
    }
    function f() {}
    function g(a) {
        return !a || "loaded" == a || "complete" == a || "uninitialized" == a;
    }
    function h() {
        var a = p.shift();
        q = 1, a ? a.t ? m(function() {
            ("c" == a.t ? B.injectCss : B.injectJs)(a.s, 0, a.a, a.x, a.e, 1);
        }, 0) : (a(), h()) : q = 0;
    }
    function i(a, c, d, e, f, i, j) {
        function k(b) {
            if (!o && g(l.readyState) && (u.r = o = 1, !q && h(), l.onload = l.onreadystatechange = null, 
            b)) {
                "img" != a && m(function() {
                    t.removeChild(l);
                }, 50);
                for (var d in y[c]) y[c].hasOwnProperty(d) && y[c][d].onload();
            }
        }
        var j = j || B.errorTimeout, l = b.createElement(a), o = 0, r = 0, u = {
            t: d,
            s: c,
            e: f,
            a: i,
            x: j
        };
        1 === y[c] && (r = 1, y[c] = []), "object" == a ? l.data = c : (l.src = c, l.type = a), 
        l.width = l.height = "0", l.onerror = l.onload = l.onreadystatechange = function() {
            k.call(this, r);
        }, p.splice(e, 0, u), "img" != a && (r || 2 === y[c] ? (t.insertBefore(l, s ? null : n), 
        m(k, j)) : y[c].push(l));
    }
    function j(a, b, c, d, f) {
        return q = 0, b = b || "j", e(a) ? i("c" == b ? v : u, a, b, this.i++, c, d, f) : (p.splice(this.i++, 0, a), 
        1 == p.length && h()), this;
    }
    function k() {
        var a = B;
        return a.loader = {
            load: j,
            i: 0
        }, a;
    }
    var l = b.documentElement, m = a.setTimeout, n = b.getElementsByTagName("script")[0], o = {}.toString, p = [], q = 0, r = "MozAppearance" in l.style, s = r && !!b.createRange().compareNode, t = s ? l : n.parentNode, l = a.opera && "[object Opera]" == o.call(a.opera), l = !!b.attachEvent && !l, u = r ? "object" : l ? "script" : "img", v = l ? "script" : u, w = Array.isArray || function(a) {
        return "[object Array]" == o.call(a);
    }, x = [], y = {}, z = {
        timeout: function(a, b) {
            return b.length && (a.timeout = b[0]), a;
        }
    }, A, B;
    B = function(a) {
        function b(a) {
            var a = a.split("!"), b = x.length, c = a.pop(), d = a.length, c = {
                url: c,
                origUrl: c,
                prefixes: a
            }, e, f, g;
            for (f = 0; f < d; f++) g = a[f].split("="), (e = z[g.shift()]) && (c = e(c, g));
            for (f = 0; f < b; f++) c = x[f](c);
            return c;
        }
        function g(a, e, f, g, h) {
            var i = b(a), j = i.autoCallback;
            i.url.split(".").pop().split("?").shift(), i.bypass || (e && (e = d(e) ? e : e[a] || e[g] || e[a.split("/").pop().split("?")[0]]), 
            i.instead ? i.instead(a, e, f, g, h) : (y[i.url] ? i.noexec = !0 : y[i.url] = 1, 
            f.load(i.url, i.forceCSS || !i.forceJS && "css" == i.url.split(".").pop().split("?").shift() ? "c" : c, i.noexec, i.attrs, i.timeout), 
            (d(e) || d(j)) && f.load(function() {
                k(), e && e(i.origUrl, h, g), j && j(i.origUrl, h, g), y[i.url] = 2;
            })));
        }
        function h(a, b) {
            function c(a, c) {
                if (a) {
                    if (e(a)) c || (j = function() {
                        var a = [].slice.call(arguments);
                        k.apply(this, a), l();
                    }), g(a, j, b, 0, h); else if (Object(a) === a) for (n in m = function() {
                        var b = 0, c;
                        for (c in a) a.hasOwnProperty(c) && b++;
                        return b;
                    }(), a) a.hasOwnProperty(n) && (!c && !--m && (d(j) ? j = function() {
                        var a = [].slice.call(arguments);
                        k.apply(this, a), l();
                    } : j[n] = function(a) {
                        return function() {
                            var b = [].slice.call(arguments);
                            a && a.apply(this, b), l();
                        };
                    }(k[n])), g(a[n], j, b, n, h));
                } else !c && l();
            }
            var h = !!a.test, i = a.load || a.both, j = a.callback || f, k = j, l = a.complete || f, m, n;
            c(h ? a.yep : a.nope, !!i), i && c(i);
        }
        var i, j, l = this.yepnope.loader;
        if (e(a)) g(a, 0, l, 0); else if (w(a)) for (i = 0; i < a.length; i++) j = a[i], 
        e(j) ? g(j, 0, l, 0) : w(j) ? B(j) : Object(j) === j && h(j, l); else Object(a) === a && h(a, l);
    }, B.addPrefix = function(a, b) {
        z[a] = b;
    }, B.addFilter = function(a) {
        x.push(a);
    }, B.errorTimeout = 1e4, null == b.readyState && b.addEventListener && (b.readyState = "loading", 
    b.addEventListener("DOMContentLoaded", A = function() {
        b.removeEventListener("DOMContentLoaded", A, 0), b.readyState = "complete";
    }, 0)), a.yepnope = k(), a.yepnope.executeStack = h, a.yepnope.injectJs = function(a, c, d, e, i, j) {
        var k = b.createElement("script"), l, o, e = e || B.errorTimeout;
        k.src = a;
        for (o in d) k.setAttribute(o, d[o]);
        c = j ? h : c || f, k.onreadystatechange = k.onload = function() {
            !l && g(k.readyState) && (l = 1, c(), k.onload = k.onreadystatechange = null);
        }, m(function() {
            l || (l = 1, c(1));
        }, e), i ? k.onload() : n.parentNode.insertBefore(k, n);
    }, a.yepnope.injectCss = function(a, c, d, e, g, i) {
        var e = b.createElement("link"), j, c = i ? h : c || f;
        e.href = a, e.rel = "stylesheet", e.type = "text/css";
        for (j in d) e.setAttribute(j, d[j]);
        g || (n.parentNode.insertBefore(e, n), m(c, 0));
    };
})(this, document);

Modernizr.load = function() {
    yepnope.apply(window, [].slice.call(arguments, 0));
};

if (typeof jQuery === "undefined") {
    throw new Error("Bootstrap's JavaScript requires jQuery");
}

+function($) {
    "use strict";
    var version = $.fn.jquery.split(" ")[0].split(".");
    if (version[0] < 2 && version[1] < 9 || version[0] == 1 && version[1] == 9 && version[2] < 1) {
        throw new Error("Bootstrap's JavaScript requires jQuery version 1.9.1 or higher");
    }
}(jQuery);

+function($) {
    "use strict";
    function transitionEnd() {
        var el = document.createElement("bootstrap");
        var transEndEventNames = {
            WebkitTransition: "webkitTransitionEnd",
            MozTransition: "transitionend",
            OTransition: "oTransitionEnd otransitionend",
            transition: "transitionend"
        };
        for (var name in transEndEventNames) {
            if (el.style[name] !== undefined) {
                return {
                    end: transEndEventNames[name]
                };
            }
        }
        return false;
    }
    $.fn.emulateTransitionEnd = function(duration) {
        var called = false;
        var $el = this;
        $(this).one("bsTransitionEnd", function() {
            called = true;
        });
        var callback = function() {
            if (!called) $($el).trigger($.support.transition.end);
        };
        setTimeout(callback, duration);
        return this;
    };
    $(function() {
        $.support.transition = transitionEnd();
        if (!$.support.transition) return;
        $.event.special.bsTransitionEnd = {
            bindType: $.support.transition.end,
            delegateType: $.support.transition.end,
            handle: function(e) {
                if ($(e.target).is(this)) return e.handleObj.handler.apply(this, arguments);
            }
        };
    });
}(jQuery);

+function($) {
    "use strict";
    var dismiss = '[data-dismiss="alert"]';
    var Alert = function(el) {
        $(el).on("click", dismiss, this.close);
    };
    Alert.VERSION = "3.3.4";
    Alert.TRANSITION_DURATION = 150;
    Alert.prototype.close = function(e) {
        var $this = $(this);
        var selector = $this.attr("data-target");
        if (!selector) {
            selector = $this.attr("href");
            selector = selector && selector.replace(/.*(?=#[^\s]*$)/, "");
        }
        var $parent = $(selector);
        if (e) e.preventDefault();
        if (!$parent.length) {
            $parent = $this.closest(".alert");
        }
        $parent.trigger(e = $.Event("close.bs.alert"));
        if (e.isDefaultPrevented()) return;
        $parent.removeClass("in");
        function removeElement() {
            $parent.detach().trigger("closed.bs.alert").remove();
        }
        $.support.transition && $parent.hasClass("fade") ? $parent.one("bsTransitionEnd", removeElement).emulateTransitionEnd(Alert.TRANSITION_DURATION) : removeElement();
    };
    function Plugin(option) {
        return this.each(function() {
            var $this = $(this);
            var data = $this.data("bs.alert");
            if (!data) $this.data("bs.alert", data = new Alert(this));
            if (typeof option == "string") data[option].call($this);
        });
    }
    var old = $.fn.alert;
    $.fn.alert = Plugin;
    $.fn.alert.Constructor = Alert;
    $.fn.alert.noConflict = function() {
        $.fn.alert = old;
        return this;
    };
    $(document).on("click.bs.alert.data-api", dismiss, Alert.prototype.close);
}(jQuery);

+function($) {
    "use strict";
    var Button = function(element, options) {
        this.$element = $(element);
        this.options = $.extend({}, Button.DEFAULTS, options);
        this.isLoading = false;
    };
    Button.VERSION = "3.3.4";
    Button.DEFAULTS = {
        loadingText: "loading..."
    };
    Button.prototype.setState = function(state) {
        var d = "disabled";
        var $el = this.$element;
        var val = $el.is("input") ? "val" : "html";
        var data = $el.data();
        state = state + "Text";
        if (data.resetText == null) $el.data("resetText", $el[val]());
        setTimeout($.proxy(function() {
            $el[val](data[state] == null ? this.options[state] : data[state]);
            if (state == "loadingText") {
                this.isLoading = true;
                $el.addClass(d).attr(d, d);
            } else if (this.isLoading) {
                this.isLoading = false;
                $el.removeClass(d).removeAttr(d);
            }
        }, this), 0);
    };
    Button.prototype.toggle = function() {
        var changed = true;
        var $parent = this.$element.closest('[data-toggle="buttons"]');
        if ($parent.length) {
            var $input = this.$element.find("input");
            if ($input.prop("type") == "radio") {
                if ($input.prop("checked") && this.$element.hasClass("active")) changed = false; else $parent.find(".active").removeClass("active");
            }
            if (changed) $input.prop("checked", !this.$element.hasClass("active")).trigger("change");
        } else {
            this.$element.attr("aria-pressed", !this.$element.hasClass("active"));
        }
        if (changed) this.$element.toggleClass("active");
    };
    function Plugin(option) {
        return this.each(function() {
            var $this = $(this);
            var data = $this.data("bs.button");
            var options = typeof option == "object" && option;
            if (!data) $this.data("bs.button", data = new Button(this, options));
            if (option == "toggle") data.toggle(); else if (option) data.setState(option);
        });
    }
    var old = $.fn.button;
    $.fn.button = Plugin;
    $.fn.button.Constructor = Button;
    $.fn.button.noConflict = function() {
        $.fn.button = old;
        return this;
    };
    $(document).on("click.bs.button.data-api", '[data-toggle^="button"]', function(e) {
        var $btn = $(e.target);
        if (!$btn.hasClass("btn")) $btn = $btn.closest(".btn");
        Plugin.call($btn, "toggle");
        e.preventDefault();
    }).on("focus.bs.button.data-api blur.bs.button.data-api", '[data-toggle^="button"]', function(e) {
        $(e.target).closest(".btn").toggleClass("focus", /^focus(in)?$/.test(e.type));
    });
}(jQuery);

+function($) {
    "use strict";
    var Carousel = function(element, options) {
        this.$element = $(element);
        this.$indicators = this.$element.find(".carousel-indicators");
        this.options = options;
        this.paused = null;
        this.sliding = null;
        this.interval = null;
        this.$active = null;
        this.$items = null;
        this.options.keyboard && this.$element.on("keydown.bs.carousel", $.proxy(this.keydown, this));
        this.options.pause == "hover" && !("ontouchstart" in document.documentElement) && this.$element.on("mouseenter.bs.carousel", $.proxy(this.pause, this)).on("mouseleave.bs.carousel", $.proxy(this.cycle, this));
    };
    Carousel.VERSION = "3.3.4";
    Carousel.TRANSITION_DURATION = 600;
    Carousel.DEFAULTS = {
        interval: 5e3,
        pause: "hover",
        wrap: true,
        keyboard: true
    };
    Carousel.prototype.keydown = function(e) {
        if (/input|textarea/i.test(e.target.tagName)) return;
        switch (e.which) {
          case 37:
            this.prev();
            break;

          case 39:
            this.next();
            break;

          default:
            return;
        }
        e.preventDefault();
    };
    Carousel.prototype.cycle = function(e) {
        e || (this.paused = false);
        this.interval && clearInterval(this.interval);
        this.options.interval && !this.paused && (this.interval = setInterval($.proxy(this.next, this), this.options.interval));
        return this;
    };
    Carousel.prototype.getItemIndex = function(item) {
        this.$items = item.parent().children(".item");
        return this.$items.index(item || this.$active);
    };
    Carousel.prototype.getItemForDirection = function(direction, active) {
        var activeIndex = this.getItemIndex(active);
        var willWrap = direction == "prev" && activeIndex === 0 || direction == "next" && activeIndex == this.$items.length - 1;
        if (willWrap && !this.options.wrap) return active;
        var delta = direction == "prev" ? -1 : 1;
        var itemIndex = (activeIndex + delta) % this.$items.length;
        return this.$items.eq(itemIndex);
    };
    Carousel.prototype.to = function(pos) {
        var that = this;
        var activeIndex = this.getItemIndex(this.$active = this.$element.find(".item.active"));
        if (pos > this.$items.length - 1 || pos < 0) return;
        if (this.sliding) return this.$element.one("slid.bs.carousel", function() {
            that.to(pos);
        });
        if (activeIndex == pos) return this.pause().cycle();
        return this.slide(pos > activeIndex ? "next" : "prev", this.$items.eq(pos));
    };
    Carousel.prototype.pause = function(e) {
        e || (this.paused = true);
        if (this.$element.find(".next, .prev").length && $.support.transition) {
            this.$element.trigger($.support.transition.end);
            this.cycle(true);
        }
        this.interval = clearInterval(this.interval);
        return this;
    };
    Carousel.prototype.next = function() {
        if (this.sliding) return;
        return this.slide("next");
    };
    Carousel.prototype.prev = function() {
        if (this.sliding) return;
        return this.slide("prev");
    };
    Carousel.prototype.slide = function(type, next) {
        var $active = this.$element.find(".item.active");
        var $next = next || this.getItemForDirection(type, $active);
        var isCycling = this.interval;
        var direction = type == "next" ? "left" : "right";
        var that = this;
        if ($next.hasClass("active")) return this.sliding = false;
        var relatedTarget = $next[0];
        var slideEvent = $.Event("slide.bs.carousel", {
            relatedTarget: relatedTarget,
            direction: direction
        });
        this.$element.trigger(slideEvent);
        if (slideEvent.isDefaultPrevented()) return;
        this.sliding = true;
        isCycling && this.pause();
        if (this.$indicators.length) {
            this.$indicators.find(".active").removeClass("active");
            var $nextIndicator = $(this.$indicators.children()[this.getItemIndex($next)]);
            $nextIndicator && $nextIndicator.addClass("active");
        }
        var slidEvent = $.Event("slid.bs.carousel", {
            relatedTarget: relatedTarget,
            direction: direction
        });
        if ($.support.transition && this.$element.hasClass("slide")) {
            $next.addClass(type);
            $next[0].offsetWidth;
            $active.addClass(direction);
            $next.addClass(direction);
            $active.one("bsTransitionEnd", function() {
                $next.removeClass([ type, direction ].join(" ")).addClass("active");
                $active.removeClass([ "active", direction ].join(" "));
                that.sliding = false;
                setTimeout(function() {
                    that.$element.trigger(slidEvent);
                }, 0);
            }).emulateTransitionEnd(Carousel.TRANSITION_DURATION);
        } else {
            $active.removeClass("active");
            $next.addClass("active");
            this.sliding = false;
            this.$element.trigger(slidEvent);
        }
        isCycling && this.cycle();
        return this;
    };
    function Plugin(option) {
        return this.each(function() {
            var $this = $(this);
            var data = $this.data("bs.carousel");
            var options = $.extend({}, Carousel.DEFAULTS, $this.data(), typeof option == "object" && option);
            var action = typeof option == "string" ? option : options.slide;
            if (!data) $this.data("bs.carousel", data = new Carousel(this, options));
            if (typeof option == "number") data.to(option); else if (action) data[action](); else if (options.interval) data.pause().cycle();
        });
    }
    var old = $.fn.carousel;
    $.fn.carousel = Plugin;
    $.fn.carousel.Constructor = Carousel;
    $.fn.carousel.noConflict = function() {
        $.fn.carousel = old;
        return this;
    };
    var clickHandler = function(e) {
        var href;
        var $this = $(this);
        var $target = $($this.attr("data-target") || (href = $this.attr("href")) && href.replace(/.*(?=#[^\s]+$)/, ""));
        if (!$target.hasClass("carousel")) return;
        var options = $.extend({}, $target.data(), $this.data());
        var slideIndex = $this.attr("data-slide-to");
        if (slideIndex) options.interval = false;
        Plugin.call($target, options);
        if (slideIndex) {
            $target.data("bs.carousel").to(slideIndex);
        }
        e.preventDefault();
    };
    $(document).on("click.bs.carousel.data-api", "[data-slide]", clickHandler).on("click.bs.carousel.data-api", "[data-slide-to]", clickHandler);
    $(window).on("load", function() {
        $('[data-ride="carousel"]').each(function() {
            var $carousel = $(this);
            Plugin.call($carousel, $carousel.data());
        });
    });
}(jQuery);

+function($) {
    "use strict";
    var Collapse = function(element, options) {
        this.$element = $(element);
        this.options = $.extend({}, Collapse.DEFAULTS, options);
        this.$trigger = $('[data-toggle="collapse"][href="#' + element.id + '"],' + '[data-toggle="collapse"][data-target="#' + element.id + '"]');
        this.transitioning = null;
        if (this.options.parent) {
            this.$parent = this.getParent();
        } else {
            this.addAriaAndCollapsedClass(this.$element, this.$trigger);
        }
        if (this.options.toggle) this.toggle();
    };
    Collapse.VERSION = "3.3.4";
    Collapse.TRANSITION_DURATION = 350;
    Collapse.DEFAULTS = {
        toggle: true
    };
    Collapse.prototype.dimension = function() {
        var hasWidth = this.$element.hasClass("width");
        return hasWidth ? "width" : "height";
    };
    Collapse.prototype.show = function() {
        if (this.transitioning || this.$element.hasClass("in")) return;
        var activesData;
        var actives = this.$parent && this.$parent.children(".panel").children(".in, .collapsing");
        if (actives && actives.length) {
            activesData = actives.data("bs.collapse");
            if (activesData && activesData.transitioning) return;
        }
        var startEvent = $.Event("show.bs.collapse");
        this.$element.trigger(startEvent);
        if (startEvent.isDefaultPrevented()) return;
        if (actives && actives.length) {
            Plugin.call(actives, "hide");
            activesData || actives.data("bs.collapse", null);
        }
        var dimension = this.dimension();
        this.$element.removeClass("collapse").addClass("collapsing")[dimension](0).attr("aria-expanded", true);
        this.$trigger.removeClass("collapsed").attr("aria-expanded", true);
        this.transitioning = 1;
        var complete = function() {
            this.$element.removeClass("collapsing").addClass("collapse in")[dimension]("");
            this.transitioning = 0;
            this.$element.trigger("shown.bs.collapse");
        };
        if (!$.support.transition) return complete.call(this);
        var scrollSize = $.camelCase([ "scroll", dimension ].join("-"));
        this.$element.one("bsTransitionEnd", $.proxy(complete, this)).emulateTransitionEnd(Collapse.TRANSITION_DURATION)[dimension](this.$element[0][scrollSize]);
    };
    Collapse.prototype.hide = function() {
        if (this.transitioning || !this.$element.hasClass("in")) return;
        var startEvent = $.Event("hide.bs.collapse");
        this.$element.trigger(startEvent);
        if (startEvent.isDefaultPrevented()) return;
        var dimension = this.dimension();
        this.$element[dimension](this.$element[dimension]())[0].offsetHeight;
        this.$element.addClass("collapsing").removeClass("collapse in").attr("aria-expanded", false);
        this.$trigger.addClass("collapsed").attr("aria-expanded", false);
        this.transitioning = 1;
        var complete = function() {
            this.transitioning = 0;
            this.$element.removeClass("collapsing").addClass("collapse").trigger("hidden.bs.collapse");
        };
        if (!$.support.transition) return complete.call(this);
        this.$element[dimension](0).one("bsTransitionEnd", $.proxy(complete, this)).emulateTransitionEnd(Collapse.TRANSITION_DURATION);
    };
    Collapse.prototype.toggle = function() {
        this[this.$element.hasClass("in") ? "hide" : "show"]();
    };
    Collapse.prototype.getParent = function() {
        return $(this.options.parent).find('[data-toggle="collapse"][data-parent="' + this.options.parent + '"]').each($.proxy(function(i, element) {
            var $element = $(element);
            this.addAriaAndCollapsedClass(getTargetFromTrigger($element), $element);
        }, this)).end();
    };
    Collapse.prototype.addAriaAndCollapsedClass = function($element, $trigger) {
        var isOpen = $element.hasClass("in");
        $element.attr("aria-expanded", isOpen);
        $trigger.toggleClass("collapsed", !isOpen).attr("aria-expanded", isOpen);
    };
    function getTargetFromTrigger($trigger) {
        var href;
        var target = $trigger.attr("data-target") || (href = $trigger.attr("href")) && href.replace(/.*(?=#[^\s]+$)/, "");
        return $(target);
    }
    function Plugin(option) {
        return this.each(function() {
            var $this = $(this);
            var data = $this.data("bs.collapse");
            var options = $.extend({}, Collapse.DEFAULTS, $this.data(), typeof option == "object" && option);
            if (!data && options.toggle && /show|hide/.test(option)) options.toggle = false;
            if (!data) $this.data("bs.collapse", data = new Collapse(this, options));
            if (typeof option == "string") data[option]();
        });
    }
    var old = $.fn.collapse;
    $.fn.collapse = Plugin;
    $.fn.collapse.Constructor = Collapse;
    $.fn.collapse.noConflict = function() {
        $.fn.collapse = old;
        return this;
    };
    $(document).on("click.bs.collapse.data-api", '[data-toggle="collapse"]', function(e) {
        var $this = $(this);
        if (!$this.attr("data-target")) e.preventDefault();
        var $target = getTargetFromTrigger($this);
        var data = $target.data("bs.collapse");
        var option = data ? "toggle" : $this.data();
        Plugin.call($target, option);
    });
}(jQuery);

+function($) {
    "use strict";
    var backdrop = ".dropdown-backdrop";
    var toggle = '[data-toggle="dropdown"]';
    var Dropdown = function(element) {
        $(element).on("click.bs.dropdown", this.toggle);
    };
    Dropdown.VERSION = "3.3.4";
    Dropdown.prototype.toggle = function(e) {
        var $this = $(this);
        if ($this.is(".disabled, :disabled")) return;
        var $parent = getParent($this);
        var isActive = $parent.hasClass("open");
        clearMenus();
        if (!isActive) {
            if ("ontouchstart" in document.documentElement && !$parent.closest(".navbar-nav").length) {
                $('<div class="dropdown-backdrop"/>').insertAfter($(this)).on("click", clearMenus);
            }
            var relatedTarget = {
                relatedTarget: this
            };
            $parent.trigger(e = $.Event("show.bs.dropdown", relatedTarget));
            if (e.isDefaultPrevented()) return;
            $this.trigger("focus").attr("aria-expanded", "true");
            $parent.toggleClass("open").trigger("shown.bs.dropdown", relatedTarget);
        }
        return false;
    };
    Dropdown.prototype.keydown = function(e) {
        if (!/(38|40|27|32)/.test(e.which) || /input|textarea/i.test(e.target.tagName)) return;
        var $this = $(this);
        e.preventDefault();
        e.stopPropagation();
        if ($this.is(".disabled, :disabled")) return;
        var $parent = getParent($this);
        var isActive = $parent.hasClass("open");
        if (!isActive && e.which != 27 || isActive && e.which == 27) {
            if (e.which == 27) $parent.find(toggle).trigger("focus");
            return $this.trigger("click");
        }
        var desc = " li:not(.disabled):visible a";
        var $items = $parent.find('[role="menu"]' + desc + ', [role="listbox"]' + desc);
        if (!$items.length) return;
        var index = $items.index(e.target);
        if (e.which == 38 && index > 0) index--;
        if (e.which == 40 && index < $items.length - 1) index++;
        if (!~index) index = 0;
        $items.eq(index).trigger("focus");
    };
    function clearMenus(e) {
        if (e && e.which === 3) return;
        $(backdrop).remove();
        $(toggle).each(function() {
            var $this = $(this);
            var $parent = getParent($this);
            var relatedTarget = {
                relatedTarget: this
            };
            if (!$parent.hasClass("open")) return;
            $parent.trigger(e = $.Event("hide.bs.dropdown", relatedTarget));
            if (e.isDefaultPrevented()) return;
            $this.attr("aria-expanded", "false");
            $parent.removeClass("open").trigger("hidden.bs.dropdown", relatedTarget);
        });
    }
    function getParent($this) {
        var selector = $this.attr("data-target");
        if (!selector) {
            selector = $this.attr("href");
            selector = selector && /#[A-Za-z]/.test(selector) && selector.replace(/.*(?=#[^\s]*$)/, "");
        }
        var $parent = selector && $(selector);
        return $parent && $parent.length ? $parent : $this.parent();
    }
    function Plugin(option) {
        return this.each(function() {
            var $this = $(this);
            var data = $this.data("bs.dropdown");
            if (!data) $this.data("bs.dropdown", data = new Dropdown(this));
            if (typeof option == "string") data[option].call($this);
        });
    }
    var old = $.fn.dropdown;
    $.fn.dropdown = Plugin;
    $.fn.dropdown.Constructor = Dropdown;
    $.fn.dropdown.noConflict = function() {
        $.fn.dropdown = old;
        return this;
    };
    $(document).on("click.bs.dropdown.data-api", clearMenus).on("click.bs.dropdown.data-api", ".dropdown form", function(e) {
        e.stopPropagation();
    }).on("click.bs.dropdown.data-api", toggle, Dropdown.prototype.toggle).on("keydown.bs.dropdown.data-api", toggle, Dropdown.prototype.keydown).on("keydown.bs.dropdown.data-api", '[role="menu"]', Dropdown.prototype.keydown).on("keydown.bs.dropdown.data-api", '[role="listbox"]', Dropdown.prototype.keydown);
}(jQuery);

+function($) {
    "use strict";
    var Modal = function(element, options) {
        this.options = options;
        this.$body = $(document.body);
        this.$element = $(element);
        this.$dialog = this.$element.find(".modal-dialog");
        this.$backdrop = null;
        this.isShown = null;
        this.originalBodyPad = null;
        this.scrollbarWidth = 0;
        this.ignoreBackdropClick = false;
        if (this.options.remote) {
            this.$element.find(".modal-content").load(this.options.remote, $.proxy(function() {
                this.$element.trigger("loaded.bs.modal");
            }, this));
        }
    };
    Modal.VERSION = "3.3.4";
    Modal.TRANSITION_DURATION = 300;
    Modal.BACKDROP_TRANSITION_DURATION = 150;
    Modal.DEFAULTS = {
        backdrop: true,
        keyboard: true,
        show: true
    };
    Modal.prototype.toggle = function(_relatedTarget) {
        return this.isShown ? this.hide() : this.show(_relatedTarget);
    };
    Modal.prototype.show = function(_relatedTarget) {
        var that = this;
        var e = $.Event("show.bs.modal", {
            relatedTarget: _relatedTarget
        });
        this.$element.trigger(e);
        if (this.isShown || e.isDefaultPrevented()) return;
        this.isShown = true;
        this.checkScrollbar();
        this.setScrollbar();
        this.$body.addClass("modal-open");
        this.escape();
        this.resize();
        this.$element.on("click.dismiss.bs.modal", '[data-dismiss="modal"]', $.proxy(this.hide, this));
        this.$dialog.on("mousedown.dismiss.bs.modal", function() {
            that.$element.one("mouseup.dismiss.bs.modal", function(e) {
                if ($(e.target).is(that.$element)) that.ignoreBackdropClick = true;
            });
        });
        this.backdrop(function() {
            var transition = $.support.transition && that.$element.hasClass("fade");
            if (!that.$element.parent().length) {
                that.$element.appendTo(that.$body);
            }
            that.$element.show().scrollTop(0);
            that.adjustDialog();
            if (transition) {
                that.$element[0].offsetWidth;
            }
            that.$element.addClass("in").attr("aria-hidden", false);
            that.enforceFocus();
            var e = $.Event("shown.bs.modal", {
                relatedTarget: _relatedTarget
            });
            transition ? that.$dialog.one("bsTransitionEnd", function() {
                that.$element.trigger("focus").trigger(e);
            }).emulateTransitionEnd(Modal.TRANSITION_DURATION) : that.$element.trigger("focus").trigger(e);
        });
    };
    Modal.prototype.hide = function(e) {
        if (e) e.preventDefault();
        e = $.Event("hide.bs.modal");
        this.$element.trigger(e);
        if (!this.isShown || e.isDefaultPrevented()) return;
        this.isShown = false;
        this.escape();
        this.resize();
        $(document).off("focusin.bs.modal");
        this.$element.removeClass("in").attr("aria-hidden", true).off("click.dismiss.bs.modal").off("mouseup.dismiss.bs.modal");
        this.$dialog.off("mousedown.dismiss.bs.modal");
        $.support.transition && this.$element.hasClass("fade") ? this.$element.one("bsTransitionEnd", $.proxy(this.hideModal, this)).emulateTransitionEnd(Modal.TRANSITION_DURATION) : this.hideModal();
    };
    Modal.prototype.enforceFocus = function() {
        $(document).off("focusin.bs.modal").on("focusin.bs.modal", $.proxy(function(e) {
            if (this.$element[0] !== e.target && !this.$element.has(e.target).length) {
                this.$element.trigger("focus");
            }
        }, this));
    };
    Modal.prototype.escape = function() {
        if (this.isShown && this.options.keyboard) {
            this.$element.on("keydown.dismiss.bs.modal", $.proxy(function(e) {
                e.which == 27 && this.hide();
            }, this));
        } else if (!this.isShown) {
            this.$element.off("keydown.dismiss.bs.modal");
        }
    };
    Modal.prototype.resize = function() {
        if (this.isShown) {
            $(window).on("resize.bs.modal", $.proxy(this.handleUpdate, this));
        } else {
            $(window).off("resize.bs.modal");
        }
    };
    Modal.prototype.hideModal = function() {
        var that = this;
        this.$element.hide();
        this.backdrop(function() {
            that.$body.removeClass("modal-open");
            that.resetAdjustments();
            that.resetScrollbar();
            that.$element.trigger("hidden.bs.modal");
        });
    };
    Modal.prototype.removeBackdrop = function() {
        this.$backdrop && this.$backdrop.remove();
        this.$backdrop = null;
    };
    Modal.prototype.backdrop = function(callback) {
        var that = this;
        var animate = this.$element.hasClass("fade") ? "fade" : "";
        if (this.isShown && this.options.backdrop) {
            var doAnimate = $.support.transition && animate;
            this.$backdrop = $('<div class="modal-backdrop ' + animate + '" />').appendTo(this.$body);
            this.$element.on("click.dismiss.bs.modal", $.proxy(function(e) {
                if (this.ignoreBackdropClick) {
                    this.ignoreBackdropClick = false;
                    return;
                }
                if (e.target !== e.currentTarget) return;
                this.options.backdrop == "static" ? this.$element[0].focus() : this.hide();
            }, this));
            if (doAnimate) this.$backdrop[0].offsetWidth;
            this.$backdrop.addClass("in");
            if (!callback) return;
            doAnimate ? this.$backdrop.one("bsTransitionEnd", callback).emulateTransitionEnd(Modal.BACKDROP_TRANSITION_DURATION) : callback();
        } else if (!this.isShown && this.$backdrop) {
            this.$backdrop.removeClass("in");
            var callbackRemove = function() {
                that.removeBackdrop();
                callback && callback();
            };
            $.support.transition && this.$element.hasClass("fade") ? this.$backdrop.one("bsTransitionEnd", callbackRemove).emulateTransitionEnd(Modal.BACKDROP_TRANSITION_DURATION) : callbackRemove();
        } else if (callback) {
            callback();
        }
    };
    Modal.prototype.handleUpdate = function() {
        this.adjustDialog();
    };
    Modal.prototype.adjustDialog = function() {
        var modalIsOverflowing = this.$element[0].scrollHeight > document.documentElement.clientHeight;
        this.$element.css({
            paddingLeft: !this.bodyIsOverflowing && modalIsOverflowing ? this.scrollbarWidth : "",
            paddingRight: this.bodyIsOverflowing && !modalIsOverflowing ? this.scrollbarWidth : ""
        });
    };
    Modal.prototype.resetAdjustments = function() {
        this.$element.css({
            paddingLeft: "",
            paddingRight: ""
        });
    };
    Modal.prototype.checkScrollbar = function() {
        var fullWindowWidth = window.innerWidth;
        if (!fullWindowWidth) {
            var documentElementRect = document.documentElement.getBoundingClientRect();
            fullWindowWidth = documentElementRect.right - Math.abs(documentElementRect.left);
        }
        this.bodyIsOverflowing = document.body.clientWidth < fullWindowWidth;
        this.scrollbarWidth = this.measureScrollbar();
    };
    Modal.prototype.setScrollbar = function() {
        var bodyPad = parseInt(this.$body.css("padding-right") || 0, 10);
        this.originalBodyPad = document.body.style.paddingRight || "";
        if (this.bodyIsOverflowing) this.$body.css("padding-right", bodyPad + this.scrollbarWidth);
    };
    Modal.prototype.resetScrollbar = function() {
        this.$body.css("padding-right", this.originalBodyPad);
    };
    Modal.prototype.measureScrollbar = function() {
        var scrollDiv = document.createElement("div");
        scrollDiv.className = "modal-scrollbar-measure";
        this.$body.append(scrollDiv);
        var scrollbarWidth = scrollDiv.offsetWidth - scrollDiv.clientWidth;
        this.$body[0].removeChild(scrollDiv);
        return scrollbarWidth;
    };
    function Plugin(option, _relatedTarget) {
        return this.each(function() {
            var $this = $(this);
            var data = $this.data("bs.modal");
            var options = $.extend({}, Modal.DEFAULTS, $this.data(), typeof option == "object" && option);
            if (!data) $this.data("bs.modal", data = new Modal(this, options));
            if (typeof option == "string") data[option](_relatedTarget); else if (options.show) data.show(_relatedTarget);
        });
    }
    var old = $.fn.modal;
    $.fn.modal = Plugin;
    $.fn.modal.Constructor = Modal;
    $.fn.modal.noConflict = function() {
        $.fn.modal = old;
        return this;
    };
    $(document).on("click.bs.modal.data-api", '[data-toggle="modal"]', function(e) {
        var $this = $(this);
        var href = $this.attr("href");
        var $target = $($this.attr("data-target") || href && href.replace(/.*(?=#[^\s]+$)/, ""));
        var option = $target.data("bs.modal") ? "toggle" : $.extend({
            remote: !/#/.test(href) && href
        }, $target.data(), $this.data());
        if ($this.is("a")) e.preventDefault();
        $target.one("show.bs.modal", function(showEvent) {
            if (showEvent.isDefaultPrevented()) return;
            $target.one("hidden.bs.modal", function() {
                $this.is(":visible") && $this.trigger("focus");
            });
        });
        Plugin.call($target, option, this);
    });
}(jQuery);

+function($) {
    "use strict";
    var Tooltip = function(element, options) {
        this.type = null;
        this.options = null;
        this.enabled = null;
        this.timeout = null;
        this.hoverState = null;
        this.$element = null;
        this.init("tooltip", element, options);
    };
    Tooltip.VERSION = "3.3.4";
    Tooltip.TRANSITION_DURATION = 150;
    Tooltip.DEFAULTS = {
        animation: true,
        placement: "top",
        selector: false,
        template: '<div class="tooltip" role="tooltip"><div class="tooltip-arrow"></div><div class="tooltip-inner"></div></div>',
        trigger: "hover focus",
        title: "",
        delay: 0,
        html: false,
        container: false,
        viewport: {
            selector: "body",
            padding: 0
        }
    };
    Tooltip.prototype.init = function(type, element, options) {
        this.enabled = true;
        this.type = type;
        this.$element = $(element);
        this.options = this.getOptions(options);
        this.$viewport = this.options.viewport && $(this.options.viewport.selector || this.options.viewport);
        if (this.$element[0] instanceof document.constructor && !this.options.selector) {
            throw new Error("`selector` option must be specified when initializing " + this.type + " on the window.document object!");
        }
        var triggers = this.options.trigger.split(" ");
        for (var i = triggers.length; i--; ) {
            var trigger = triggers[i];
            if (trigger == "click") {
                this.$element.on("click." + this.type, this.options.selector, $.proxy(this.toggle, this));
            } else if (trigger != "manual") {
                var eventIn = trigger == "hover" ? "mouseenter" : "focusin";
                var eventOut = trigger == "hover" ? "mouseleave" : "focusout";
                this.$element.on(eventIn + "." + this.type, this.options.selector, $.proxy(this.enter, this));
                this.$element.on(eventOut + "." + this.type, this.options.selector, $.proxy(this.leave, this));
            }
        }
        this.options.selector ? this._options = $.extend({}, this.options, {
            trigger: "manual",
            selector: ""
        }) : this.fixTitle();
    };
    Tooltip.prototype.getDefaults = function() {
        return Tooltip.DEFAULTS;
    };
    Tooltip.prototype.getOptions = function(options) {
        options = $.extend({}, this.getDefaults(), this.$element.data(), options);
        if (options.delay && typeof options.delay == "number") {
            options.delay = {
                show: options.delay,
                hide: options.delay
            };
        }
        return options;
    };
    Tooltip.prototype.getDelegateOptions = function() {
        var options = {};
        var defaults = this.getDefaults();
        this._options && $.each(this._options, function(key, value) {
            if (defaults[key] != value) options[key] = value;
        });
        return options;
    };
    Tooltip.prototype.enter = function(obj) {
        var self = obj instanceof this.constructor ? obj : $(obj.currentTarget).data("bs." + this.type);
        if (self && self.$tip && self.$tip.is(":visible")) {
            self.hoverState = "in";
            return;
        }
        if (!self) {
            self = new this.constructor(obj.currentTarget, this.getDelegateOptions());
            $(obj.currentTarget).data("bs." + this.type, self);
        }
        clearTimeout(self.timeout);
        self.hoverState = "in";
        if (!self.options.delay || !self.options.delay.show) return self.show();
        self.timeout = setTimeout(function() {
            if (self.hoverState == "in") self.show();
        }, self.options.delay.show);
    };
    Tooltip.prototype.leave = function(obj) {
        var self = obj instanceof this.constructor ? obj : $(obj.currentTarget).data("bs." + this.type);
        if (!self) {
            self = new this.constructor(obj.currentTarget, this.getDelegateOptions());
            $(obj.currentTarget).data("bs." + this.type, self);
        }
        clearTimeout(self.timeout);
        self.hoverState = "out";
        if (!self.options.delay || !self.options.delay.hide) return self.hide();
        self.timeout = setTimeout(function() {
            if (self.hoverState == "out") self.hide();
        }, self.options.delay.hide);
    };
    Tooltip.prototype.show = function() {
        var e = $.Event("show.bs." + this.type);
        if (this.hasContent() && this.enabled) {
            this.$element.trigger(e);
            var inDom = $.contains(this.$element[0].ownerDocument.documentElement, this.$element[0]);
            if (e.isDefaultPrevented() || !inDom) return;
            var that = this;
            var $tip = this.tip();
            var tipId = this.getUID(this.type);
            this.setContent();
            $tip.attr("id", tipId);
            this.$element.attr("aria-describedby", tipId);
            if (this.options.animation) $tip.addClass("fade");
            var placement = typeof this.options.placement == "function" ? this.options.placement.call(this, $tip[0], this.$element[0]) : this.options.placement;
            var autoToken = /\s?auto?\s?/i;
            var autoPlace = autoToken.test(placement);
            if (autoPlace) placement = placement.replace(autoToken, "") || "top";
            $tip.detach().css({
                top: 0,
                left: 0,
                display: "block"
            }).addClass(placement).data("bs." + this.type, this);
            this.options.container ? $tip.appendTo(this.options.container) : $tip.insertAfter(this.$element);
            var pos = this.getPosition();
            var actualWidth = $tip[0].offsetWidth;
            var actualHeight = $tip[0].offsetHeight;
            if (autoPlace) {
                var orgPlacement = placement;
                var $container = this.options.container ? $(this.options.container) : this.$element.parent();
                var containerDim = this.getPosition($container);
                placement = placement == "bottom" && pos.bottom + actualHeight > containerDim.bottom ? "top" : placement == "top" && pos.top - actualHeight < containerDim.top ? "bottom" : placement == "right" && pos.right + actualWidth > containerDim.width ? "left" : placement == "left" && pos.left - actualWidth < containerDim.left ? "right" : placement;
                $tip.removeClass(orgPlacement).addClass(placement);
            }
            var calculatedOffset = this.getCalculatedOffset(placement, pos, actualWidth, actualHeight);
            this.applyPlacement(calculatedOffset, placement);
            var complete = function() {
                var prevHoverState = that.hoverState;
                that.$element.trigger("shown.bs." + that.type);
                that.hoverState = null;
                if (prevHoverState == "out") that.leave(that);
            };
            $.support.transition && this.$tip.hasClass("fade") ? $tip.one("bsTransitionEnd", complete).emulateTransitionEnd(Tooltip.TRANSITION_DURATION) : complete();
        }
    };
    Tooltip.prototype.applyPlacement = function(offset, placement) {
        var $tip = this.tip();
        var width = $tip[0].offsetWidth;
        var height = $tip[0].offsetHeight;
        var marginTop = parseInt($tip.css("margin-top"), 10);
        var marginLeft = parseInt($tip.css("margin-left"), 10);
        if (isNaN(marginTop)) marginTop = 0;
        if (isNaN(marginLeft)) marginLeft = 0;
        offset.top = offset.top + marginTop;
        offset.left = offset.left + marginLeft;
        $.offset.setOffset($tip[0], $.extend({
            using: function(props) {
                $tip.css({
                    top: Math.round(props.top),
                    left: Math.round(props.left)
                });
            }
        }, offset), 0);
        $tip.addClass("in");
        var actualWidth = $tip[0].offsetWidth;
        var actualHeight = $tip[0].offsetHeight;
        if (placement == "top" && actualHeight != height) {
            offset.top = offset.top + height - actualHeight;
        }
        var delta = this.getViewportAdjustedDelta(placement, offset, actualWidth, actualHeight);
        if (delta.left) offset.left += delta.left; else offset.top += delta.top;
        var isVertical = /top|bottom/.test(placement);
        var arrowDelta = isVertical ? delta.left * 2 - width + actualWidth : delta.top * 2 - height + actualHeight;
        var arrowOffsetPosition = isVertical ? "offsetWidth" : "offsetHeight";
        $tip.offset(offset);
        this.replaceArrow(arrowDelta, $tip[0][arrowOffsetPosition], isVertical);
    };
    Tooltip.prototype.replaceArrow = function(delta, dimension, isVertical) {
        this.arrow().css(isVertical ? "left" : "top", 50 * (1 - delta / dimension) + "%").css(isVertical ? "top" : "left", "");
    };
    Tooltip.prototype.setContent = function() {
        var $tip = this.tip();
        var title = this.getTitle();
        $tip.find(".tooltip-inner")[this.options.html ? "html" : "text"](title);
        $tip.removeClass("fade in top bottom left right");
    };
    Tooltip.prototype.hide = function(callback) {
        var that = this;
        var $tip = $(this.$tip);
        var e = $.Event("hide.bs." + this.type);
        function complete() {
            if (that.hoverState != "in") $tip.detach();
            that.$element.removeAttr("aria-describedby").trigger("hidden.bs." + that.type);
            callback && callback();
        }
        this.$element.trigger(e);
        if (e.isDefaultPrevented()) return;
        $tip.removeClass("in");
        $.support.transition && $tip.hasClass("fade") ? $tip.one("bsTransitionEnd", complete).emulateTransitionEnd(Tooltip.TRANSITION_DURATION) : complete();
        this.hoverState = null;
        return this;
    };
    Tooltip.prototype.fixTitle = function() {
        var $e = this.$element;
        if ($e.attr("title") || typeof $e.attr("data-original-title") != "string") {
            $e.attr("data-original-title", $e.attr("title") || "").attr("title", "");
        }
    };
    Tooltip.prototype.hasContent = function() {
        return this.getTitle();
    };
    Tooltip.prototype.getPosition = function($element) {
        $element = $element || this.$element;
        var el = $element[0];
        var isBody = el.tagName == "BODY";
        var elRect = el.getBoundingClientRect();
        if (elRect.width == null) {
            elRect = $.extend({}, elRect, {
                width: elRect.right - elRect.left,
                height: elRect.bottom - elRect.top
            });
        }
        var elOffset = isBody ? {
            top: 0,
            left: 0
        } : $element.offset();
        var scroll = {
            scroll: isBody ? document.documentElement.scrollTop || document.body.scrollTop : $element.scrollTop()
        };
        var outerDims = isBody ? {
            width: $(window).width(),
            height: $(window).height()
        } : null;
        return $.extend({}, elRect, scroll, outerDims, elOffset);
    };
    Tooltip.prototype.getCalculatedOffset = function(placement, pos, actualWidth, actualHeight) {
        return placement == "bottom" ? {
            top: pos.top + pos.height,
            left: pos.left + pos.width / 2 - actualWidth / 2
        } : placement == "top" ? {
            top: pos.top - actualHeight,
            left: pos.left + pos.width / 2 - actualWidth / 2
        } : placement == "left" ? {
            top: pos.top + pos.height / 2 - actualHeight / 2,
            left: pos.left - actualWidth
        } : {
            top: pos.top + pos.height / 2 - actualHeight / 2,
            left: pos.left + pos.width
        };
    };
    Tooltip.prototype.getViewportAdjustedDelta = function(placement, pos, actualWidth, actualHeight) {
        var delta = {
            top: 0,
            left: 0
        };
        if (!this.$viewport) return delta;
        var viewportPadding = this.options.viewport && this.options.viewport.padding || 0;
        var viewportDimensions = this.getPosition(this.$viewport);
        if (/right|left/.test(placement)) {
            var topEdgeOffset = pos.top - viewportPadding - viewportDimensions.scroll;
            var bottomEdgeOffset = pos.top + viewportPadding - viewportDimensions.scroll + actualHeight;
            if (topEdgeOffset < viewportDimensions.top) {
                delta.top = viewportDimensions.top - topEdgeOffset;
            } else if (bottomEdgeOffset > viewportDimensions.top + viewportDimensions.height) {
                delta.top = viewportDimensions.top + viewportDimensions.height - bottomEdgeOffset;
            }
        } else {
            var leftEdgeOffset = pos.left - viewportPadding;
            var rightEdgeOffset = pos.left + viewportPadding + actualWidth;
            if (leftEdgeOffset < viewportDimensions.left) {
                delta.left = viewportDimensions.left - leftEdgeOffset;
            } else if (rightEdgeOffset > viewportDimensions.width) {
                delta.left = viewportDimensions.left + viewportDimensions.width - rightEdgeOffset;
            }
        }
        return delta;
    };
    Tooltip.prototype.getTitle = function() {
        var title;
        var $e = this.$element;
        var o = this.options;
        title = $e.attr("data-original-title") || (typeof o.title == "function" ? o.title.call($e[0]) : o.title);
        return title;
    };
    Tooltip.prototype.getUID = function(prefix) {
        do prefix += ~~(Math.random() * 1e6); while (document.getElementById(prefix));
        return prefix;
    };
    Tooltip.prototype.tip = function() {
        return this.$tip = this.$tip || $(this.options.template);
    };
    Tooltip.prototype.arrow = function() {
        return this.$arrow = this.$arrow || this.tip().find(".tooltip-arrow");
    };
    Tooltip.prototype.enable = function() {
        this.enabled = true;
    };
    Tooltip.prototype.disable = function() {
        this.enabled = false;
    };
    Tooltip.prototype.toggleEnabled = function() {
        this.enabled = !this.enabled;
    };
    Tooltip.prototype.toggle = function(e) {
        var self = this;
        if (e) {
            self = $(e.currentTarget).data("bs." + this.type);
            if (!self) {
                self = new this.constructor(e.currentTarget, this.getDelegateOptions());
                $(e.currentTarget).data("bs." + this.type, self);
            }
        }
        self.tip().hasClass("in") ? self.leave(self) : self.enter(self);
    };
    Tooltip.prototype.destroy = function() {
        var that = this;
        clearTimeout(this.timeout);
        this.hide(function() {
            that.$element.off("." + that.type).removeData("bs." + that.type);
        });
    };
    function Plugin(option) {
        return this.each(function() {
            var $this = $(this);
            var data = $this.data("bs.tooltip");
            var options = typeof option == "object" && option;
            if (!data && /destroy|hide/.test(option)) return;
            if (!data) $this.data("bs.tooltip", data = new Tooltip(this, options));
            if (typeof option == "string") data[option]();
        });
    }
    var old = $.fn.tooltip;
    $.fn.tooltip = Plugin;
    $.fn.tooltip.Constructor = Tooltip;
    $.fn.tooltip.noConflict = function() {
        $.fn.tooltip = old;
        return this;
    };
}(jQuery);

+function($) {
    "use strict";
    var Popover = function(element, options) {
        this.init("popover", element, options);
    };
    if (!$.fn.tooltip) throw new Error("Popover requires tooltip.js");
    Popover.VERSION = "3.3.4";
    Popover.DEFAULTS = $.extend({}, $.fn.tooltip.Constructor.DEFAULTS, {
        placement: "right",
        trigger: "click",
        content: "",
        template: '<div class="popover" role="tooltip"><div class="arrow"></div><h3 class="popover-title"></h3><div class="popover-content"></div></div>'
    });
    Popover.prototype = $.extend({}, $.fn.tooltip.Constructor.prototype);
    Popover.prototype.constructor = Popover;
    Popover.prototype.getDefaults = function() {
        return Popover.DEFAULTS;
    };
    Popover.prototype.setContent = function() {
        var $tip = this.tip();
        var title = this.getTitle();
        var content = this.getContent();
        $tip.find(".popover-title")[this.options.html ? "html" : "text"](title);
        $tip.find(".popover-content").children().detach().end()[this.options.html ? typeof content == "string" ? "html" : "append" : "text"](content);
        $tip.removeClass("fade top bottom left right in");
        if (!$tip.find(".popover-title").html()) $tip.find(".popover-title").hide();
    };
    Popover.prototype.hasContent = function() {
        return this.getTitle() || this.getContent();
    };
    Popover.prototype.getContent = function() {
        var $e = this.$element;
        var o = this.options;
        return $e.attr("data-content") || (typeof o.content == "function" ? o.content.call($e[0]) : o.content);
    };
    Popover.prototype.arrow = function() {
        return this.$arrow = this.$arrow || this.tip().find(".arrow");
    };
    function Plugin(option) {
        return this.each(function() {
            var $this = $(this);
            var data = $this.data("bs.popover");
            var options = typeof option == "object" && option;
            if (!data && /destroy|hide/.test(option)) return;
            if (!data) $this.data("bs.popover", data = new Popover(this, options));
            if (typeof option == "string") data[option]();
        });
    }
    var old = $.fn.popover;
    $.fn.popover = Plugin;
    $.fn.popover.Constructor = Popover;
    $.fn.popover.noConflict = function() {
        $.fn.popover = old;
        return this;
    };
}(jQuery);

+function($) {
    "use strict";
    function ScrollSpy(element, options) {
        this.$body = $(document.body);
        this.$scrollElement = $(element).is(document.body) ? $(window) : $(element);
        this.options = $.extend({}, ScrollSpy.DEFAULTS, options);
        this.selector = (this.options.target || "") + " .nav li > a";
        this.offsets = [];
        this.targets = [];
        this.activeTarget = null;
        this.scrollHeight = 0;
        this.$scrollElement.on("scroll.bs.scrollspy", $.proxy(this.process, this));
        this.refresh();
        this.process();
    }
    ScrollSpy.VERSION = "3.3.4";
    ScrollSpy.DEFAULTS = {
        offset: 10
    };
    ScrollSpy.prototype.getScrollHeight = function() {
        return this.$scrollElement[0].scrollHeight || Math.max(this.$body[0].scrollHeight, document.documentElement.scrollHeight);
    };
    ScrollSpy.prototype.refresh = function() {
        var that = this;
        var offsetMethod = "offset";
        var offsetBase = 0;
        this.offsets = [];
        this.targets = [];
        this.scrollHeight = this.getScrollHeight();
        if (!$.isWindow(this.$scrollElement[0])) {
            offsetMethod = "position";
            offsetBase = this.$scrollElement.scrollTop();
        }
        this.$body.find(this.selector).map(function() {
            var $el = $(this);
            var href = $el.data("target") || $el.attr("href");
            var $href = /^#./.test(href) && $(href);
            return $href && $href.length && $href.is(":visible") && [ [ $href[offsetMethod]().top + offsetBase, href ] ] || null;
        }).sort(function(a, b) {
            return a[0] - b[0];
        }).each(function() {
            that.offsets.push(this[0]);
            that.targets.push(this[1]);
        });
    };
    ScrollSpy.prototype.process = function() {
        var scrollTop = this.$scrollElement.scrollTop() + this.options.offset;
        var scrollHeight = this.getScrollHeight();
        var maxScroll = this.options.offset + scrollHeight - this.$scrollElement.height();
        var offsets = this.offsets;
        var targets = this.targets;
        var activeTarget = this.activeTarget;
        var i;
        if (this.scrollHeight != scrollHeight) {
            this.refresh();
        }
        if (scrollTop >= maxScroll) {
            return activeTarget != (i = targets[targets.length - 1]) && this.activate(i);
        }
        if (activeTarget && scrollTop < offsets[0]) {
            this.activeTarget = null;
            return this.clear();
        }
        for (i = offsets.length; i--; ) {
            activeTarget != targets[i] && scrollTop >= offsets[i] && (offsets[i + 1] === undefined || scrollTop < offsets[i + 1]) && this.activate(targets[i]);
        }
    };
    ScrollSpy.prototype.activate = function(target) {
        this.activeTarget = target;
        this.clear();
        var selector = this.selector + '[data-target="' + target + '"],' + this.selector + '[href="' + target + '"]';
        var active = $(selector).parents("li").addClass("active");
        if (active.parent(".dropdown-menu").length) {
            active = active.closest("li.dropdown").addClass("active");
        }
        active.trigger("activate.bs.scrollspy");
    };
    ScrollSpy.prototype.clear = function() {
        $(this.selector).parentsUntil(this.options.target, ".active").removeClass("active");
    };
    function Plugin(option) {
        return this.each(function() {
            var $this = $(this);
            var data = $this.data("bs.scrollspy");
            var options = typeof option == "object" && option;
            if (!data) $this.data("bs.scrollspy", data = new ScrollSpy(this, options));
            if (typeof option == "string") data[option]();
        });
    }
    var old = $.fn.scrollspy;
    $.fn.scrollspy = Plugin;
    $.fn.scrollspy.Constructor = ScrollSpy;
    $.fn.scrollspy.noConflict = function() {
        $.fn.scrollspy = old;
        return this;
    };
    $(window).on("load.bs.scrollspy.data-api", function() {
        $('[data-spy="scroll"]').each(function() {
            var $spy = $(this);
            Plugin.call($spy, $spy.data());
        });
    });
}(jQuery);

+function($) {
    "use strict";
    var Tab = function(element) {
        this.element = $(element);
    };
    Tab.VERSION = "3.3.4";
    Tab.TRANSITION_DURATION = 150;
    Tab.prototype.show = function() {
        var $this = this.element;
        var $ul = $this.closest("ul:not(.dropdown-menu)");
        var selector = $this.data("target");
        if (!selector) {
            selector = $this.attr("href");
            selector = selector && selector.replace(/.*(?=#[^\s]*$)/, "");
        }
        if ($this.parent("li").hasClass("active")) return;
        var $previous = $ul.find(".active:last a");
        var hideEvent = $.Event("hide.bs.tab", {
            relatedTarget: $this[0]
        });
        var showEvent = $.Event("show.bs.tab", {
            relatedTarget: $previous[0]
        });
        $previous.trigger(hideEvent);
        $this.trigger(showEvent);
        if (showEvent.isDefaultPrevented() || hideEvent.isDefaultPrevented()) return;
        var $target = $(selector);
        this.activate($this.closest("li"), $ul);
        this.activate($target, $target.parent(), function() {
            $previous.trigger({
                type: "hidden.bs.tab",
                relatedTarget: $this[0]
            });
            $this.trigger({
                type: "shown.bs.tab",
                relatedTarget: $previous[0]
            });
        });
    };
    Tab.prototype.activate = function(element, container, callback) {
        var $active = container.find("> .active");
        var transition = callback && $.support.transition && ($active.length && $active.hasClass("fade") || !!container.find("> .fade").length);
        function next() {
            $active.removeClass("active").find("> .dropdown-menu > .active").removeClass("active").end().find('[data-toggle="tab"]').attr("aria-expanded", false);
            element.addClass("active").find('[data-toggle="tab"]').attr("aria-expanded", true);
            if (transition) {
                element[0].offsetWidth;
                element.addClass("in");
            } else {
                element.removeClass("fade");
            }
            if (element.parent(".dropdown-menu").length) {
                element.closest("li.dropdown").addClass("active").end().find('[data-toggle="tab"]').attr("aria-expanded", true);
            }
            callback && callback();
        }
        $active.length && transition ? $active.one("bsTransitionEnd", next).emulateTransitionEnd(Tab.TRANSITION_DURATION) : next();
        $active.removeClass("in");
    };
    function Plugin(option) {
        return this.each(function() {
            var $this = $(this);
            var data = $this.data("bs.tab");
            if (!data) $this.data("bs.tab", data = new Tab(this));
            if (typeof option == "string") data[option]();
        });
    }
    var old = $.fn.tab;
    $.fn.tab = Plugin;
    $.fn.tab.Constructor = Tab;
    $.fn.tab.noConflict = function() {
        $.fn.tab = old;
        return this;
    };
    var clickHandler = function(e) {
        e.preventDefault();
        Plugin.call($(this), "show");
    };
    $(document).on("click.bs.tab.data-api", '[data-toggle="tab"]', clickHandler).on("click.bs.tab.data-api", '[data-toggle="pill"]', clickHandler);
}(jQuery);

+function($) {
    "use strict";
    var Affix = function(element, options) {
        this.options = $.extend({}, Affix.DEFAULTS, options);
        this.$target = $(this.options.target).on("scroll.bs.affix.data-api", $.proxy(this.checkPosition, this)).on("click.bs.affix.data-api", $.proxy(this.checkPositionWithEventLoop, this));
        this.$element = $(element);
        this.affixed = null;
        this.unpin = null;
        this.pinnedOffset = null;
        this.checkPosition();
    };
    Affix.VERSION = "3.3.4";
    Affix.RESET = "affix affix-top affix-bottom";
    Affix.DEFAULTS = {
        offset: 0,
        target: window
    };
    Affix.prototype.getState = function(scrollHeight, height, offsetTop, offsetBottom) {
        var scrollTop = this.$target.scrollTop();
        var position = this.$element.offset();
        var targetHeight = this.$target.height();
        if (offsetTop != null && this.affixed == "top") return scrollTop < offsetTop ? "top" : false;
        if (this.affixed == "bottom") {
            if (offsetTop != null) return scrollTop + this.unpin <= position.top ? false : "bottom";
            return scrollTop + targetHeight <= scrollHeight - offsetBottom ? false : "bottom";
        }
        var initializing = this.affixed == null;
        var colliderTop = initializing ? scrollTop : position.top;
        var colliderHeight = initializing ? targetHeight : height;
        if (offsetTop != null && scrollTop <= offsetTop) return "top";
        if (offsetBottom != null && colliderTop + colliderHeight >= scrollHeight - offsetBottom) return "bottom";
        return false;
    };
    Affix.prototype.getPinnedOffset = function() {
        if (this.pinnedOffset) return this.pinnedOffset;
        this.$element.removeClass(Affix.RESET).addClass("affix");
        var scrollTop = this.$target.scrollTop();
        var position = this.$element.offset();
        return this.pinnedOffset = position.top - scrollTop;
    };
    Affix.prototype.checkPositionWithEventLoop = function() {
        setTimeout($.proxy(this.checkPosition, this), 1);
    };
    Affix.prototype.checkPosition = function() {
        if (!this.$element.is(":visible")) return;
        var height = this.$element.height();
        var offset = this.options.offset;
        var offsetTop = offset.top;
        var offsetBottom = offset.bottom;
        var scrollHeight = $(document.body).height();
        if (typeof offset != "object") offsetBottom = offsetTop = offset;
        if (typeof offsetTop == "function") offsetTop = offset.top(this.$element);
        if (typeof offsetBottom == "function") offsetBottom = offset.bottom(this.$element);
        var affix = this.getState(scrollHeight, height, offsetTop, offsetBottom);
        if (this.affixed != affix) {
            if (this.unpin != null) this.$element.css("top", "");
            var affixType = "affix" + (affix ? "-" + affix : "");
            var e = $.Event(affixType + ".bs.affix");
            this.$element.trigger(e);
            if (e.isDefaultPrevented()) return;
            this.affixed = affix;
            this.unpin = affix == "bottom" ? this.getPinnedOffset() : null;
            this.$element.removeClass(Affix.RESET).addClass(affixType).trigger(affixType.replace("affix", "affixed") + ".bs.affix");
        }
        if (affix == "bottom") {
            this.$element.offset({
                top: scrollHeight - height - offsetBottom
            });
        }
    };
    function Plugin(option) {
        return this.each(function() {
            var $this = $(this);
            var data = $this.data("bs.affix");
            var options = typeof option == "object" && option;
            if (!data) $this.data("bs.affix", data = new Affix(this, options));
            if (typeof option == "string") data[option]();
        });
    }
    var old = $.fn.affix;
    $.fn.affix = Plugin;
    $.fn.affix.Constructor = Affix;
    $.fn.affix.noConflict = function() {
        $.fn.affix = old;
        return this;
    };
    $(window).on("load", function() {
        $('[data-spy="affix"]').each(function() {
            var $spy = $(this);
            var data = $spy.data();
            data.offset = data.offset || {};
            if (data.offsetBottom != null) data.offset.bottom = data.offsetBottom;
            if (data.offsetTop != null) data.offset.top = data.offsetTop;
            Plugin.call($spy, data);
        });
    });
}(jQuery);

!function($) {
    function UTCDate() {
        return new Date(Date.UTC.apply(Date, arguments));
    }
    function UTCToday() {
        var today = new Date();
        return UTCDate(today.getUTCFullYear(), today.getUTCMonth(), today.getUTCDate(), today.getUTCHours(), today.getUTCMinutes(), today.getUTCSeconds(), 0);
    }
    var Datetimepicker = function(element, options) {
        var that = this;
        this.element = $(element);
        this.container = options.container || "body";
        this.language = options.language || this.element.data("date-language") || "en";
        this.language = this.language in dates ? this.language : "en";
        this.isRTL = dates[this.language].rtl || false;
        this.formatType = options.formatType || this.element.data("format-type") || "standard";
        this.format = DPGlobal.parseFormat(options.format || this.element.data("date-format") || dates[this.language].format || DPGlobal.getDefaultFormat(this.formatType, "input"), this.formatType);
        this.isInline = false;
        this.isVisible = false;
        this.isInput = this.element.is("input");
        this.fontAwesome = options.fontAwesome || this.element.data("font-awesome") || false;
        this.bootcssVer = options.bootcssVer || (this.isInput ? this.element.is(".form-control") ? 3 : 2 : this.bootcssVer = this.element.is(".input-group") ? 3 : 2);
        this.component = this.element.is(".date") ? this.bootcssVer == 3 ? this.element.find(".input-group-addon .glyphicon-th, .input-group-addon .glyphicon-time, .input-group-addon .glyphicon-calendar, .input-group-addon .glyphicon-calendar, .input-group-addon .fa-calendar, .input-group-addon .fa-clock-o").parent() : this.element.find(".add-on .icon-th, .add-on .icon-time, .add-on .icon-calendar .fa-calendar .fa-clock-o").parent() : false;
        this.componentReset = this.element.is(".date") ? this.bootcssVer == 3 ? this.element.find(".input-group-addon .glyphicon-remove, .input-group-addon .fa-times").parent() : this.element.find(".add-on .icon-remove, .add-on .fa-times").parent() : false;
        this.hasInput = this.component && this.element.find("input").length;
        if (this.component && this.component.length === 0) {
            this.component = false;
        }
        this.linkField = options.linkField || this.element.data("link-field") || false;
        this.linkFormat = DPGlobal.parseFormat(options.linkFormat || this.element.data("link-format") || DPGlobal.getDefaultFormat(this.formatType, "link"), this.formatType);
        this.minuteStep = options.minuteStep || this.element.data("minute-step") || 5;
        this.pickerPosition = options.pickerPosition || this.element.data("picker-position") || "bottom-right";
        this.showMeridian = options.showMeridian || this.element.data("show-meridian") || false;
        this.initialDate = options.initialDate || new Date();
        this.zIndex = options.zIndex || this.element.data("z-index") || undefined;
        this.icons = {
            leftArrow: this.fontAwesome ? "fa-arrow-left" : this.bootcssVer === 3 ? "glyphicon-arrow-left" : "icon-arrow-left",
            rightArrow: this.fontAwesome ? "fa-arrow-right" : this.bootcssVer === 3 ? "glyphicon-arrow-right" : "icon-arrow-right"
        };
        this.icontype = this.fontAwesome ? "fa" : "glyphicon";
        this._attachEvents();
        this.formatViewType = "datetime";
        if ("formatViewType" in options) {
            this.formatViewType = options.formatViewType;
        } else if ("formatViewType" in this.element.data()) {
            this.formatViewType = this.element.data("formatViewType");
        }
        this.minView = 0;
        if ("minView" in options) {
            this.minView = options.minView;
        } else if ("minView" in this.element.data()) {
            this.minView = this.element.data("min-view");
        }
        this.minView = DPGlobal.convertViewMode(this.minView);
        this.maxView = DPGlobal.modes.length - 1;
        if ("maxView" in options) {
            this.maxView = options.maxView;
        } else if ("maxView" in this.element.data()) {
            this.maxView = this.element.data("max-view");
        }
        this.maxView = DPGlobal.convertViewMode(this.maxView);
        this.wheelViewModeNavigation = false;
        if ("wheelViewModeNavigation" in options) {
            this.wheelViewModeNavigation = options.wheelViewModeNavigation;
        } else if ("wheelViewModeNavigation" in this.element.data()) {
            this.wheelViewModeNavigation = this.element.data("view-mode-wheel-navigation");
        }
        this.wheelViewModeNavigationInverseDirection = false;
        if ("wheelViewModeNavigationInverseDirection" in options) {
            this.wheelViewModeNavigationInverseDirection = options.wheelViewModeNavigationInverseDirection;
        } else if ("wheelViewModeNavigationInverseDirection" in this.element.data()) {
            this.wheelViewModeNavigationInverseDirection = this.element.data("view-mode-wheel-navigation-inverse-dir");
        }
        this.wheelViewModeNavigationDelay = 100;
        if ("wheelViewModeNavigationDelay" in options) {
            this.wheelViewModeNavigationDelay = options.wheelViewModeNavigationDelay;
        } else if ("wheelViewModeNavigationDelay" in this.element.data()) {
            this.wheelViewModeNavigationDelay = this.element.data("view-mode-wheel-navigation-delay");
        }
        this.startViewMode = 2;
        if ("startView" in options) {
            this.startViewMode = options.startView;
        } else if ("startView" in this.element.data()) {
            this.startViewMode = this.element.data("start-view");
        }
        this.startViewMode = DPGlobal.convertViewMode(this.startViewMode);
        this.viewMode = this.startViewMode;
        this.viewSelect = this.minView;
        if ("viewSelect" in options) {
            this.viewSelect = options.viewSelect;
        } else if ("viewSelect" in this.element.data()) {
            this.viewSelect = this.element.data("view-select");
        }
        this.viewSelect = DPGlobal.convertViewMode(this.viewSelect);
        this.forceParse = true;
        if ("forceParse" in options) {
            this.forceParse = options.forceParse;
        } else if ("dateForceParse" in this.element.data()) {
            this.forceParse = this.element.data("date-force-parse");
        }
        var template = this.bootcssVer === 3 ? DPGlobal.templateV3 : DPGlobal.template;
        while (template.indexOf("{iconType}") !== -1) {
            template = template.replace("{iconType}", this.icontype);
        }
        while (template.indexOf("{leftArrow}") !== -1) {
            template = template.replace("{leftArrow}", this.icons.leftArrow);
        }
        while (template.indexOf("{rightArrow}") !== -1) {
            template = template.replace("{rightArrow}", this.icons.rightArrow);
        }
        this.picker = $(template).appendTo(this.isInline ? this.element : this.container).on({
            click: $.proxy(this.click, this),
            mousedown: $.proxy(this.mousedown, this)
        });
        if (this.wheelViewModeNavigation) {
            if ($.fn.mousewheel) {
                this.picker.on({
                    mousewheel: $.proxy(this.mousewheel, this)
                });
            } else {
                console.log("Mouse Wheel event is not supported. Please include the jQuery Mouse Wheel plugin before enabling this option");
            }
        }
        if (this.isInline) {
            this.picker.addClass("datetimepicker-inline");
        } else {
            this.picker.addClass("datetimepicker-dropdown-" + this.pickerPosition + " dropdown-menu");
        }
        if (this.isRTL) {
            this.picker.addClass("datetimepicker-rtl");
            var selector = this.bootcssVer === 3 ? ".prev span, .next span" : ".prev i, .next i";
            this.picker.find(selector).toggleClass(this.icons.leftArrow + " " + this.icons.rightArrow);
        }
        $(document).on("mousedown", function(e) {
            if ($(e.target).closest(".datetimepicker").length === 0) {
                that.hide();
            }
        });
        this.autoclose = false;
        if ("autoclose" in options) {
            this.autoclose = options.autoclose;
        } else if ("dateAutoclose" in this.element.data()) {
            this.autoclose = this.element.data("date-autoclose");
        }
        this.keyboardNavigation = true;
        if ("keyboardNavigation" in options) {
            this.keyboardNavigation = options.keyboardNavigation;
        } else if ("dateKeyboardNavigation" in this.element.data()) {
            this.keyboardNavigation = this.element.data("date-keyboard-navigation");
        }
        this.todayBtn = options.todayBtn || this.element.data("date-today-btn") || false;
        this.todayHighlight = options.todayHighlight || this.element.data("date-today-highlight") || false;
        this.weekStart = (options.weekStart || this.element.data("date-weekstart") || dates[this.language].weekStart || 0) % 7;
        this.weekEnd = (this.weekStart + 6) % 7;
        this.startDate = -Infinity;
        this.endDate = Infinity;
        this.daysOfWeekDisabled = [];
        this.setStartDate(options.startDate || this.element.data("date-startdate"));
        this.setEndDate(options.endDate || this.element.data("date-enddate"));
        this.setDaysOfWeekDisabled(options.daysOfWeekDisabled || this.element.data("date-days-of-week-disabled"));
        this.setMinutesDisabled(options.minutesDisabled || this.element.data("date-minute-disabled"));
        this.setHoursDisabled(options.hoursDisabled || this.element.data("date-hour-disabled"));
        this.fillDow();
        this.fillMonths();
        this.update();
        this.showMode();
        if (this.isInline) {
            this.show();
        }
    };
    Datetimepicker.prototype = {
        constructor: Datetimepicker,
        _events: [],
        _attachEvents: function() {
            this._detachEvents();
            if (this.isInput) {
                this._events = [ [ this.element, {
                    focus: $.proxy(this.show, this),
                    keyup: $.proxy(this.update, this),
                    keydown: $.proxy(this.keydown, this)
                } ] ];
            } else if (this.component && this.hasInput) {
                this._events = [ [ this.element.find("input"), {
                    focus: $.proxy(this.show, this),
                    keyup: $.proxy(this.update, this),
                    keydown: $.proxy(this.keydown, this)
                } ], [ this.component, {
                    click: $.proxy(this.show, this)
                } ] ];
                if (this.componentReset) {
                    this._events.push([ this.componentReset, {
                        click: $.proxy(this.reset, this)
                    } ]);
                }
            } else if (this.element.is("div")) {
                this.isInline = true;
            } else {
                this._events = [ [ this.element, {
                    click: $.proxy(this.show, this)
                } ] ];
            }
            for (var i = 0, el, ev; i < this._events.length; i++) {
                el = this._events[i][0];
                ev = this._events[i][1];
                el.on(ev);
            }
        },
        _detachEvents: function() {
            for (var i = 0, el, ev; i < this._events.length; i++) {
                el = this._events[i][0];
                ev = this._events[i][1];
                el.off(ev);
            }
            this._events = [];
        },
        show: function(e) {
            this.picker.show();
            this.height = this.component ? this.component.outerHeight() : this.element.outerHeight();
            if (this.forceParse) {
                this.update();
            }
            this.place();
            $(window).on("resize", $.proxy(this.place, this));
            if (e) {
                e.stopPropagation();
                e.preventDefault();
            }
            this.isVisible = true;
            this.element.trigger({
                type: "show",
                date: this.date
            });
        },
        hide: function(e) {
            if (!this.isVisible) return;
            if (this.isInline) return;
            this.picker.hide();
            $(window).off("resize", this.place);
            this.viewMode = this.startViewMode;
            this.showMode();
            if (!this.isInput) {
                $(document).off("mousedown", this.hide);
            }
            if (this.forceParse && (this.isInput && this.element.val() || this.hasInput && this.element.find("input").val())) this.setValue();
            this.isVisible = false;
            this.element.trigger({
                type: "hide",
                date: this.date
            });
        },
        remove: function() {
            this._detachEvents();
            this.picker.remove();
            delete this.picker;
            delete this.element.data().datetimepicker;
        },
        getDate: function() {
            var d = this.getUTCDate();
            return new Date(d.getTime() + d.getTimezoneOffset() * 6e4);
        },
        getUTCDate: function() {
            return this.date;
        },
        setDate: function(d) {
            this.setUTCDate(new Date(d.getTime() - d.getTimezoneOffset() * 6e4));
        },
        setUTCDate: function(d) {
            if (d >= this.startDate && d <= this.endDate) {
                this.date = d;
                this.setValue();
                this.viewDate = this.date;
                this.fill();
            } else {
                this.element.trigger({
                    type: "outOfRange",
                    date: d,
                    startDate: this.startDate,
                    endDate: this.endDate
                });
            }
        },
        setFormat: function(format) {
            this.format = DPGlobal.parseFormat(format, this.formatType);
            var element;
            if (this.isInput) {
                element = this.element;
            } else if (this.component) {
                element = this.element.find("input");
            }
            if (element && element.val()) {
                this.setValue();
            }
        },
        setValue: function() {
            var formatted = this.getFormattedDate();
            if (!this.isInput) {
                if (this.component) {
                    this.element.find("input").val(formatted);
                }
                this.element.data("date", formatted);
            } else {
                this.element.val(formatted);
            }
            if (this.linkField) {
                $("#" + this.linkField).val(this.getFormattedDate(this.linkFormat));
            }
        },
        getFormattedDate: function(format) {
            if (format == undefined) format = this.format;
            return DPGlobal.formatDate(this.date, format, this.language, this.formatType);
        },
        setStartDate: function(startDate) {
            this.startDate = startDate || -Infinity;
            if (this.startDate !== -Infinity) {
                this.startDate = DPGlobal.parseDate(this.startDate, this.format, this.language, this.formatType);
            }
            this.update();
            this.updateNavArrows();
        },
        setEndDate: function(endDate) {
            this.endDate = endDate || Infinity;
            if (this.endDate !== Infinity) {
                this.endDate = DPGlobal.parseDate(this.endDate, this.format, this.language, this.formatType);
            }
            this.update();
            this.updateNavArrows();
        },
        setDaysOfWeekDisabled: function(daysOfWeekDisabled) {
            this.daysOfWeekDisabled = daysOfWeekDisabled || [];
            if (!$.isArray(this.daysOfWeekDisabled)) {
                this.daysOfWeekDisabled = this.daysOfWeekDisabled.split(/,\s*/);
            }
            this.daysOfWeekDisabled = $.map(this.daysOfWeekDisabled, function(d) {
                return parseInt(d, 10);
            });
            this.update();
            this.updateNavArrows();
        },
        setMinutesDisabled: function(minutesDisabled) {
            this.minutesDisabled = minutesDisabled || [];
            if (!$.isArray(this.minutesDisabled)) {
                this.minutesDisabled = this.minutesDisabled.split(/,\s*/);
            }
            this.minutesDisabled = $.map(this.minutesDisabled, function(d) {
                return parseInt(d, 10);
            });
            this.update();
            this.updateNavArrows();
        },
        setHoursDisabled: function(hoursDisabled) {
            this.hoursDisabled = hoursDisabled || [];
            if (!$.isArray(this.hoursDisabled)) {
                this.hoursDisabled = this.hoursDisabled.split(/,\s*/);
            }
            this.hoursDisabled = $.map(this.hoursDisabled, function(d) {
                return parseInt(d, 10);
            });
            this.update();
            this.updateNavArrows();
        },
        place: function() {
            if (this.isInline) return;
            if (!this.zIndex) {
                var index_highest = 0;
                $("div").each(function() {
                    var index_current = parseInt($(this).css("zIndex"), 10);
                    if (index_current > index_highest) {
                        index_highest = index_current;
                    }
                });
                this.zIndex = index_highest + 10;
            }
            var offset, top, left, containerOffset;
            if (this.container instanceof $) {
                containerOffset = this.container.offset();
            } else {
                containerOffset = $(this.container).offset();
            }
            if (this.component) {
                offset = this.component.offset();
                left = offset.left;
                if (this.pickerPosition == "bottom-left" || this.pickerPosition == "top-left") {
                    left += this.component.outerWidth() - this.picker.outerWidth();
                }
            } else {
                offset = this.element.offset();
                left = offset.left;
            }
            if (left + 220 > document.body.clientWidth) {
                left = document.body.clientWidth - 220;
            }
            if (this.pickerPosition == "top-left" || this.pickerPosition == "top-right") {
                top = offset.top - this.picker.outerHeight();
            } else {
                top = offset.top + this.height;
            }
            top = top - containerOffset.top;
            left = left - containerOffset.left;
            if (this.container != "body") top = top + document.body.scrollTop;
            this.picker.css({
                top: top,
                left: left,
                zIndex: this.zIndex
            });
        },
        update: function() {
            var date, fromArgs = false;
            if (arguments && arguments.length && (typeof arguments[0] === "string" || arguments[0] instanceof Date)) {
                date = arguments[0];
                fromArgs = true;
            } else {
                date = (this.isInput ? this.element.val() : this.element.find("input").val()) || this.element.data("date") || this.initialDate;
                if (typeof date == "string" || date instanceof String) {
                    date = date.replace(/^\s+|\s+$/g, "");
                }
            }
            if (!date) {
                date = new Date();
                fromArgs = false;
            }
            this.date = DPGlobal.parseDate(date, this.format, this.language, this.formatType);
            if (fromArgs) this.setValue();
            if (this.date < this.startDate) {
                this.viewDate = new Date(this.startDate);
            } else if (this.date > this.endDate) {
                this.viewDate = new Date(this.endDate);
            } else {
                this.viewDate = new Date(this.date);
            }
            this.fill();
        },
        fillDow: function() {
            var dowCnt = this.weekStart, html = "<tr>";
            while (dowCnt < this.weekStart + 7) {
                html += '<th class="dow">' + dates[this.language].daysMin[dowCnt++ % 7] + "</th>";
            }
            html += "</tr>";
            this.picker.find(".datetimepicker-days thead").append(html);
        },
        fillMonths: function() {
            var html = "", i = 0;
            while (i < 12) {
                html += '<span class="month">' + dates[this.language].monthsShort[i++] + "</span>";
            }
            this.picker.find(".datetimepicker-months td").html(html);
        },
        fill: function() {
            if (this.date == null || this.viewDate == null) {
                return;
            }
            var d = new Date(this.viewDate), year = d.getUTCFullYear(), month = d.getUTCMonth(), dayMonth = d.getUTCDate(), hours = d.getUTCHours(), minutes = d.getUTCMinutes(), startYear = this.startDate !== -Infinity ? this.startDate.getUTCFullYear() : -Infinity, startMonth = this.startDate !== -Infinity ? this.startDate.getUTCMonth() + 1 : -Infinity, endYear = this.endDate !== Infinity ? this.endDate.getUTCFullYear() : Infinity, endMonth = this.endDate !== Infinity ? this.endDate.getUTCMonth() + 1 : Infinity, currentDate = new UTCDate(this.date.getUTCFullYear(), this.date.getUTCMonth(), this.date.getUTCDate()).valueOf(), today = new Date();
            this.picker.find(".datetimepicker-days thead th:eq(1)").text(dates[this.language].months[month] + " " + year);
            if (this.formatViewType == "time") {
                var formatted = this.getFormattedDate();
                this.picker.find(".datetimepicker-hours thead th:eq(1)").text(formatted);
                this.picker.find(".datetimepicker-minutes thead th:eq(1)").text(formatted);
            } else {
                this.picker.find(".datetimepicker-hours thead th:eq(1)").text(dayMonth + " " + dates[this.language].months[month] + " " + year);
                this.picker.find(".datetimepicker-minutes thead th:eq(1)").text(dayMonth + " " + dates[this.language].months[month] + " " + year);
            }
            this.picker.find("tfoot th.today").text(dates[this.language].today).toggle(this.todayBtn !== false);
            this.updateNavArrows();
            this.fillMonths();
            var prevMonth = UTCDate(year, month - 1, 28, 0, 0, 0, 0), day = DPGlobal.getDaysInMonth(prevMonth.getUTCFullYear(), prevMonth.getUTCMonth());
            prevMonth.setUTCDate(day);
            prevMonth.setUTCDate(day - (prevMonth.getUTCDay() - this.weekStart + 7) % 7);
            var nextMonth = new Date(prevMonth);
            nextMonth.setUTCDate(nextMonth.getUTCDate() + 42);
            nextMonth = nextMonth.valueOf();
            var html = [];
            var clsName;
            while (prevMonth.valueOf() < nextMonth) {
                if (prevMonth.getUTCDay() == this.weekStart) {
                    html.push("<tr>");
                }
                clsName = "";
                if (prevMonth.getUTCFullYear() < year || prevMonth.getUTCFullYear() == year && prevMonth.getUTCMonth() < month) {
                    clsName += " old";
                } else if (prevMonth.getUTCFullYear() > year || prevMonth.getUTCFullYear() == year && prevMonth.getUTCMonth() > month) {
                    clsName += " new";
                }
                if (this.todayHighlight && prevMonth.getUTCFullYear() == today.getFullYear() && prevMonth.getUTCMonth() == today.getMonth() && prevMonth.getUTCDate() == today.getDate()) {
                    clsName += " today";
                }
                if (prevMonth.valueOf() == currentDate) {
                    clsName += " active";
                }
                if (prevMonth.valueOf() + 864e5 <= this.startDate || prevMonth.valueOf() > this.endDate || $.inArray(prevMonth.getUTCDay(), this.daysOfWeekDisabled) !== -1) {
                    clsName += " disabled";
                }
                html.push('<td class="day' + clsName + '">' + prevMonth.getUTCDate() + "</td>");
                if (prevMonth.getUTCDay() == this.weekEnd) {
                    html.push("</tr>");
                }
                prevMonth.setUTCDate(prevMonth.getUTCDate() + 1);
            }
            this.picker.find(".datetimepicker-days tbody").empty().append(html.join(""));
            html = [];
            var txt = "", meridian = "", meridianOld = "";
            var hoursDisabled = this.hoursDisabled || [];
            for (var i = 0; i < 24; i++) {
                if (hoursDisabled.indexOf(i) !== -1) continue;
                var actual = UTCDate(year, month, dayMonth, i);
                clsName = "";
                if (actual.valueOf() + 36e5 <= this.startDate || actual.valueOf() > this.endDate) {
                    clsName += " disabled";
                } else if (hours == i) {
                    clsName += " active";
                }
                if (this.showMeridian && dates[this.language].meridiem.length == 2) {
                    meridian = i < 12 ? dates[this.language].meridiem[0] : dates[this.language].meridiem[1];
                    if (meridian != meridianOld) {
                        if (meridianOld != "") {
                            html.push("</fieldset>");
                        }
                        html.push('<fieldset class="hour"><legend>' + meridian.toUpperCase() + "</legend>");
                    }
                    meridianOld = meridian;
                    txt = i % 12 ? i % 12 : 12;
                    html.push('<span class="hour' + clsName + " hour_" + (i < 12 ? "am" : "pm") + '">' + txt + "</span>");
                    if (i == 23) {
                        html.push("</fieldset>");
                    }
                } else {
                    txt = i + ":00";
                    html.push('<span class="hour' + clsName + '">' + txt + "</span>");
                }
            }
            this.picker.find(".datetimepicker-hours td").html(html.join(""));
            html = [];
            txt = "", meridian = "", meridianOld = "";
            var minutesDisabled = this.minutesDisabled || [];
            for (var i = 0; i < 60; i += this.minuteStep) {
                if (minutesDisabled.indexOf(i) !== -1) continue;
                var actual = UTCDate(year, month, dayMonth, hours, i, 0);
                clsName = "";
                if (actual.valueOf() < this.startDate || actual.valueOf() > this.endDate) {
                    clsName += " disabled";
                } else if (Math.floor(minutes / this.minuteStep) == Math.floor(i / this.minuteStep)) {
                    clsName += " active";
                }
                if (this.showMeridian && dates[this.language].meridiem.length == 2) {
                    meridian = hours < 12 ? dates[this.language].meridiem[0] : dates[this.language].meridiem[1];
                    if (meridian != meridianOld) {
                        if (meridianOld != "") {
                            html.push("</fieldset>");
                        }
                        html.push('<fieldset class="minute"><legend>' + meridian.toUpperCase() + "</legend>");
                    }
                    meridianOld = meridian;
                    txt = hours % 12 ? hours % 12 : 12;
                    html.push('<span class="minute' + clsName + '">' + txt + ":" + (i < 10 ? "0" + i : i) + "</span>");
                    if (i == 59) {
                        html.push("</fieldset>");
                    }
                } else {
                    txt = i + ":00";
                    html.push('<span class="minute' + clsName + '">' + hours + ":" + (i < 10 ? "0" + i : i) + "</span>");
                }
            }
            this.picker.find(".datetimepicker-minutes td").html(html.join(""));
            var currentYear = this.date.getUTCFullYear();
            var months = this.picker.find(".datetimepicker-months").find("th:eq(1)").text(year).end().find("span").removeClass("active");
            if (currentYear == year) {
                months.eq(this.date.getUTCMonth() + 2).addClass("active");
            }
            if (year < startYear || year > endYear) {
                months.addClass("disabled");
            }
            if (year == startYear) {
                months.slice(0, startMonth + 1).addClass("disabled");
            }
            if (year == endYear) {
                months.slice(endMonth).addClass("disabled");
            }
            html = "";
            year = parseInt(year / 10, 10) * 10;
            var yearCont = this.picker.find(".datetimepicker-years").find("th:eq(1)").text(year + "-" + (year + 9)).end().find("td");
            year -= 1;
            for (var i = -1; i < 11; i++) {
                html += '<span class="year' + (i == -1 || i == 10 ? " old" : "") + (currentYear == year ? " active" : "") + (year < startYear || year > endYear ? " disabled" : "") + '">' + year + "</span>";
                year += 1;
            }
            yearCont.html(html);
            this.place();
        },
        updateNavArrows: function() {
            var d = new Date(this.viewDate), year = d.getUTCFullYear(), month = d.getUTCMonth(), day = d.getUTCDate(), hour = d.getUTCHours();
            switch (this.viewMode) {
              case 0:
                if (this.startDate !== -Infinity && year <= this.startDate.getUTCFullYear() && month <= this.startDate.getUTCMonth() && day <= this.startDate.getUTCDate() && hour <= this.startDate.getUTCHours()) {
                    this.picker.find(".prev").css({
                        visibility: "hidden"
                    });
                } else {
                    this.picker.find(".prev").css({
                        visibility: "visible"
                    });
                }
                if (this.endDate !== Infinity && year >= this.endDate.getUTCFullYear() && month >= this.endDate.getUTCMonth() && day >= this.endDate.getUTCDate() && hour >= this.endDate.getUTCHours()) {
                    this.picker.find(".next").css({
                        visibility: "hidden"
                    });
                } else {
                    this.picker.find(".next").css({
                        visibility: "visible"
                    });
                }
                break;

              case 1:
                if (this.startDate !== -Infinity && year <= this.startDate.getUTCFullYear() && month <= this.startDate.getUTCMonth() && day <= this.startDate.getUTCDate()) {
                    this.picker.find(".prev").css({
                        visibility: "hidden"
                    });
                } else {
                    this.picker.find(".prev").css({
                        visibility: "visible"
                    });
                }
                if (this.endDate !== Infinity && year >= this.endDate.getUTCFullYear() && month >= this.endDate.getUTCMonth() && day >= this.endDate.getUTCDate()) {
                    this.picker.find(".next").css({
                        visibility: "hidden"
                    });
                } else {
                    this.picker.find(".next").css({
                        visibility: "visible"
                    });
                }
                break;

              case 2:
                if (this.startDate !== -Infinity && year <= this.startDate.getUTCFullYear() && month <= this.startDate.getUTCMonth()) {
                    this.picker.find(".prev").css({
                        visibility: "hidden"
                    });
                } else {
                    this.picker.find(".prev").css({
                        visibility: "visible"
                    });
                }
                if (this.endDate !== Infinity && year >= this.endDate.getUTCFullYear() && month >= this.endDate.getUTCMonth()) {
                    this.picker.find(".next").css({
                        visibility: "hidden"
                    });
                } else {
                    this.picker.find(".next").css({
                        visibility: "visible"
                    });
                }
                break;

              case 3:
              case 4:
                if (this.startDate !== -Infinity && year <= this.startDate.getUTCFullYear()) {
                    this.picker.find(".prev").css({
                        visibility: "hidden"
                    });
                } else {
                    this.picker.find(".prev").css({
                        visibility: "visible"
                    });
                }
                if (this.endDate !== Infinity && year >= this.endDate.getUTCFullYear()) {
                    this.picker.find(".next").css({
                        visibility: "hidden"
                    });
                } else {
                    this.picker.find(".next").css({
                        visibility: "visible"
                    });
                }
                break;
            }
        },
        mousewheel: function(e) {
            e.preventDefault();
            e.stopPropagation();
            if (this.wheelPause) {
                return;
            }
            this.wheelPause = true;
            var originalEvent = e.originalEvent;
            var delta = originalEvent.wheelDelta;
            var mode = delta > 0 ? 1 : delta === 0 ? 0 : -1;
            if (this.wheelViewModeNavigationInverseDirection) {
                mode = -mode;
            }
            this.showMode(mode);
            setTimeout($.proxy(function() {
                this.wheelPause = false;
            }, this), this.wheelViewModeNavigationDelay);
        },
        click: function(e) {
            e.stopPropagation();
            e.preventDefault();
            var target = $(e.target).closest("span, td, th, legend");
            if (target.is("." + this.icontype)) {
                target = $(target).parent().closest("span, td, th, legend");
            }
            if (target.length == 1) {
                if (target.is(".disabled")) {
                    this.element.trigger({
                        type: "outOfRange",
                        date: this.viewDate,
                        startDate: this.startDate,
                        endDate: this.endDate
                    });
                    return;
                }
                switch (target[0].nodeName.toLowerCase()) {
                  case "th":
                    switch (target[0].className) {
                      case "switch":
                        this.showMode(1);
                        break;

                      case "prev":
                      case "next":
                        var dir = DPGlobal.modes[this.viewMode].navStep * (target[0].className == "prev" ? -1 : 1);
                        switch (this.viewMode) {
                          case 0:
                            this.viewDate = this.moveHour(this.viewDate, dir);
                            break;

                          case 1:
                            this.viewDate = this.moveDate(this.viewDate, dir);
                            break;

                          case 2:
                            this.viewDate = this.moveMonth(this.viewDate, dir);
                            break;

                          case 3:
                          case 4:
                            this.viewDate = this.moveYear(this.viewDate, dir);
                            break;
                        }
                        this.fill();
                        this.element.trigger({
                            type: target[0].className + ":" + this.convertViewModeText(this.viewMode),
                            date: this.viewDate,
                            startDate: this.startDate,
                            endDate: this.endDate
                        });
                        break;

                      case "today":
                        var date = new Date();
                        date = UTCDate(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds(), 0);
                        if (date < this.startDate) date = this.startDate; else if (date > this.endDate) date = this.endDate;
                        this.viewMode = this.startViewMode;
                        this.showMode(0);
                        this._setDate(date);
                        this.fill();
                        if (this.autoclose) {
                            this.hide();
                        }
                        break;
                    }
                    break;

                  case "span":
                    if (!target.is(".disabled")) {
                        var year = this.viewDate.getUTCFullYear(), month = this.viewDate.getUTCMonth(), day = this.viewDate.getUTCDate(), hours = this.viewDate.getUTCHours(), minutes = this.viewDate.getUTCMinutes(), seconds = this.viewDate.getUTCSeconds();
                        if (target.is(".month")) {
                            this.viewDate.setUTCDate(1);
                            month = target.parent().find("span").index(target);
                            day = this.viewDate.getUTCDate();
                            this.viewDate.setUTCMonth(month);
                            this.element.trigger({
                                type: "changeMonth",
                                date: this.viewDate
                            });
                            if (this.viewSelect >= 3) {
                                this._setDate(UTCDate(year, month, day, hours, minutes, seconds, 0));
                            }
                        } else if (target.is(".year")) {
                            this.viewDate.setUTCDate(1);
                            year = parseInt(target.text(), 10) || 0;
                            this.viewDate.setUTCFullYear(year);
                            this.element.trigger({
                                type: "changeYear",
                                date: this.viewDate
                            });
                            if (this.viewSelect >= 4) {
                                this._setDate(UTCDate(year, month, day, hours, minutes, seconds, 0));
                            }
                        } else if (target.is(".hour")) {
                            hours = parseInt(target.text(), 10) || 0;
                            if (target.hasClass("hour_am") || target.hasClass("hour_pm")) {
                                if (hours == 12 && target.hasClass("hour_am")) {
                                    hours = 0;
                                } else if (hours != 12 && target.hasClass("hour_pm")) {
                                    hours += 12;
                                }
                            }
                            this.viewDate.setUTCHours(hours);
                            this.element.trigger({
                                type: "changeHour",
                                date: this.viewDate
                            });
                            if (this.viewSelect >= 1) {
                                this._setDate(UTCDate(year, month, day, hours, minutes, seconds, 0));
                            }
                        } else if (target.is(".minute")) {
                            minutes = parseInt(target.text().substr(target.text().indexOf(":") + 1), 10) || 0;
                            this.viewDate.setUTCMinutes(minutes);
                            this.element.trigger({
                                type: "changeMinute",
                                date: this.viewDate
                            });
                            if (this.viewSelect >= 0) {
                                this._setDate(UTCDate(year, month, day, hours, minutes, seconds, 0));
                            }
                        }
                        if (this.viewMode != 0) {
                            var oldViewMode = this.viewMode;
                            this.showMode(-1);
                            this.fill();
                            if (oldViewMode == this.viewMode && this.autoclose) {
                                this.hide();
                            }
                        } else {
                            this.fill();
                            if (this.autoclose) {
                                this.hide();
                            }
                        }
                    }
                    break;

                  case "td":
                    if (target.is(".day") && !target.is(".disabled")) {
                        var day = parseInt(target.text(), 10) || 1;
                        var year = this.viewDate.getUTCFullYear(), month = this.viewDate.getUTCMonth(), hours = this.viewDate.getUTCHours(), minutes = this.viewDate.getUTCMinutes(), seconds = this.viewDate.getUTCSeconds();
                        if (target.is(".old")) {
                            if (month === 0) {
                                month = 11;
                                year -= 1;
                            } else {
                                month -= 1;
                            }
                        } else if (target.is(".new")) {
                            if (month == 11) {
                                month = 0;
                                year += 1;
                            } else {
                                month += 1;
                            }
                        }
                        this.viewDate.setUTCFullYear(year);
                        this.viewDate.setUTCMonth(month, day);
                        this.element.trigger({
                            type: "changeDay",
                            date: this.viewDate
                        });
                        if (this.viewSelect >= 2) {
                            this._setDate(UTCDate(year, month, day, hours, minutes, seconds, 0));
                        }
                    }
                    var oldViewMode = this.viewMode;
                    this.showMode(-1);
                    this.fill();
                    if (oldViewMode == this.viewMode && this.autoclose) {
                        this.hide();
                    }
                    break;
                }
            }
        },
        _setDate: function(date, which) {
            if (!which || which == "date") this.date = date;
            if (!which || which == "view") this.viewDate = date;
            this.fill();
            this.setValue();
            var element;
            if (this.isInput) {
                element = this.element;
            } else if (this.component) {
                element = this.element.find("input");
            }
            if (element) {
                element.change();
                if (this.autoclose && (!which || which == "date")) {}
            }
            this.element.trigger({
                type: "changeDate",
                date: this.date
            });
        },
        moveMinute: function(date, dir) {
            if (!dir) return date;
            var new_date = new Date(date.valueOf());
            new_date.setUTCMinutes(new_date.getUTCMinutes() + dir * this.minuteStep);
            return new_date;
        },
        moveHour: function(date, dir) {
            if (!dir) return date;
            var new_date = new Date(date.valueOf());
            new_date.setUTCHours(new_date.getUTCHours() + dir);
            return new_date;
        },
        moveDate: function(date, dir) {
            if (!dir) return date;
            var new_date = new Date(date.valueOf());
            new_date.setUTCDate(new_date.getUTCDate() + dir);
            return new_date;
        },
        moveMonth: function(date, dir) {
            if (!dir) return date;
            var new_date = new Date(date.valueOf()), day = new_date.getUTCDate(), month = new_date.getUTCMonth(), mag = Math.abs(dir), new_month, test;
            dir = dir > 0 ? 1 : -1;
            if (mag == 1) {
                test = dir == -1 ? function() {
                    return new_date.getUTCMonth() == month;
                } : function() {
                    return new_date.getUTCMonth() != new_month;
                };
                new_month = month + dir;
                new_date.setUTCMonth(new_month);
                if (new_month < 0 || new_month > 11) new_month = (new_month + 12) % 12;
            } else {
                for (var i = 0; i < mag; i++) new_date = this.moveMonth(new_date, dir);
                new_month = new_date.getUTCMonth();
                new_date.setUTCDate(day);
                test = function() {
                    return new_month != new_date.getUTCMonth();
                };
            }
            while (test()) {
                new_date.setUTCDate(--day);
                new_date.setUTCMonth(new_month);
            }
            return new_date;
        },
        moveYear: function(date, dir) {
            return this.moveMonth(date, dir * 12);
        },
        dateWithinRange: function(date) {
            return date >= this.startDate && date <= this.endDate;
        },
        keydown: function(e) {
            if (this.picker.is(":not(:visible)")) {
                if (e.keyCode == 27) this.show();
                return;
            }
            var dateChanged = false, dir, day, month, newDate, newViewDate;
            switch (e.keyCode) {
              case 27:
                this.hide();
                e.preventDefault();
                break;

              case 37:
              case 39:
                if (!this.keyboardNavigation) break;
                dir = e.keyCode == 37 ? -1 : 1;
                viewMode = this.viewMode;
                if (e.ctrlKey) {
                    viewMode += 2;
                } else if (e.shiftKey) {
                    viewMode += 1;
                }
                if (viewMode == 4) {
                    newDate = this.moveYear(this.date, dir);
                    newViewDate = this.moveYear(this.viewDate, dir);
                } else if (viewMode == 3) {
                    newDate = this.moveMonth(this.date, dir);
                    newViewDate = this.moveMonth(this.viewDate, dir);
                } else if (viewMode == 2) {
                    newDate = this.moveDate(this.date, dir);
                    newViewDate = this.moveDate(this.viewDate, dir);
                } else if (viewMode == 1) {
                    newDate = this.moveHour(this.date, dir);
                    newViewDate = this.moveHour(this.viewDate, dir);
                } else if (viewMode == 0) {
                    newDate = this.moveMinute(this.date, dir);
                    newViewDate = this.moveMinute(this.viewDate, dir);
                }
                if (this.dateWithinRange(newDate)) {
                    this.date = newDate;
                    this.viewDate = newViewDate;
                    this.setValue();
                    this.update();
                    e.preventDefault();
                    dateChanged = true;
                }
                break;

              case 38:
              case 40:
                if (!this.keyboardNavigation) break;
                dir = e.keyCode == 38 ? -1 : 1;
                viewMode = this.viewMode;
                if (e.ctrlKey) {
                    viewMode += 2;
                } else if (e.shiftKey) {
                    viewMode += 1;
                }
                if (viewMode == 4) {
                    newDate = this.moveYear(this.date, dir);
                    newViewDate = this.moveYear(this.viewDate, dir);
                } else if (viewMode == 3) {
                    newDate = this.moveMonth(this.date, dir);
                    newViewDate = this.moveMonth(this.viewDate, dir);
                } else if (viewMode == 2) {
                    newDate = this.moveDate(this.date, dir * 7);
                    newViewDate = this.moveDate(this.viewDate, dir * 7);
                } else if (viewMode == 1) {
                    if (this.showMeridian) {
                        newDate = this.moveHour(this.date, dir * 6);
                        newViewDate = this.moveHour(this.viewDate, dir * 6);
                    } else {
                        newDate = this.moveHour(this.date, dir * 4);
                        newViewDate = this.moveHour(this.viewDate, dir * 4);
                    }
                } else if (viewMode == 0) {
                    newDate = this.moveMinute(this.date, dir * 4);
                    newViewDate = this.moveMinute(this.viewDate, dir * 4);
                }
                if (this.dateWithinRange(newDate)) {
                    this.date = newDate;
                    this.viewDate = newViewDate;
                    this.setValue();
                    this.update();
                    e.preventDefault();
                    dateChanged = true;
                }
                break;

              case 13:
                if (this.viewMode != 0) {
                    var oldViewMode = this.viewMode;
                    this.showMode(-1);
                    this.fill();
                    if (oldViewMode == this.viewMode && this.autoclose) {
                        this.hide();
                    }
                } else {
                    this.fill();
                    if (this.autoclose) {
                        this.hide();
                    }
                }
                e.preventDefault();
                break;

              case 9:
                this.hide();
                break;
            }
            if (dateChanged) {
                var element;
                if (this.isInput) {
                    element = this.element;
                } else if (this.component) {
                    element = this.element.find("input");
                }
                if (element) {
                    element.change();
                }
                this.element.trigger({
                    type: "changeDate",
                    date: this.date
                });
            }
        },
        showMode: function(dir) {
            if (dir) {
                var newViewMode = Math.max(0, Math.min(DPGlobal.modes.length - 1, this.viewMode + dir));
                if (newViewMode >= this.minView && newViewMode <= this.maxView) {
                    this.element.trigger({
                        type: "changeMode",
                        date: this.viewDate,
                        oldViewMode: this.viewMode,
                        newViewMode: newViewMode
                    });
                    this.viewMode = newViewMode;
                }
            }
            this.picker.find(">div").hide().filter(".datetimepicker-" + DPGlobal.modes[this.viewMode].clsName).css("display", "block");
            this.updateNavArrows();
        },
        reset: function(e) {
            this._setDate(null, "date");
        },
        convertViewModeText: function(viewMode) {
            switch (viewMode) {
              case 4:
                return "decade";

              case 3:
                return "year";

              case 2:
                return "month";

              case 1:
                return "day";

              case 0:
                return "hour";
            }
        }
    };
    var old = $.fn.datetimepicker;
    $.fn.datetimepicker = function(option) {
        var args = Array.apply(null, arguments);
        args.shift();
        var internal_return;
        this.each(function() {
            var $this = $(this), data = $this.data("datetimepicker"), options = typeof option == "object" && option;
            if (!data) {
                $this.data("datetimepicker", data = new Datetimepicker(this, $.extend({}, $.fn.datetimepicker.defaults, options)));
            }
            if (typeof option == "string" && typeof data[option] == "function") {
                internal_return = data[option].apply(data, args);
                if (internal_return !== undefined) {
                    return false;
                }
            }
        });
        if (internal_return !== undefined) return internal_return; else return this;
    };
    $.fn.datetimepicker.defaults = {};
    $.fn.datetimepicker.Constructor = Datetimepicker;
    var dates = $.fn.datetimepicker.dates = {
        en: {
            days: [ "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" ],
            daysShort: [ "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" ],
            daysMin: [ "Su", "Mo", "Tu", "We", "Th", "Fr", "Sa", "Su" ],
            months: [ "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" ],
            monthsShort: [ "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec" ],
            meridiem: [ "am", "pm" ],
            suffix: [ "st", "nd", "rd", "th" ],
            today: "Today"
        }
    };
    var DPGlobal = {
        modes: [ {
            clsName: "minutes",
            navFnc: "Hours",
            navStep: 1
        }, {
            clsName: "hours",
            navFnc: "Date",
            navStep: 1
        }, {
            clsName: "days",
            navFnc: "Month",
            navStep: 1
        }, {
            clsName: "months",
            navFnc: "FullYear",
            navStep: 1
        }, {
            clsName: "years",
            navFnc: "FullYear",
            navStep: 10
        } ],
        isLeapYear: function(year) {
            return year % 4 === 0 && year % 100 !== 0 || year % 400 === 0;
        },
        getDaysInMonth: function(year, month) {
            return [ 31, DPGlobal.isLeapYear(year) ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 ][month];
        },
        getDefaultFormat: function(type, field) {
            if (type == "standard") {
                if (field == "input") return "yyyy-mm-dd hh:ii"; else return "yyyy-mm-dd hh:ii:ss";
            } else if (type == "php") {
                if (field == "input") return "Y-m-d H:i"; else return "Y-m-d H:i:s";
            } else {
                throw new Error("Invalid format type.");
            }
        },
        validParts: function(type) {
            if (type == "standard") {
                return /hh?|HH?|p|P|ii?|ss?|dd?|DD?|mm?|MM?|yy(?:yy)?/g;
            } else if (type == "php") {
                return /[dDjlNwzFmMnStyYaABgGhHis]/g;
            } else {
                throw new Error("Invalid format type.");
            }
        },
        nonpunctuation: /[^ -\/:-@\[-`{-~\t\n\rTZ]+/g,
        parseFormat: function(format, type) {
            var separators = format.replace(this.validParts(type), "\x00").split("\x00"), parts = format.match(this.validParts(type));
            if (!separators || !separators.length || !parts || parts.length == 0) {
                throw new Error("Invalid date format.");
            }
            return {
                separators: separators,
                parts: parts
            };
        },
        parseDate: function(date, format, language, type) {
            if (date instanceof Date) {
                var dateUTC = new Date(date.valueOf() - date.getTimezoneOffset() * 6e4);
                dateUTC.setMilliseconds(0);
                return dateUTC;
            }
            if (/^\d{4}\-\d{1,2}\-\d{1,2}$/.test(date)) {
                format = this.parseFormat("yyyy-mm-dd", type);
            }
            if (/^\d{4}\-\d{1,2}\-\d{1,2}[T ]\d{1,2}\:\d{1,2}$/.test(date)) {
                format = this.parseFormat("yyyy-mm-dd hh:ii", type);
            }
            if (/^\d{4}\-\d{1,2}\-\d{1,2}[T ]\d{1,2}\:\d{1,2}\:\d{1,2}[Z]{0,1}$/.test(date)) {
                format = this.parseFormat("yyyy-mm-dd hh:ii:ss", type);
            }
            if (/^[-+]\d+[dmwy]([\s,]+[-+]\d+[dmwy])*$/.test(date)) {
                var part_re = /([-+]\d+)([dmwy])/, parts = date.match(/([-+]\d+)([dmwy])/g), part, dir;
                date = new Date();
                for (var i = 0; i < parts.length; i++) {
                    part = part_re.exec(parts[i]);
                    dir = parseInt(part[1]);
                    switch (part[2]) {
                      case "d":
                        date.setUTCDate(date.getUTCDate() + dir);
                        break;

                      case "m":
                        date = Datetimepicker.prototype.moveMonth.call(Datetimepicker.prototype, date, dir);
                        break;

                      case "w":
                        date.setUTCDate(date.getUTCDate() + dir * 7);
                        break;

                      case "y":
                        date = Datetimepicker.prototype.moveYear.call(Datetimepicker.prototype, date, dir);
                        break;
                    }
                }
                return UTCDate(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(), date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds(), 0);
            }
            var parts = date && date.toString().match(this.nonpunctuation) || [], date = new Date(0, 0, 0, 0, 0, 0, 0), parsed = {}, setters_order = [ "hh", "h", "ii", "i", "ss", "s", "yyyy", "yy", "M", "MM", "m", "mm", "D", "DD", "d", "dd", "H", "HH", "p", "P" ], setters_map = {
                hh: function(d, v) {
                    return d.setUTCHours(v);
                },
                h: function(d, v) {
                    return d.setUTCHours(v);
                },
                HH: function(d, v) {
                    return d.setUTCHours(v == 12 ? 0 : v);
                },
                H: function(d, v) {
                    return d.setUTCHours(v == 12 ? 0 : v);
                },
                ii: function(d, v) {
                    return d.setUTCMinutes(v);
                },
                i: function(d, v) {
                    return d.setUTCMinutes(v);
                },
                ss: function(d, v) {
                    return d.setUTCSeconds(v);
                },
                s: function(d, v) {
                    return d.setUTCSeconds(v);
                },
                yyyy: function(d, v) {
                    return d.setUTCFullYear(v);
                },
                yy: function(d, v) {
                    return d.setUTCFullYear(2e3 + v);
                },
                m: function(d, v) {
                    v -= 1;
                    while (v < 0) v += 12;
                    v %= 12;
                    d.setUTCMonth(v);
                    while (d.getUTCMonth() != v) if (isNaN(d.getUTCMonth())) return d; else d.setUTCDate(d.getUTCDate() - 1);
                    return d;
                },
                d: function(d, v) {
                    return d.setUTCDate(v);
                },
                p: function(d, v) {
                    return d.setUTCHours(v == 1 ? d.getUTCHours() + 12 : d.getUTCHours());
                }
            }, val, filtered, part;
            setters_map["M"] = setters_map["MM"] = setters_map["mm"] = setters_map["m"];
            setters_map["dd"] = setters_map["d"];
            setters_map["P"] = setters_map["p"];
            date = UTCDate(date.getFullYear(), date.getMonth(), date.getDate(), date.getHours(), date.getMinutes(), date.getSeconds());
            if (parts.length == format.parts.length) {
                for (var i = 0, cnt = format.parts.length; i < cnt; i++) {
                    val = parseInt(parts[i], 10);
                    part = format.parts[i];
                    if (isNaN(val)) {
                        switch (part) {
                          case "MM":
                            filtered = $(dates[language].months).filter(function() {
                                var m = this.slice(0, parts[i].length), p = parts[i].slice(0, m.length);
                                return m == p;
                            });
                            val = $.inArray(filtered[0], dates[language].months) + 1;
                            break;

                          case "M":
                            filtered = $(dates[language].monthsShort).filter(function() {
                                var m = this.slice(0, parts[i].length), p = parts[i].slice(0, m.length);
                                return m.toLowerCase() == p.toLowerCase();
                            });
                            val = $.inArray(filtered[0], dates[language].monthsShort) + 1;
                            break;

                          case "p":
                          case "P":
                            val = $.inArray(parts[i].toLowerCase(), dates[language].meridiem);
                            break;
                        }
                    }
                    parsed[part] = val;
                }
                for (var i = 0, s; i < setters_order.length; i++) {
                    s = setters_order[i];
                    if (s in parsed && !isNaN(parsed[s])) setters_map[s](date, parsed[s]);
                }
            }
            return date;
        },
        formatDate: function(date, format, language, type) {
            if (date == null) {
                return "";
            }
            var val;
            if (type == "standard") {
                val = {
                    yy: date.getUTCFullYear().toString().substring(2),
                    yyyy: date.getUTCFullYear(),
                    m: date.getUTCMonth() + 1,
                    M: dates[language].monthsShort[date.getUTCMonth()],
                    MM: dates[language].months[date.getUTCMonth()],
                    d: date.getUTCDate(),
                    D: dates[language].daysShort[date.getUTCDay()],
                    DD: dates[language].days[date.getUTCDay()],
                    p: dates[language].meridiem.length == 2 ? dates[language].meridiem[date.getUTCHours() < 12 ? 0 : 1] : "",
                    h: date.getUTCHours(),
                    i: date.getUTCMinutes(),
                    s: date.getUTCSeconds()
                };
                if (dates[language].meridiem.length == 2) {
                    val.H = val.h % 12 == 0 ? 12 : val.h % 12;
                } else {
                    val.H = val.h;
                }
                val.HH = (val.H < 10 ? "0" : "") + val.H;
                val.P = val.p.toUpperCase();
                val.hh = (val.h < 10 ? "0" : "") + val.h;
                val.ii = (val.i < 10 ? "0" : "") + val.i;
                val.ss = (val.s < 10 ? "0" : "") + val.s;
                val.dd = (val.d < 10 ? "0" : "") + val.d;
                val.mm = (val.m < 10 ? "0" : "") + val.m;
            } else if (type == "php") {
                val = {
                    y: date.getUTCFullYear().toString().substring(2),
                    Y: date.getUTCFullYear(),
                    F: dates[language].months[date.getUTCMonth()],
                    M: dates[language].monthsShort[date.getUTCMonth()],
                    n: date.getUTCMonth() + 1,
                    t: DPGlobal.getDaysInMonth(date.getUTCFullYear(), date.getUTCMonth()),
                    j: date.getUTCDate(),
                    l: dates[language].days[date.getUTCDay()],
                    D: dates[language].daysShort[date.getUTCDay()],
                    w: date.getUTCDay(),
                    N: date.getUTCDay() == 0 ? 7 : date.getUTCDay(),
                    S: date.getUTCDate() % 10 <= dates[language].suffix.length ? dates[language].suffix[date.getUTCDate() % 10 - 1] : "",
                    a: dates[language].meridiem.length == 2 ? dates[language].meridiem[date.getUTCHours() < 12 ? 0 : 1] : "",
                    g: date.getUTCHours() % 12 == 0 ? 12 : date.getUTCHours() % 12,
                    G: date.getUTCHours(),
                    i: date.getUTCMinutes(),
                    s: date.getUTCSeconds()
                };
                val.m = (val.n < 10 ? "0" : "") + val.n;
                val.d = (val.j < 10 ? "0" : "") + val.j;
                val.A = val.a.toString().toUpperCase();
                val.h = (val.g < 10 ? "0" : "") + val.g;
                val.H = (val.G < 10 ? "0" : "") + val.G;
                val.i = (val.i < 10 ? "0" : "") + val.i;
                val.s = (val.s < 10 ? "0" : "") + val.s;
            } else {
                throw new Error("Invalid format type.");
            }
            var date = [], seps = $.extend([], format.separators);
            for (var i = 0, cnt = format.parts.length; i < cnt; i++) {
                if (seps.length) {
                    date.push(seps.shift());
                }
                date.push(val[format.parts[i]]);
            }
            if (seps.length) {
                date.push(seps.shift());
            }
            return date.join("");
        },
        convertViewMode: function(viewMode) {
            switch (viewMode) {
              case 4:
              case "decade":
                viewMode = 4;
                break;

              case 3:
              case "year":
                viewMode = 3;
                break;

              case 2:
              case "month":
                viewMode = 2;
                break;

              case 1:
              case "day":
                viewMode = 1;
                break;

              case 0:
              case "hour":
                viewMode = 0;
                break;
            }
            return viewMode;
        },
        headTemplate: "<thead>" + "<tr>" + '<th class="prev"><i class="{leftArrow}"/></th>' + '<th colspan="5" class="switch"></th>' + '<th class="next"><i class="{rightArrow}"/></th>' + "</tr>" + "</thead>",
        headTemplateV3: "<thead>" + "<tr>" + '<th class="prev"><span class="{iconType} {leftArrow}"></span> </th>' + '<th colspan="5" class="switch"></th>' + '<th class="next"><span class="{iconType} {rightArrow}"></span> </th>' + "</tr>" + "</thead>",
        contTemplate: '<tbody><tr><td colspan="7"></td></tr></tbody>',
        footTemplate: '<tfoot><tr><th colspan="7" class="today"></th></tr></tfoot>'
    };
    DPGlobal.template = '<div class="datetimepicker">' + '<div class="datetimepicker-minutes">' + '<table class=" table-condensed">' + DPGlobal.headTemplate + DPGlobal.contTemplate + DPGlobal.footTemplate + "</table>" + "</div>" + '<div class="datetimepicker-hours">' + '<table class=" table-condensed">' + DPGlobal.headTemplate + DPGlobal.contTemplate + DPGlobal.footTemplate + "</table>" + "</div>" + '<div class="datetimepicker-days">' + '<table class=" table-condensed">' + DPGlobal.headTemplate + "<tbody></tbody>" + DPGlobal.footTemplate + "</table>" + "</div>" + '<div class="datetimepicker-months">' + '<table class="table-condensed">' + DPGlobal.headTemplate + DPGlobal.contTemplate + DPGlobal.footTemplate + "</table>" + "</div>" + '<div class="datetimepicker-years">' + '<table class="table-condensed">' + DPGlobal.headTemplate + DPGlobal.contTemplate + DPGlobal.footTemplate + "</table>" + "</div>" + "</div>";
    DPGlobal.templateV3 = '<div class="datetimepicker">' + '<div class="datetimepicker-minutes">' + '<table class=" table-condensed">' + DPGlobal.headTemplateV3 + DPGlobal.contTemplate + DPGlobal.footTemplate + "</table>" + "</div>" + '<div class="datetimepicker-hours">' + '<table class=" table-condensed">' + DPGlobal.headTemplateV3 + DPGlobal.contTemplate + DPGlobal.footTemplate + "</table>" + "</div>" + '<div class="datetimepicker-days">' + '<table class=" table-condensed">' + DPGlobal.headTemplateV3 + "<tbody></tbody>" + DPGlobal.footTemplate + "</table>" + "</div>" + '<div class="datetimepicker-months">' + '<table class="table-condensed">' + DPGlobal.headTemplateV3 + DPGlobal.contTemplate + DPGlobal.footTemplate + "</table>" + "</div>" + '<div class="datetimepicker-years">' + '<table class="table-condensed">' + DPGlobal.headTemplateV3 + DPGlobal.contTemplate + DPGlobal.footTemplate + "</table>" + "</div>" + "</div>";
    $.fn.datetimepicker.DPGlobal = DPGlobal;
    $.fn.datetimepicker.noConflict = function() {
        $.fn.datetimepicker = old;
        return this;
    };
    $(document).on("focus.datetimepicker.data-api click.datetimepicker.data-api", '[data-provide="datetimepicker"]', function(e) {
        var $this = $(this);
        if ($this.data("datetimepicker")) return;
        e.preventDefault();
        $this.datetimepicker("show");
    });
    $(function() {
        $('[data-provide="datetimepicker-inline"]').datetimepicker();
    });
}(window.jQuery);

(function() {
    var $, AbstractChosen, Chosen, SelectParser, _ref, __hasProp = {}.hasOwnProperty, __extends = function(child, parent) {
        for (var key in parent) {
            if (__hasProp.call(parent, key)) child[key] = parent[key];
        }
        function ctor() {
            this.constructor = child;
        }
        ctor.prototype = parent.prototype;
        child.prototype = new ctor();
        child.__super__ = parent.prototype;
        return child;
    };
    SelectParser = function() {
        function SelectParser() {
            this.options_index = 0;
            this.parsed = [];
        }
        SelectParser.prototype.add_node = function(child) {
            if (child.nodeName.toUpperCase() === "OPTGROUP") {
                return this.add_group(child);
            } else {
                return this.add_option(child);
            }
        };
        SelectParser.prototype.add_group = function(group) {
            var group_position, option, _i, _len, _ref, _results;
            group_position = this.parsed.length;
            this.parsed.push({
                array_index: group_position,
                group: true,
                label: this.escapeExpression(group.label),
                title: group.title ? group.title : void 0,
                children: 0,
                disabled: group.disabled,
                classes: group.className
            });
            _ref = group.childNodes;
            _results = [];
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                option = _ref[_i];
                _results.push(this.add_option(option, group_position, group.disabled));
            }
            return _results;
        };
        SelectParser.prototype.add_option = function(option, group_position, group_disabled) {
            if (option.nodeName.toUpperCase() === "OPTION") {
                if (option.text !== "") {
                    if (group_position != null) {
                        this.parsed[group_position].children += 1;
                    }
                    this.parsed.push({
                        array_index: this.parsed.length,
                        options_index: this.options_index,
                        value: option.value,
                        text: option.text,
                        html: option.innerHTML,
                        title: option.title ? option.title : void 0,
                        selected: option.selected,
                        disabled: group_disabled === true ? group_disabled : option.disabled,
                        group_array_index: group_position,
                        group_label: group_position != null ? this.parsed[group_position].label : null,
                        classes: option.className,
                        style: option.style.cssText
                    });
                } else {
                    this.parsed.push({
                        array_index: this.parsed.length,
                        options_index: this.options_index,
                        empty: true
                    });
                }
                return this.options_index += 1;
            }
        };
        SelectParser.prototype.escapeExpression = function(text) {
            var map, unsafe_chars;
            if (text == null || text === false) {
                return "";
            }
            if (!/[\&\<\>\"\'\`]/.test(text)) {
                return text;
            }
            map = {
                "<": "&lt;",
                ">": "&gt;",
                '"': "&quot;",
                "'": "&#x27;",
                "`": "&#x60;"
            };
            unsafe_chars = /&(?!\w+;)|[\<\>\"\'\`]/g;
            return text.replace(unsafe_chars, function(chr) {
                return map[chr] || "&amp;";
            });
        };
        return SelectParser;
    }();
    SelectParser.select_to_array = function(select) {
        var child, parser, _i, _len, _ref;
        parser = new SelectParser();
        _ref = select.childNodes;
        for (_i = 0, _len = _ref.length; _i < _len; _i++) {
            child = _ref[_i];
            parser.add_node(child);
        }
        return parser.parsed;
    };
    AbstractChosen = function() {
        function AbstractChosen(form_field, options) {
            this.form_field = form_field;
            this.options = options != null ? options : {};
            if (!AbstractChosen.browser_is_supported()) {
                return;
            }
            this.is_multiple = this.form_field.multiple;
            this.set_default_text();
            this.set_default_values();
            this.setup();
            this.set_up_html();
            this.register_observers();
            this.on_ready();
        }
        AbstractChosen.prototype.set_default_values = function() {
            var _this = this;
            this.click_test_action = function(evt) {
                return _this.test_active_click(evt);
            };
            this.activate_action = function(evt) {
                return _this.activate_field(evt);
            };
            this.active_field = false;
            this.mouse_on_container = false;
            this.results_showing = false;
            this.result_highlighted = null;
            this.allow_single_deselect = this.options.allow_single_deselect != null && this.form_field.options[0] != null && this.form_field.options[0].text === "" ? this.options.allow_single_deselect : false;
            this.disable_search_threshold = this.options.disable_search_threshold || 0;
            this.disable_search = this.options.disable_search || false;
            this.enable_split_word_search = this.options.enable_split_word_search != null ? this.options.enable_split_word_search : true;
            this.group_search = this.options.group_search != null ? this.options.group_search : true;
            this.search_contains = this.options.search_contains || false;
            this.single_backstroke_delete = this.options.single_backstroke_delete != null ? this.options.single_backstroke_delete : true;
            this.max_selected_options = this.options.max_selected_options || Infinity;
            this.inherit_select_classes = this.options.inherit_select_classes || false;
            this.display_selected_options = this.options.display_selected_options != null ? this.options.display_selected_options : true;
            this.display_disabled_options = this.options.display_disabled_options != null ? this.options.display_disabled_options : true;
            return this.include_group_label_in_selected = this.options.include_group_label_in_selected || false;
        };
        AbstractChosen.prototype.set_default_text = function() {
            if (this.form_field.getAttribute("data-placeholder")) {
                this.default_text = this.form_field.getAttribute("data-placeholder");
            } else if (this.is_multiple) {
                this.default_text = this.options.placeholder_text_multiple || this.options.placeholder_text || AbstractChosen.default_multiple_text;
            } else {
                this.default_text = this.options.placeholder_text_single || this.options.placeholder_text || AbstractChosen.default_single_text;
            }
            return this.results_none_found = this.form_field.getAttribute("data-no_results_text") || this.options.no_results_text || AbstractChosen.default_no_result_text;
        };
        AbstractChosen.prototype.choice_label = function(item) {
            if (this.include_group_label_in_selected && item.group_label != null) {
                return "<b class='group-name'>" + item.group_label + "</b>" + item.html;
            } else {
                return item.html;
            }
        };
        AbstractChosen.prototype.mouse_enter = function() {
            return this.mouse_on_container = true;
        };
        AbstractChosen.prototype.mouse_leave = function() {
            return this.mouse_on_container = false;
        };
        AbstractChosen.prototype.input_focus = function(evt) {
            var _this = this;
            if (this.is_multiple) {
                if (!this.active_field) {
                    return setTimeout(function() {
                        return _this.container_mousedown();
                    }, 50);
                }
            } else {
                if (!this.active_field) {
                    return this.activate_field();
                }
            }
        };
        AbstractChosen.prototype.input_blur = function(evt) {
            var _this = this;
            if (!this.mouse_on_container) {
                this.active_field = false;
                return setTimeout(function() {
                    return _this.blur_test();
                }, 100);
            }
        };
        AbstractChosen.prototype.results_option_build = function(options) {
            var content, data, _i, _len, _ref;
            content = "";
            _ref = this.results_data;
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                data = _ref[_i];
                if (data.group) {
                    content += this.result_add_group(data);
                } else {
                    content += this.result_add_option(data);
                }
                if (options != null ? options.first : void 0) {
                    if (data.selected && this.is_multiple) {
                        this.choice_build(data);
                    } else if (data.selected && !this.is_multiple) {
                        this.single_set_selected_text(this.choice_label(data));
                    }
                }
            }
            return content;
        };
        AbstractChosen.prototype.result_add_option = function(option) {
            var classes, option_el;
            if (!option.search_match) {
                return "";
            }
            if (!this.include_option_in_results(option)) {
                return "";
            }
            classes = [];
            if (!option.disabled && !(option.selected && this.is_multiple)) {
                classes.push("active-result");
            }
            if (option.disabled && !(option.selected && this.is_multiple)) {
                classes.push("disabled-result");
            }
            if (option.selected) {
                classes.push("result-selected");
            }
            if (option.group_array_index != null) {
                classes.push("group-option");
            }
            if (option.classes !== "") {
                classes.push(option.classes);
            }
            option_el = document.createElement("li");
            option_el.className = classes.join(" ");
            option_el.style.cssText = option.style;
            option_el.setAttribute("data-option-array-index", option.array_index);
            option_el.innerHTML = option.search_text;
            if (option.title) {
                option_el.title = option.title;
            }
            return this.outerHTML(option_el);
        };
        AbstractChosen.prototype.result_add_group = function(group) {
            var classes, group_el;
            if (!(group.search_match || group.group_match)) {
                return "";
            }
            if (!(group.active_options > 0)) {
                return "";
            }
            classes = [];
            classes.push("group-result");
            if (group.classes) {
                classes.push(group.classes);
            }
            group_el = document.createElement("li");
            group_el.className = classes.join(" ");
            group_el.innerHTML = group.search_text;
            if (group.title) {
                group_el.title = group.title;
            }
            return this.outerHTML(group_el);
        };
        AbstractChosen.prototype.results_update_field = function() {
            this.set_default_text();
            if (!this.is_multiple) {
                this.results_reset_cleanup();
            }
            this.result_clear_highlight();
            this.results_build();
            if (this.results_showing) {
                return this.winnow_results();
            }
        };
        AbstractChosen.prototype.reset_single_select_options = function() {
            var result, _i, _len, _ref, _results;
            _ref = this.results_data;
            _results = [];
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                result = _ref[_i];
                if (result.selected) {
                    _results.push(result.selected = false);
                } else {
                    _results.push(void 0);
                }
            }
            return _results;
        };
        AbstractChosen.prototype.results_toggle = function() {
            if (this.results_showing) {
                return this.results_hide();
            } else {
                return this.results_show();
            }
        };
        AbstractChosen.prototype.results_search = function(evt) {
            if (this.results_showing) {
                return this.winnow_results();
            } else {
                return this.results_show();
            }
        };
        AbstractChosen.prototype.winnow_results = function() {
            var escapedSearchText, option, regex, results, results_group, searchText, startpos, text, zregex, _i, _len, _ref;
            this.no_results_clear();
            results = 0;
            searchText = this.get_search_text();
            escapedSearchText = searchText.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, "\\$&");
            zregex = new RegExp(escapedSearchText, "i");
            regex = this.get_search_regex(escapedSearchText);
            _ref = this.results_data;
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                option = _ref[_i];
                option.search_match = false;
                results_group = null;
                if (this.include_option_in_results(option)) {
                    if (option.group) {
                        option.group_match = false;
                        option.active_options = 0;
                    }
                    if (option.group_array_index != null && this.results_data[option.group_array_index]) {
                        results_group = this.results_data[option.group_array_index];
                        if (results_group.active_options === 0 && results_group.search_match) {
                            results += 1;
                        }
                        results_group.active_options += 1;
                    }
                    option.search_text = option.group ? option.label : option.html;
                    if (!(option.group && !this.group_search)) {
                        option.search_match = this.search_string_match(option.search_text, regex);
                        if (option.search_match && !option.group) {
                            results += 1;
                        }
                        if (option.search_match) {
                            if (searchText.length) {
                                startpos = option.search_text.search(zregex);
                                text = option.search_text.substr(0, startpos + searchText.length) + "</em>" + option.search_text.substr(startpos + searchText.length);
                                option.search_text = text.substr(0, startpos) + "<em>" + text.substr(startpos);
                            }
                            if (results_group != null) {
                                results_group.group_match = true;
                            }
                        } else if (option.group_array_index != null && this.results_data[option.group_array_index].search_match) {
                            option.search_match = true;
                        }
                    }
                }
            }
            this.result_clear_highlight();
            if (results < 1 && searchText.length) {
                this.update_results_content("");
                return this.no_results(searchText);
            } else {
                this.update_results_content(this.results_option_build());
                return this.winnow_results_set_highlight();
            }
        };
        AbstractChosen.prototype.get_search_regex = function(escaped_search_string) {
            var regex_anchor;
            regex_anchor = this.search_contains ? "" : "^";
            return new RegExp(regex_anchor + escaped_search_string, "i");
        };
        AbstractChosen.prototype.search_string_match = function(search_string, regex) {
            var part, parts, _i, _len;
            if (regex.test(search_string)) {
                return true;
            } else if (this.enable_split_word_search && (search_string.indexOf(" ") >= 0 || search_string.indexOf("[") === 0)) {
                parts = search_string.replace(/\[|\]/g, "").split(" ");
                if (parts.length) {
                    for (_i = 0, _len = parts.length; _i < _len; _i++) {
                        part = parts[_i];
                        if (regex.test(part)) {
                            return true;
                        }
                    }
                }
            }
        };
        AbstractChosen.prototype.choices_count = function() {
            var option, _i, _len, _ref;
            if (this.selected_option_count != null) {
                return this.selected_option_count;
            }
            this.selected_option_count = 0;
            _ref = this.form_field.options;
            for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                option = _ref[_i];
                if (option.selected) {
                    this.selected_option_count += 1;
                }
            }
            return this.selected_option_count;
        };
        AbstractChosen.prototype.choices_click = function(evt) {
            evt.preventDefault();
            if (!(this.results_showing || this.is_disabled)) {
                return this.results_show();
            }
        };
        AbstractChosen.prototype.keyup_checker = function(evt) {
            var stroke, _ref;
            stroke = (_ref = evt.which) != null ? _ref : evt.keyCode;
            this.search_field_scale();
            switch (stroke) {
              case 8:
                if (this.is_multiple && this.backstroke_length < 1 && this.choices_count() > 0) {
                    return this.keydown_backstroke();
                } else if (!this.pending_backstroke) {
                    this.result_clear_highlight();
                    return this.results_search();
                }
                break;

              case 13:
                evt.preventDefault();
                if (this.results_showing) {
                    return this.result_select(evt);
                }
                break;

              case 27:
                if (this.results_showing) {
                    this.results_hide();
                }
                return true;

              case 9:
              case 38:
              case 40:
              case 16:
              case 91:
              case 17:
                break;

              default:
                return this.results_search();
            }
        };
        AbstractChosen.prototype.clipboard_event_checker = function(evt) {
            var _this = this;
            return setTimeout(function() {
                return _this.results_search();
            }, 50);
        };
        AbstractChosen.prototype.container_width = function() {
            if (this.options.width != null) {
                return this.options.width;
            } else {
                return "" + this.form_field.offsetWidth + "px";
            }
        };
        AbstractChosen.prototype.include_option_in_results = function(option) {
            if (this.is_multiple && (!this.display_selected_options && option.selected)) {
                return false;
            }
            if (!this.display_disabled_options && option.disabled) {
                return false;
            }
            if (option.empty) {
                return false;
            }
            return true;
        };
        AbstractChosen.prototype.search_results_touchstart = function(evt) {
            this.touch_started = true;
            return this.search_results_mouseover(evt);
        };
        AbstractChosen.prototype.search_results_touchmove = function(evt) {
            this.touch_started = false;
            return this.search_results_mouseout(evt);
        };
        AbstractChosen.prototype.search_results_touchend = function(evt) {
            if (this.touch_started) {
                return this.search_results_mouseup(evt);
            }
        };
        AbstractChosen.prototype.outerHTML = function(element) {
            var tmp;
            if (element.outerHTML) {
                return element.outerHTML;
            }
            tmp = document.createElement("div");
            tmp.appendChild(element);
            return tmp.innerHTML;
        };
        AbstractChosen.browser_is_supported = function() {
            if (window.navigator.appName === "Microsoft Internet Explorer") {
                return document.documentMode >= 8;
            }
            if (/iP(od|hone)/i.test(window.navigator.userAgent)) {
                return false;
            }
            if (/Android/i.test(window.navigator.userAgent)) {
                if (/Mobile/i.test(window.navigator.userAgent)) {
                    return false;
                }
            }
            return true;
        };
        AbstractChosen.default_multiple_text = "Select Some Options";
        AbstractChosen.default_single_text = "Select an Option";
        AbstractChosen.default_no_result_text = "No results match";
        return AbstractChosen;
    }();
    $ = jQuery;
    $.fn.extend({
        chosen: function(options) {
            if (!AbstractChosen.browser_is_supported()) {
                return this;
            }
            return this.each(function(input_field) {
                var $this, chosen;
                $this = $(this);
                chosen = $this.data("chosen");
                if (options === "destroy" && chosen instanceof Chosen) {
                    chosen.destroy();
                } else if (!(chosen instanceof Chosen)) {
                    $this.data("chosen", new Chosen(this, options));
                }
            });
        }
    });
    Chosen = function(_super) {
        __extends(Chosen, _super);
        function Chosen() {
            _ref = Chosen.__super__.constructor.apply(this, arguments);
            return _ref;
        }
        Chosen.prototype.setup = function() {
            this.form_field_jq = $(this.form_field);
            this.current_selectedIndex = this.form_field.selectedIndex;
            return this.is_rtl = this.form_field_jq.hasClass("chosen-rtl");
        };
        Chosen.prototype.set_up_html = function() {
            var container_classes, container_props;
            container_classes = [ "chosen-container" ];
            container_classes.push("chosen-container-" + (this.is_multiple ? "multi" : "single"));
            if (this.inherit_select_classes && this.form_field.className) {
                container_classes.push(this.form_field.className);
            }
            if (this.is_rtl) {
                container_classes.push("chosen-rtl");
            }
            container_props = {
                "class": container_classes.join(" "),
                style: "width: " + this.container_width() + ";",
                title: this.form_field.title
            };
            if (this.form_field.id.length) {
                container_props.id = this.form_field.id.replace(/[^\w]/g, "_") + "_chosen";
            }
            this.container = $("<div />", container_props);
            if (this.is_multiple) {
                this.container.html('<ul class="chosen-choices"><li class="search-field"><input type="text" value="' + this.default_text + '" class="default" autocomplete="off" style="width:25px;" /></li></ul><div class="chosen-drop"><ul class="chosen-results"></ul></div>');
            } else {
                this.container.html('<a class="chosen-single chosen-default" tabindex="-1"><span>' + this.default_text + '</span><div><b></b></div></a><div class="chosen-drop"><div class="chosen-search"><input type="text" autocomplete="off" /></div><ul class="chosen-results"></ul></div>');
            }
            this.form_field_jq.hide().after(this.container);
            this.dropdown = this.container.find("div.chosen-drop").first();
            this.search_field = this.container.find("input").first();
            this.search_results = this.container.find("ul.chosen-results").first();
            this.search_field_scale();
            this.search_no_results = this.container.find("li.no-results").first();
            if (this.is_multiple) {
                this.search_choices = this.container.find("ul.chosen-choices").first();
                this.search_container = this.container.find("li.search-field").first();
            } else {
                this.search_container = this.container.find("div.chosen-search").first();
                this.selected_item = this.container.find(".chosen-single").first();
            }
            this.results_build();
            this.set_tab_index();
            return this.set_label_behavior();
        };
        Chosen.prototype.on_ready = function() {
            return this.form_field_jq.trigger("chosen:ready", {
                chosen: this
            });
        };
        Chosen.prototype.register_observers = function() {
            var _this = this;
            this.container.bind("touchstart.chosen", function(evt) {
                _this.container_mousedown(evt);
                return evt.preventDefault();
            });
            this.container.bind("touchend.chosen", function(evt) {
                _this.container_mouseup(evt);
                return evt.preventDefault();
            });
            this.container.bind("mousedown.chosen", function(evt) {
                _this.container_mousedown(evt);
            });
            this.container.bind("mouseup.chosen", function(evt) {
                _this.container_mouseup(evt);
            });
            this.container.bind("mouseenter.chosen", function(evt) {
                _this.mouse_enter(evt);
            });
            this.container.bind("mouseleave.chosen", function(evt) {
                _this.mouse_leave(evt);
            });
            this.search_results.bind("mouseup.chosen", function(evt) {
                _this.search_results_mouseup(evt);
            });
            this.search_results.bind("mouseover.chosen", function(evt) {
                _this.search_results_mouseover(evt);
            });
            this.search_results.bind("mouseout.chosen", function(evt) {
                _this.search_results_mouseout(evt);
            });
            this.search_results.bind("mousewheel.chosen DOMMouseScroll.chosen", function(evt) {
                _this.search_results_mousewheel(evt);
            });
            this.search_results.bind("touchstart.chosen", function(evt) {
                _this.search_results_touchstart(evt);
            });
            this.search_results.bind("touchmove.chosen", function(evt) {
                _this.search_results_touchmove(evt);
            });
            this.search_results.bind("touchend.chosen", function(evt) {
                _this.search_results_touchend(evt);
            });
            this.form_field_jq.bind("chosen:updated.chosen", function(evt) {
                _this.results_update_field(evt);
            });
            this.form_field_jq.bind("chosen:activate.chosen", function(evt) {
                _this.activate_field(evt);
            });
            this.form_field_jq.bind("chosen:open.chosen", function(evt) {
                _this.container_mousedown(evt);
            });
            this.form_field_jq.bind("chosen:close.chosen", function(evt) {
                _this.input_blur(evt);
            });
            this.search_field.bind("blur.chosen", function(evt) {
                _this.input_blur(evt);
            });
            this.search_field.bind("keyup.chosen", function(evt) {
                _this.keyup_checker(evt);
            });
            this.search_field.bind("keydown.chosen", function(evt) {
                _this.keydown_checker(evt);
            });
            this.search_field.bind("focus.chosen", function(evt) {
                _this.input_focus(evt);
            });
            this.search_field.bind("cut.chosen", function(evt) {
                _this.clipboard_event_checker(evt);
            });
            this.search_field.bind("paste.chosen", function(evt) {
                _this.clipboard_event_checker(evt);
            });
            if (this.is_multiple) {
                return this.search_choices.bind("click.chosen", function(evt) {
                    _this.choices_click(evt);
                });
            } else {
                return this.container.bind("click.chosen", function(evt) {
                    evt.preventDefault();
                });
            }
        };
        Chosen.prototype.destroy = function() {
            $(this.container[0].ownerDocument).unbind("click.chosen", this.click_test_action);
            if (this.search_field[0].tabIndex) {
                this.form_field_jq[0].tabIndex = this.search_field[0].tabIndex;
            }
            this.container.remove();
            this.form_field_jq.removeData("chosen");
            return this.form_field_jq.show();
        };
        Chosen.prototype.search_field_disabled = function() {
            this.is_disabled = this.form_field_jq[0].disabled;
            if (this.is_disabled) {
                this.container.addClass("chosen-disabled");
                this.search_field[0].disabled = true;
                if (!this.is_multiple) {
                    this.selected_item.unbind("focus.chosen", this.activate_action);
                }
                return this.close_field();
            } else {
                this.container.removeClass("chosen-disabled");
                this.search_field[0].disabled = false;
                if (!this.is_multiple) {
                    return this.selected_item.bind("focus.chosen", this.activate_action);
                }
            }
        };
        Chosen.prototype.container_mousedown = function(evt) {
            if (!this.is_disabled) {
                if (evt && evt.type === "mousedown" && !this.results_showing) {
                    evt.preventDefault();
                }
                if (!(evt != null && $(evt.target).hasClass("search-choice-close"))) {
                    if (!this.active_field) {
                        if (this.is_multiple) {
                            this.search_field.val("");
                        }
                        $(this.container[0].ownerDocument).bind("click.chosen", this.click_test_action);
                        this.results_show();
                    } else if (!this.is_multiple && evt && ($(evt.target)[0] === this.selected_item[0] || $(evt.target).parents("a.chosen-single").length)) {
                        evt.preventDefault();
                        this.results_toggle();
                    }
                    return this.activate_field();
                }
            }
        };
        Chosen.prototype.container_mouseup = function(evt) {
            if (evt.target.nodeName === "ABBR" && !this.is_disabled) {
                return this.results_reset(evt);
            }
        };
        Chosen.prototype.search_results_mousewheel = function(evt) {
            var delta;
            if (evt.originalEvent) {
                delta = evt.originalEvent.deltaY || -evt.originalEvent.wheelDelta || evt.originalEvent.detail;
            }
            if (delta != null) {
                evt.preventDefault();
                if (evt.type === "DOMMouseScroll") {
                    delta = delta * 40;
                }
                return this.search_results.scrollTop(delta + this.search_results.scrollTop());
            }
        };
        Chosen.prototype.blur_test = function(evt) {
            if (!this.active_field && this.container.hasClass("chosen-container-active")) {
                return this.close_field();
            }
        };
        Chosen.prototype.close_field = function() {
            $(this.container[0].ownerDocument).unbind("click.chosen", this.click_test_action);
            this.active_field = false;
            this.results_hide();
            this.container.removeClass("chosen-container-active");
            this.clear_backstroke();
            this.show_search_field_default();
            return this.search_field_scale();
        };
        Chosen.prototype.activate_field = function() {
            this.container.addClass("chosen-container-active");
            this.active_field = true;
            this.search_field.val(this.search_field.val());
            return this.search_field.focus();
        };
        Chosen.prototype.test_active_click = function(evt) {
            var active_container;
            active_container = $(evt.target).closest(".chosen-container");
            if (active_container.length && this.container[0] === active_container[0]) {
                return this.active_field = true;
            } else {
                return this.close_field();
            }
        };
        Chosen.prototype.results_build = function() {
            this.parsing = true;
            this.selected_option_count = null;
            this.results_data = SelectParser.select_to_array(this.form_field);
            if (this.is_multiple) {
                this.search_choices.find("li.search-choice").remove();
            } else if (!this.is_multiple) {
                this.single_set_selected_text();
                if (this.disable_search || this.form_field.options.length <= this.disable_search_threshold) {
                    this.search_field[0].readOnly = true;
                    this.container.addClass("chosen-container-single-nosearch");
                } else {
                    this.search_field[0].readOnly = false;
                    this.container.removeClass("chosen-container-single-nosearch");
                }
            }
            this.update_results_content(this.results_option_build({
                first: true
            }));
            this.search_field_disabled();
            this.show_search_field_default();
            this.search_field_scale();
            return this.parsing = false;
        };
        Chosen.prototype.result_do_highlight = function(el) {
            var high_bottom, high_top, maxHeight, visible_bottom, visible_top;
            if (el.length) {
                this.result_clear_highlight();
                this.result_highlight = el;
                this.result_highlight.addClass("highlighted");
                maxHeight = parseInt(this.search_results.css("maxHeight"), 10);
                visible_top = this.search_results.scrollTop();
                visible_bottom = maxHeight + visible_top;
                high_top = this.result_highlight.position().top + this.search_results.scrollTop();
                high_bottom = high_top + this.result_highlight.outerHeight();
                if (high_bottom >= visible_bottom) {
                    return this.search_results.scrollTop(high_bottom - maxHeight > 0 ? high_bottom - maxHeight : 0);
                } else if (high_top < visible_top) {
                    return this.search_results.scrollTop(high_top);
                }
            }
        };
        Chosen.prototype.result_clear_highlight = function() {
            if (this.result_highlight) {
                this.result_highlight.removeClass("highlighted");
            }
            return this.result_highlight = null;
        };
        Chosen.prototype.results_show = function() {
            if (this.is_multiple && this.max_selected_options <= this.choices_count()) {
                this.form_field_jq.trigger("chosen:maxselected", {
                    chosen: this
                });
                return false;
            }
            this.container.addClass("chosen-with-drop");
            this.results_showing = true;
            this.search_field.focus();
            this.search_field.val(this.search_field.val());
            this.winnow_results();
            return this.form_field_jq.trigger("chosen:showing_dropdown", {
                chosen: this
            });
        };
        Chosen.prototype.update_results_content = function(content) {
            return this.search_results.html(content);
        };
        Chosen.prototype.results_hide = function() {
            if (this.results_showing) {
                this.result_clear_highlight();
                this.container.removeClass("chosen-with-drop");
                this.form_field_jq.trigger("chosen:hiding_dropdown", {
                    chosen: this
                });
            }
            return this.results_showing = false;
        };
        Chosen.prototype.set_tab_index = function(el) {
            var ti;
            if (this.form_field.tabIndex) {
                ti = this.form_field.tabIndex;
                this.form_field.tabIndex = -1;
                return this.search_field[0].tabIndex = ti;
            }
        };
        Chosen.prototype.set_label_behavior = function() {
            var _this = this;
            this.form_field_label = this.form_field_jq.parents("label");
            if (!this.form_field_label.length && this.form_field.id.length) {
                this.form_field_label = $("label[for='" + this.form_field.id + "']");
            }
            if (this.form_field_label.length > 0) {
                return this.form_field_label.bind("click.chosen", function(evt) {
                    if (_this.is_multiple) {
                        return _this.container_mousedown(evt);
                    } else {
                        return _this.activate_field();
                    }
                });
            }
        };
        Chosen.prototype.show_search_field_default = function() {
            if (this.is_multiple && this.choices_count() < 1 && !this.active_field) {
                this.search_field.val(this.default_text);
                return this.search_field.addClass("default");
            } else {
                this.search_field.val("");
                return this.search_field.removeClass("default");
            }
        };
        Chosen.prototype.search_results_mouseup = function(evt) {
            var target;
            target = $(evt.target).hasClass("active-result") ? $(evt.target) : $(evt.target).parents(".active-result").first();
            if (target.length) {
                this.result_highlight = target;
                this.result_select(evt);
                return this.search_field.focus();
            }
        };
        Chosen.prototype.search_results_mouseover = function(evt) {
            var target;
            target = $(evt.target).hasClass("active-result") ? $(evt.target) : $(evt.target).parents(".active-result").first();
            if (target) {
                return this.result_do_highlight(target);
            }
        };
        Chosen.prototype.search_results_mouseout = function(evt) {
            if ($(evt.target).hasClass("active-result" || $(evt.target).parents(".active-result").first())) {
                return this.result_clear_highlight();
            }
        };
        Chosen.prototype.choice_build = function(item) {
            var choice, close_link, _this = this;
            choice = $("<li />", {
                "class": "search-choice"
            }).html("<span>" + this.choice_label(item) + "</span>");
            if (item.disabled) {
                choice.addClass("search-choice-disabled");
            } else {
                close_link = $("<a />", {
                    "class": "search-choice-close",
                    "data-option-array-index": item.array_index
                });
                close_link.bind("click.chosen", function(evt) {
                    return _this.choice_destroy_link_click(evt);
                });
                choice.append(close_link);
            }
            return this.search_container.before(choice);
        };
        Chosen.prototype.choice_destroy_link_click = function(evt) {
            evt.preventDefault();
            evt.stopPropagation();
            if (!this.is_disabled) {
                return this.choice_destroy($(evt.target));
            }
        };
        Chosen.prototype.choice_destroy = function(link) {
            if (this.result_deselect(link[0].getAttribute("data-option-array-index"))) {
                this.show_search_field_default();
                if (this.is_multiple && this.choices_count() > 0 && this.search_field.val().length < 1) {
                    this.results_hide();
                }
                link.parents("li").first().remove();
                return this.search_field_scale();
            }
        };
        Chosen.prototype.results_reset = function() {
            this.reset_single_select_options();
            this.form_field.options[0].selected = true;
            this.single_set_selected_text();
            this.show_search_field_default();
            this.results_reset_cleanup();
            this.form_field_jq.trigger("change");
            if (this.active_field) {
                return this.results_hide();
            }
        };
        Chosen.prototype.results_reset_cleanup = function() {
            this.current_selectedIndex = this.form_field.selectedIndex;
            return this.selected_item.find("abbr").remove();
        };
        Chosen.prototype.result_select = function(evt) {
            var high, item;
            if (this.result_highlight) {
                high = this.result_highlight;
                this.result_clear_highlight();
                if (this.is_multiple && this.max_selected_options <= this.choices_count()) {
                    this.form_field_jq.trigger("chosen:maxselected", {
                        chosen: this
                    });
                    return false;
                }
                if (this.is_multiple) {
                    high.removeClass("active-result");
                } else {
                    this.reset_single_select_options();
                }
                high.addClass("result-selected");
                item = this.results_data[high[0].getAttribute("data-option-array-index")];
                item.selected = true;
                this.form_field.options[item.options_index].selected = true;
                this.selected_option_count = null;
                if (this.is_multiple) {
                    this.choice_build(item);
                } else {
                    this.single_set_selected_text(this.choice_label(item));
                }
                if (!((evt.metaKey || evt.ctrlKey) && this.is_multiple)) {
                    this.results_hide();
                }
                this.search_field.val("");
                if (this.is_multiple || this.form_field.selectedIndex !== this.current_selectedIndex) {
                    this.form_field_jq.trigger("change", {
                        selected: this.form_field.options[item.options_index].value
                    });
                }
                this.current_selectedIndex = this.form_field.selectedIndex;
                evt.preventDefault();
                return this.search_field_scale();
            }
        };
        Chosen.prototype.single_set_selected_text = function(text) {
            if (text == null) {
                text = this.default_text;
            }
            if (text === this.default_text) {
                this.selected_item.addClass("chosen-default");
            } else {
                this.single_deselect_control_build();
                this.selected_item.removeClass("chosen-default");
            }
            return this.selected_item.find("span").html(text);
        };
        Chosen.prototype.result_deselect = function(pos) {
            var result_data;
            result_data = this.results_data[pos];
            if (!this.form_field.options[result_data.options_index].disabled) {
                result_data.selected = false;
                this.form_field.options[result_data.options_index].selected = false;
                this.selected_option_count = null;
                this.result_clear_highlight();
                if (this.results_showing) {
                    this.winnow_results();
                }
                this.form_field_jq.trigger("change", {
                    deselected: this.form_field.options[result_data.options_index].value
                });
                this.search_field_scale();
                return true;
            } else {
                return false;
            }
        };
        Chosen.prototype.single_deselect_control_build = function() {
            if (!this.allow_single_deselect) {
                return;
            }
            if (!this.selected_item.find("abbr").length) {
                this.selected_item.find("span").first().after('<abbr class="search-choice-close"></abbr>');
            }
            return this.selected_item.addClass("chosen-single-with-deselect");
        };
        Chosen.prototype.get_search_text = function() {
            return $("<div/>").text($.trim(this.search_field.val())).html();
        };
        Chosen.prototype.winnow_results_set_highlight = function() {
            var do_high, selected_results;
            selected_results = !this.is_multiple ? this.search_results.find(".result-selected.active-result") : [];
            do_high = selected_results.length ? selected_results.first() : this.search_results.find(".active-result").first();
            if (do_high != null) {
                return this.result_do_highlight(do_high);
            }
        };
        Chosen.prototype.no_results = function(terms) {
            var no_results_html;
            no_results_html = $('<li class="no-results">' + this.results_none_found + ' "<span></span>"</li>');
            no_results_html.find("span").first().html(terms);
            this.search_results.append(no_results_html);
            return this.form_field_jq.trigger("chosen:no_results", {
                chosen: this
            });
        };
        Chosen.prototype.no_results_clear = function() {
            return this.search_results.find(".no-results").remove();
        };
        Chosen.prototype.keydown_arrow = function() {
            var next_sib;
            if (this.results_showing && this.result_highlight) {
                next_sib = this.result_highlight.nextAll("li.active-result").first();
                if (next_sib) {
                    return this.result_do_highlight(next_sib);
                }
            } else {
                return this.results_show();
            }
        };
        Chosen.prototype.keyup_arrow = function() {
            var prev_sibs;
            if (!this.results_showing && !this.is_multiple) {
                return this.results_show();
            } else if (this.result_highlight) {
                prev_sibs = this.result_highlight.prevAll("li.active-result");
                if (prev_sibs.length) {
                    return this.result_do_highlight(prev_sibs.first());
                } else {
                    if (this.choices_count() > 0) {
                        this.results_hide();
                    }
                    return this.result_clear_highlight();
                }
            }
        };
        Chosen.prototype.keydown_backstroke = function() {
            var next_available_destroy;
            if (this.pending_backstroke) {
                this.choice_destroy(this.pending_backstroke.find("a").first());
                return this.clear_backstroke();
            } else {
                next_available_destroy = this.search_container.siblings("li.search-choice").last();
                if (next_available_destroy.length && !next_available_destroy.hasClass("search-choice-disabled")) {
                    this.pending_backstroke = next_available_destroy;
                    if (this.single_backstroke_delete) {
                        return this.keydown_backstroke();
                    } else {
                        return this.pending_backstroke.addClass("search-choice-focus");
                    }
                }
            }
        };
        Chosen.prototype.clear_backstroke = function() {
            if (this.pending_backstroke) {
                this.pending_backstroke.removeClass("search-choice-focus");
            }
            return this.pending_backstroke = null;
        };
        Chosen.prototype.keydown_checker = function(evt) {
            var stroke, _ref1;
            stroke = (_ref1 = evt.which) != null ? _ref1 : evt.keyCode;
            this.search_field_scale();
            if (stroke !== 8 && this.pending_backstroke) {
                this.clear_backstroke();
            }
            switch (stroke) {
              case 8:
                this.backstroke_length = this.search_field.val().length;
                break;

              case 9:
                if (this.results_showing && !this.is_multiple) {
                    this.result_select(evt);
                }
                this.mouse_on_container = false;
                break;

              case 13:
                if (this.results_showing) {
                    evt.preventDefault();
                }
                break;

              case 32:
                if (this.disable_search) {
                    evt.preventDefault();
                }
                break;

              case 38:
                evt.preventDefault();
                this.keyup_arrow();
                break;

              case 40:
                evt.preventDefault();
                this.keydown_arrow();
                break;
            }
        };
        Chosen.prototype.search_field_scale = function() {
            var div, f_width, h, style, style_block, styles, w, _i, _len;
            if (this.is_multiple) {
                h = 0;
                w = 0;
                style_block = "position:absolute; left: -1000px; top: -1000px; display:none;";
                styles = [ "font-size", "font-style", "font-weight", "font-family", "line-height", "text-transform", "letter-spacing" ];
                for (_i = 0, _len = styles.length; _i < _len; _i++) {
                    style = styles[_i];
                    style_block += style + ":" + this.search_field.css(style) + ";";
                }
                div = $("<div />", {
                    style: style_block
                });
                div.text(this.search_field.val());
                $("body").append(div);
                w = div.width() + 25;
                div.remove();
                f_width = this.container.outerWidth();
                if (w > f_width - 10) {
                    w = f_width - 10;
                }
                return this.search_field.css({
                    width: w + "px"
                });
            }
        };
        return Chosen;
    }(AbstractChosen);
}).call(this);

(function($) {
    $.fn.appear = function(fn, options) {
        var settings = $.extend({
            data: undefined,
            one: true,
            accX: 0,
            accY: 0
        }, options);
        return this.each(function() {
            var t = $(this);
            t.appeared = false;
            if (!fn) {
                t.trigger("appear", settings.data);
                return;
            }
            var w = $(window);
            var check = function() {
                if (!t.is(":visible")) {
                    t.appeared = false;
                    return;
                }
                var a = w.scrollLeft();
                var b = w.scrollTop();
                var o = t.offset();
                var x = o.left;
                var y = o.top;
                var ax = settings.accX;
                var ay = settings.accY;
                var th = t.height();
                var wh = w.height();
                var tw = t.width();
                var ww = w.width();
                if (y + th + ay >= b && y <= b + wh + ay && x + tw + ax >= a && x <= a + ww + ax) {
                    if (!t.appeared) t.trigger("appear", settings.data);
                } else {
                    t.appeared = false;
                }
            };
            var modifiedFn = function() {
                t.appeared = true;
                if (settings.one) {
                    w.unbind("scroll", check);
                    w.unbind("resize", check);
                    var i = $.inArray(check, $.fn.appear.checks);
                    if (i >= 0) $.fn.appear.checks.splice(i, 1);
                }
                fn.apply(this, arguments);
            };
            if (settings.one) t.one("appear", settings.data, modifiedFn); else t.bind("appear", settings.data, modifiedFn);
            w.scroll(check);
            w.resize(check);
            $.fn.appear.checks.push(check);
            check();
        });
    };
    $.extend($.fn.appear, {
        checks: [],
        timeout: null,
        checkAll: function() {
            var length = $.fn.appear.checks.length;
            if (length > 0) while (length--) $.fn.appear.checks[length]();
        },
        run: function() {
            if ($.fn.appear.timeout) clearTimeout($.fn.appear.timeout);
            $.fn.appear.timeout = setTimeout($.fn.appear.checkAll, 20);
        }
    });
    $.each([ "append", "prepend", "after", "before", "attr", "removeAttr", "addClass", "removeClass", "toggleClass", "remove", "css", "show", "hide" ], function(i, n) {
        var old = $.fn[n];
        if (old) {
            $.fn[n] = function() {
                var r = old.apply(this, arguments);
                $.fn.appear.run();
                return r;
            };
        }
    });
})(jQuery);

(function($) {
    "use strict";
    var _currentSpinnerId = 0;
    function _scopedEventName(name, id) {
        return name + ".touchspin_" + id;
    }
    function _scopeEventNames(names, id) {
        return $.map(names, function(name) {
            return _scopedEventName(name, id);
        });
    }
    $.fn.TouchSpin = function(options) {
        if (options === "destroy") {
            this.each(function() {
                var originalinput = $(this), originalinput_data = originalinput.data();
                $(document).off(_scopeEventNames([ "mouseup", "touchend", "touchcancel", "mousemove", "touchmove", "scroll", "scrollstart" ], originalinput_data.spinnerid).join(" "));
            });
            return;
        }
        var defaults = {
            min: 0,
            max: 100,
            initval: "",
            step: 1,
            decimals: 0,
            stepinterval: 100,
            forcestepdivisibility: "round",
            stepintervaldelay: 500,
            verticalbuttons: false,
            verticalupclass: "glyphicon glyphicon-chevron-up",
            verticaldownclass: "glyphicon glyphicon-chevron-down",
            prefix: "",
            postfix: "",
            prefix_extraclass: "",
            postfix_extraclass: "",
            booster: true,
            boostat: 10,
            maxboostedstep: false,
            mousewheel: true,
            buttondown_class: "btn btn-default",
            buttonup_class: "btn btn-default",
            buttondown_txt: "-",
            buttonup_txt: "+"
        };
        var attributeMap = {
            min: "min",
            max: "max",
            initval: "init-val",
            step: "step",
            decimals: "decimals",
            stepinterval: "step-interval",
            verticalbuttons: "vertical-buttons",
            verticalupclass: "vertical-up-class",
            verticaldownclass: "vertical-down-class",
            forcestepdivisibility: "force-step-divisibility",
            stepintervaldelay: "step-interval-delay",
            prefix: "prefix",
            postfix: "postfix",
            prefix_extraclass: "prefix-extra-class",
            postfix_extraclass: "postfix-extra-class",
            booster: "booster",
            boostat: "boostat",
            maxboostedstep: "max-boosted-step",
            mousewheel: "mouse-wheel",
            buttondown_class: "button-down-class",
            buttonup_class: "button-up-class",
            buttondown_txt: "button-down-txt",
            buttonup_txt: "button-up-txt"
        };
        return this.each(function() {
            var settings, originalinput = $(this), originalinput_data = originalinput.data(), container, elements, value, downSpinTimer, upSpinTimer, downDelayTimeout, upDelayTimeout, spincount = 0, spinning = false;
            init();
            function init() {
                if (originalinput.data("alreadyinitialized")) {
                    return;
                }
                originalinput.data("alreadyinitialized", true);
                _currentSpinnerId += 1;
                originalinput.data("spinnerid", _currentSpinnerId);
                if (!originalinput.is("input")) {
                    console.log("Must be an input.");
                    return;
                }
                _initSettings();
                _setInitval();
                _checkValue();
                _buildHtml();
                _initElements();
                _hideEmptyPrefixPostfix();
                _bindEvents();
                _bindEventsInterface();
                elements.input.css("display", "block");
            }
            function _setInitval() {
                if (settings.initval !== "" && originalinput.val() === "") {
                    originalinput.val(settings.initval);
                }
            }
            function changeSettings(newsettings) {
                _updateSettings(newsettings);
                _checkValue();
                var value = elements.input.val();
                if (value !== "") {
                    value = Number(elements.input.val());
                    elements.input.val(value.toFixed(settings.decimals));
                }
            }
            function _initSettings() {
                settings = $.extend({}, defaults, originalinput_data, _parseAttributes(), options);
            }
            function _parseAttributes() {
                var data = {};
                $.each(attributeMap, function(key, value) {
                    var attrName = "bts-" + value + "";
                    if (originalinput.is("[data-" + attrName + "]")) {
                        data[key] = originalinput.data(attrName);
                    }
                });
                return data;
            }
            function _updateSettings(newsettings) {
                settings = $.extend({}, settings, newsettings);
            }
            function _buildHtml() {
                var initval = originalinput.val(), parentelement = originalinput.parent();
                if (initval !== "") {
                    initval = Number(initval).toFixed(settings.decimals);
                }
                originalinput.data("initvalue", initval).val(initval);
                originalinput.addClass("form-control");
                if (parentelement.hasClass("input-group")) {
                    _advanceInputGroup(parentelement);
                } else {
                    _buildInputGroup();
                }
            }
            function _advanceInputGroup(parentelement) {
                parentelement.addClass("bootstrap-touchspin");
                var prev = originalinput.prev(), next = originalinput.next();
                var downhtml, uphtml, prefixhtml = '<span class="input-group-addon bootstrap-touchspin-prefix">' + settings.prefix + "</span>", postfixhtml = '<span class="input-group-addon bootstrap-touchspin-postfix">' + settings.postfix + "</span>";
                if (prev.hasClass("input-group-btn")) {
                    downhtml = '<button class="' + settings.buttondown_class + ' bootstrap-touchspin-down" type="button">' + settings.buttondown_txt + "</button>";
                    prev.append(downhtml);
                } else {
                    downhtml = '<span class="input-group-btn"><button class="' + settings.buttondown_class + ' bootstrap-touchspin-down" type="button">' + settings.buttondown_txt + "</button></span>";
                    $(downhtml).insertBefore(originalinput);
                }
                if (next.hasClass("input-group-btn")) {
                    uphtml = '<button class="' + settings.buttonup_class + ' bootstrap-touchspin-up" type="button">' + settings.buttonup_txt + "</button>";
                    next.prepend(uphtml);
                } else {
                    uphtml = '<span class="input-group-btn"><button class="' + settings.buttonup_class + ' bootstrap-touchspin-up" type="button">' + settings.buttonup_txt + "</button></span>";
                    $(uphtml).insertAfter(originalinput);
                }
                $(prefixhtml).insertBefore(originalinput);
                $(postfixhtml).insertAfter(originalinput);
                container = parentelement;
            }
            function _buildInputGroup() {
                var html;
                if (settings.verticalbuttons) {
                    html = '<div class="input-group bootstrap-touchspin"><span class="input-group-addon bootstrap-touchspin-prefix">' + settings.prefix + '</span><span class="input-group-addon bootstrap-touchspin-postfix">' + settings.postfix + '</span><span class="input-group-btn-vertical"><button class="' + settings.buttondown_class + ' bootstrap-touchspin-up" type="button"><i class="' + settings.verticalupclass + '"></i></button><button class="' + settings.buttonup_class + ' bootstrap-touchspin-down" type="button"><i class="' + settings.verticaldownclass + '"></i></button></span></div>';
                } else {
                    html = '<div class="input-group bootstrap-touchspin"><span class="input-group-btn"><button class="' + settings.buttondown_class + ' bootstrap-touchspin-down" type="button">' + settings.buttondown_txt + '</button></span><span class="input-group-addon bootstrap-touchspin-prefix">' + settings.prefix + '</span><span class="input-group-addon bootstrap-touchspin-postfix">' + settings.postfix + '</span><span class="input-group-btn"><button class="' + settings.buttonup_class + ' bootstrap-touchspin-up" type="button">' + settings.buttonup_txt + "</button></span></div>";
                }
                container = $(html).insertBefore(originalinput);
                $(".bootstrap-touchspin-prefix", container).after(originalinput);
                if (originalinput.hasClass("input-sm")) {
                    container.addClass("input-group-sm");
                } else if (originalinput.hasClass("input-lg")) {
                    container.addClass("input-group-lg");
                }
            }
            function _initElements() {
                elements = {
                    down: $(".bootstrap-touchspin-down", container),
                    up: $(".bootstrap-touchspin-up", container),
                    input: $("input", container),
                    prefix: $(".bootstrap-touchspin-prefix", container).addClass(settings.prefix_extraclass),
                    postfix: $(".bootstrap-touchspin-postfix", container).addClass(settings.postfix_extraclass)
                };
            }
            function _hideEmptyPrefixPostfix() {
                if (settings.prefix === "") {
                    elements.prefix.hide();
                }
                if (settings.postfix === "") {
                    elements.postfix.hide();
                }
            }
            function _bindEvents() {
                originalinput.on("keydown", function(ev) {
                    var code = ev.keyCode || ev.which;
                    if (code === 38) {
                        if (spinning !== "up") {
                            upOnce();
                            startUpSpin();
                        }
                        ev.preventDefault();
                    } else if (code === 40) {
                        if (spinning !== "down") {
                            downOnce();
                            startDownSpin();
                        }
                        ev.preventDefault();
                    }
                });
                originalinput.on("keyup", function(ev) {
                    var code = ev.keyCode || ev.which;
                    if (code === 38) {
                        stopSpin();
                    } else if (code === 40) {
                        stopSpin();
                    }
                });
                originalinput.on("blur", function() {
                    _checkValue();
                });
                elements.down.on("keydown", function(ev) {
                    var code = ev.keyCode || ev.which;
                    if (code === 32 || code === 13) {
                        if (spinning !== "down") {
                            downOnce();
                            startDownSpin();
                        }
                        ev.preventDefault();
                    }
                });
                elements.down.on("keyup", function(ev) {
                    var code = ev.keyCode || ev.which;
                    if (code === 32 || code === 13) {
                        stopSpin();
                    }
                });
                elements.up.on("keydown", function(ev) {
                    var code = ev.keyCode || ev.which;
                    if (code === 32 || code === 13) {
                        if (spinning !== "up") {
                            upOnce();
                            startUpSpin();
                        }
                        ev.preventDefault();
                    }
                });
                elements.up.on("keyup", function(ev) {
                    var code = ev.keyCode || ev.which;
                    if (code === 32 || code === 13) {
                        stopSpin();
                    }
                });
                elements.down.on("mousedown.touchspin", function(ev) {
                    elements.down.off("touchstart.touchspin");
                    if (originalinput.is(":disabled")) {
                        return;
                    }
                    downOnce();
                    startDownSpin();
                    ev.preventDefault();
                    ev.stopPropagation();
                });
                elements.down.on("touchstart.touchspin", function(ev) {
                    elements.down.off("mousedown.touchspin");
                    if (originalinput.is(":disabled")) {
                        return;
                    }
                    downOnce();
                    startDownSpin();
                    ev.preventDefault();
                    ev.stopPropagation();
                });
                elements.up.on("mousedown.touchspin", function(ev) {
                    elements.up.off("touchstart.touchspin");
                    if (originalinput.is(":disabled")) {
                        return;
                    }
                    upOnce();
                    startUpSpin();
                    ev.preventDefault();
                    ev.stopPropagation();
                });
                elements.up.on("touchstart.touchspin", function(ev) {
                    elements.up.off("mousedown.touchspin");
                    if (originalinput.is(":disabled")) {
                        return;
                    }
                    upOnce();
                    startUpSpin();
                    ev.preventDefault();
                    ev.stopPropagation();
                });
                elements.up.on("mouseout touchleave touchend touchcancel", function(ev) {
                    if (!spinning) {
                        return;
                    }
                    ev.stopPropagation();
                    stopSpin();
                });
                elements.down.on("mouseout touchleave touchend touchcancel", function(ev) {
                    if (!spinning) {
                        return;
                    }
                    ev.stopPropagation();
                    stopSpin();
                });
                elements.down.on("mousemove touchmove", function(ev) {
                    if (!spinning) {
                        return;
                    }
                    ev.stopPropagation();
                    ev.preventDefault();
                });
                elements.up.on("mousemove touchmove", function(ev) {
                    if (!spinning) {
                        return;
                    }
                    ev.stopPropagation();
                    ev.preventDefault();
                });
                $(document).on(_scopeEventNames([ "mouseup", "touchend", "touchcancel" ], _currentSpinnerId).join(" "), function(ev) {
                    if (!spinning) {
                        return;
                    }
                    ev.preventDefault();
                    stopSpin();
                });
                $(document).on(_scopeEventNames([ "mousemove", "touchmove", "scroll", "scrollstart" ], _currentSpinnerId).join(" "), function(ev) {
                    if (!spinning) {
                        return;
                    }
                    ev.preventDefault();
                    stopSpin();
                });
                originalinput.on("mousewheel DOMMouseScroll", function(ev) {
                    if (!settings.mousewheel || !originalinput.is(":focus")) {
                        return;
                    }
                    var delta = ev.originalEvent.wheelDelta || -ev.originalEvent.deltaY || -ev.originalEvent.detail;
                    ev.stopPropagation();
                    ev.preventDefault();
                    if (delta < 0) {
                        downOnce();
                    } else {
                        upOnce();
                    }
                });
            }
            function _bindEventsInterface() {
                originalinput.on("touchspin.uponce", function() {
                    stopSpin();
                    upOnce();
                });
                originalinput.on("touchspin.downonce", function() {
                    stopSpin();
                    downOnce();
                });
                originalinput.on("touchspin.startupspin", function() {
                    startUpSpin();
                });
                originalinput.on("touchspin.startdownspin", function() {
                    startDownSpin();
                });
                originalinput.on("touchspin.stopspin", function() {
                    stopSpin();
                });
                originalinput.on("touchspin.updatesettings", function(e, newsettings) {
                    changeSettings(newsettings);
                });
            }
            function _forcestepdivisibility(value) {
                switch (settings.forcestepdivisibility) {
                  case "round":
                    return (Math.round(value / settings.step) * settings.step).toFixed(settings.decimals);

                  case "floor":
                    return (Math.floor(value / settings.step) * settings.step).toFixed(settings.decimals);

                  case "ceil":
                    return (Math.ceil(value / settings.step) * settings.step).toFixed(settings.decimals);

                  default:
                    return value;
                }
            }
            function _checkValue() {
                var val, parsedval, returnval;
                val = originalinput.val();
                if (val === "") {
                    return;
                }
                if (settings.decimals > 0 && val === ".") {
                    return;
                }
                parsedval = parseFloat(val);
                if (isNaN(parsedval)) {
                    parsedval = 0;
                }
                returnval = parsedval;
                if (parsedval.toString() !== val) {
                    returnval = parsedval;
                }
                if (parsedval < settings.min) {
                    returnval = settings.min;
                }
                if (parsedval > settings.max) {
                    returnval = settings.max;
                }
                returnval = _forcestepdivisibility(returnval);
                if (Number(val).toString() !== returnval.toString()) {
                    originalinput.val(returnval);
                    originalinput.trigger("change");
                }
            }
            function _getBoostedStep() {
                if (!settings.booster) {
                    return settings.step;
                } else {
                    var boosted = Math.pow(2, Math.floor(spincount / settings.boostat)) * settings.step;
                    if (settings.maxboostedstep) {
                        if (boosted > settings.maxboostedstep) {
                            boosted = settings.maxboostedstep;
                            value = Math.round(value / boosted) * boosted;
                        }
                    }
                    return Math.max(settings.step, boosted);
                }
            }
            function upOnce() {
                _checkValue();
                value = parseFloat(elements.input.val());
                if (isNaN(value)) {
                    value = 0;
                }
                var initvalue = value, boostedstep = _getBoostedStep();
                value = value + boostedstep;
                if (value > settings.max) {
                    value = settings.max;
                    originalinput.trigger("touchspin.on.max");
                    stopSpin();
                }
                elements.input.val(Number(value).toFixed(settings.decimals));
                if (initvalue !== value) {
                    originalinput.trigger("change");
                }
            }
            function downOnce() {
                _checkValue();
                value = parseFloat(elements.input.val());
                if (isNaN(value)) {
                    value = 0;
                }
                var initvalue = value, boostedstep = _getBoostedStep();
                value = value - boostedstep;
                if (value < settings.min) {
                    value = settings.min;
                    originalinput.trigger("touchspin.on.min");
                    stopSpin();
                }
                elements.input.val(value.toFixed(settings.decimals));
                if (initvalue !== value) {
                    originalinput.trigger("change");
                }
            }
            function startDownSpin() {
                stopSpin();
                spincount = 0;
                spinning = "down";
                originalinput.trigger("touchspin.on.startspin");
                originalinput.trigger("touchspin.on.startdownspin");
                downDelayTimeout = setTimeout(function() {
                    downSpinTimer = setInterval(function() {
                        spincount++;
                        downOnce();
                    }, settings.stepinterval);
                }, settings.stepintervaldelay);
            }
            function startUpSpin() {
                stopSpin();
                spincount = 0;
                spinning = "up";
                originalinput.trigger("touchspin.on.startspin");
                originalinput.trigger("touchspin.on.startupspin");
                upDelayTimeout = setTimeout(function() {
                    upSpinTimer = setInterval(function() {
                        spincount++;
                        upOnce();
                    }, settings.stepinterval);
                }, settings.stepintervaldelay);
            }
            function stopSpin() {
                clearTimeout(downDelayTimeout);
                clearTimeout(upDelayTimeout);
                clearInterval(downSpinTimer);
                clearInterval(upSpinTimer);
                switch (spinning) {
                  case "up":
                    originalinput.trigger("touchspin.on.stopupspin");
                    originalinput.trigger("touchspin.on.stopspin");
                    break;

                  case "down":
                    originalinput.trigger("touchspin.on.stopdownspin");
                    originalinput.trigger("touchspin.on.stopspin");
                    break;
                }
                spincount = 0;
                spinning = false;
            }
        });
    };
})(jQuery);

(function(factory) {
    "use strict";
    if (typeof define === "function" && define.amd) {
        define([ "jquery" ], factory);
    } else {
        factory(typeof jQuery != "undefined" ? jQuery : window.Zepto);
    }
})(function($) {
    "use strict";
    var feature = {};
    feature.fileapi = $("<input type='file'/>").get(0).files !== undefined;
    feature.formdata = window.FormData !== undefined;
    var hasProp = !!$.fn.prop;
    $.fn.attr2 = function() {
        if (!hasProp) {
            return this.attr.apply(this, arguments);
        }
        var val = this.prop.apply(this, arguments);
        if (val && val.jquery || typeof val === "string") {
            return val;
        }
        return this.attr.apply(this, arguments);
    };
    $.fn.ajaxSubmit = function(options) {
        if (!this.length) {
            log("ajaxSubmit: skipping submit process - no element selected");
            return this;
        }
        var method, action, url, $form = this;
        if (typeof options == "function") {
            options = {
                success: options
            };
        } else if (options === undefined) {
            options = {};
        }
        method = options.type || this.attr2("method");
        action = options.url || this.attr2("action");
        url = typeof action === "string" ? $.trim(action) : "";
        url = url || window.location.href || "";
        if (url) {
            url = (url.match(/^([^#]+)/) || [])[1];
        }
        options = $.extend(true, {
            url: url,
            success: $.ajaxSettings.success,
            type: method || $.ajaxSettings.type,
            iframeSrc: /^https/i.test(window.location.href || "") ? "javascript:false" : "about:blank"
        }, options);
        var veto = {};
        this.trigger("form-pre-serialize", [ this, options, veto ]);
        if (veto.veto) {
            log("ajaxSubmit: submit vetoed via form-pre-serialize trigger");
            return this;
        }
        if (options.beforeSerialize && options.beforeSerialize(this, options) === false) {
            log("ajaxSubmit: submit aborted via beforeSerialize callback");
            return this;
        }
        var traditional = options.traditional;
        if (traditional === undefined) {
            traditional = $.ajaxSettings.traditional;
        }
        var elements = [];
        var qx, a = this.formToArray(options.semantic, elements);
        if (options.data) {
            options.extraData = options.data;
            qx = $.param(options.data, traditional);
        }
        if (options.beforeSubmit && options.beforeSubmit(a, this, options) === false) {
            log("ajaxSubmit: submit aborted via beforeSubmit callback");
            return this;
        }
        this.trigger("form-submit-validate", [ a, this, options, veto ]);
        if (veto.veto) {
            log("ajaxSubmit: submit vetoed via form-submit-validate trigger");
            return this;
        }
        var q = $.param(a, traditional);
        if (qx) {
            q = q ? q + "&" + qx : qx;
        }
        if (options.type.toUpperCase() == "GET") {
            options.url += (options.url.indexOf("?") >= 0 ? "&" : "?") + q;
            options.data = null;
        } else {
            options.data = q;
        }
        var callbacks = [];
        if (options.resetForm) {
            callbacks.push(function() {
                $form.resetForm();
            });
        }
        if (options.clearForm) {
            callbacks.push(function() {
                $form.clearForm(options.includeHidden);
            });
        }
        if (!options.dataType && options.target) {
            var oldSuccess = options.success || function() {};
            callbacks.push(function(data) {
                var fn = options.replaceTarget ? "replaceWith" : "html";
                $(options.target)[fn](data).each(oldSuccess, arguments);
            });
        } else if (options.success) {
            callbacks.push(options.success);
        }
        options.success = function(data, status, xhr) {
            var context = options.context || this;
            for (var i = 0, max = callbacks.length; i < max; i++) {
                callbacks[i].apply(context, [ data, status, xhr || $form, $form ]);
            }
        };
        if (options.error) {
            var oldError = options.error;
            options.error = function(xhr, status, error) {
                var context = options.context || this;
                oldError.apply(context, [ xhr, status, error, $form ]);
            };
        }
        if (options.complete) {
            var oldComplete = options.complete;
            options.complete = function(xhr, status) {
                var context = options.context || this;
                oldComplete.apply(context, [ xhr, status, $form ]);
            };
        }
        var fileInputs = $("input[type=file]:enabled", this).filter(function() {
            return $(this).val() !== "";
        });
        var hasFileInputs = fileInputs.length > 0;
        var mp = "multipart/form-data";
        var multipart = $form.attr("enctype") == mp || $form.attr("encoding") == mp;
        var fileAPI = feature.fileapi && feature.formdata;
        log("fileAPI :" + fileAPI);
        var shouldUseFrame = (hasFileInputs || multipart) && !fileAPI;
        var jqxhr;
        if (options.iframe !== false && (options.iframe || shouldUseFrame)) {
            if (options.closeKeepAlive) {
                $.get(options.closeKeepAlive, function() {
                    jqxhr = fileUploadIframe(a);
                });
            } else {
                jqxhr = fileUploadIframe(a);
            }
        } else if ((hasFileInputs || multipart) && fileAPI) {
            jqxhr = fileUploadXhr(a);
        } else {
            jqxhr = $.ajax(options);
        }
        $form.removeData("jqxhr").data("jqxhr", jqxhr);
        for (var k = 0; k < elements.length; k++) {
            elements[k] = null;
        }
        this.trigger("form-submit-notify", [ this, options ]);
        return this;
        function deepSerialize(extraData) {
            var serialized = $.param(extraData, options.traditional).split("&");
            var len = serialized.length;
            var result = [];
            var i, part;
            for (i = 0; i < len; i++) {
                serialized[i] = serialized[i].replace(/\+/g, " ");
                part = serialized[i].split("=");
                result.push([ decodeURIComponent(part[0]), decodeURIComponent(part[1]) ]);
            }
            return result;
        }
        function fileUploadXhr(a) {
            var formdata = new FormData();
            for (var i = 0; i < a.length; i++) {
                formdata.append(a[i].name, a[i].value);
            }
            if (options.extraData) {
                var serializedData = deepSerialize(options.extraData);
                for (i = 0; i < serializedData.length; i++) {
                    if (serializedData[i]) {
                        formdata.append(serializedData[i][0], serializedData[i][1]);
                    }
                }
            }
            options.data = null;
            var s = $.extend(true, {}, $.ajaxSettings, options, {
                contentType: false,
                processData: false,
                cache: false,
                type: method || "POST"
            });
            if (options.uploadProgress) {
                s.xhr = function() {
                    var xhr = $.ajaxSettings.xhr();
                    if (xhr.upload) {
                        xhr.upload.addEventListener("progress", function(event) {
                            var percent = 0;
                            var position = event.loaded || event.position;
                            var total = event.total;
                            if (event.lengthComputable) {
                                percent = Math.ceil(position / total * 100);
                            }
                            options.uploadProgress(event, position, total, percent);
                        }, false);
                    }
                    return xhr;
                };
            }
            s.data = null;
            var beforeSend = s.beforeSend;
            s.beforeSend = function(xhr, o) {
                if (options.formData) {
                    o.data = options.formData;
                } else {
                    o.data = formdata;
                }
                if (beforeSend) {
                    beforeSend.call(this, xhr, o);
                }
            };
            return $.ajax(s);
        }
        function fileUploadIframe(a) {
            var form = $form[0], el, i, s, g, id, $io, io, xhr, sub, n, timedOut, timeoutHandle;
            var deferred = $.Deferred();
            deferred.abort = function(status) {
                xhr.abort(status);
            };
            if (a) {
                for (i = 0; i < elements.length; i++) {
                    el = $(elements[i]);
                    if (hasProp) {
                        el.prop("disabled", false);
                    } else {
                        el.removeAttr("disabled");
                    }
                }
            }
            s = $.extend(true, {}, $.ajaxSettings, options);
            s.context = s.context || s;
            id = "jqFormIO" + new Date().getTime();
            if (s.iframeTarget) {
                $io = $(s.iframeTarget);
                n = $io.attr2("name");
                if (!n) {
                    $io.attr2("name", id);
                } else {
                    id = n;
                }
            } else {
                $io = $('<iframe name="' + id + '" src="' + s.iframeSrc + '" />');
                $io.css({
                    position: "absolute",
                    top: "-1000px",
                    left: "-1000px"
                });
            }
            io = $io[0];
            xhr = {
                aborted: 0,
                responseText: null,
                responseXML: null,
                status: 0,
                statusText: "n/a",
                getAllResponseHeaders: function() {},
                getResponseHeader: function() {},
                setRequestHeader: function() {},
                abort: function(status) {
                    var e = status === "timeout" ? "timeout" : "aborted";
                    log("aborting upload... " + e);
                    this.aborted = 1;
                    try {
                        if (io.contentWindow.document.execCommand) {
                            io.contentWindow.document.execCommand("Stop");
                        }
                    } catch (ignore) {}
                    $io.attr("src", s.iframeSrc);
                    xhr.error = e;
                    if (s.error) {
                        s.error.call(s.context, xhr, e, status);
                    }
                    if (g) {
                        $.event.trigger("ajaxError", [ xhr, s, e ]);
                    }
                    if (s.complete) {
                        s.complete.call(s.context, xhr, e);
                    }
                }
            };
            g = s.global;
            if (g && 0 === $.active++) {
                $.event.trigger("ajaxStart");
            }
            if (g) {
                $.event.trigger("ajaxSend", [ xhr, s ]);
            }
            if (s.beforeSend && s.beforeSend.call(s.context, xhr, s) === false) {
                if (s.global) {
                    $.active--;
                }
                deferred.reject();
                return deferred;
            }
            if (xhr.aborted) {
                deferred.reject();
                return deferred;
            }
            sub = form.clk;
            if (sub) {
                n = sub.getAttribute("data-name") || sub.name;
                if (n && !sub.disabled) {
                    s.extraData = s.extraData || {};
                    s.extraData[n] = sub.value;
                    if (sub.type == "image") {
                        s.extraData[n + ".x"] = form.clk_x;
                        s.extraData[n + ".y"] = form.clk_y;
                    }
                }
            }
            var CLIENT_TIMEOUT_ABORT = 1;
            var SERVER_ABORT = 2;
            function getDoc(frame) {
                var doc = null;
                try {
                    if (frame.contentWindow) {
                        doc = frame.contentWindow.document;
                    }
                } catch (err) {
                    log("cannot get iframe.contentWindow document: " + err);
                }
                if (doc) {
                    return doc;
                }
                try {
                    doc = frame.contentDocument ? frame.contentDocument : frame.document;
                } catch (err) {
                    log("cannot get iframe.contentDocument: " + err);
                    doc = frame.document;
                }
                return doc;
            }
            var csrf_token = $("meta[name=csrf-token]").attr("content");
            var csrf_param = $("meta[name=csrf-param]").attr("content");
            if (csrf_param && csrf_token) {
                s.extraData = s.extraData || {};
                s.extraData[csrf_param] = csrf_token;
            }
            function doSubmit() {
                var t = $form.attr2("target"), a = $form.attr2("action"), mp = "multipart/form-data", et = $form.attr("enctype") || $form.attr("encoding") || mp;
                form.setAttribute("target", id);
                if (!method || /post/i.test(method)) {
                    form.setAttribute("method", "POST");
                }
                if (a != s.url) {
                    form.setAttribute("action", s.url);
                }
                if (!s.skipEncodingOverride && (!method || /post/i.test(method))) {
                    $form.attr({
                        encoding: "multipart/form-data",
                        enctype: "multipart/form-data"
                    });
                }
                if (s.timeout) {
                    timeoutHandle = setTimeout(function() {
                        timedOut = true;
                        cb(CLIENT_TIMEOUT_ABORT);
                    }, s.timeout);
                }
                function checkState() {
                    try {
                        var state = getDoc(io).readyState;
                        log("state = " + state);
                        if (state && state.toLowerCase() == "uninitialized") {
                            setTimeout(checkState, 50);
                        }
                    } catch (e) {
                        log("Server abort: ", e, " (", e.name, ")");
                        cb(SERVER_ABORT);
                        if (timeoutHandle) {
                            clearTimeout(timeoutHandle);
                        }
                        timeoutHandle = undefined;
                    }
                }
                var extraInputs = [];
                try {
                    if (s.extraData) {
                        for (var n in s.extraData) {
                            if (s.extraData.hasOwnProperty(n)) {
                                if ($.isPlainObject(s.extraData[n]) && s.extraData[n].hasOwnProperty("name") && s.extraData[n].hasOwnProperty("value")) {
                                    extraInputs.push($('<input type="hidden" name="' + s.extraData[n].name + '">').val(s.extraData[n].value).appendTo(form)[0]);
                                } else {
                                    extraInputs.push($('<input type="hidden" name="' + n + '">').val(s.extraData[n]).appendTo(form)[0]);
                                }
                            }
                        }
                    }
                    if (!s.iframeTarget) {
                        $io.appendTo("body");
                    }
                    if (io.attachEvent) {
                        io.attachEvent("onload", cb);
                    } else {
                        io.addEventListener("load", cb, false);
                    }
                    setTimeout(checkState, 15);
                    try {
                        form.submit();
                    } catch (err) {
                        var submitFn = document.createElement("form").submit;
                        submitFn.apply(form);
                    }
                } finally {
                    form.setAttribute("action", a);
                    form.setAttribute("enctype", et);
                    if (t) {
                        form.setAttribute("target", t);
                    } else {
                        $form.removeAttr("target");
                    }
                    $(extraInputs).remove();
                }
            }
            if (s.forceSync) {
                doSubmit();
            } else {
                setTimeout(doSubmit, 10);
            }
            var data, doc, domCheckCount = 50, callbackProcessed;
            function cb(e) {
                if (xhr.aborted || callbackProcessed) {
                    return;
                }
                doc = getDoc(io);
                if (!doc) {
                    log("cannot access response document");
                    e = SERVER_ABORT;
                }
                if (e === CLIENT_TIMEOUT_ABORT && xhr) {
                    xhr.abort("timeout");
                    deferred.reject(xhr, "timeout");
                    return;
                } else if (e == SERVER_ABORT && xhr) {
                    xhr.abort("server abort");
                    deferred.reject(xhr, "error", "server abort");
                    return;
                }
                if (!doc || doc.location.href == s.iframeSrc) {
                    if (!timedOut) {
                        return;
                    }
                }
                if (io.detachEvent) {
                    io.detachEvent("onload", cb);
                } else {
                    io.removeEventListener("load", cb, false);
                }
                var status = "success", errMsg;
                try {
                    if (timedOut) {
                        throw "timeout";
                    }
                    var isXml = s.dataType == "xml" || doc.XMLDocument || $.isXMLDoc(doc);
                    log("isXml=" + isXml);
                    if (!isXml && window.opera && (doc.body === null || !doc.body.innerHTML)) {
                        if (--domCheckCount) {
                            log("requeing onLoad callback, DOM not available");
                            setTimeout(cb, 250);
                            return;
                        }
                    }
                    var docRoot = doc.body ? doc.body : doc.documentElement;
                    xhr.responseText = docRoot ? docRoot.innerHTML : null;
                    xhr.responseXML = doc.XMLDocument ? doc.XMLDocument : doc;
                    if (isXml) {
                        s.dataType = "xml";
                    }
                    xhr.getResponseHeader = function(header) {
                        var headers = {
                            "content-type": s.dataType
                        };
                        return headers[header.toLowerCase()];
                    };
                    if (docRoot) {
                        xhr.status = Number(docRoot.getAttribute("status")) || xhr.status;
                        xhr.statusText = docRoot.getAttribute("statusText") || xhr.statusText;
                    }
                    var dt = (s.dataType || "").toLowerCase();
                    var scr = /(json|script|text)/.test(dt);
                    if (scr || s.textarea) {
                        var ta = doc.getElementsByTagName("textarea")[0];
                        if (ta) {
                            xhr.responseText = ta.value;
                            xhr.status = Number(ta.getAttribute("status")) || xhr.status;
                            xhr.statusText = ta.getAttribute("statusText") || xhr.statusText;
                        } else if (scr) {
                            var pre = doc.getElementsByTagName("pre")[0];
                            var b = doc.getElementsByTagName("body")[0];
                            if (pre) {
                                xhr.responseText = pre.textContent ? pre.textContent : pre.innerText;
                            } else if (b) {
                                xhr.responseText = b.textContent ? b.textContent : b.innerText;
                            }
                        }
                    } else if (dt == "xml" && !xhr.responseXML && xhr.responseText) {
                        xhr.responseXML = toXml(xhr.responseText);
                    }
                    try {
                        data = httpData(xhr, dt, s);
                    } catch (err) {
                        status = "parsererror";
                        xhr.error = errMsg = err || status;
                    }
                } catch (err) {
                    log("error caught: ", err);
                    status = "error";
                    xhr.error = errMsg = err || status;
                }
                if (xhr.aborted) {
                    log("upload aborted");
                    status = null;
                }
                if (xhr.status) {
                    status = xhr.status >= 200 && xhr.status < 300 || xhr.status === 304 ? "success" : "error";
                }
                if (status === "success") {
                    if (s.success) {
                        s.success.call(s.context, data, "success", xhr);
                    }
                    deferred.resolve(xhr.responseText, "success", xhr);
                    if (g) {
                        $.event.trigger("ajaxSuccess", [ xhr, s ]);
                    }
                } else if (status) {
                    if (errMsg === undefined) {
                        errMsg = xhr.statusText;
                    }
                    if (s.error) {
                        s.error.call(s.context, xhr, status, errMsg);
                    }
                    deferred.reject(xhr, "error", errMsg);
                    if (g) {
                        $.event.trigger("ajaxError", [ xhr, s, errMsg ]);
                    }
                }
                if (g) {
                    $.event.trigger("ajaxComplete", [ xhr, s ]);
                }
                if (g && !--$.active) {
                    $.event.trigger("ajaxStop");
                }
                if (s.complete) {
                    s.complete.call(s.context, xhr, status);
                }
                callbackProcessed = true;
                if (s.timeout) {
                    clearTimeout(timeoutHandle);
                }
                setTimeout(function() {
                    if (!s.iframeTarget) {
                        $io.remove();
                    } else {
                        $io.attr("src", s.iframeSrc);
                    }
                    xhr.responseXML = null;
                }, 100);
            }
            var toXml = $.parseXML || function(s, doc) {
                if (window.ActiveXObject) {
                    doc = new ActiveXObject("Microsoft.XMLDOM");
                    doc.async = "false";
                    doc.loadXML(s);
                } else {
                    doc = new DOMParser().parseFromString(s, "text/xml");
                }
                return doc && doc.documentElement && doc.documentElement.nodeName != "parsererror" ? doc : null;
            };
            var parseJSON = $.parseJSON || function(s) {
                return window["eval"]("(" + s + ")");
            };
            var httpData = function(xhr, type, s) {
                var ct = xhr.getResponseHeader("content-type") || "", xml = type === "xml" || !type && ct.indexOf("xml") >= 0, data = xml ? xhr.responseXML : xhr.responseText;
                if (xml && data.documentElement.nodeName === "parsererror") {
                    if ($.error) {
                        $.error("parsererror");
                    }
                }
                if (s && s.dataFilter) {
                    data = s.dataFilter(data, type);
                }
                if (typeof data === "string") {
                    if (type === "json" || !type && ct.indexOf("json") >= 0) {
                        data = parseJSON(data);
                    } else if (type === "script" || !type && ct.indexOf("javascript") >= 0) {
                        $.globalEval(data);
                    }
                }
                return data;
            };
            return deferred;
        }
    };
    $.fn.ajaxForm = function(options) {
        options = options || {};
        options.delegation = options.delegation && $.isFunction($.fn.on);
        if (!options.delegation && this.length === 0) {
            var o = {
                s: this.selector,
                c: this.context
            };
            if (!$.isReady && o.s) {
                log("DOM not ready, queuing ajaxForm");
                $(function() {
                    $(o.s, o.c).ajaxForm(options);
                });
                return this;
            }
            log("terminating; zero elements found by selector" + ($.isReady ? "" : " (DOM not ready)"));
            return this;
        }
        if (options.delegation) {
            $(document).off("submit.form-plugin", this.selector, doAjaxSubmit).off("click.form-plugin", this.selector, captureSubmittingElement).on("submit.form-plugin", this.selector, options, doAjaxSubmit).on("click.form-plugin", this.selector, options, captureSubmittingElement);
            return this;
        }
        return this.ajaxFormUnbind().bind("submit.form-plugin", options, doAjaxSubmit).bind("click.form-plugin", options, captureSubmittingElement);
    };
    function doAjaxSubmit(e) {
        var options = e.data;
        if (!e.isDefaultPrevented()) {
            e.preventDefault();
            $(e.target).ajaxSubmit(options);
        }
    }
    function captureSubmittingElement(e) {
        var target = e.target;
        var $el = $(target);
        if (!$el.is("[type=submit],[type=image]")) {
            var t = $el.closest("[type=submit]");
            if (t.length === 0) {
                return;
            }
            target = t[0];
        }
        var form = this;
        form.clk = target;
        if (target.type == "image") {
            if (e.offsetX !== undefined) {
                form.clk_x = e.offsetX;
                form.clk_y = e.offsetY;
            } else if (typeof $.fn.offset == "function") {
                var offset = $el.offset();
                form.clk_x = e.pageX - offset.left;
                form.clk_y = e.pageY - offset.top;
            } else {
                form.clk_x = e.pageX - target.offsetLeft;
                form.clk_y = e.pageY - target.offsetTop;
            }
        }
        setTimeout(function() {
            form.clk = form.clk_x = form.clk_y = null;
        }, 100);
    }
    $.fn.ajaxFormUnbind = function() {
        return this.unbind("submit.form-plugin click.form-plugin");
    };
    $.fn.formToArray = function(semantic, elements) {
        var a = [];
        if (this.length === 0) {
            return a;
        }
        var form = this[0];
        var formId = this.attr("id");
        var els = semantic ? form.getElementsByTagName("*") : form.elements;
        var els2;
        if (els && !/MSIE [678]/.test(navigator.userAgent)) {
            els = $(els).get();
        }
        if (formId) {
            els2 = $(':input[form="' + formId + '"]').get();
            if (els2.length) {
                els = (els || []).concat(els2);
            }
        }
        if (!els || !els.length) {
            return a;
        }
        var i, j, n, v, el, max, jmax;
        for (i = 0, max = els.length; i < max; i++) {
            el = els[i];
            n = el.getAttribute("data-name") || el.name;
            if (!n || el.disabled) {
                continue;
            }
            if (semantic && form.clk && el.type == "image") {
                if (form.clk == el) {
                    a.push({
                        name: n,
                        value: $(el).val(),
                        type: el.type
                    });
                    a.push({
                        name: n + ".x",
                        value: form.clk_x
                    }, {
                        name: n + ".y",
                        value: form.clk_y
                    });
                }
                continue;
            }
            v = $.fieldValue(el, true);
            if (v && v.constructor == Array) {
                if (elements) {
                    elements.push(el);
                }
                for (j = 0, jmax = v.length; j < jmax; j++) {
                    a.push({
                        name: n,
                        value: v[j]
                    });
                }
            } else if (feature.fileapi && el.type == "file") {
                if (elements) {
                    elements.push(el);
                }
                var files = el.files;
                if (files.length) {
                    for (j = 0; j < files.length; j++) {
                        a.push({
                            name: n,
                            value: files[j],
                            type: el.type
                        });
                    }
                } else {
                    a.push({
                        name: n,
                        value: "",
                        type: el.type
                    });
                }
            } else if (v !== null && typeof v != "undefined") {
                if (elements) {
                    elements.push(el);
                }
                a.push({
                    name: n,
                    value: v,
                    type: el.type,
                    required: el.required
                });
            }
        }
        if (!semantic && form.clk) {
            var $input = $(form.clk), input = $input[0];
            n = input.getAttribute("data-name") || input.name;
            if (n && !input.disabled && input.type == "image") {
                a.push({
                    name: n,
                    value: $input.val()
                });
                a.push({
                    name: n + ".x",
                    value: form.clk_x
                }, {
                    name: n + ".y",
                    value: form.clk_y
                });
            }
        }
        $(form).find('div[contenteditable="true"]').each(function() {
            var n = this.getAttribute("data-name") || this.name;
            if (!n) {
                return;
            }
            a.push({
                name: n,
                value: $(this).html()
            });
        });
        return a;
    };
    $.fn.formSerialize = function(semantic) {
        return $.param(this.formToArray(semantic));
    };
    $.fn.fieldSerialize = function(successful) {
        var a = [];
        this.each(function() {
            var n = this.getAttribute("data-name") || this.name;
            if (!n) {
                return;
            }
            var v = $.fieldValue(this, successful);
            if (v && v.constructor == Array) {
                for (var i = 0, max = v.length; i < max; i++) {
                    a.push({
                        name: n,
                        value: v[i]
                    });
                }
            } else if (v !== null && typeof v != "undefined") {
                a.push({
                    name: this.getAttribute("data-name") || this.name,
                    value: v
                });
            }
        });
        return $.param(a);
    };
    $.fn.fieldValue = function(successful) {
        for (var val = [], i = 0, max = this.length; i < max; i++) {
            var el = this[i];
            var v = $.fieldValue(el, successful);
            if (v === null || typeof v == "undefined" || v.constructor == Array && !v.length) {
                continue;
            }
            if (v.constructor == Array) {
                $.merge(val, v);
            } else {
                val.push(v);
            }
        }
        return val;
    };
    $.fieldValue = function(el, successful) {
        var n = el.getAttribute("data-name") || el.name, t = el.type, tag = el.tagName.toLowerCase();
        if (successful === undefined) {
            successful = true;
        }
        if (successful && (!n || el.disabled || t == "reset" || t == "button" || (t == "checkbox" || t == "radio") && !el.checked || (t == "submit" || t == "image") && el.form && el.form.clk != el || tag == "select" && el.selectedIndex == -1)) {
            return null;
        }
        if (tag == "select") {
            var index = el.selectedIndex;
            if (index < 0) {
                return null;
            }
            var a = [], ops = el.options;
            var one = t == "select-one";
            var max = one ? index + 1 : ops.length;
            for (var i = one ? index : 0; i < max; i++) {
                var op = ops[i];
                if (op.selected) {
                    var v = op.value;
                    if (!v) {
                        v = op.attributes && op.attributes.value && !op.attributes.value.specified ? op.text : op.value;
                    }
                    if (one) {
                        return v;
                    }
                    a.push(v);
                }
            }
            return a;
        }
        return $(el).val();
    };
    $.fn.clearForm = function(includeHidden) {
        return this.each(function() {
            $("input,select,textarea", this).clearFields(includeHidden);
        });
    };
    $.fn.clearFields = $.fn.clearInputs = function(includeHidden) {
        var re = /^(?:color|date|datetime|email|month|number|password|range|search|tel|text|time|url|week)$/i;
        return this.each(function() {
            var t = this.type, tag = this.tagName.toLowerCase();
            if (re.test(t) || tag == "textarea") {
                this.value = "";
            } else if (t == "checkbox" || t == "radio") {
                this.checked = false;
            } else if (tag == "select") {
                this.selectedIndex = -1;
            } else if (t == "file") {
                if (/MSIE/.test(navigator.userAgent)) {
                    $(this).replaceWith($(this).clone(true));
                } else {
                    $(this).val("");
                }
            } else if (includeHidden) {
                if (includeHidden === true && /hidden/.test(t) || typeof includeHidden == "string" && $(this).is(includeHidden)) {
                    this.value = "";
                }
            }
        });
    };
    $.fn.resetForm = function() {
        return this.each(function() {
            if (typeof this.reset == "function" || typeof this.reset == "object" && !this.reset.nodeType) {
                this.reset();
            }
        });
    };
    $.fn.enable = function(b) {
        if (b === undefined) {
            b = true;
        }
        return this.each(function() {
            this.disabled = !b;
        });
    };
    $.fn.selected = function(select) {
        if (select === undefined) {
            select = true;
        }
        return this.each(function() {
            var t = this.type;
            if (t == "checkbox" || t == "radio") {
                this.checked = select;
            } else if (this.tagName.toLowerCase() == "option") {
                var $sel = $(this).parent("select");
                if (select && $sel[0] && $sel[0].type == "select-one") {
                    $sel.find("option").selected(false);
                }
                this.selected = select;
            }
        });
    };
    $.fn.ajaxSubmit.debug = false;
    function log() {
        if (false) {
            var msg = "[jquery.form] " + Array.prototype.join.call(arguments, "");
            if (window.console && window.console.log) {
                window.console.log(msg);
            } else if (window.opera && window.opera.postError) {
                window.opera.postError(msg);
            }
        }
    }
});

(function(factory) {
    if (typeof define === "function" && define.amd) {
        define("pnotify", [ "jquery" ], factory);
    } else {
        factory(jQuery);
    }
})(function($) {
    var default_stack = {
        dir1: "down",
        dir2: "left",
        push: "bottom",
        spacing1: 25,
        spacing2: 25,
        context: $("body")
    };
    var timer, body, jwindow = $(window);
    var do_when_ready = function() {
        body = $("body");
        PNotify.prototype.options.stack.context = body;
        jwindow = $(window);
        jwindow.bind("resize", function() {
            if (timer) clearTimeout(timer);
            timer = setTimeout(function() {
                PNotify.positionAll(true);
            }, 10);
        });
    };
    PNotify = function(options) {
        this.parseOptions(options);
        this.init();
    };
    $.extend(PNotify.prototype, {
        version: "2.0.1",
        options: {
            title: false,
            title_escape: false,
            text: false,
            text_escape: false,
            styling: "bootstrap3",
            addclass: "",
            cornerclass: "",
            auto_display: true,
            width: "300px",
            min_height: "16px",
            type: "notice",
            icon: true,
            opacity: 1,
            animation: "fade",
            animate_speed: "slow",
            position_animate_speed: 500,
            shadow: true,
            hide: true,
            delay: 8e3,
            mouse_reset: true,
            remove: true,
            insert_brs: true,
            destroy: true,
            stack: default_stack
        },
        modules: {},
        runModules: function(event, arg) {
            var curArg;
            for (var module in this.modules) {
                curArg = typeof arg === "object" && module in arg ? arg[module] : arg;
                if (typeof this.modules[module][event] === "function") this.modules[module][event](this, typeof this.options[module] === "object" ? this.options[module] : {}, curArg);
            }
        },
        state: "initializing",
        timer: null,
        styles: null,
        elem: null,
        container: null,
        title_container: null,
        text_container: null,
        animating: false,
        timerHide: false,
        init: function() {
            var that = this;
            this.modules = {};
            $.extend(true, this.modules, PNotify.prototype.modules);
            if (typeof this.options.styling === "object") {
                this.styles = this.options.styling;
            } else {
                this.styles = PNotify.styling[this.options.styling];
            }
            this.elem = $("<div />", {
                "class": "ui-pnotify " + this.options.addclass,
                css: {
                    display: "none"
                },
                mouseenter: function(e) {
                    if (that.options.mouse_reset && that.animating === "out") {
                        if (!that.timerHide) return;
                        that.cancelRemove();
                    }
                    if (that.options.hide && that.options.mouse_reset) that.cancelRemove();
                },
                mouseleave: function(e) {
                    if (that.options.hide && that.options.mouse_reset) that.queueRemove();
                    PNotify.positionAll();
                }
            });
            this.container = $("<div />", {
                "class": this.styles.container + " ui-pnotify-container " + (this.options.type === "error" ? this.styles.error : this.options.type === "info" ? this.styles.info : this.options.type === "success" ? this.styles.success : this.styles.notice)
            }).appendTo(this.elem);
            if (this.options.cornerclass !== "") this.container.removeClass("ui-corner-all").addClass(this.options.cornerclass);
            if (this.options.shadow) this.container.addClass("ui-pnotify-shadow");
            if (this.options.icon !== false) {
                $("<div />", {
                    "class": "ui-pnotify-icon"
                }).append($("<span />", {
                    "class": this.options.icon === true ? this.options.type === "error" ? this.styles.error_icon : this.options.type === "info" ? this.styles.info_icon : this.options.type === "success" ? this.styles.success_icon : this.styles.notice_icon : this.options.icon
                })).prependTo(this.container);
            }
            this.title_container = $("<h4 />", {
                "class": "ui-pnotify-title"
            }).appendTo(this.container);
            if (this.options.title === false) this.title_container.hide(); else if (this.options.title_escape) this.title_container.text(this.options.title); else this.title_container.html(this.options.title);
            this.text_container = $("<div />", {
                "class": "ui-pnotify-text"
            }).appendTo(this.container);
            if (this.options.text === false) this.text_container.hide(); else if (this.options.text_escape) this.text_container.text(this.options.text); else this.text_container.html(this.options.insert_brs ? String(this.options.text).replace(/\n/g, "<br />") : this.options.text);
            if (typeof this.options.width === "string") this.elem.css("width", this.options.width);
            if (typeof this.options.min_height === "string") this.container.css("min-height", this.options.min_height);
            if (this.options.stack.push === "top") PNotify.notices = $.merge([ this ], PNotify.notices); else PNotify.notices = $.merge(PNotify.notices, [ this ]);
            if (this.options.stack.push === "top") this.queuePosition(false, 1);
            this.options.stack.animation = false;
            this.runModules("init");
            if (this.options.auto_display) this.open();
            return this;
        },
        update: function(options) {
            var oldOpts = this.options;
            this.parseOptions(oldOpts, options);
            if (this.options.cornerclass !== oldOpts.cornerclass) this.container.removeClass("ui-corner-all " + oldOpts.cornerclass).addClass(this.options.cornerclass);
            if (this.options.shadow !== oldOpts.shadow) {
                if (this.options.shadow) this.container.addClass("ui-pnotify-shadow"); else this.container.removeClass("ui-pnotify-shadow");
            }
            if (this.options.addclass === false) this.elem.removeClass(oldOpts.addclass); else if (this.options.addclass !== oldOpts.addclass) this.elem.removeClass(oldOpts.addclass).addClass(this.options.addclass);
            if (this.options.title === false) this.title_container.slideUp("fast"); else if (this.options.title !== oldOpts.title) {
                if (this.options.title_escape) this.title_container.text(this.options.title); else this.title_container.html(this.options.title);
                if (oldOpts.title === false) this.title_container.slideDown(200);
            }
            if (this.options.text === false) {
                this.text_container.slideUp("fast");
            } else if (this.options.text !== oldOpts.text) {
                if (this.options.text_escape) this.text_container.text(this.options.text); else this.text_container.html(this.options.insert_brs ? String(this.options.text).replace(/\n/g, "<br />") : this.options.text);
                if (oldOpts.text === false) this.text_container.slideDown(200);
            }
            if (this.options.type !== oldOpts.type) this.container.removeClass(this.styles.error + " " + this.styles.notice + " " + this.styles.success + " " + this.styles.info).addClass(this.options.type === "error" ? this.styles.error : this.options.type === "info" ? this.styles.info : this.options.type === "success" ? this.styles.success : this.styles.notice);
            if (this.options.icon !== oldOpts.icon || this.options.icon === true && this.options.type !== oldOpts.type) {
                this.container.find("div.ui-pnotify-icon").remove();
                if (this.options.icon !== false) {
                    $("<div />", {
                        "class": "ui-pnotify-icon"
                    }).append($("<span />", {
                        "class": this.options.icon === true ? this.options.type === "error" ? this.styles.error_icon : this.options.type === "info" ? this.styles.info_icon : this.options.type === "success" ? this.styles.success_icon : this.styles.notice_icon : this.options.icon
                    })).prependTo(this.container);
                }
            }
            if (this.options.width !== oldOpts.width) this.elem.animate({
                width: this.options.width
            });
            if (this.options.min_height !== oldOpts.min_height) this.container.animate({
                minHeight: this.options.min_height
            });
            if (this.options.opacity !== oldOpts.opacity) this.elem.fadeTo(this.options.animate_speed, this.options.opacity);
            if (!this.options.hide) this.cancelRemove(); else if (!oldOpts.hide) this.queueRemove();
            this.queuePosition(true);
            this.runModules("update", oldOpts);
            return this;
        },
        open: function() {
            this.state = "opening";
            this.runModules("beforeOpen");
            var that = this;
            if (!this.elem.parent().length) this.elem.appendTo(this.options.stack.context ? this.options.stack.context : body);
            if (this.options.stack.push !== "top") this.position(true);
            if (this.options.animation === "fade" || this.options.animation.effect_in === "fade") {
                this.elem.show().fadeTo(0, 0).hide();
            } else {
                if (this.options.opacity !== 1) this.elem.show().fadeTo(0, this.options.opacity).hide();
            }
            this.animateIn(function() {
                that.queuePosition(true);
                if (that.options.hide) that.queueRemove();
                that.state = "open";
                that.runModules("afterOpen");
            });
            return this;
        },
        remove: function(timer_hide) {
            this.state = "closing";
            this.timerHide = !!timer_hide;
            this.runModules("beforeClose");
            var that = this;
            if (this.timer) {
                window.clearTimeout(this.timer);
                this.timer = null;
            }
            this.animateOut(function() {
                that.state = "closed";
                that.runModules("afterClose");
                that.queuePosition(true);
                if (that.options.remove) that.elem.detach();
                that.runModules("beforeDestroy");
                if (that.options.destroy) {
                    if (PNotify.notices !== null) {
                        var idx = $.inArray(that, PNotify.notices);
                        if (idx !== -1) {
                            PNotify.notices.splice(idx, 1);
                        }
                    }
                }
                that.runModules("afterDestroy");
            });
            return this;
        },
        get: function() {
            return this.elem;
        },
        parseOptions: function(options, moreOptions) {
            this.options = $.extend(true, {}, PNotify.prototype.options);
            this.options.stack = PNotify.prototype.options.stack;
            var optArray = [ options, moreOptions ], curOpts;
            for (var curIndex in optArray) {
                curOpts = optArray[curIndex];
                if (typeof curOpts == "undefined") break;
                if (typeof curOpts !== "object") {
                    this.options.text = curOpts;
                } else {
                    for (var option in curOpts) {
                        if (this.modules[option]) {
                            $.extend(true, this.options[option], curOpts[option]);
                        } else {
                            this.options[option] = curOpts[option];
                        }
                    }
                }
            }
        },
        animateIn: function(callback) {
            this.animating = "in";
            var animation;
            if (typeof this.options.animation.effect_in !== "undefined") animation = this.options.animation.effect_in; else animation = this.options.animation;
            if (animation === "none") {
                this.elem.show();
                callback();
            } else if (animation === "show") this.elem.show(this.options.animate_speed, callback); else if (animation === "fade") this.elem.show().fadeTo(this.options.animate_speed, this.options.opacity, callback); else if (animation === "slide") this.elem.slideDown(this.options.animate_speed, callback); else if (typeof animation === "function") animation("in", callback, this.elem); else this.elem.show(animation, typeof this.options.animation.options_in === "object" ? this.options.animation.options_in : {}, this.options.animate_speed, callback);
            if (this.elem.parent().hasClass("ui-effects-wrapper")) this.elem.parent().css({
                position: "fixed",
                overflow: "visible"
            });
            if (animation !== "slide") this.elem.css("overflow", "visible");
            this.container.css("overflow", "hidden");
        },
        animateOut: function(callback) {
            this.animating = "out";
            var animation;
            if (typeof this.options.animation.effect_out !== "undefined") animation = this.options.animation.effect_out; else animation = this.options.animation;
            if (animation === "none") {
                this.elem.hide();
                callback();
            } else if (animation === "show") this.elem.hide(this.options.animate_speed, callback); else if (animation === "fade") this.elem.fadeOut(this.options.animate_speed, callback); else if (animation === "slide") this.elem.slideUp(this.options.animate_speed, callback); else if (typeof animation === "function") animation("out", callback, this.elem); else this.elem.hide(animation, typeof this.options.animation.options_out === "object" ? this.options.animation.options_out : {}, this.options.animate_speed, callback);
            if (this.elem.parent().hasClass("ui-effects-wrapper")) this.elem.parent().css({
                position: "fixed",
                overflow: "visible"
            });
            if (animation !== "slide") this.elem.css("overflow", "visible");
            this.container.css("overflow", "hidden");
        },
        position: function(dontSkipHidden) {
            var s = this.options.stack, e = this.elem;
            if (e.parent().hasClass("ui-effects-wrapper")) e = this.elem.css({
                left: "0",
                top: "0",
                right: "0",
                bottom: "0"
            }).parent();
            if (typeof s.context === "undefined") s.context = body;
            if (!s) return;
            if (typeof s.nextpos1 !== "number") s.nextpos1 = s.firstpos1;
            if (typeof s.nextpos2 !== "number") s.nextpos2 = s.firstpos2;
            if (typeof s.addpos2 !== "number") s.addpos2 = 0;
            var hidden = e.css("display") === "none";
            if (!hidden || dontSkipHidden) {
                var curpos1, curpos2;
                var animate = {};
                var csspos1;
                switch (s.dir1) {
                  case "down":
                    csspos1 = "top";
                    break;

                  case "up":
                    csspos1 = "bottom";
                    break;

                  case "left":
                    csspos1 = "right";
                    break;

                  case "right":
                    csspos1 = "left";
                    break;
                }
                curpos1 = parseInt(e.css(csspos1).replace(/(?:\..*|[^0-9.])/g, ""));
                if (isNaN(curpos1)) curpos1 = 0;
                if (typeof s.firstpos1 === "undefined" && !hidden) {
                    s.firstpos1 = curpos1;
                    s.nextpos1 = s.firstpos1;
                }
                var csspos2;
                switch (s.dir2) {
                  case "down":
                    csspos2 = "top";
                    break;

                  case "up":
                    csspos2 = "bottom";
                    break;

                  case "left":
                    csspos2 = "right";
                    break;

                  case "right":
                    csspos2 = "left";
                    break;
                }
                curpos2 = parseInt(e.css(csspos2).replace(/(?:\..*|[^0-9.])/g, ""));
                if (isNaN(curpos2)) curpos2 = 0;
                if (typeof s.firstpos2 === "undefined" && !hidden) {
                    s.firstpos2 = curpos2;
                    s.nextpos2 = s.firstpos2;
                }
                if (s.dir1 === "down" && s.nextpos1 + e.height() > (s.context.is(body) ? jwindow.height() : s.context.prop("scrollHeight")) || s.dir1 === "up" && s.nextpos1 + e.height() > (s.context.is(body) ? jwindow.height() : s.context.prop("scrollHeight")) || s.dir1 === "left" && s.nextpos1 + e.width() > (s.context.is(body) ? jwindow.width() : s.context.prop("scrollWidth")) || s.dir1 === "right" && s.nextpos1 + e.width() > (s.context.is(body) ? jwindow.width() : s.context.prop("scrollWidth"))) {
                    s.nextpos1 = s.firstpos1;
                    s.nextpos2 += s.addpos2 + (typeof s.spacing2 === "undefined" ? 25 : s.spacing2);
                    s.addpos2 = 0;
                }
                if (s.animation && s.nextpos2 < curpos2) {
                    switch (s.dir2) {
                      case "down":
                        animate.top = s.nextpos2 + "px";
                        break;

                      case "up":
                        animate.bottom = s.nextpos2 + "px";
                        break;

                      case "left":
                        animate.right = s.nextpos2 + "px";
                        break;

                      case "right":
                        animate.left = s.nextpos2 + "px";
                        break;
                    }
                } else {
                    if (typeof s.nextpos2 === "number") e.css(csspos2, s.nextpos2 + "px");
                }
                switch (s.dir2) {
                  case "down":
                  case "up":
                    if (e.outerHeight(true) > s.addpos2) s.addpos2 = e.height();
                    break;

                  case "left":
                  case "right":
                    if (e.outerWidth(true) > s.addpos2) s.addpos2 = e.width();
                    break;
                }
                if (typeof s.nextpos1 === "number") {
                    if (s.animation && (curpos1 > s.nextpos1 || animate.top || animate.bottom || animate.right || animate.left)) {
                        switch (s.dir1) {
                          case "down":
                            animate.top = s.nextpos1 + "px";
                            break;

                          case "up":
                            animate.bottom = s.nextpos1 + "px";
                            break;

                          case "left":
                            animate.right = s.nextpos1 + "px";
                            break;

                          case "right":
                            animate.left = s.nextpos1 + "px";
                            break;
                        }
                    } else e.css(csspos1, s.nextpos1 + "px");
                }
                if (animate.top || animate.bottom || animate.right || animate.left) e.animate(animate, {
                    duration: this.options.position_animate_speed,
                    queue: false
                });
                switch (s.dir1) {
                  case "down":
                  case "up":
                    s.nextpos1 += e.height() + (typeof s.spacing1 === "undefined" ? 25 : s.spacing1);
                    break;

                  case "left":
                  case "right":
                    s.nextpos1 += e.width() + (typeof s.spacing1 === "undefined" ? 25 : s.spacing1);
                    break;
                }
            }
            return this;
        },
        queuePosition: function(animate, milliseconds) {
            if (timer) clearTimeout(timer);
            if (!milliseconds) milliseconds = 10;
            timer = setTimeout(function() {
                PNotify.positionAll(animate);
            }, milliseconds);
            return this;
        },
        cancelRemove: function() {
            if (this.timer) window.clearTimeout(this.timer);
            if (this.state === "closing") {
                this.elem.stop(true);
                this.state = "open";
                this.animating = "in";
                this.elem.css("height", "auto").animate({
                    width: this.options.width,
                    opacity: this.options.opacity
                }, "fast");
            }
            return this;
        },
        queueRemove: function() {
            var that = this;
            this.cancelRemove();
            this.timer = window.setTimeout(function() {
                that.remove(true);
            }, isNaN(this.options.delay) ? 0 : this.options.delay);
            return this;
        }
    });
    $.extend(PNotify, {
        notices: [],
        removeAll: function() {
            $.each(PNotify.notices, function() {
                if (this.remove) this.remove();
            });
        },
        positionAll: function(animate) {
            if (timer) clearTimeout(timer);
            timer = null;
            $.each(PNotify.notices, function() {
                var s = this.options.stack;
                if (!s) return;
                s.nextpos1 = s.firstpos1;
                s.nextpos2 = s.firstpos2;
                s.addpos2 = 0;
                s.animation = animate;
            });
            $.each(PNotify.notices, function() {
                this.position();
            });
        },
        styling: {
            jqueryui: {
                container: "ui-widget ui-widget-content ui-corner-all",
                notice: "ui-state-highlight",
                notice_icon: "ui-icon ui-icon-info",
                info: "",
                info_icon: "ui-icon ui-icon-info",
                success: "ui-state-default",
                success_icon: "ui-icon ui-icon-circle-check",
                error: "ui-state-error",
                error_icon: "ui-icon ui-icon-alert"
            },
            bootstrap2: {
                container: "alert",
                notice: "",
                notice_icon: "icon-exclamation-sign",
                info: "alert-info",
                info_icon: "icon-info-sign",
                success: "alert-success",
                success_icon: "icon-ok-sign",
                error: "alert-error",
                error_icon: "icon-warning-sign"
            },
            bootstrap3: {
                container: "alert",
                notice: "alert-warning",
                notice_icon: "glyphicon glyphicon-exclamation-sign",
                info: "alert-info",
                info_icon: "glyphicon glyphicon-info-sign",
                success: "alert-success",
                success_icon: "glyphicon glyphicon-ok-sign",
                error: "alert-danger",
                error_icon: "glyphicon glyphicon-warning-sign"
            }
        }
    });
    PNotify.styling.fontawesome = $.extend({}, PNotify.styling.bootstrap3);
    $.extend(PNotify.styling.fontawesome, {
        notice_icon: "fa fa-exclamation-circle",
        info_icon: "fa fa-info",
        success_icon: "fa fa-check",
        error_icon: "fa fa-warning"
    });
    if (document.body) do_when_ready(); else $(do_when_ready);
    return PNotify;
});

(function(factory) {
    if (typeof define === "function" && define.amd) {
        define("pnotify.buttons", [ "jquery", "pnotify" ], factory);
    } else {
        factory(jQuery, PNotify);
    }
})(function($, PNotify) {
    PNotify.prototype.options.buttons = {
        closer: true,
        closer_hover: true,
        sticker: true,
        sticker_hover: true,
        labels: {
            close: "Close",
            stick: "Stick"
        }
    };
    PNotify.prototype.modules.buttons = {
        myOptions: null,
        closer: null,
        sticker: null,
        init: function(notice, options) {
            var that = this;
            this.myOptions = options;
            notice.elem.on({
                mouseenter: function(e) {
                    if (that.myOptions.sticker && !(notice.options.nonblock && notice.options.nonblock.nonblock)) that.sticker.trigger("pnotify_icon").css("visibility", "visible");
                    if (that.myOptions.closer && !(notice.options.nonblock && notice.options.nonblock.nonblock)) that.closer.css("visibility", "visible");
                },
                mouseleave: function(e) {
                    if (that.myOptions.sticker_hover) that.sticker.css("visibility", "hidden");
                    if (that.myOptions.closer_hover) that.closer.css("visibility", "hidden");
                }
            });
            this.sticker = $("<div />", {
                "class": "ui-pnotify-sticker",
                css: {
                    cursor: "pointer",
                    visibility: options.sticker_hover ? "hidden" : "visible"
                },
                click: function() {
                    notice.options.hide = !notice.options.hide;
                    if (notice.options.hide) notice.queueRemove(); else notice.cancelRemove();
                    $(this).trigger("pnotify_icon");
                }
            }).bind("pnotify_icon", function() {
                $(this).children().removeClass(notice.styles.pin_up + " " + notice.styles.pin_down).addClass(notice.options.hide ? notice.styles.pin_up : notice.styles.pin_down);
            }).append($("<span />", {
                "class": notice.styles.pin_up,
                title: options.labels.stick
            })).prependTo(notice.container);
            if (!options.sticker || notice.options.nonblock && notice.options.nonblock.nonblock) this.sticker.css("display", "none");
            this.closer = $("<div />", {
                "class": "ui-pnotify-closer",
                css: {
                    cursor: "pointer",
                    visibility: options.closer_hover ? "hidden" : "visible"
                },
                click: function() {
                    notice.remove(false);
                    that.sticker.css("visibility", "hidden");
                    that.closer.css("visibility", "hidden");
                }
            }).append($("<span />", {
                "class": notice.styles.closer,
                title: options.labels.close
            })).prependTo(notice.container);
            if (!options.closer || notice.options.nonblock && notice.options.nonblock.nonblock) this.closer.css("display", "none");
        },
        update: function(notice, options) {
            this.myOptions = options;
            if (!options.closer || notice.options.nonblock && notice.options.nonblock.nonblock) this.closer.css("display", "none"); else if (options.closer) this.closer.css("display", "block");
            if (!options.sticker || notice.options.nonblock && notice.options.nonblock.nonblock) this.sticker.css("display", "none"); else if (options.sticker) this.sticker.css("display", "block");
            this.sticker.trigger("pnotify_icon");
            if (options.sticker_hover) this.sticker.css("visibility", "hidden"); else if (!(notice.options.nonblock && notice.options.nonblock.nonblock)) this.sticker.css("visibility", "visible");
            if (options.closer_hover) this.closer.css("visibility", "hidden"); else if (!(notice.options.nonblock && notice.options.nonblock.nonblock)) this.closer.css("visibility", "visible");
        }
    };
    $.extend(PNotify.styling.jqueryui, {
        closer: "ui-icon ui-icon-close",
        pin_up: "ui-icon ui-icon-pin-w",
        pin_down: "ui-icon ui-icon-pin-s"
    });
    $.extend(PNotify.styling.bootstrap2, {
        closer: "icon-remove",
        pin_up: "icon-pause",
        pin_down: "icon-play"
    });
    $.extend(PNotify.styling.bootstrap3, {
        closer: "glyphicon glyphicon-remove",
        pin_up: "glyphicon glyphicon-pause",
        pin_down: "glyphicon glyphicon-play"
    });
    $.extend(PNotify.styling.fontawesome, {
        closer: "fa fa-times",
        pin_up: "fa fa-pause",
        pin_down: "fa fa-play"
    });
});

(function(factory) {
    if (typeof define === "function" && define.amd) {
        define("pnotify.confirm", [ "jquery", "pnotify" ], factory);
    } else {
        factory(jQuery, PNotify);
    }
})(function($, PNotify) {
    PNotify.prototype.options.confirm = {
        confirm: false,
        prompt: false,
        prompt_class: "",
        prompt_default: "",
        prompt_multi_line: false,
        align: "right",
        buttons: [ {
            text: "Ok",
            addClass: "",
            promptTrigger: true,
            click: function(notice, value) {
                notice.remove();
                notice.get().trigger("pnotify.confirm", [ notice, value ]);
            }
        }, {
            text: "Cancel",
            addClass: "",
            click: function(notice) {
                notice.remove();
                notice.get().trigger("pnotify.cancel", notice);
            }
        } ]
    };
    PNotify.prototype.modules.confirm = {
        container: null,
        prompt: null,
        init: function(notice, options) {
            this.container = $('<div style="margin-top:5px;clear:both;" />').css("text-align", options.align).appendTo(notice.container);
            if (options.confirm || options.prompt) this.makeDialog(notice, options); else this.container.hide();
        },
        update: function(notice, options) {
            if (options.confirm) {
                this.makeDialog(notice, options);
                this.container.show();
            } else {
                this.container.hide().empty();
            }
        },
        afterOpen: function(notice, options) {
            if (options.prompt) this.prompt.focus();
        },
        makeDialog: function(notice, options) {
            var already = false, that = this, btn, elem;
            this.container.empty();
            if (options.prompt) {
                this.prompt = $("<" + (options.prompt_multi_line ? 'textarea rows="5"' : 'input type="text"') + ' style="margin-bottom:5px;clear:both;" />').addClass(notice.styles.input + " " + options.prompt_class).val(options.prompt_default).appendTo(this.container);
            }
            for (var i in options.buttons) {
                btn = options.buttons[i];
                if (already) this.container.append(" "); else already = true;
                elem = $('<button type="button" />').addClass(notice.styles.btn + " " + btn.addClass).text(btn.text).appendTo(this.container).on("click", function(btn) {
                    return function() {
                        if (typeof btn.click == "function") {
                            btn.click(notice, options.prompt ? that.prompt.val() : null);
                        }
                    };
                }(btn));
                if (options.prompt && !options.prompt_multi_line && btn.promptTrigger) this.prompt.keypress(function(elem) {
                    return function(e) {
                        if (e.keyCode == 13) elem.click();
                    };
                }(elem));
                if (notice.styles.text) {
                    elem.wrapInner('<span class="' + notice.styles.text + '"></span>');
                }
                if (notice.styles.btnhover) {
                    elem.hover(function(elem) {
                        return function() {
                            elem.addClass(notice.styles.btnhover);
                        };
                    }(elem), function(elem) {
                        return function() {
                            elem.removeClass(notice.styles.btnhover);
                        };
                    }(elem));
                }
                if (notice.styles.btnactive) {
                    elem.on("mousedown", function(elem) {
                        return function() {
                            elem.addClass(notice.styles.btnactive);
                        };
                    }(elem)).on("mouseup", function(elem) {
                        return function() {
                            elem.removeClass(notice.styles.btnactive);
                        };
                    }(elem));
                }
                if (notice.styles.btnfocus) {
                    elem.on("focus", function(elem) {
                        return function() {
                            elem.addClass(notice.styles.btnfocus);
                        };
                    }(elem)).on("blur", function(elem) {
                        return function() {
                            elem.removeClass(notice.styles.btnfocus);
                        };
                    }(elem));
                }
            }
        }
    };
    $.extend(PNotify.styling.jqueryui, {
        btn: "ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only",
        btnhover: "ui-state-hover",
        btnactive: "ui-state-active",
        btnfocus: "ui-state-focus",
        input: "",
        text: "ui-button-text"
    });
    $.extend(PNotify.styling.bootstrap2, {
        btn: "btn",
        input: ""
    });
    $.extend(PNotify.styling.bootstrap3, {
        btn: "btn btn-default",
        input: "form-control"
    });
    $.extend(PNotify.styling.fontawesome, {
        btn: "btn btn-default",
        input: "form-control"
    });
});

(function(factory) {
    if (typeof define === "function" && define.amd) {
        define("pnotify.history", [ "jquery", "pnotify" ], factory);
    } else {
        factory(jQuery, PNotify);
    }
})(function($, PNotify) {
    var history_menu, history_handle_top;
    $(function() {
        $("body").on("pnotify.history-all", function() {
            $.each(PNotify.notices, function() {
                if (this.modules.history.inHistory) {
                    if (this.elem.is(":visible")) {
                        if (this.options.hide) this.queueRemove();
                    } else if (this.open) this.open();
                }
            });
        }).on("pnotify.history-last", function() {
            var pushTop = PNotify.prototype.options.stack.push === "top";
            var i = pushTop ? 0 : -1;
            var notice;
            do {
                if (i === -1) notice = PNotify.notices.slice(i); else notice = PNotify.notices.slice(i, i + 1);
                if (!notice[0]) return false;
                i = pushTop ? i + 1 : i - 1;
            } while (!notice[0].modules.history.inHistory || notice[0].elem.is(":visible"));
            if (notice[0].open) notice[0].open();
        });
    });
    PNotify.prototype.options.history = {
        history: true,
        menu: false,
        fixed: true,
        maxonscreen: Infinity,
        labels: {
            redisplay: "Redisplay",
            all: "All",
            last: "Last"
        }
    };
    PNotify.prototype.modules.history = {
        inHistory: false,
        init: function(notice, options) {
            notice.options.destroy = false;
            this.inHistory = options.history;
            if (options.menu) {
                if (typeof history_menu === "undefined") {
                    history_menu = $("<div />", {
                        "class": "ui-pnotify-history-container " + notice.styles.hi_menu,
                        mouseleave: function() {
                            history_menu.animate({
                                top: "-" + history_handle_top + "px"
                            }, {
                                duration: 100,
                                queue: false
                            });
                        }
                    }).append($("<div />", {
                        "class": "ui-pnotify-history-header",
                        text: options.labels.redisplay
                    })).append($("<button />", {
                        "class": "ui-pnotify-history-all " + notice.styles.hi_btn,
                        text: options.labels.all,
                        mouseenter: function() {
                            $(this).addClass(notice.styles.hi_btnhov);
                        },
                        mouseleave: function() {
                            $(this).removeClass(notice.styles.hi_btnhov);
                        },
                        click: function() {
                            $(this).trigger("pnotify.history-all");
                            return false;
                        }
                    })).append($("<button />", {
                        "class": "ui-pnotify-history-last " + notice.styles.hi_btn,
                        text: options.labels.last,
                        mouseenter: function() {
                            $(this).addClass(notice.styles.hi_btnhov);
                        },
                        mouseleave: function() {
                            $(this).removeClass(notice.styles.hi_btnhov);
                        },
                        click: function() {
                            $(this).trigger("pnotify.history-last");
                            return false;
                        }
                    })).appendTo("body");
                    var handle = $("<span />", {
                        "class": "ui-pnotify-history-pulldown " + notice.styles.hi_hnd,
                        mouseenter: function() {
                            history_menu.animate({
                                top: "0"
                            }, {
                                duration: 100,
                                queue: false
                            });
                        }
                    }).appendTo(history_menu);
                    console.log(handle.offset());
                    history_handle_top = handle.offset().top + 2;
                    history_menu.css({
                        top: "-" + history_handle_top + "px"
                    });
                    if (options.fixed) {
                        history_menu.addClass("ui-pnotify-history-fixed");
                    }
                }
            }
        },
        update: function(notice, options) {
            this.inHistory = options.history;
            if (options.fixed && history_menu) {
                history_menu.addClass("ui-pnotify-history-fixed");
            } else if (history_menu) {
                history_menu.removeClass("ui-pnotify-history-fixed");
            }
        },
        beforeOpen: function(notice, options) {
            if (PNotify.notices && PNotify.notices.length > options.maxonscreen) {
                var el;
                if (notice.options.stack.push !== "top") el = PNotify.notices.slice(0, PNotify.notices.length - options.maxonscreen); else el = PNotify.notices.slice(options.maxonscreen, PNotify.notices.length);
                $.each(el, function() {
                    if (this.remove) this.remove();
                });
            }
        }
    };
    $.extend(PNotify.styling.jqueryui, {
        hi_menu: "ui-state-default ui-corner-bottom",
        hi_btn: "ui-state-default ui-corner-all",
        hi_btnhov: "ui-state-hover",
        hi_hnd: "ui-icon ui-icon-grip-dotted-horizontal"
    });
    $.extend(PNotify.styling.bootstrap2, {
        hi_menu: "well",
        hi_btn: "btn",
        hi_btnhov: "",
        hi_hnd: "icon-chevron-down"
    });
    $.extend(PNotify.styling.bootstrap3, {
        hi_menu: "well",
        hi_btn: "btn btn-default",
        hi_btnhov: "",
        hi_hnd: "glyphicon glyphicon-chevron-down"
    });
    $.extend(PNotify.styling.fontawesome, {
        hi_menu: "well",
        hi_btn: "btn btn-default",
        hi_btnhov: "",
        hi_hnd: "fa fa-chevron-down"
    });
});

(function(root, factory) {
    if (typeof define === "function" && define.amd) {
        define([ "jquery" ], factory);
    } else if (typeof exports === "object") {
        module.exports = factory(require("jquery"));
    } else {
        root.sortable = factory(root.jQuery);
    }
})(this, function($) {
    var dragging;
    var draggingHeight;
    var placeholders = $();
    var sortable = function(options) {
        "use strict";
        var method = String(options);
        options = $.extend({
            connectWith: false,
            placeholder: null,
            dragImage: null,
            placeholderClass: "sortable-placeholder",
            draggingClass: "sortable-dragging"
        }, options);
        return this.each(function() {
            var index;
            var $sortable = $(this);
            var items = $sortable.children(options.items);
            var handles = options.handle ? items.find(options.handle) : items;
            if (method === "reload") {
                items.off("dragstart.h5s");
                items.off("dragend.h5s");
                items.off("selectstart.h5s");
                items.off("dragover.h5s");
                items.off("dragenter.h5s");
                items.off("drop.h5s");
                $sortable.off("dragover.h5s");
                $sortable.off("dragenter.h5s");
                $sortable.off("drop.h5s");
            }
            if (/^enable|disable|destroy$/.test(method)) {
                var citems = $(this).children($(this).data("items"));
                citems.attr("draggable", method === "enable");
                $(this).attr("aria-dropeffect", /^disable|destroy$/.test(method) ? "none" : "move");
                if (method === "destroy") {
                    $(this).off("sortstart sortupdate");
                    $(this).removeData("opts");
                    citems.add(this).removeData("connectWith items").off("dragstart.h5s dragend.h5s dragover.h5s dragenter.h5s drop.h5s sortupdate");
                    handles.off("selectstart.h5s");
                }
                return;
            }
            var soptions = $(this).data("opts");
            if (typeof soptions === "undefined") {
                $(this).data("opts", options);
            } else {
                options = soptions;
            }
            var startParent;
            var newParent;
            var placeholder = options.placeholder === null ? $("<" + (/^ul|ol$/i.test(this.tagName) ? "li" : "div") + ' class="' + options.placeholderClass + '"/>') : $(options.placeholder).addClass(options.placeholderClass);
            $(this).data("items", options.items);
            placeholders = placeholders.add(placeholder);
            if (options.connectWith) {
                $(options.connectWith).add(this).data("connectWith", options.connectWith);
            }
            items.attr("role", "option");
            items.attr("aria-grabbed", "false");
            handles.attr("draggable", "true");
            handles.not("a[href], img").on("selectstart.h5s", function() {
                if (this.dragDrop) {
                    this.dragDrop();
                }
            }).end();
            items.on("dragstart.h5s", function(e) {
                e.stopImmediatePropagation();
                var dt = e.originalEvent.dataTransfer;
                dt.effectAllowed = "move";
                dt.setData("text", "");
                if (options.dragImage && dt.setDragImage) {
                    dt.setDragImage(options.dragImage, 0, 0);
                }
                dragging = $(this);
                dragging.addClass(options.draggingClass);
                dragging.attr("aria-grabbed", "true");
                index = dragging.index();
                draggingHeight = dragging.height();
                startParent = $(this).parent();
                dragging.parent().triggerHandler("sortstart", {
                    item: dragging,
                    startparent: startParent
                });
            });
            items.on("dragend.h5s", function() {
                if (!dragging) {
                    return;
                }
                dragging.removeClass(options.draggingClass);
                dragging.attr("aria-grabbed", "false");
                dragging.show();
                placeholders.detach();
                newParent = $(this).parent();
                if (index !== dragging.index() || startParent.get(0) !== newParent.get(0)) {
                    dragging.parent().triggerHandler("sortupdate", {
                        item: dragging,
                        oldindex: index,
                        startparent: startParent,
                        endparent: newParent
                    });
                }
                dragging = null;
                draggingHeight = null;
            });
            items.add([ this, placeholder ]).on("dragover.h5s dragenter.h5s drop.h5s", function(e) {
                if (!items.is(dragging) && options.connectWith !== $(dragging).parent().data("connectWith")) {
                    return true;
                }
                if (e.type === "drop") {
                    e.stopPropagation();
                    placeholders.filter(":visible").after(dragging);
                    dragging.trigger("dragend.h5s");
                    return false;
                }
                e.preventDefault();
                e.originalEvent.dataTransfer.dropEffect = "move";
                if (items.is(this)) {
                    var thisHeight = $(this).height();
                    if (options.forcePlaceholderSize) {
                        placeholder.height(draggingHeight);
                    }
                    if (thisHeight > draggingHeight) {
                        var deadZone = thisHeight - draggingHeight;
                        var offsetTop = $(this).offset().top;
                        if (placeholder.index() < $(this).index() && e.originalEvent.pageY < offsetTop + deadZone) {
                            return false;
                        }
                        if (placeholder.index() > $(this).index() && e.originalEvent.pageY > offsetTop + thisHeight - deadZone) {
                            return false;
                        }
                    }
                    dragging.hide();
                    if (placeholder.index() < $(this).index()) {
                        $(this).after(placeholder);
                    } else {
                        $(this).before(placeholder);
                    }
                    placeholders.not(placeholder).detach();
                } else {
                    if (!placeholders.is(this) && !$(this).children(options.items).length) {
                        placeholders.detach();
                        $(this).append(placeholder);
                    }
                }
                return false;
            });
        });
    };
    $.fn.sortable = sortable;
    return sortable;
});

Modernizr.load({
    test: Modernizr.input.placeholder,
    nope: [ "https://cdnjs.cloudflare.com/ajax/libs/jquery-placeholder/2.0.8/jquery.placeholder.min.js" ]
});

var Twibs = {
    updateAfterDomChange: function() {
        $(document).trigger("twibs-update-dom");
    },
    pushState: function(state, title, pathname) {
        if (window.location.pathname != pathname) {
            window.history.replaceState("twibs-reload", document.title, document.location.href);
            window.history.pushState(state, title, pathname);
            document.title = title;
        }
    },
    mailme: function(host, name, subject) {
        if (subject) {
            self.location = "mail" + "to:" + name + "@" + host + "?subject=" + subject;
        } else {
            self.location = "mail" + "to:" + name + "@" + host;
        }
        return false;
    }
};

$(function() {
    var cp = typeof contextPath !== "undefined" ? contextPath : "";
    var oFocus = jQuery.fn.focus;
    jQuery.fn.focus = function() {
        return this.hasClass("chosen") ? this.trigger("chosen:activate.chosen") : oFocus.apply(this, arguments);
    };
    $.fn.disableForm = function() {
        return this.each(function() {
            var $this = $(this);
            $this.find(".can-be-disabled").attr("disabled", "disabled");
            $this.find(".can-be-disabled-by-class").addClass("disabled");
            $this.find("select.form-control").trigger("chosen:updated");
        });
    };
    $.fn.reenableForm = function() {
        return this.each(function() {
            var $this = $(this);
            $this.find(".can-be-disabled").removeAttr("disabled");
            $this.find(".can-be-disabled-by-class").removeClass("disabled");
        });
    };
    $.fn.ckeditor = function(callback, config) {
        $('<script src="' + cp + '/ckeditor/ckeditor.js"></script><script src="' + cp + '/ckeditor/adapters/jquery.js"></script>').appendTo("head");
        $(this).ckeditor(callback, config);
    };
    $.fn.initFormElements = function() {
        return this.each(function() {
            var $this = $(this);
            $this.find("select.chosen").chosen({
                disable_search_threshold: 6,
                width: "100%"
            });
            $this.find("select.chosen-optional").chosen({
                allow_single_deselect: true,
                disable_search_threshold: 6,
                width: "100%"
            });
            $this.find(".sortable").sortable({
                forcePlaceholderSize: true
            }).bind("sortupdate", function(e, ui) {
                ui.item.submitForm("sortable", ui.item.id);
            });
        });
    };
    $.fn.destroyFormElements = function() {
        return this.each(function() {
            var $this = $(this);
            $this.find(".sortable").sortable("destroy");
            $this.find("select.chosen").chosen("destroy");
            $this.find("select.chosen-optional").chosen("destroy");
            $this.find(".cke_editable").each(function() {
                var $this = $(this);
                var ckeditor = $this.data("ckeditorInstance");
                if (ckeditor) {
                    ckeditor.destroy();
                    $this.data("ckeditorInstance", null);
                }
            });
        });
    };
    $.fn.updateFormElements = function(htmlString) {
        return this.destroyFormElements().html(htmlString).initFormElements();
    };
    $.fn.submitForm = function(reason, actionId, name, value) {
        var data = {};
        if (reason) data["t-action-reason"] = reason;
        if (actionId) data["t-action-id"] = actionId;
        if (name) {
            data["t-action-name"] = name;
            if (value !== undefined) data[name] = value;
        }
        var ae = $(document.activeElement);
        var focusedId = ae.attr("id") || ae.closest(".chosen-container").prev().attr("id");
        if (focusedId) data["t-focused-id"] = focusedId;
        var bf = $("html").attr("class");
        if (bf) data["t-browser-features"] = bf;
        var config = {
            data: data
        };
        $(this).each(function() {
            ajaxSubmitForm($(this).closest("form"), config);
        });
        return this;
    };
    $.fn.twibsModal = function() {
        return this.each(function() {
            var $this = $(this);
            $this.on("hidden.bs.modal", function() {
                $this.remove();
                $("body").updateDynamics();
            }).on("shown.bs.modal", function() {
                $this.find("input:visible,.btn:visible").first().focus();
                Twibs.updateAfterDomChange();
            }).modal();
        });
    };
    $.fn.closePopoversByScript = function() {
        return this.each(function() {
            var $this = $(this);
        });
    };
    $.fn.updateDynamics = function() {
        return this.each(function() {
            var $this = $(this);
            $this.find(".t-form:visible").submitForm("update-dynamics", "");
        });
    };
    var submitWhileTypingTimer;
    $(window).on("hashchange", hashchange).on("load", Twibs.updateAfterDomChange).on("popstate", function(e) {
        if (e.originalEvent.state == "twibs-reload") {
            location.reload();
        }
    }).scroll(positionFixedContainers);
    $(document).on("click", '.t-form button[type="submit"],.t-form input[type="submit"]', function(e) {
        e.stopPropagation();
        e.preventDefault();
        var $this = $(this);
        $this.closest("form").submitForm("click", $this.id, $this.attr("name"), $this.val());
    }).on("click", ".submit", function(e) {
        var $this = $(e.target);
        if (!$this.closest(".ignore-submit,.submit").hasClass("ignore-submit")) {
            e.stopPropagation();
            e.preventDefault();
            var $button = $this.closest(".submit");
            $button.submitForm("click", $button.id, $button.attr("name"), $button.attr("value"));
        }
    }).on("keyup", ".submit-while-typing", function(e) {
        clearTimeout(submitWhileTypingTimer);
        var $this = $(e.target);
        submitWhileTypingTimer = setTimeout(function() {
            $this.trigger("change");
        }, 300);
    }).on("change", ".submit-while-typing", function(e) {
        var $this = $(e.target);
        var was = $this.data("previous");
        var val = $this.val();
        if (was != val) {
            $this.data("previous", val);
            $this.closest("form").submitForm("submit-while-typing", $this.id, $this.data("name") || $this.attr("name"));
        }
    }).on("click", ".input-clear", function(e) {
        var $prev = $(e.target).prev();
        $prev.val("");
        $prev.focus();
        $prev.trigger("change");
    }).on("change", ".t-form .submit-on-change", function() {
        var $this = $(this);
        window.setTimeout(function() {
            $this.submitForm("submit-on-change", $this.id, $this.data("name") || $this.attr("name"));
        }, 1);
    }).on("click", '[data-dismiss="detachable"]', function(e) {
        e.preventDefault();
        var $parent = $(this).closest(".detachable");
        $parent.fadeOut(200, function() {
            $parent.detach();
        });
    }).on("click", function(e) {}).on("twibs-update-dom", function() {
        $('[data-toggle="popover"]').popover();
        $('[data-toggle="tooltip"]').tooltip();
        initFixedContainers();
        positionFixedContainers();
        $(".click-on-appear").removeClass(".click-on-appear").filter("[data-call]").appear(function() {
            $(this).trigger("click");
        });
    });
    $("body").on("focus", ".show-focus-on-parent", function() {
        $(this).closest(".inherit-focus-from-child").addClass("focused");
    }).on("blur", ".show-focus-on-parent", function() {
        $(this).closest(".inherit-focus-from-child").removeClass("focused");
    }).on("click", "[data-call]", function(e) {
        e.preventDefault();
        var $this = $(this);
        $this.addClass("disabled");
        $.ajax({
            url: $this.attr("data-call"),
            dataType: "script"
        }).fail(failError).done(function() {
            $this.removeClass("disabled");
            Twibs.updateAfterDomChange();
        });
    });
    $('a[data-toggle="tab"]').on("shown.bs.tab", function(e) {
        $($(e.target).attr("href")).updateDynamics();
    }).on("show.bs.tab", function(e) {
        return location.hash = $(e.target).attr("href").substr(1);
    });
    $(".t-form").initFormElements();
    function hashchange() {
        $('a[href="' + location.hash + '"]').tab("show");
    }
    if (location.hash !== "") hashchange();
    function ajaxSubmitForm($form, config) {
        var $modal = $form.find(".transfer-modal");
        var $bar = $modal.find(".progress-bar");
        var $percent = $modal.find(".transfer-percent");
        var modalTimeout;
        var disableTimeout;
        function update(percent) {
            $bar.width(percent + "%").attr("aria-valuenow", percent);
            $percent.html(percent);
        }
        var jqxhr = $form.data("jqxhr");
        if (jqxhr) jqxhr.abort();
        $form.ajaxSubmit($.extend(config, {
            dataType: "script",
            beforeSend: function() {
                update(0);
                modalTimeout = setTimeout(function() {
                    $modal.modal({
                        backdrop: "static",
                        keyboard: false
                    });
                }, 2e3);
                disableTimeout = setTimeout(function() {
                    $form.disableForm();
                }, 250);
            },
            error: function(jqxhr, status, error, $form) {
                clearTimeout(modalTimeout);
                clearTimeout(disableTimeout);
                $modal.modal("hide");
                if (error != "abort") {
                    console.log(error);
                    showAjaxError(jqxhr, error);
                    $form.reenableForm();
                }
            },
            dataFilter: function(d) {
                update(100);
                clearTimeout(modalTimeout);
                clearTimeout(disableTimeout);
                $modal.modal("hide");
                return d;
            },
            uploadProgress: function(event, position, total, percentComplete) {
                update(percentComplete);
            },
            success: function() {
                Twibs.updateAfterDomChange();
            }
        }));
    }
    function failError(jqxhr, textStatus, exception) {
        showAjaxError(jqxhr, exception.message);
    }
    function showAjaxError(jqxhr, error) {
        var regex = /<div class="twibs-message-detail">(.*)<\/div>/g;
        var arr = regex.exec(jqxhr.responseText);
        var text = arr != null ? arr[0] : error;
        if (!text) {
            text = jqxhr.statusText;
            if (text == "error") {
                if (jqxhr.status == 404) text = "Not found"; else if (jqxhr.status == 0 && jqxhr.readyState === 0) text = "Server unreachable"; else text = "Unkown error";
            }
        }
        showError(text);
    }
    function showError(content) {
        new PNotify({
            text: content,
            type: "error",
            nonblock: {
                nonblock: true
            }
        });
    }
    function initFixedContainers() {
        $(".fixed-container").not(".fixed-managed").each(function() {
            var container = $(this);
            container.addClass("fixed-managed");
            var item = $(this).find(".fixed-content");
            var width = item.width();
            var height = item.height();
            container.css("width", width);
            container.css("height", height);
            item.css("position", "absolute").css("left", "auto").css("top", "auto").css("width", width).css("height", height);
        });
    }
    function positionFixedContainers() {
        var scrollTop = $(document).scrollTop();
        $(".fixed-container.fixed-managed").each(function() {
            var container = $(this);
            var item = $(this).find(".fixed-content");
            var width = container.css("width");
            var height = container.css("height");
            var offset = container.offset();
            var fixed = item.hasClass("fixed");
            if (offset.top < scrollTop) {
                if (!fixed) {
                    item.css({
                        position: "fixed",
                        left: offset.left,
                        top: 0
                    });
                    item.addClass("fixed");
                }
            } else if (fixed) {
                item.css({
                    position: "absolute",
                    left: "auto",
                    top: "auto"
                });
                item.removeClass("fixed");
            }
        });
    }
    if (/firefox/i.test(navigator.userAgent)) {
        $(document).find(".can-be-disabled").removeAttr("disabled");
        $(document).find(".can-be-disabled-by-class").removeClass("disabled");
        $("input").not("[autocomplete]").not("[type=checkbox]").not("[type=radio]").each(function() {
            $(this).val(this.getAttribute("value") || "");
        });
        $("textarea").not("[autocomplete]").each(function() {
            $(this).val(this.innerHTML || "");
        });
        $("input[type=checkbox]").not("[autocomplete]").each(function() {
            this.checked = this.hasAttribute("checked");
        });
        $("select").not("[autocomplete]").find("option").each(function() {
            this.selected = this.hasAttribute("selected");
        });
    }
    var event = document.createEvent("Event");
    event.initEvent("twibs-loaded", true, true);
    document.dispatchEvent(event);
});
//# sourceMappingURL=twibs.js.map