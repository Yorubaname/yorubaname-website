'use strict';

/**
 * Service for performing GET, POST, PUT operations with the end point
 *
 * @param $http angulars http service
 * @param $rootScope angulars root scope
 */
var endpointService = function($http, $rootScope, ENV) {

    this.get = function(endpoint, data, headers) {

        var request = $http({
            method: 'GET',
            url: ENV.appEndpoint + endpoint,
            params: data ? data : '',
            headers: headers ? headers : ''
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
            data: data ? data : ''
        });

        return request;
    };

    this.put = function(endpoint, data) {
        $http.defaults.headers.put["Content-Type"] = "application/x-www-form-urlencoded";
        var request = $http({
            method: 'PUT',
            url: ENV.appEndpoint + endpoint,
            data: data ? data : ''
        });

        return request;
    };

    this.putJson = function(endpoint, data) {

        var request = $http({
            method: 'PUT',
            url: ENV.appEndpoint + endpoint,
            data: data ? data : ''
        });

        return request;
    };

    this.delete = function(endpoint, data) {
      $http.defaults.headers.put["Content-Type"] = "application/x-www-form-urlencoded";

      var request = $http({
        method: 'DELETE',
        url: ENV.appEndpoint + endpoint,
        data: data ? data : ''
      });

      return request;

    };
    this.deleteJson = function(endpoint, data) {
        // had to explicitly set the content-type for the delete request to work, Why? I do not know yet
        $http.defaults.headers.common['Content-Type'] = "application/json";
        var request = $http({
            method: 'DELETE',
            url: ENV.appEndpoint + endpoint,
            data: data ? data : ''
        });

        return request;
    };

    this.authenticate = function(authData) {
        var endpoint = "/v1/auth/login";
        $http.defaults.headers.common['Authorization'] = 'Basic ' + authData;

        var request = $http({
            method: 'POST',
            url: ENV.appEndpoint + endpoint
        });

        return request;
    }

};

angular.module('dashboardappApp').service('endpointService', endpointService);
