/*
 * Copyright (C) 2013-2014 by Michael Hombre Brinkmann
 */

includeFile("bootstrap/js/affix.js");
includeFile("bootstrap/js/transition.js");
includeFile("bootstrap/js/alert.js");
includeFile("bootstrap/js/button.js");
includeFile("bootstrap/js/carousel.js");
includeFile("bootstrap/js/collapse.js");
includeFile("bootstrap/js/dropdown.js");
includeFile("bootstrap/js/modal.js");
includeFile("bootstrap/js/tooltip.js");
includeFile("bootstrap/js/popover.js");
includeFile("bootstrap/js/scrollspy.js");
includeFile("bootstrap/js/tab.js");
includeFile("bootstrap-datetimepicker.js");
includeFile("bootstrap-datetimepicker-locales/bootstrap-datetimepicker.de.js");
includeFile("jquery.pnotify.js");
includeFile("jquery.form.js");
includeFile("chosen.jquery.js");

function mailme(host, name, subject) {
    if (subject) {
        self.location = "mail" + "to:" + name + "@" + host + "?subject="
            + subject;
    } else {
        self.location = "mail" + "to:" + name + "@" + host;
    }
    return false;
}

function twibsUpdateAfterDomChange() {
    $(document).trigger("twibs-update-dom");
}

function twibsClosePopoversByScript() {
    $('[data-toggle=popover],.popover-by-script').popover('hide');
}

$(function () {
    var cp = typeof contextPath !== 'undefined' ? contextPath : '';

    $.fn.ckeditor = function (callback, config) {
        $('<script src="' + cp + '/inc/ckeditor/ckeditor.js"></script><script src="' + cp + '/inc/ckeditor/adapters/jquery.js"></script>').appendTo('head');
        $(this).ckeditor(callback, config);
    };

    $.fn.reloadForm = function (value) {
        $(this).submitForm("form-change", value, true);
    };

    $.fn.submitForm = function (key, value, enabledForm) {
        var data = {};
        data[key] = value;
        var config = {data: data};
        if (!enabledForm) {
            config["beforeSubmit"] = disableForm;
        }
        $(this).each(function () {
            var $form = $(this).closest('form');
            $form.ajaxSubmit(ajaxSubmitConfig($form, config));
        });
        return this;
    };

    $.fn.twibsModal = function () {
        return this.each(function () {
            var $this = $(this);
            $this
                .on('hidden.bs.modal', function () {
                    $this.remove();
                    $('body').updateDynamics();
                })
                .on('shown.bs.modal', function () {
                    $this.find('input:visible,.btn:visible').first().focus();
                    twibsUpdateAfterDomChange();
                }).modal();
        })
    };

    $.fn.updateDynamics = function () {
        return this.each(function () {
            $(this).find(".twibs-form:visible").submitForm("form-reload", "")
            twibsClosePopoversByScript();
        })
    };

    var submitWhileTypingTimer;

    $(window)
        .on('hashchange', hashchange);

    $(document)
        .on('click', 'button[type="submit"]', function (e) {
            var $this = $(e.target);
            $this.closest('form').submitForm($this.attr('name'), $this.val(), $this.hasClass("enabled-form"));
            e.preventDefault();
        })
        .on('click', '.submit', function (e) {
            var $this = $(e.target);
            if (!$this.closest(".ignore-submit,.submit").hasClass("ignore-submit")) {
                var $button = $this.closest(".submit");
                $button.submitForm($button.attr('name'), $button.attr('value'), $this.hasClass("enabled-form"));
                e.stopPropagation();
                e.preventDefault();
            }
        })
        .on('keyup', '.submit-while-typing', function (e) {
            clearTimeout(submitWhileTypingTimer);
            var $this = $(e.target);
            submitWhileTypingTimer = setTimeout(function () {
                $this.trigger("change")
            }, 300);
        })
        .on('change', '.submit-while-typing', function (e) {
            var $this = $(e.target);
            var was = $this.data("previous");
            var val = $this.val();
            if (was != val) {
                $this.data("previous", val);
                $this.closest('form').submitForm($this.attr('name') + "-submit-while-typing", val, true);
            }
        })
        .on('click', '.input-clear', function (e) {
            var $prev = $(e.target).prev();
            $prev.val("");
            $prev.focus();
            $prev.trigger("change");
        })
        .on('change', 'form.twibs-form .submit-on-change', function () {
            var $this = $(this);
            $this.reloadForm($this.attr('name'));
        })
        .on('click', '[data-dismiss="detachable"]', function (e) {
            e.preventDefault();
            var $parent = $(this).closest(".detachable");
            $parent.fadeOut(200, function () {
                $parent.detach();
            });
        })
        .on("click", function (e) {
            var $target = $(e.target);
            if ($target.closest('.popover').length == 0) {
                $('[data-toggle=popover],.popover-by-script').not($target.closest('[data-toggle=popover]')).popover('hide');
                // Bootstrap 3.0.2 does not detach the popup on hide (wait until fade out is done).
                setTimeout(function () {
                    $('.popover').not(".in").detach()
                }, 400)
            }
        })
        .on("twibs-update-dom", function () {
            $('.twibs-form select.chosen').chosen({disable_search_threshold: 6, width: '100%'});

            // 'chosen' does not preserve the focus, 'twibs' does.
            $('.twibs-form select.chosen:focus').each(function () {
                $(this).parent().find(".chosen-container .chosen-drop .chosen-search input[type='text']").focus();
            });

            $('[data-toggle="popover"]').popover();

            $('textarea.hidden-print').each(function () {
                var $this = $(this);
                var $next = $this.next("div.textarea-print");
                if ($next.length === 0) {
                    $next = $('<div class="textarea-print form-control visible-print"></div>').insertBefore($this);
                }
                $next.html($this.val());
            });

            fixContainers();
        });

    $('body')
        .on("focus", ".show-focus-on-parent", function (e) {
            $(this).closest(".inherit-focus-from-child").addClass("focused");
        })
        .on("blur", ".show-focus-on-parent", function (e) {
            $(this).closest(".inherit-focus-from-child").removeClass("focused");
        })
        .on("click", '[data-call]', function (e) {
            e.preventDefault();
            $.ajax({
                url: $(this).attr('data-call'),
                dataType: "script"
            }).fail(failError);
        });

    $('a[data-toggle="tab"]')
        .on('shown.bs.tab', function (e) {
            $($(e.target).attr('href')).updateDynamics();
        })
        .on('show.bs.tab', function (e) {
            return location.hash = $(e.target).attr('href').substr(1);
        });

    var failError = function (jqxhr, settings, exception) {
        var regex = /<div class="twibs-message-detail">(.*)<\/div>/g;
        var arr = regex.exec(jqxhr.responseText);
        var text = arr != null ? arr[0] : exception.message;
        if (!text) {
            text = jqxhr.statusText;
            if (text == "error" && jqxhr.statusCode == 404) {
                text = "Not found";
            }
        }
        showError(text);
    };

    function hashchange() {
        $('a[href="' + location.hash + '"]').tab('show');
    }

    if (location.hash !== '') hashchange();

    function ajaxSubmitConfig($form, config) {
        var $modal = $form.find(".transfer-modal");
        var $bar = $modal.find('.progress-bar');
        var $percent = $modal.find('.transfer-percent');
        var modalTimeout;

        function update(percent) {
            $bar.width(percent + '%');
            $percent.html(percent);
        }

        return $.extend(config, {
            dataType: 'script',
            error: function (xhr, status, error, $form) {
                clearTimeout(modalTimeout);
                ajaxFormError(xhr, status, error, $form);
            },
            beforeSend: function () {
                update(0);
                modalTimeout = setTimeout(function () {
                    $modal.modal({backdrop: 'static', keyboard: false});
                }, 1000);
            },
            uploadProgress: function (event, position, total, percentComplete) {
                update(percentComplete);
            },
            success: function () {
                update(100);
                clearTimeout(modalTimeout);
                $modal.modal("hide");
                twibsUpdateAfterDomChange();
            }
        });
    }

    function ajaxFormError(xhr, status, error, $form) {
        var regex = /<div class="twibs-message-detail">(.*)<\/div>/g;
        var arr = regex.exec(xhr.responseText);
        var text = arr != null ? arr[0] : error;
        showError(text);
    }

    function showError(content) {
        $.pnotify({
            text: content,
            type: 'danger',
            nonblock: true,
            nonblock_opacity: 0.2
        });
    }

    function disableForm(arr, $form) {
        $form.find('.can-be-disabled').attr('disabled', 'disabled');
        $form.find('.can-be-disabled-by-class').addClass('disabled');
        $form.find('select.form-control').trigger("chosen:updated");
    }

    function fixContainers() {
        $(".fixed-container").each(function () {
            var container = $(this);
            container.removeClass("fixed-container");
            var item = $(this).find(".fixed-content");
            var width = item.width();
            var height = item.height();

            container.css("width", width);
            container.css("height", height);
            item.css("position", "absolute").css("left", "auto").css("top", "auto").css("width", width).css("height", height);
            $(window).scroll(function () {
                var offset = container.offset();
                var scrollTop = $(document).scrollTop();
                var fixed = item.hasClass("fixed");
                if (offset.top < scrollTop) {
                    if (!fixed) {
                        item.css({position: "fixed", left: offset.left, top: 0});
                        item.addClass("fixed");
                    }
                } else if (fixed) {
                    item.css({position: "absolute", left: "auto", top: "auto"});
                    item.removeClass("fixed");
                }
            });
        });
    }

    // Fix autofill in firefox: Reset disabled state after load
    // (firefox disables fields after ajax submit and page reload).
    // Either "disabled" or "can-be-disabled" can be given, not both.
    if (/firefox/i.test(navigator.userAgent)) {
        $(document).find('.can-be-disabled').removeAttr('disabled');
        $(document).find('.can-be-disabled-by-class').removeClass('disabled');
        // And restore the values from html. Do not use "autocomplete="off" in html to preserve autocomplete feature
        $('input').not('[autocomplete]').not('[type=checkbox]').not('[type=radio]').each(function () {
            $(this).val(this.getAttribute("value") || '');
        });
        $('textarea').not('[autocomplete]').each(function () {
            $(this).val(this.innerHTML || '');
        });
        $('input[type=checkbox]').not('[autocomplete]').each(function () {
            this.checked = this.hasAttribute("checked");
        });
        $("select").not('[autocomplete]').find("option").each(function () {
            this.selected = this.hasAttribute("selected");
        });
    }

    // Store a reference to the original remove method.
    var originalCkeditor = $.fn.ckeditor;

    // Define overriding method.
    jQuery.fn.remove = function () {
        // Log the fact that we are calling our override.
        console.log("Override method");

        // Execute the original method.
        originalRemoveMethod.apply(this, arguments);
    }

    twibsUpdateAfterDomChange();
})
;
