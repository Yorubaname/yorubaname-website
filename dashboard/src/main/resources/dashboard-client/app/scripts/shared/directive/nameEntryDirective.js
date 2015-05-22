'use strict';


/**
 * Directive that is used to create html form to create/update
 * Name entry
 */
// TODO move the dependent template into a directory with the directive This involves updating the grunt task to deal with when views are not in the root views dir
// TODO update checks to also cater for other HTTP actions. POST etc
// TODO this todo is not perculair to this file. Standardize either to use toneMark or tonalMark
var nameEntry = function($http, $location, $state, $rootScope, endpointService, stubService, nameEntryService, sToArraysFilter, $compile) {

    var populateForm = function(scope, getDuplicates) {
        // reach out to the endpoint to get data for this name

        var request = endpointService.get('/v1/names/' + scope.currentName, {
            'duplicates': 'yes'
        });
        request.success(function(data) {
            //REASON: since objects values are parse by angular, and objects attribute coincide with api attributes
            var delim = "-"
            scope.formEntry = data.mainEntry
            scope.formEntry.ipaNotation = sToArraysFilter(data.mainEntry.ipaNotation, delim)
            scope.formEntry.pronunciation = sToArraysFilter(data.mainEntry.pronunciation, delim)
            scope.formEntry.syllables = sToArraysFilter(data.mainEntry.syllables, delim)
            scope.formEntry.morphology = sToArraysFilter(data.mainEntry.morphology, delim)
            scope.formEntry.etymology = !isEmptyObj(data.mainEntry.etymology) ? data.mainEntry.etymology : []
            scope.formEntry.media = !isEmptyObj(data.mainEntry.media) ? data.mainEntry.media : []

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
        link: function(scope, element, attrs, notification) {
            var request;
            scope.selectedName;
            scope.formEntry = {
                etymology: [],
                media: []
            };
            scope.msg = {};
            scope.buttonAction = "Create Entry";
            scope.duplicateView = attrs.duplicates == "true" ? true : false;
            scope.locationTemplate = stubService.getLocationTemplate
            scope.nameMatchList = function() {
                return nameEntryService.getNames().then(function(response) {
                    if (!isEmptyObj(response.data)) {
                        return response.data
                    }
                    return []
                }, function(error) {
                    console.log(error)
                })
            }

            scope.onNameSelected = function(formEntry) {
                scope.currentName = scope.selectedName = formEntry.name
                scope.buttonAction = "Submit Duplicate";
                populateForm(scope, true);
            }


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
                parsedEntry = stubService.arraysToString(parsedEntry, delim)
                if (attrs.action === 'put') {
                    request = endpointService.put('/v1/name', parsedEntry);
                } else {
                    request = endpointService.post('/v1/name', parsedEntry);
                }

                request.success(function(data) {
                    if (data === "success") {
                        if (attrs.action === "put") {
                            resetAfterPut(element, scope);
                            $state.go($rootScope.previousState)
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

            scope.$watch('formEntry.name', function(newname) {
                if (!isEmpty(scope.selectedName) && newname === "") {
                    scope.formEntry = {}
                }
            }, true);
        }
    };
};


angular.module('dashboardappApp').directive('nameentry', nameEntry);
