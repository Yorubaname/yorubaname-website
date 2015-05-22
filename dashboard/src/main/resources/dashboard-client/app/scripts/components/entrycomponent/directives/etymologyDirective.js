'use strict';


/**
 * Directive to generate etymology input attributes
 */
var etymology = function() {
    return {
        restrict: 'E',
        templateUrl: '/scripts/components/entrycomponent/views/addEtymologyComponent.html',
        link: function(scope, element, iAttrs) {
            var etyObj = {
                part: '',
                meaning: ''
            };
            scope.addEtymology = function() {
                scope.formEntry.etymology.push(etyObj);
            };
            scope.removeEtymology = function(index) {
                scope.formEntry.etymology.splice(index, 1);
            };
        }
    };
};


angular.module('dashboardappApp').directive('etymology', etymology);
