'use strict';
/**
 * Controller for the upload view
 * @param $scope angular scope
 * @param $element angular element
 * @param fileUploader service for uploading file to a url
 */
var uploadEntryController = function($scope, $element, fileUploader, ENV) {
    $scope.namesfile = {};
    $scope.names = [];
    $scope.postErrors = [];
    $scope.msg = {};
    $scope.validFile = false;
    var columns = ["name", "pronounciation", "ipanotation", "syllable", "meaning", "extended meaning", "morphology", "etymology", "geolocation", "media"]
    var columnsHash = {}
    $scope.invalidColumns = []
        /*Generate list of columns found in csv that's not in attribute list*/
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
        /*verify if invalid columns exist in csv*/
    var isValidFile = function(list) {
            $scope.invalidColumns = getInvalidColumns(list)
            if ($scope.invalidColumns.length < 1) {
                $scope.validFile = true
            }
            $scope.$apply()
        }
        /*Parse cvs to json, get callback to validated parse file*/
    var parseForm = function() {
            $('input[type=file]').parse({
                config: {
                    skipEmptyLines: true,
                    header: true,
                    complete: function(results, file) {
                        $scope.filename = file.name;
                        isValidFile(results.meta.fields)
                        $scope.names = results.data
                    },
                    error: function(error, file) {
                        console.log(error)
                        $scope.validFile = false;
                    },
                },
                before: function(file, inputElem) {
                    // executed before parsing each file begins;
                    // what you return here controls the flow
                    console.log(file)
                },
                error: function(error, file) {
                    console.log(error)
                },
                complete: function() {
                    // console.log("Parsing complete:", results, file);

                }
            });
        }
        /*watch the uploaded file to start parsing*/
    $scope.watch("namesfile", function(file) {
        parseForm()
    }, true)


    $scope.submitNames = function() {
        for (var i = $scope.names.length - 1; i >= 0; i--) {
            console.log($scope.names[i])
            request = endpointService.post('/v1/name', $scope.names[i]);
            request.success(function(data) {
                if (data === "success") {}
            }).error(function(data, status, headers, config) {
                $scope.postErrors.push(data);
            });
        }
    }

    if ($scope.namesfile === undefined) {
        $scope.msg.text = "You need to choose file before uploading";
        $scope.msg.type = "msg-error";
        return;
    }

    $scope.upload = function() {
        if ($scope.namesfile === undefined || Object.keys($scope.namesfile).length === 0) {
            $scope.msg.text = "You need to choose file before uploading";
            $scope.msg.type = "msg-error";
            return;
        }

        var success = function(response) {
            $scope.msg.text = "File successful uploaded";
            $scope.msg.type = "msg-success";
            $element[0].reset();

            setTimeout(function() {
                $scope.$apply(function() {
                    $scope.msg.text = "";
                    $scope.msg.type = "";
                });
            }, 5000);
        };

        var error = function(response) {
            $scope.msg.text = response.errorMessages[0];
            $scope.msg.type = "msg-error";
            setTimeout(function() {
                $scope.$apply(function() {
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
