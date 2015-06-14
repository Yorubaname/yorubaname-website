'use strict';

/**
 * Controller for the parent view for the listing of names views (all names, editing names etc)
 * @param $scope angular's scope
 * @param $location angular's location service
 * @param nameEntryService
 */
var listController = function($scope, $location) {
    $scope.title = "All Names";
};

angular.module('dashboardappApp').controller("listController", listController);
