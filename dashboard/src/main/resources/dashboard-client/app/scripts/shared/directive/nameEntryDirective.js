'use strict';


/**
 * Directive that is used to create html form to create/update
 * Name entry
 */
// TODO move the dependent template into a directory with the directive This involves updating the grunt task to deal with when views are not in the root views dir
// TODO update checks to also cater for other HTTP actions. POST etc
// TODO this todo is not perculair to this file. Standardize either to use toneMark or tonalMark
var nameEntry = function($http, $location, $state, $rootScope, endpointService, aToStringFilter, nameEntryService, sToArraysFilter, $compile, $q) {
    var delim = '-'
    var getGeolocations = function(scope) {
        // reach out to the endpoint to get data for this name
        var request = endpointService.get('/v1/admin/geolocations');
        request.then(function(data) {
            scope.geoList = data.data
            scope.formEntry.geolocation = data.data[0]
        }, function(data) {
            console.log(data);
        });
    }

    var delim = "-";
    var populateForm = function(scope, getDuplicates) {
        // reach out to the endpoint to get data for this name
        var request = nameEntryService.getName(scope.currentName, 'yes');
        request.success(function(data) {
            //REASON: since objects values are parse by angular, and objects attribute coincide with api attributes
            scope.formEntry = data.mainEntry
            scope.formEntry.ipaNotation = sToArraysFilter(data.mainEntry.ipaNotation, delim)
            scope.formEntry.pronunciation = sToArraysFilter(data.mainEntry.pronunciation, delim)
            scope.formEntry.syllables = sToArraysFilter(data.mainEntry.syllables, delim)
            scope.formEntry.morphology = sToArraysFilter(data.mainEntry.morphology, delim)
            scope.formEntry.etymology = !isEmptyObj(data.mainEntry.etymology) ? $.parseJSON(data.mainEntry.etymology) : []
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
            scope.geoList = []
            scope.tempList = ["Something", "yes"]
            scope.selectedName;
            scope.loadingMsg = "Retrieving list of matching names..."
            scope.formEntry = {}
            getGeolocations(scope)
            scope.duplicateView = attrs.duplicates == "true" ? true : false;
            scope.msg = {};
            scope.buttonAction = "Create Entry";
            var setupForm = function() {
                scope.formEntry = {
                    etymology: [],
                    media: []
                }
            }
            setupForm()
            scope.setGeolocation = function(geo) {
                    scope.geolocation = geo
                }
                /* Promise for the autocomplete input box query*/
            scope.nameAutocomplete = function(name) {
                    var names = nameEntryService.getName(name)
                    return names.then(function(response) {
                        if (!isEmptyObj(response.data)) {
                            return response.data
                        }
                        return []
                    }, function(error) {
                        console.log(error)
                    })
                }
                /* DO these when a name match is selected from the autocomplete dropdown*/
            scope.onNameSelected = function(formEntry) {
                scope.currentName = scope.selectedName = formEntry.name
                scope.buttonAction = "Submit Duplicate";
                populateForm(scope, true);
            }

            var resetAfterPost = function(element, scope) {
                scope.formEntry = {};
                scope.msg.text = "Successfully added name";
                scope.msg.type = "msg-success";
            }

            var resetAfterPut = function(element, sope) {
                scope.formEntry = {};
                scope.msg.text = "Successfully updated name";
                scope.msg.type = "msg-success";
            }

            if (attrs.action === 'put') {
                scope.buttonAction = "Update Entry";
                scope.currentName = $location.search().name;
                populateForm(scope, attrs.duplicates);
                console.log(scope.formEntry.geolocation)
            }

            /*Submit new or update entries*/
            scope.create = function() {
                // force form validation check
                scope.$broadcast('show-errors-check-validity');
                if (!scope.nameForm.$valid) {
                    return
                }
                // parse tags to strings using arraysToString filter
                var parsedEntry = angular.copy(scope.formEntry);
                parsedEntry.ipaNotation = aToStringFilter(parsedEntry.ipaNotation, delim)
                parsedEntry.pronunciation = aToStringFilter(parsedEntry.pronunciation, delim)
                parsedEntry.syllables = aToStringFilter(parsedEntry.syllables, delim)
                parsedEntry.morphology = aToStringFilter(parsedEntry.morphology, delim)
                if (attrs.action === 'put') {
                    request = endpointService.putJson('/v1/names/' + parsedEntry.name, parsedEntry);
                } else {
                    request = endpointService.postJson('/v1/names', parsedEntry);
                }
                request.success(function(data) {
                    if (data === "success") {
                        if (attrs.action === 'put') {
                            $state.go("list.all")
                        } else {
                            setupForm()
                        }
                        scope.msg.text = "Successfully added name";
                        scope.msg.type = "msg-success";
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
                    console.log(data)
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
