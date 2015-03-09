'use strict';

/**
 * Service use to communicate with endpoint to fetch names
 */
  // TODO refactor to use the endpointService
var nameEntryService = function($http, $rootScope) {
  /**
   * Get a name by id
   * returns the one or no result
   */
  this.getName = function getName(id) {

  };

  this.getNames = function getNames(page, count) {
    var request = $http({
            method: 'GET',
            url: $rootScope.appEndpoint + '/v1/names/',
            params: {
              page: page,
              count: count
            }
          });

    return request;

  };

};

angular.module('dashboardappApp').service('nameEntryService', nameEntryService);
