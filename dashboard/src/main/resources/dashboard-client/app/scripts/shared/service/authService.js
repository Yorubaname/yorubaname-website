'use strict';

/**
 * Service for Auth related tasks
 */

var authService = function($cookies, endpointService) {

  this.getUser = function getUser(callback) {
    endpointService.get("/v1/auth/user").then(function(response) {
      return response;
    });
  };

  // Authenticates clients.
  // authData is the base64 encoding of username and password
  // currentScope is the angular scope.
  // on authentication the currentScope and cookie is updated as necessary
  this.authenticate = function(authData, currentScope) {
    var response = endpointService.authenticate(authData);
    response.success(function(response) {
      $cookies.isAuthenticated = true;
      currentScope.isAuthenticated = true;
      $cookies.username = response.username;
      $cookies.token = authData;

      response.roles.every(function(role) {
        if (role === "ROLE_ADMIN") {
          $cookies.isAdmin = true;
          currentScope.isAdmin = true;
          return false;
        }
      });

      currentScope.msg = {};
      window.location.href = "#/home";

    }).error(function(response){
      $cookies.isAuthenticated = false;
      $cookies.isAdmin = false;
      currentScope.isAuthenticated = false;
      currentScope.isAdmin = false;
      currentScope.msg.type = "msg-error";
      currentScope.msg.text = "Can not login with the credentials provided";
    });
  };
};

angular.module('dashboardappApp').service('authService', authService);
