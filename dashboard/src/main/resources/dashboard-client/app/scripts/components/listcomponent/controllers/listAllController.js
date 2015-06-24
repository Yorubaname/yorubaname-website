'use strict';

/**
 * Controller for the view that lists all names
 * @param $scope angular scope
 * @param $location angular's location service
 * @param nameEntryService service for getting names from end point
 */
var listAllController = function($scope, $location, nameEntryService, Notification) {
    $scope.title = "All Names";
    $scope.childTitle = "All entries in database";
    $scope.names = "";
    $scope.page = $location.search().page ? $location.search().page : 1;
    $scope.count = $location.search().count ? $location.search().count : 50;

    $scope.next = function() {
        $scope.page++;
        $location.path('/list/all').search({
            'page': $scope.page
        });

        getNamesAndPutOnScope($scope.page, $scope.count);
    };

    $scope.previous = function() {
        if ($scope.page !== 1) {
            $scope.page--;
            $location.path('/list/all').search({
                'page': $scope.page
            });

            getNamesAndPutOnScope($scope.page, $scope.count);
        }

    };

    $scope.indexName = function(name) {
        var result = nameEntryService.indexName(name);
        result.success(function(data) {
            $scope.names.some(function(aname) {
                if (aname.name === name) {
                    aname.indexed = true;
                    return true;
                }
            });
        }).error(function(data) {
            Notification.error({
                title: 'An error occured',
                message: data.message
            })
        });
    };

    $scope.removeNameFromIndex = function(name) {
        var result = nameEntryService.removeNameFromIndex(name);
        result.success(function(data) {
            $scope.names.some(function(aname) {
                if (aname.name === name) {
                    aname.indexed = false;
                    return true;
                }
            });
        }).error(function(data) {
            Notification.error({
                title: 'An error occured',
                message: data.message
            })
        });
    };

    $scope.edit = function(toEdit) {
        $scope.toEdit = toEdit;
        $location.path('/list/edit').search({
            'name': $scope.toEdit
        });
    };

    $scope.filterList = function(filter) {
        getNamesAndPutOnScope($scope.page, $scope.count, filter);
    };
    $scope.indexedList = function() {
        var indexParam = {
            indexed: true
        }
        getNamesAndPutOnScope($scope.page, $scope.count, indexParam);
    };
    $scope.reviewList = function() {
        var indexParam = {
            indexed: false
        }
        getNamesAndPutOnScope($scope.page, $scope.count, indexParam);
    };
    var getNamesAndPutOnScope = function(page, count, filter) {
        page = (page != 'undefined' && !isNaN(page)) ? page : 1;
        count = (count != 'undefined' && !isNaN(count)) ? count : 50;


        var names = nameEntryService.getNames(page, count, filter);
        names.then(function(response) {
            $scope.count = count;
            $scope.names = response.data;
        }, function(error) {
            Notification.error({
                title: 'An error occured',
                message: error
            })
        })
    };

    if ($location.search().submittedBy && !isEmpty($location.search().submittedBy)) {
        var filter = {
            submittedBy: $location.search().submittedBy
        }
        $scope.filterList(filter)
    } else {
        getNamesAndPutOnScope($scope.page, $scope.count);
    }


};

angular.module('dashboardappApp').controller("listAllController", listAllController);
