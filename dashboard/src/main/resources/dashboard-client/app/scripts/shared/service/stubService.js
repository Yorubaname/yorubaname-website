'use strict';

/**
 * Service for Stubs and stored variables for service and directives
 */

var stub = function() {
    /**
     * Parse form by convering array objects generated from inline tags to string for submission
     */
    this.arraysToString = function(obj, delimeter) {
        for (var attr in obj) {
            if (Object.prototype.toString.call(obj[attr]) == "[object Array]") {
                if (!isEmptyObj(obj[attr])) {
                    var objArray = obj[attr]
                    obj[attr] = []
                    for (var i = objArray.length - 1; i >= 0; i--) {
                        obj[attr][i] = objArray[i].text
                    };
                    obj[attr] = obj[attr].join(delimeter)
                } else {
                    obj[attr] = ""
                }
            }
        }
        return obj
    }

};

angular.module('dashboardappApp').service('stubService', stub);
