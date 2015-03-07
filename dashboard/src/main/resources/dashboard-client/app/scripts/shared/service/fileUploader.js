'use strict'


/**
 * Service for uploading a file to given url
 *
 * @param $http
 */
var fileUploader = function ($http) {
  this.uploadFileToUrl = function(file, uploadUrl, success, failure){
    var data = new FormData();
    data.append('nameFiles', file);

    $http.post(uploadUrl, data, {
      transformRequest: angular.identity,
      headers: {'Content-Type': undefined}
    }).success(function(response){

      if (success != undefined && success instanceof Function) {
        success(response);
      }

    }).error(function(response){
      if (failure != undefined && failure instanceof Function) {
          failure(response);
      }
    });
  }
};

angular.module('dashboardappApp').service('fileUploader', fileUploader);


