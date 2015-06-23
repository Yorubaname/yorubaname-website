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

jQuery(function($) {
    if($(window).width()>769){
        $('.navbar .dropdown').hover(function() {
            $(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();

        }, function() {
            $(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();

        });

        $('.navbar .dropdown > a').click(function(){
            location.href = this.href;
        });

    }
});

<!-- initialize keyboard (required) -->
$(function(){
	$('#keyboard').keyboard({
		openOn   : null,
		stayOpen : false,
		layout: 'custom',
		customLayout: {
			'normal': [
				'á à é ẹ́ è ẹ̀ í ì',
				'ó ò ọ́  ọ̀ ú ù',
			],
			'shift': [
				'Ň W Ĕ R T Ž Ú Å S D Í Ò',
				'Ý J Ŵ P Ț X Ç V Õ',
			]
		},
		repeatRate : 0
	});

//--------------------------------------------------------------------------------//
$('#keyboardp').click(function(){
	var kb = $('#keyboard').getkeyboard();
	// close the keyboard if the keyboard is visible and the button is clicked a second time
	if ( kb.isOpen ) {
		kb.close();
	} else {
		kb.reveal();
	}
});
// since IE adds an overlay behind the input to prevent clicking in other inputs (the keyboard may not automatically open on focus... silly IE bug)
// We can remove the overlay (transparent) if desired using this code:
$('#keyboard').bind('visible', function(e, keyboard, el){
 $('.ui-keyboard-overlay').remove(); // remove overlay because clicking on it will close the keyboard... we set "openOn" to null to prevent closing.
});

});

<!-- Enable all the tooltip -->
$(document).ready(function(){
    $('[data-toggle="tooltip"]').tooltip();
});

<!-- Background image -->
 $(function() {
  var images = ['pic1.jpg', 'pic2.jpg', 'pic3.jpg', 'pic4.jpg', 'pic5.jpg', 'pic6.jpg', 'pic7.jpg', 'pic8.jpg', 'pic9.jpg',];
  $('#home-banner').css({'background-image': 'url(assets/img/backgrounds/' + images[Math.floor(Math.random() * images.length)] + ')'});
 });
 
<!-- Typeahead -->
$(document).ready(function(){
var substringMatcher = function(strs) {
  return function findMatches(q, cb) {
    var matches, substringRegex;
 
    // an array that will be populated with substring matches
    matches = [];
 
    // regex used to determine if a string contains the substring `q`
    substrRegex = new RegExp(q, 'i');
 
    // iterate through the pool of strings and for any string that
    // contains the substring `q`, add it to the `matches` array
    $.each(strs, function(i, str) {
      if (substrRegex.test(str)) {
        matches.push(str);
      }
    });
 
    cb(matches);
  };
};
 
var names = ['Olubunmi', 'Otondo', 'Akinwunmi', 'Akerele', 'Kola',
  'Kunle', 'Gbogije', 'Taiwo', 'Abiola', 'Omoishe', 'Laila',
  'Tola', 'Tolulope', 'Bolanle', 'Yoruba', 'Ife', 'Ifeoluwa', 'Shalewa',
  'Shola', 'Eniola', 'Dadepo', 'Koko', 'Jagada'
];
 
$('#search-tph .th').typeahead({
  hint: true,
  highlight: true,
  minLength: 1
},
{
  name: 'searchname',
  source: substringMatcher(names)
}); 
});
