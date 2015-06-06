'use strict';

/**
 * Service use to communicate with endpoint to fetch names
 */
// TODO refactor endpoint raw strings to global variables
var nameEntryService = function ($http, endpointService) {
  /**
   * Get a name
   * returns the one or zero result
   */
  this.getName = function getName(name) {
    var data = {
      name: name
    };
    var request = endpointService.get('/v1/names/', data);
    return request;
  };

  this.getNames = function getNames(page, count, filter, indexed) {
    var data = {
      page: page,
      count: count,
      filter: filter,
      indexed: indexed
    };
    var request = endpointService.get('/v1/names/', data);
    return request;

  };

  this.indexName = function (name) {
    var request = endpointService.postJson('/v1/search/indexes/'+name);
    return request;
  };

  this.removeNameFromIndex = function (name) {
    var request = endpointService.deleteJson('/v1/search/indexes/'+name);
    return request;
  }

};


angular.module('dashboardappApp').service('nameEntryService', nameEntryService);
