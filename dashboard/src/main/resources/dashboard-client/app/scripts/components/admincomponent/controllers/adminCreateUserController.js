'use strict';
/**
 * Controller for parent view for the name entry views
 * @param $scope angular scope
 */
var adminCreateUserController = function($scope) {
  $scope.childTitle = "Create a new user"
};

angular.module('dashboardappApp').controller("adminCreateUserController", adminCreateUserController);
