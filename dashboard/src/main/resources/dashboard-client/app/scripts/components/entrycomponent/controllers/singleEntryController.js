'use strict';

/**
 * Controller for the single entry view
 * @param $scope angular scope
 * @param $http angular http service
 * @param $element angular element
 */
// TODO a nameEntryDirective is now available. Use that instead
var singleEntryController = function ($scope, $http, $element, $cookies, ENV) {
  $scope.formEntry = {};
  $scope.msg = {};
  $scope.formEntry.submittedBy = $cookies.username;

  $scope.create = function () {
    $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";

    $http({
            method: 'POST',
            url: ENV.appEndpoint+'/v1/names',
            data: $element.serialize($scope.formEntry)
          })
      .success(function (data) {
       if (data === "success") {
         $element[0].reset();
         $scope.msg.text = "Successfully added name";
         $scope.msg.type = "msg-success";
         setTimeout(function() {
           $scope.$apply(function () {
             $scope.msg.text = "";
             $scope.msg.type = "";
           });
         }, 5000);
       }
    }).error(function (data, status, headers, config) {
                   $scope.msg.text = "Error in adding name:" + data.errorMessage;
                   $scope.msg.type = "msg-error";
                   setTimeout(function() {
                     $scope.$apply(function () {
                       $scope.msg.text = "";
                       $scope.msg.type = "";
                     });
     }, 5000);
    });


  };
};

angular.module('dashboardappApp').controller("singleEntryController", singleEntryController);
