'use strict';

/**
 * Controller for the view that lists all names
 * @param $scope angular scope
 * @param $location angular's location service
 * @param nameEntryService service for getting names from end point
 */
var listAllController = function($scope, $location, nameEntryService) {
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

    $scope.edit = function(toEdit) {
        $scope.toEdit = toEdit;
        $location.path('/list/edit').search({
            'name': $scope.toEdit
        });
    };

    $scope.filterList = function(filter) {
        getNamesAndPutOnScope($scope.page, $scope.count, filter);
    }

    var getNamesAndPutOnScope = function(page, count, filter) {
        page = (page != 'undefined' && !isNaN(page)) ? page : 1;
        count = (count != 'undefined' && !isNaN(count)) ? count : 50;

        var names = nameEntryService.getNames(page, count, filter);
        names.then(function(response) {
            $scope.count = count;
            $scope.names = response.data;
        });
    };

    getNamesAndPutOnScope($scope.page, $scope.count);
};

angular.module('dashboardappApp').controller("listAllController", listAllController);
