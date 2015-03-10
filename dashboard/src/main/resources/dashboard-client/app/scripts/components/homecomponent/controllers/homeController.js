'use strict';

/**
 * Controller for the home/dashboard view
 * @param $scope angular scope
 * @param profileService the service use to fetch profile information
 */
var homeController = function($scope, profileService) {

  $scope.title = "Dictionary Dashboard";
  var profileInfo = profileService.getProfileInfo();
  $scope.profile = {};

  $scope.profile.profilePix = profileInfo.profilePix;
  $scope.profile.email = profileInfo.email;
  $scope.profile.role = profileInfo.role;
  $scope.profile.contributions = profileInfo.contributions;

};

angular.module('dashboardappApp').controller("homeController", homeController);
