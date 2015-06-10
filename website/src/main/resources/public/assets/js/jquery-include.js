/**
 * jquery-include
 *
 * Copyright (C) 2015 Gfactor <amir_saniyan@yahoo.com>
 */
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
