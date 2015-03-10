'use strict';

// TODO a nameEntryDirective is not available. Use that instead
var singleEntryController = function ($scope, $http, $element, $animate) {
  $scope.formEntry = {};
  $scope.msg = {};

  $scope.create = function () {
    $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
    $http({
            method: 'POST',
            url: $scope.appEndpoint+'/v1/name',
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
