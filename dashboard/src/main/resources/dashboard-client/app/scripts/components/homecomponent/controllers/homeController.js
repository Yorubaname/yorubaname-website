'use strict';

/**
 * Controller for the home/dashboard view
 * @param $scope angular scope
 * @param profileService the service use to fetch profile information
 */
var homeController = function($scope, profileService, $cookies) {

    $scope.title = "Dictionary Dashboard";
    $scope.profile = {
        username: $cookies.username,
        role: $cookies.role
    }
};

angular.module('dashboardappApp').controller("homeController", homeController);
