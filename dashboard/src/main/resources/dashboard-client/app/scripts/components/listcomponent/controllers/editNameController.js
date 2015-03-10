'use strict';

/**
 * Controller for the editing of a name entry view
 * @param $scope angular scope
 */
var editNameController = function($scope) {
  $scope.title = "Edit Name";
  $scope.childTitle = "Edit the entry";

};

angular.module('dashboardappApp').controller("editNameController", editNameController);
