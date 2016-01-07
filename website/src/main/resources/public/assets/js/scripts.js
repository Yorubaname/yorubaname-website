var alert_error = function (error) {
    return '<div class="alert alert-danger alert-dismissible" role="alert">' +
            '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>'
            +
            error +
            '</div>';
}

var alert_success = function (message) {
    return '<div class="alert alert-success alert-dismissible" role="alert">' +
            '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>'
            +
            message +
            '</div>';
}

$(document).ready(function () {

    $("#tts-button").on("click", function () {
        var host = $("#host").html();
        var audio = new Audio("http://" + host + ":8081/v1/tts/" + $("#name-entry").html());
        audio.play();
    });

    // submit name feedback form
    $('form[name="name_feedback"]').on('submit', function (e) {
        e.preventDefault()
        return $.ajax({
            url: e.currentTarget.action,
            method: e.currentTarget.method,
            contentType: 'application/json',
            data: JSON.stringify({feedback: $('textarea[name="feedback"]').val()}),
            type: 'json',
            success: function (resp) {
                $('.response').html(alert_success("Feecback posted successfully. Thanks."))
                $('.response').fadeIn()
            },
            error: function (jqXHR) {
                $('.response').html(alert_error(jqXHR.responseJSON || jqXHR.responseText))
                $('.response').fadeIn()
            }
        })
    })
});

(function ($) {
    $.include = function (url) {
        $.ajax({
            url: url,
            async: false,
            success: function (result) {
                document.write(result);
            }
        });
    };
}(jQuery));

jQuery(function ($) {
    if ($(window).width() > 769) {
        $('.navbar .dropdown').hover(function () {
            $(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();

        }, function () {
            $(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();

        });

        $('.navbar .dropdown > a').click(function () {
            location.href = this.href;
        });

        <!-- initialize keyboard (required) -->
        $(function () {
            $('#keyboard').keyboard({
                openOn: null,
                stayOpen: false,
                layout: 'custom',
                customLayout: {
                    'normal': [
                        'á à é ẹ́ è ẹ̀ í ì',
                        'ó ò ọ́  ọ̀ ṣ ú ù',
                    ],
                    'shift': [
                        'Ň W Ĕ R T Ž Ú Å S D Í Ò',
                        'Ý J Ŵ P Ț X Ç V Õ',
                    ]
                },
                repeatRate: 0
            });

//--------------------------------------------------------------------------------//
            $('#keyboardp').click(function () {
                var kb = $('#keyboard').getkeyboard();
                // close the keyboard if the keyboard is visible and the button is clicked a second time
                if (kb.isOpen) {
                    kb.close();
                } else {
                    kb.reveal();
                }
            });
// since IE adds an overlay behind the input to prevent clicking in other inputs (the keyboard may not automatically open on focus... silly IE bug)
// We can remove the overlay (transparent) if desired using this code:
            $('#keyboard').bind('visible', function (e, keyboard, el) {
                $('.ui-keyboard-overlay').remove(); // remove overlay because clicking on it will close the keyboard... we set "openOn" to null to prevent closing.
            });

        });

        <!-- initialize mini-keyboard (required) -->
        $(function () {
            $('#miniKeyboard').keyboard({
                openOn: null,
                stayOpen: false,
                layout: 'custom',
                customLayout: {
                    'normal': [
                        'á à é ẹ́ è ẹ̀ í ì',
                        'ó ò ọ́ ọ̀ ṣ ú ù',
                    ],
                    'shift': [
                        'Ň W Ĕ R T Ž Ú Å S D Í Ò',
                        'Ý J Ŵ P Ț X Ç V Õ',
                    ]
                },
                repeatRate: 0
            });

//--------------------------------------------------------------------------------//
            $('#miniKeyboardp').click(function () {
                var kb = $('#miniKeyboard').getkeyboard();
                // close the keyboard if the keyboard is visible and the button is clicked a second time
                if (kb.isOpen) {
                    kb.close();
                } else {
                    kb.reveal();
                }
            });
// since IE adds an overlay behind the input to prevent clicking in other inputs (the keyboard may not automatically open on focus... silly IE bug)
// We can remove the overlay (transparent) if desired using this code:
            $('#miniKeyboard').bind('visible', function (e, keyboard, el) {
                $('.ui-keyboard-overlay').remove(); // remove overlay because clicking on it will close the keyboard... we set "openOn" to null to prevent closing.
            });

        });

    }
});

<!-- Enable all the tooltip -->
$(document).ready(function () {
    $('[data-toggle="tooltip"]').tooltip();
});

<!-- Background image -->
$(function () {
    var images = ['0086.jpg', '0089.jpg'];
    $('#home-banner').css({
        'background-image': 'url(assets/img/bg/IMG_' + images[Math.floor(Math.random() * images.length)] + ')'
    });
});

<!-- Typeahead -->
$(document).ready(function () {

    var names = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.whitespace,
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        remote: {
            url: '/v1/search/autocomplete?q=%QUERY',
            wildcard: '%QUERY'
        }
    });

    $('#search-tph .th').typeahead({
                hint: true,
                highlight: true,
                minLength: 1
            },
            {
                name: 'searchname',
                source: names
            });
});

<!-- Triggers the search -->
$('.btn-search').on("click", function (event) {
    event.preventDefault();
    var q = $('#keyboard').val() || "";
    var hostOrigin = window.location.origin;
    window.location.href = hostOrigin + '/entries/?q=' + q;
})

$(function () {

    if ($(document).innerWidth() >= 768) {

        $('.share a.btn-social').on('click', function (ev) {
            ev.preventDefault()
            if (/(facebook|twitter)/.test(ev.currentTarget.href)) {
                var c = 575,
                        d = 520,
                        e = ($(window).width() - c) / 2,
                        f = ($(window).height() - d) / 2,
                        g = "status=1,width=" + c + ",height=" + d + ",top=" + f + ",left=" + e;
                window.open(ev.currentTarget.href, "Share Yoruba Names", g)
            } else {
                window.open(ev.currentTarget.href);
            }
        })
    }

});

// puts latest searches, latest addition and most popular in local storage
$(document).ready(function () {

    var searches = [];
    var additions = [];
    var popular = [];

    $("#recent_searches li.recent_entry").each(function () {
        if ($(this).text() !== "") {
            searches.push($(this).text());
        }
    });

    $("#recent_additions li.recent_entry").each(function () {
        if ($(this).text() !== "") {
            additions.push($(this).text());
        }
    });

    $("#recent_popular li.recent_entry").each(function () {
        if ($(this).text() !== "") {
            popular.push($(this).text());
            console.log($(this).text(), 123);
        }
    });

    if (searches && searches.length !== 0) {
        localStorage.setItem("searches", JSON.stringify(searches));
    }
    if (additions && additions.length !== 0) {
        localStorage.setItem("additions", JSON.stringify(additions));
    }
    if (popular && popular.length !== 0) {
        localStorage.setItem("popular", JSON.stringify(popular));
    }

});

// used by side bar to show popular names
$(document).ready(function () {

    var $ul = $("ul#side_popular");

    var item = JSON.parse(localStorage.getItem("popular") || '[]');
    item.forEach(function(i) {
        $ul.append("<li><a href='/entries/"+i+"'>" + i + "</a></li>");
    });

});

// set style for current alphabet whose entry is being displayed
$(document).ready(function() {
    var alphabet = location.pathname.split("/").pop();
    if ($(".alphabets").length !== 0 && alphabet && alphabet.length === 1) {

        $("ul.alphabets li").filter(function() {
            return $(this).text() === alphabet;
        }).css( {"background-color":"#D3A463", "font-weight": "bold"});
    }

});

