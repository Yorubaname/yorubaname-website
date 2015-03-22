'use strict';

/**
 * Service for Auth related tasks
 */

var authService = function(endpointService) {

  this.getUser = function getUser(callback) {
    endpointService.get("/auth/user").then(function(response) {
      return response;
    });
  };
};

angular.module('dashboardappApp').service('authService', authService);
