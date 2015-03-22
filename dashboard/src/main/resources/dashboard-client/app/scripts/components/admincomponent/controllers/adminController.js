'use strict';
/**
 * Controller for parent view for the name entry views
 * @param $scope angular scope
 */
var adminController = function($scope) {
  $scope.title = "Admin Dashboard"
  if (!$scope.isAdmin) {
    window.location.href = "#/home";
  }
};

angular.module('dashboardappApp').controller("adminController", adminController);
