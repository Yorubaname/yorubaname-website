'use strict'

var entryController = function($scope) {
  $scope.title = "Name Entry"
};

angular.module('dashboardappApp').controller("entryController", entryController);
