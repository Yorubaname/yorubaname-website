'use strict';

var homeController = function($scope, profileService) {

  $scope.title = "Dictionary Dashboard";
  var profileInfo = profileService.getProfileInfo();
  $scope.profile = {};

  $scope.profile.profilePix = profileInfo.profilePix;
  $scope.profile.email = profileInfo.email;
  $scope.profile.role = profileInfo.role;
  $scope.profile.contributions = profileInfo.contributions;


  $scope.search = function() {
    console.log("Doing search");
  };
};

angular.module('dashboardappApp').controller("homeController", homeController);
