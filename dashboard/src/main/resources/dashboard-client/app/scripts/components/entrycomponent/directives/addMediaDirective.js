'use strict';


/**
 * Directive to get the files in a form into $scope
 * @param $parse
 * @returns {{restrict: string, link: Function}}
 */
var addMedia = function($parse) {
    return {
        restrict: 'E',
        template: '<div class="col-sm-12"><label for="mediaEntry" class="control-label">Media</label><input class="form-control" id="mediaEntry" placeholder="Title" name="media" ng-model="formEntry.media"><br><input class="form-control" id="mediaEntry" placeholder="Link" name="media" ng-model="formEntry.media"></div>'
    };
};



angular.module('dashboardappApp').directive('addMedia', addMedia);
