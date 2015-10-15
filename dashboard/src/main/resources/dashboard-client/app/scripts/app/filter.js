'use strict';

dashboardappApp

/**
 * Filter to parse delimated string to arrays
 */

  .filter('sToArrays', function() {
    return function(text, delimeter) {
      if (!text || !delimeter) return text;
      return text.split(delimeter)
    }
  })

/**
 * Filter to parse input value arrays to single inline strings
 */

  .filter('aToString', function() {
    return function(inputArray, delimeter) {
      var val = "";
      for (var i = inputArray.length - 1; i >= 0; i--) {
            val += inputArray[i].text
            if (i != 0) {
                val += delimeter
            }
      }
      return val
    }
  })