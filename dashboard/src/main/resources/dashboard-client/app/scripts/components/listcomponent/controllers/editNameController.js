'use strict';

var editNameController = function($scope, $location, nameEntryService) {
  $scope.title = "Edit Name";
  $scope.childTitle = "Edit the entry";

};

angular.module('dashboardappApp').controller("editNameController", editNameController);
