$(document).ready(function () {

    $("#submittts").click(function () {
        console.log("Submitting " + name);
        var name = $("#nametts").val();
        $("#nametts").val("");

        var audio = "TTS generated for <strong>" + name +"</strong><br/><audio type='audio/wav' src='" + "http://localhost:8081/v1/tts/" + name
                + "' controls><p>Your browser does not support the <code>audio</code> element </p> "
                + "</audio>";

        $("#ttsPlayer").html(audio);
    });
});
