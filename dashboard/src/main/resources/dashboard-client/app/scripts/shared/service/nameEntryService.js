'use strict';

/**
 * Service use to communicate with endpoint to fetch names
 */
// TODO refactor endpoint raw strings to global variables
var nameEntryService = function($http, endpointService) {
    /**
     * Get a name by id
     * returns the one or no result
     */
    this.getName = function getName(id) {
        var data = {
            id: id
        };
        var request = endpointService.get('/v1/name/', data);
        return request;
    };

    this.getNames = function getNames(page, count) {
        var data = {
            page: page,
            count: count
        };
        var request = endpointService.get('/v1/names/', data);
        return request;

    };

};

angular.module('dashboardappApp').service('nameEntryService', nameEntryService);
