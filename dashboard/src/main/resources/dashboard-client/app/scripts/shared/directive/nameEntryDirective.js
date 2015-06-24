'use strict';


/**
 * Directive that is used to create html form to create/update
 * Name entry
 */
// TODO move the dependent template into a directory with the directive This involves updating the grunt task to deal with when views are not in the root views dir
// TODO update checks to also cater for other HTTP actions. POST etc
// TODO this todo is not perculair to this file. Standardize either to use toneMark or tonalMark
var nameEntry = function($http, $location, $state, $rootScope, endpointService, aToStringFilter, nameEntryService, sToArraysFilter, $compile, $q, Notification) {
    var delim = '-'
    var getGeolocations = function(scope) {
        // reach out to the endpoint to get data for this name
        var request = endpointService.get('/v1/admin/geolocations');
        request.then(function(data) {
            scope.geoList = data.data
            scope.formEntry.geoLocation = data.data[0]
        }, function(data) {});
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
            scope.formEntry.etymology = !isEmptyObj(data.mainEntry.etymology) ? angular.fromJson(data.mainEntry.etymology) : []
            if (getDuplicates && getDuplicates === "true") {
                scope.duplicates = data.duplicates;
            }
        }).error(function(data) {
            Notification.error(data)
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
            scope.loadingMsg = "Retrieving list of matching names..."
            scope.formEntry = {}
            getGeolocations(scope)
            scope.duplicateView = attrs.duplicates == "true" ? true : false;
            scope.msg = {};
            scope.buttonAction = "Create Entry";
            var setupForm = function() {
                scope.formEntry = {
                    etymology: []
                }
            }
            setupForm()
            scope.setGeolocation = function(geo) {
                scope.geoLocation = geo
            }

            scope.testautocomplete = [{
                    name: 'ttola'
                }, {
                    name: 'odutola'
                }]
                /* Promise for the autocomplete input box query*/
            scope.findName = function(name) {
                var names = nameEntryService.getName(name)
                return names.then(function(response) {
                    scope.currentName = response.data.name
                    scope.buttonAction = "Submit Duplicate";
                    populateForm(scope, true);
                }, function(error) {
                    console.log(error)
                })
            }

            if (attrs.action === 'put') {
                scope.buttonAction = "Update Entry";
                scope.currentName = $location.search().name;
                populateForm(scope, attrs.duplicates);
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
                    if (attrs.action === 'put') {
                        $state.go("list.all")
                    } else {
                        setupForm()
                    }
                    Notification.primary('Successfully added name');

                }).error(function(data, status, headers, config) {
                    Notification.error({
                        title: 'Error adding name',
                        message: data.message
                    });
                });
            };

            scope.delete = function(name) {
                request = endpointService.deleteJson('/v1/names/' + name);
                request.success(function() {
                    $state.go("list.all")
                }).error(function(error) {
                    Notification.error({
                        title: "An error occured deleted name",
                        message: error
                    })
                })
            }


            scope.$watch('formEntry.name', function(newname) {
                if (!isEmpty(scope.currentName) && newname !== scope.currentName && attrs.action !== 'put') {
                    scope.formEntry = {
                        name: newname
                    }

                    scope.buttonAction = "Create Entry";
                }
            }, true);
        }
    };
};


angular.module('dashboardappApp').directive('nameentry', nameEntry);
