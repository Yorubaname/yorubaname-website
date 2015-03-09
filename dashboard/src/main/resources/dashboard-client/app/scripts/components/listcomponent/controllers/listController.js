'use strict';

var listController = function($scope, $location, nameEntryService) {
  $scope.title = "All Names";

};

angular.module('dashboardappApp').controller("listController", listController);
