(function ($) {
  $.include = function (url) {
    $.ajax({
      url: url,
      async: false,
      success: function (result) {
        document.write(result)
      }
    })
  }
}(jQuery))




/* Execute Callbacks on DomReady */

$(function(){

  <!-- Enable all the tooltip -->
  $('[data-toggle="tooltip"]').tooltip();

  <!-- Triggers the search -->
  $('.btn-search').on("click", function (event) {
    event.preventDefault();
    var q = $('#keyboard').val() || ""
    var hostOrigin = window.location.origin
    window.location.href = hostOrigin + '/entries/?q=' + q
  })



  /* On large devices only */
  if ($(window).width() > 769) {

    <!-- initialize keyboard & miniKeyboard (required) -->
    $('#keyboard,#miniKeyboard').keyboard({
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
    })

    //--------------------------------------------------------------------------------//
    $('#keyboardp,#miniKeyboardp').click(function () {
      var kb = $('#keyboard,#miniKeyboard').getkeyboard();
      // close the keyboard if the keyboard is visible and the button is clicked a second time
      kb.isOpen ? kb.close() : kb.reveal()
    })

    // since IE adds an overlay behind the input to prevent clicking in other inputs (the keyboard may not automatically open on focus... silly IE bug)
    // We can remove the overlay (transparent) if desired using this code:
    $('#keyboard,#miniKeyboard').bind('visible', function (e, keyboard, el) {
      $('.ui-keyboard-overlay').remove() // remove overlay because clicking on it will close the keyboard... we set "openOn" to null to prevent closing.
    })



    $('.navbar .dropdown').hover(function () {
      $(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown()
    }, function () {
      $(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp()
    })

    $('.navbar .dropdown > a').click(function () {
      location.href = this.href
    })

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
        window.open(ev.currentTarget.href)
      }
    })

  }


  /* Play Audio Sound */
  $("#tts-button").on("click", function () {
    var host = $("#host").html(),
      audio = new Audio("http://" + host + "/v1/tts/" + $("#name-entry").html());

    audio.play()
  })


  var alert_error = function (error) {
    return '<div class="alert alert-danger alert-dismissible" role="alert">' +
      '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>'
      + error + '</div>'
  }

  var alert_success = function (message) {
    return '<div class="alert alert-success alert-dismissible" role="alert">' +
      '<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>'
      + message + '</div>'
  }


  /* Submit Name Feedback */
  $('form[name="name_feedback"]').on('submit', function (e) {
    e.preventDefault()
    return $.ajax({
      url: e.currentTarget.action,
      method: e.currentTarget.method,
      contentType: 'application/json',
      data: JSON.stringify({name: $('#nameToFeedback').val(), feedback: $('textarea[name="feedback"]').val()}),
      type: 'json',
      success: function (resp) {
        e.currentTarget.reset()
        $('.response').html(alert_success("Feedback posted successfully. Thanks.")).fadeIn()
        setTimeout(function(){
          $('#improveEntryModal').modal('close')
        }, 1000)
      },
      error: function (jqXHR) {
        $('.response').html(alert_error(jqXHR.responseJSON.message || jqXHR.responseText)).fadeIn()
      }
    })
  })



  $(function () {

    <!-- Typeahead -->

    var names = new Bloodhound({
      datumTokenizer: Bloodhound.tokenizers.whitespace,
      queryTokenizer: Bloodhound.tokenizers.whitespace,
      remote: {
        url: '/v1/search/autocomplete?q=%QUERY',
        wildcard: '%QUERY'
      }
    })

    $('#search-tph .th').typeahead({
        hint: true,
        highlight: true,
        minLength: 1
      },
      {
        name: 'searchname',
        source: names
      }
    )

  })


  // puts latest searches, latest addition and most popular in local storage
  $(function () {

    var searches, additions, popular;

    searches = additions = popular = [];

    $("#recent_searches li.recent_entry").each(function () {
      if ($(this).text() !== "") {
        searches.push($(this).text())
      }
    })

    $("#recent_additions li.recent_entry").each(function () {
      if ($(this).text() !== "") {
        additions.push($(this).text())
      }
    })

    $("#recent_popular li.recent_entry").each(function () {
      if ($(this).text() !== "") {
        popular.push($(this).text())
      }
    })

    if (searches && searches.length !== 0) {
      localStorage.setItem("searches", JSON.stringify(searches))
    }

    if (additions && additions.length !== 0) {
      localStorage.setItem("additions", JSON.stringify(additions))
    }

    if (popular && popular.length !== 0) {
      localStorage.setItem("popular", JSON.stringify(popular))
    }

  })



  // used by side bar to show popular names
  $(function () {

    var $ul = $("ul#side_popular"),
      item = JSON.parse(localStorage.getItem("popular") || '[]');

    item.forEach(function (i) {
      $ul.append("<li><a href='/entries/" + i + "'>" + i + "</a></li>")
    })

  })



  // set style for current alphabet whose entry is being displayed
  $(function () {
    var alphabet = location.pathname.split("/").pop();

    if ($(".alphabets").length !== 0 && alphabet && alphabet.length === 1) {

      $("ul.alphabets li").filter(function () {
        return $(this).text() === alphabet;
      }).css({"background-color": "#D3A463", "font-weight": "bold"})

    }

  })

  /* Submit Name callbacks */
  $(function(){

    // Add GeoLocation Tags Input
    $("select[multiple]").multipleSelect()

    $('#suggestedName').blur(function () {

      var name = $(this).val()

      $.ajax({
        url: '/v1/names/' + name.toLowerCase(),
        type: 'GET',
        contentType: "application/json",
      }).success(function (response) {
        disableSending()
      }).error(function () {
        enableSending()
      })

      var enableSending = function () {
        // update link in error message
        $("#view-entry").attr("href", "")
        // hide error message
        $("#error-msg").hide()
        // enable submit button
        $("#submit-name").prop("disabled", false)
      }

      var disableSending = function () {
        // update link in error message
        $("#view-entry").attr("href", "/entries/" + name)
        // show error message
        $("#error-msg").show()
        // disable submit button
        $("#submit-name").prop("disabled", true)
      }

    })

    $('form#suggest-form').on('submit', function (event) {
      event.preventDefault()

      var suggestedName = {
        name: $('form#suggest-form #miniKeyboard').val(),
        details: $('form#suggest-form #suggestedMeaning').val(),
        geoLocation: getGeoLocations(),
        email: $('form#suggest-form #suggestedEmail').val()
      }

      function getGeoLocations() {
        var geoLocations = [];
        var rawValue = $("form#suggest-form select[multiple]").val();
        for (geoEntry in rawValue) {
          var geoLocation = {};
          var splitEntry = rawValue[geoEntry].split(".");
          geoLocation.region = splitEntry[0]
          geoLocation.place = splitEntry[1]
          geoLocations.push(geoLocation)
        }
        return geoLocations
      }

      $.ajax({
        url: '/v1/suggestions',
        type: 'POST',
        contentType: "application/json",
        data: JSON.stringify(suggestedName),
        dataType: 'json'
      }).done(function () {
        $('form#suggest-form').trigger("reset")
      }).success(function () {
        $('.response').html(alert_success("Name was submitted successfully. Thank you.")).fadeIn()
      }).fail(function (jqXHR) {
        $('.response').html(alert_error(jqXHR.responseJSON.message || jqXHR.responseText)).fadeIn()
      })

    })

  })

})