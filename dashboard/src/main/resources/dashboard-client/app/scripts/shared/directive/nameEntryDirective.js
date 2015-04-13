'use strict';


/**
 * Directive that is used to create html form to create/update
 * Name entry
 */
// TODO move the dependent template into a directory with the directive This involves updating the grunt task to deal with when views are not in the root views dir
// TODO update checks to also cater for other HTTP actions. POST etc
// TODO this todo is not perculair to this file. Standardize either to use toneMark or tonalMark
var nameEntry = function($http, $location, endpointService, nameAutocompleteService, stubService) {

    var populateForm = function(scope, getDuplicates) {
        // reach out to the endpoint to get data for this name
        var request = endpointService.get('/v1/names/' + scope.currentName, {
            'duplicates': 'yes'
        });
        request.success(function(data) {
            //REASON: since objects values are parse by angular, and objects attribute coincide with api attributes
            scope.formEntry = data.mainEntry
            if (getDuplicates && getDuplicates === "true") {
                scope.duplicates = data.duplicates;
            }

        }).error(function(data) {
            console.log(data);
        });
    };
    return {
        restrict: 'E',
        templateUrl: 'views/nameentry.directive.template.html',
        replace: true,
        controller: function($scope) {

        },
        link: function(scope, element, attrs, notification) {
            var request;
            scope.formEntry = {};
            scope.msg = {};
            // geolocation template
            scope.locationTemplate = stubService.getLocationTemplate
                // required service for all name autocomplate textbox
            scope.nameMatchList = nameAutocompleteService.getList
            scope.buttonAction = "Create Entry";
            scope.duplicateView = attrs.duplicates == "true" ? true : false;

            var resetAfterPost = function(element, scope) {
                element.children('form')[0].reset();
                scope.msg.text = "Successfully added name";
                scope.msg.type = "msg-success";
            };

            var resetAfterPut = function(element, sope) {
                scope.msg.text = "Successfully updated name";
                scope.msg.type = "msg-success";
            };

            if (attrs.action === 'put') {
                scope.buttonAction = "Update Entry";
                scope.currentName = $location.search().name;
                populateForm(scope, attrs.duplicates);
            };
            /*Submit new or update entries*/
            scope.create = function() {
                // parse tags to strings using stubservice
                var parsedEntry = angular.copy(scope.formEntry)
                parsedEntry = stubService.joinArrays(parsedEntry, ",")
                console.log(parsedEntry)
                if (attrs.action === 'put') {
                    request = endpointService.put('/v1/name', parsedEntry);
                } else {
                    request = endpointService.post('/v1/name', parsedEntry);
                }

                request.success(function(data) {
                    if (data === "success") {
                        if (attrs.action === "put") {
                            resetAfterPut(element, scope);
                        } else {
                            resetAfterPost(element, scope);
                        }
                        setTimeout(function() {
                            scope.$apply(function() {
                                scope.msg.text = "";
                                scope.msg.type = "";
                            });
                        }, 5000);
                    }
                }).error(function(data, status, headers, config) {
                    scope.msg.text = "Error in adding name:" + data;
                    scope.msg.type = "msg-error";

                    setTimeout(function() {
                        scope.$apply(function() {
                            scope.msg.text = "";
                            scope.msg.type = "";
                        });
                    }, 5000);

                });
            };

            /*Watch morphology model to build template for etymology template generation*/
            scope.$watch('formEntry.morphology', function(morphology) {
                scope.formEntry.etymology = []

                if (morphology) {
                    for (var i = morphology.length - 1; i >= 0; i--) {
                        console.log(morphology[i].text)
                        scope.formEntry.etymology.push({
                            name: morphology[i].text
                        })
                    };
                };

            }, true);
        }
    };
};


angular.module('dashboardappApp').directive('nameentry', nameEntry);
