'use strict';

/**
 * Filter to parse delimated string to arrays
 */

var sToArrays = function() {
    return function(text, delimeter) {
        if (!text || !delimeter) return text;
        return text.split(delimeter)
    }

};

angular.module('dashboardappApp').filter('sToArrays', sToArrays);
