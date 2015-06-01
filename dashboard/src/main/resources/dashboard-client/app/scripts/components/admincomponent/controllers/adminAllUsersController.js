'use strict';
/**
 * Controller for parent view for the name entry views
 * @param $scope angular scope
 */
 //TODO refactor to use profileService
var adminAllUsersController = function($scope, endpointService) {
  $scope.childTitle = "All registered users";
  $scope.users = [];
  var response = endpointService.get("/v1/auth/users");
  response.success(function(responseData){
    responseData.forEach(function(user){
      $scope.users.push({
          email: user.email,
          roles: user.roles,
          username: user.username
       });
    });

  }).error(function(responseData) {

  });

};

angular.module('dashboardappApp').controller("adminAllUsersController", adminAllUsersController);
