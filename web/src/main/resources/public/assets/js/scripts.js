$(document).ready(function () {

    $("#submittts").click(function() {
        var name = $("#nametts").val();
        console.log("Submitting "+name);

        $.ajax({
            url: "http://localhost:8081/v1/tts/"+name,
            method: "GET"
        }).done(function(data) {
            console.log("success ", data);
        }).fail(function(data) {
            console.log("fail ", data);
        });
    });
});
