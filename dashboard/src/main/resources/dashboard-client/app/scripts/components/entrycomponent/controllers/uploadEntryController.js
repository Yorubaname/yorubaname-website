'use strict';
/**
 * Controller for the upload view
 * @param $scope angular scope
 * @param $element angular element
 * @param fileUploader service for uploading file to a url
 */
var uploadEntryController = function ($scope, $element, Upload, ENV, Notification, endpointService) {
  // $scope.namesfile = {};
  // $scope.names = [];
  $scope.msg = {};
  $scope.validFile = false;
  $scope.$watch('files', function (files) {

    if (files && files.length) {
      if (files[0].name.substr(files[0].name.length - 5) !== '.xlsx') {
        Notification({
          title: 'You have selected an invalid file',
          message: 'Upload the correct template or download a new one'
        });
        return;
      }
      $scope.filename = files[0].name;
      $scope.validFile = true;
    } else {
      $scope.validFile = false;
    }
  });

  $scope.upload = function (files) {
    if (files && files.length) {
      var namesFile = files[0];
      console.log(namesFile)
      Upload.upload({
        url: ENV.appEndpoint + '/v1/names/upload',
        file: namesFile,
        fileFormDataName: 'nameFiles'
      }).progress(function (evt) {
        $scope.uploading = true;
        var progressPercentage = parseInt(100.0 * evt.loaded / evt.total);
        $scope.msg.text = "File upload:" + progressPercentage + '% done...Starting name importation...';
      }).success(function (data, status, headers, config) {
        $scope.validFile = false;
        $scope.filename = "";

        setTimeout(function () {
          // start polling for upload progress
          var interval = setInterval(function () {
            var request = endpointService.get('/v1/names/uploading?q=progress');
            request.success(function (response) {
              if (response.uploading === true) {
                $scope.msg.text = "Imported " + response.totalUploaded + " out of " + response.totalNumberOfNames
                  + " names.";
              } else {
                $scope.msg.text = "";
                $scope.uploading = false;
                clearInterval(interval);
                Notification.success('Names uploaded successfully');
              }
            }).error(function () {
              clearInterval(interval);
            });
          }, 250);
        }, 1000);


      }).error(function (data, status, headers, config) {
        $scope.validFile = false;
        $scope.filename = "";
        $scope.msg.text = "";
        $scope.uploading = false;
        Notification.error({
          title: 'An error occured uploading file',
          message: data.message
        })
      });
    }
  }

  // Incomplete implementation on server
  /* var columns = ["name", "pronunciation", "ipa_notation", "ipanotation", "syllable", "meaning", "extended_meaning", "extendedmeaning", "morphology", "etymology", "geo_location", "geolocation", "media", "variant"]
   var columnsHash = {}
   $scope.invalidColumns = []
   // Generate list of columns found in csv that's not in attribute list
   var getInvalidColumns = function(list) {
   var invalids = []
   for (var i = columns.length - 1; i >= 0; i--) {
   columnsHash[columns[i]] = columns[i]
   }
   for (var i = list.length - 1; i >= 0; i--) {
   if (typeof columnsHash[list[i].toLowerCase()] == 'undefined') {
   invalids.push(list[i])
   }
   }
   return invalids;
   }
   // verify if invalid columns exist in csv
   var isValidFile = function(list) {
   $scope.invalidColumns = []
   $scope.invalidColumns = getInvalidColumns(list)
   if ($scope.invalidColumns.length == 0) {
   $scope.validFile = true
   }
   $scope.$apply()
   }
   // Parse cvs to json, get callback to validated parse file
   var parseForm = function() {
   $('input[type=file]').parse({
   config: {
   skipEmptyLines: true,
   header: true,
   encoding: 'ISO-8859-1',
   complete: function(results, file) {
   $scope.filename = file.name;
   $scope.names = results.data
   isValidFile(results.meta.fields)

   },
   error: function(error, file) {
   $scope.validFile = false;
   Notification.error(error)
   },
   },
   before: function(file, inputElem) {
   // executed before parsing each file begins;
   // what you return here controls the flow

   },
   error: function(error, file) {
   Notification.error(error)
   },
   complete: function() {
   // console.log("Parsing complete:", results, file);
   }
   });
   }
   // watch the uploaded file to start parsing
   $scope.watch("namesfile", function(file) {
   parseForm()
   }, true)


   $scope.submitNames = function() {
   var request = endpointService.postJson('/v1/names/batch', $scope.names);
   request.success(function(data) {
   console.log(data)
   if (data === "success") {
   alert('upload successful')
   }
   }).error(function(data, status, headers, config) {
   console.log(data)
   $scope.postErrors.push(data);
   });
   }*/
}
angular.module('dashboardappApp').controller("uploadEntryController", uploadEntryController);
