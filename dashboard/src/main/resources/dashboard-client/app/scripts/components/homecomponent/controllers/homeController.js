'use strict'

var homeController = function($scope) {
  $scope.title = "All entries"
};

angular.module('dashboardappApp').controller("homeController", homeController);
