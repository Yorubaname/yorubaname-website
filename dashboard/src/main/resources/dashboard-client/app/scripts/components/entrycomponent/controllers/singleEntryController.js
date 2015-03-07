'use strict'

var singleEntryController = function ($scope, $http, $element, $animate) {
  $scope.formEntry = {};
  $scope.msg = {};

  $scope.create = function () {
    console.log(1,$scope.formEntry);
    $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
    $http({
            method: 'POST',
            url: $scope.appEndpoint+'/v1/name',
            data: $element.serialize($scope.formEntry)
          })
      .success(function (data) {
       if (data === "success") {
         $scope.msg.text = "Successfully added name";
         $scope.msg.type = "msg-success";
         setTimeout(function() {
           $scope.$apply(function () {
             $scope.msg.text = "";
             $scope.msg.type = "";
           });
         }, 3000);
       }
    }).error(function (data, status, headers, config) {
                   $scope.msg.text = "Error in adding name:" + data;
                   $scope.msg.type = "msg-error";
                   setTimeout(function() {
                     $scope.$apply(function () {
                       $scope.msg.text = "";
                       $scope.msg.type = "";
                     });
     }, 3000);
    });


  };
};

angular.module('dashboardappApp').controller("singleEntryController", singleEntryController);
