'use strict';

/**
 * Controller for the single entry view
 * @param $scope angular scope
 * @param $http angular http service
 * @param $element angular element
 */
// TODO a nameEntryDirective is now available. Use that instead
var singleEntryController = function($scope, $http, $element, $cookies, ENV, Notification) {
    $scope.formEntry = {};
    $scope.msg = {};
    $scope.formEntry.submittedBy = $cookies.username;

    $scope.create = function() {
        $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";

        $http({
                method: 'POST',
                url: ENV.appEndpoint + '/v1/names',
                data: $element.serialize($scope.formEntry)
            })
            .success(function(data) {
                if (data === "success") {
                    $element[0].reset();
                    Notification.success('Successfully added name')
                }
            }).error(function(data, status, headers, config) {
                Notification.error({
                    title: 'Error in adding name:' + data.errorMessage,
                    message: data
                })
            });


    };
};

angular.module('dashboardappApp').controller("singleEntryController", singleEntryController);
