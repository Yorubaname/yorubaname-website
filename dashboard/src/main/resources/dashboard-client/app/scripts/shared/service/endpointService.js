'use strict';

/**
 * Service for performing GET, POST, PUT operations with the end point
 *
 * @param $http angulars http service
 * @param $rootScope angulars root scope
 */
var endpointService = function ($http, $rootScope) {

  this.post = function(endpoint, data) {
    var request = $http({
          method: 'POST',
          url: $rootScope.appEndpoint + endpoint,
          data: data,
          headers: {'Content-Type': 'application/x-www-form-urlencoded'}
    });

    return request;
  };

  this.get = function(endpoint, params) {
    var request = $http({
          method: 'GET',
          url: $rootScope.appEndpoint + endpoint,
          params: params
     });

    return request;
  };

  this.put = function (endpoint, params) {
    var request = $http({
          method: 'PUT',
          url: $rootScope.appEndpoint + endpoint,
          params: params
    });

    return request;
  };

};

angular.module('dashboardappApp').service('endpointService', endpointService);
