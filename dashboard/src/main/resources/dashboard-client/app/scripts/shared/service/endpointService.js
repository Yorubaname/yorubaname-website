'use strict';

/**
 * Service for performing GET, POST, PUT operations with the end point
 *
 * @param $http angulars http service
 * @param $rootScope angulars root scope
 */
var endpointService = function ($http, $rootScope, ENV) {

  this.get = function(endpoint, data, headers) {

    var request = $http({
           method: 'GET',
           url: ENV.appEndpoint + endpoint,
           params: data ? data : '',
           headers: headers? headers : ''
    });

    return request;
  };

  this.post = function(endpoint, data) {
    $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
    var request = $http({
          method: 'POST',
          url: ENV.appEndpoint + endpoint,
          data: $.param(data)
    });

    return request;
  };

  this.postJson = function(endpoint, data) {
    var request = $http({
          method: 'POST',
          url: ENV.appEndpoint + endpoint,
          data: data
    });

    return request;
  };

  this.put = function (endpoint, data) {
    $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
    var request = $http({
          method: 'PUT',
          url: ENV.appEndpoint + endpoint,
          params: data
    });

    return request;
  };

  this.putJson = function (endpoint, data) {
    var request = $http({
            method: 'PUT',
            url: ENV.appEndpoint + endpoint,
            params: data
    });

    return request;
  };

  this.authenticate = function(authData) {
    var endpoint = "/dashboard/login";
    $http.defaults.headers.common['Authorization'] = 'Basic ' + authData;

    var request = $http({
              method: 'POST',
              url: ENV.appEndpoint + endpoint
    });

    return request;
  }

};

angular.module('dashboardappApp').service('endpointService', endpointService);
