'use strict';

/**
 * Controller for the home/dashboard view
 * @param $scope angular scope
 * @param profileService the service use to fetch profile information
 */
var homeController = function ($scope, profileService, $cookies, nameEntryService) {

  $scope.info = {};
  $scope.title = "Dictionary Dashboard";
  $scope.profile = {
    username: $cookies.username,
    role: $cookies.role
  }


  var getSuggestedNamesCount = function () {
    var result = nameEntryService.getSuggestedNames();
    result.success(function(response){
      $scope.info.suggestedNamesCount = response.length;
    }).error(function(error) {
      console.log("Error while getting suggested names", error);
    });
  };


  (function(){
    // init functions
    getSuggestedNamesCount();
  })();
};

angular.module('dashboardappApp').controller("homeController", homeController);
