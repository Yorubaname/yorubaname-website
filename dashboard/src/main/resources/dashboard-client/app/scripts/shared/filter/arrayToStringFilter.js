'use strict';

/**
 * Filter to parse input value arrays to single inline strings
 */

var aToString = function() {
    return function(inputArray, delimeter) {
        var val = ""
        for (var i = inputArray.length - 1; i >= 0; i--) {
            val += inputArray[i].text
            if (i != 0) {
                val += delimeter
            };
        };
        return val
    }
}


angular.module('dashboardappApp').filter('aToString', aToString);
