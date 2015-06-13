'use strict';


/**
 * Directive to generate media input attributes
 */
var addMedia = function() {
    return {
        restrict: 'E',
        templateUrl: 'views/addmediaComponent.html',
        link: function(scope, element, iAttrs) {

            scope.addMedia = function() {#
                var etyObj = {
                    title: '',
                    link: ''
                };
                scope.formEntry.media.push(etyObj);
            };
            scope.removeMedia = function(index) {
                scope.formEntry.media.splice(index, 1);
            };
        }
    };
};


angular.module('dashboardappApp').directive('addMedia', addMedia);
