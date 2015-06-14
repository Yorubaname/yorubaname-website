'use strict';


/**
 * Directive to generate etymology input attributes
 */
var etymology = function() {
    return {
        restrict: 'E',
        templateUrl: 'views/addEtymologyComponent.html',
        link: function(scope, element, iAttrs) {
            console.log(scope.formEntry.etymology)
            scope.addEtymology = function() {
                var etyObj = {
                    part: '',
                    meaning: ''
                };
                scope.formEntry.etymology.push(etyObj);
            };
            scope.removeEtymology = function(index) {
                scope.formEntry.etymology.splice(index, 1);
            };
        }
    };
};


angular.module('dashboardappApp').directive('etymology', etymology);
