$(document).ready(function () {

 // Add GeoLocation Tags Input 
    $("select[multiple]").multipleSelect();

 $('#suggestedName').blur(function() {
     var name = ($(this).val());

     $.ajax({
         url: '/v1/names/' + name.toLowerCase(),
         type: 'GET',
         contentType: "application/json",
     }).success(function(response) {
         disableSending()
     }).error(function() {
         enableSending();
     });


     var enableSending = function() {
         // update link in error message
         $("#view-entry").attr("href", "");
         // hide error message
         $("#error-msg").hide();
         // enable submit button
         $("#submit-name").prop("disabled", false);

     };

     var disableSending = function() {
         // update link in error message
         $("#view-entry").attr("href", "/entries/"+name);
         // show error message
         $("#error-msg").show();
         // disable submit button
         $("#submit-name").prop("disabled", true);
     };

 });

$('form#suggest-form').on('submit', function(event) {
    event.preventDefault();

    var suggestedName = {
        name: $('form#suggest-form #miniKeyboard').val(),
        details: $('form#suggest-form #suggestedMeaning').val(),
        geoLocation: getGeoLocations(),
        email: $('form#suggest-form #suggestedEmail').val()
    };

    function getGeoLocations() {
        var geoLocations = [];
        var geoLocation = {}
        var rawValue = $("form#suggest-form select[multiple]").val();
        for (geoEntry in rawValue) {
            var splitEntry = rawValue[geoEntry].split(".");
            geoLocation.region = splitEntry[0];
            geoLocation.place = splitEntry[1];
            geoLocations.push(geoLocation);
        }
        return geoLocations;
    }

    $.ajax({
        url: '/v1/suggestions',
        type: 'POST',
        contentType: "application/json",
        data: JSON.stringify(suggestedName),
        dataType: 'json'
    }).done(function() {
        $('form#suggest-form').trigger("reset")
    }).success(function() {
        $('.response').html(alert_success("Name was submitted successfully. Thank you."))
        $('.response').fadeIn()  
    }).fail(function(jqXHR) {
        $('.response').html(alert_error(jqXHR.responseJSON || jqXHR.responseText))
        $('.response').fadeIn()
    })
})

});