'use strict';

/**
 * Service for names autocomplete request
 */

var nameAutocompleteService = function(endpointService) {
    // TODO change endpoint to use elastic search
    this.getList = function getUser(val) {
        var nameRequest = endpointService.get('/v1/name', {
            name: val
        });
        return nameRequest.then(function(data) {
            if (!isEmptyObj(data)) {
                return data
            }
            return []
        })
    };
};

angular.module('dashboardappApp').service('nameAutocompleteService', nameAutocompleteService);
