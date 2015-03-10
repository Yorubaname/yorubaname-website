'use strict';
/**
 * Controller for parent view for the name entry views
 * @param $scope angular scope
 */
var entryController = function($scope) {
  $scope.title = "Name Entry Form"
};

angular.module('dashboardappApp').controller("entryController", entryController);
