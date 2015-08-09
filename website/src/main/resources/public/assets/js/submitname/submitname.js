$(document).ready(function () {

$('form#suggest-form').on('submit', function(event) {
    event.preventDefault();

    var suggestedName = {
        name: $('form#suggest-form #suggestedName').val(),
        details: $('form#suggest-form #suggestedMeaning').val(),
        geoLocation: {
            place: $('form#suggest-form #suggestedGeoLocation').val()
        },
        email: $('form#suggest-form #suggestedEmail').val()
    };

    $.ajax({
        url: '/v1/suggest/name',
        type: 'POST',
        contentType: "application/json",
        data: JSON.stringify(suggestedName),
        dataType: 'json'
    }).done(function() {
        $('form#suggest-form').trigger("reset");
    }).success(function() {
        // TODO add ui for feedback
        console.log("name successfully submitted");
    }).fail(function() {
        // TODO add ui for feedback
        console.log("Error occured while submitting name");
    });
})

});