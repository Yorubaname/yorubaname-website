'use strict';

/**
 * Service use to communicate with endpoint to fetch names
 */
// TODO refactor endpoint raw strings to global variables
var nameEntryService = function($http, endpointService) {
    /**
     * Get a name
     * returns the one or zero result
     */
    this.getName = function getName(name, duplicate) {
        var param = {
            duplicates: duplicate
        };
        var request = endpointService.get('/v1/names/' + name, param);
        return request;
    };

    this.getNames = function getNames(params) {
        var request = endpointService.get('/v1/names/', params);
        return request;

    };

    this.indexName = function(name) {
        var request = endpointService.postJson('/v1/search/indexes/' + name);
        return request;
    };

    this.removeNameFromIndex = function(name) {
        var request = endpointService.deleteJson('/v1/search/indexes/' + name);
        return request;
    }

};


angular.module('dashboardappApp').service('nameEntryService', nameEntryService);
