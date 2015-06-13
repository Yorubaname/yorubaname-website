'use strict';

/**
 * Service use to communicate with endpoint to fetch names
 */
// TODO refactor endpoint raw strings to global variables
var nameService = function($http, endpointService) {
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

};



angular.module('dashboardappApp').service('nameService', nameService);
