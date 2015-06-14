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
        // for (var attr in obj) {
        //     if (Object.prototype.toString.call(obj[attr]) == "[object Array]") {
        //         if (!isEmptyObj(obj[attr])) {
        //             var objArray = obj[attr]
        //             obj[attr] = []
        //             for (var i = objArray.length - 1; i >= 0; i--) {
        //                 obj[attr][i] = objArray[i].text
        //             };
        //             obj[attr] = obj[attr].join(delimeter)
        //         } else {
        //             obj[attr] = ""
        //         }
        //     }
        // }
        console.log(val)
        return val
    }
}


angular.module('dashboardappApp').filter('aToString', aToString);
