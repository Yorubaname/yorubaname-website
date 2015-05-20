'use strict';
/**
 * Controller for the upload view
 * @param $scope angular scope
 * @param $element angular element
 * @param fileUploader service for uploading file to a url
 */
var uploadEntryController = function ($scope, $element, fileUploader, ENV) {
  $scope.namesfile = {};
  $scope.msg = {};

  $scope.upload = function () {

    if ($scope.namesfile === undefined || Object.keys($scope.namesfile).length === 0) {
          $scope.msg.text = "You need to choose file before uploading";
          $scope.msg.type = "msg-error";
          return;
    }

    var success = function (response) {
      $scope.msg.text = "File successful uploaded";
      $scope.msg.type = "msg-success";
      $element[0].reset();

      setTimeout(function() {
        $scope.$apply(function () {
          $scope.msg.text = "";
          $scope.msg.type = "";
        });
      }, 5000);
    };

    var error = function (response) {
      $scope.msg.text = response.errorMessages[0];
      $scope.msg.type = "msg-error";
      setTimeout(function() {
        $scope.$apply(function () {
          $scope.msg.text = "";
          $scope.msg.type = "";
        });
      }, 5000);
    };

    $scope.msg.text = "Uploading...";
    $scope.msg.type = "msg-info";

    fileUploader.uploadFileToUrl($scope.namesfile, ENV.appEndpoint + '/v1/names/upload', success, error);

  };
};

angular.module('dashboardappApp').controller("uploadEntryController", uploadEntryController);
