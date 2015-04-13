'use strict';

/**
 * Service for Stubs and stored variables for service and directives
 */

var stub = function() {
    /*Generate template for form regions and subregion*/
    var local = {
        "name": "Nigeria",
        "subRegions": [{

            "name": "North West Yoruba",
            "description": "Abeokuta, Ibadan, Oyo, Ogun and Lagos (Eko) areas"
        }, {

            "name": "South East Yoruba",
            "description": "Igbomina, Yagba, Ilesa, Ife, Ekiti, Akure, Efon and Ijebu area"

        }, {

            "name": "Central Yoruba",
            "description": "Okitipupa, Ilaje, Ondo, Owo, Ikare, Sagamu and parts of Ijebu"

        }]
    }
    var foreign = {
        "name": "Diaspora",
        "subRegions": [{
            "name": "Benin"
        }, {
            "name": "Ghana"
        }]
    }
    var locationTemplate = [local, foreign]

    this.getLocationTemplate = locationTemplate

    /**
     * Parse form by convering array objects generated from inline tags to string for submission
     */
    this.joinArrays = function(obj, delimeter) {
        for (var attr in obj) {
            if (Object.prototype.toString.call(obj[attr]) == "[object Array]" && attr !== "etymology") {
                if (obj[attr].length > 0) {
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
