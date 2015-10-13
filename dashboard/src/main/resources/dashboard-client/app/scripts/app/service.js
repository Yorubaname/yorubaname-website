/* Services */
angular.module('ui.load', [])
    .service('uiLoad', [
        '$document',
        '$q',
        '$timeout',
        function ($document, $q, $timeout) {
            var loaded = [
            ];
            var promise = false;
            var deferred = $q.defer();
            this.load = function (srcs) {
                srcs = angular.isArray(srcs) ? srcs : srcs.split(/\s+/);
                var self = this;
                if (!promise) {
                    promise = deferred.promise;
                }
                angular.forEach(srcs, function (src) {
                    promise = promise.then(function () {
                        return src.indexOf('.css') >= 0 ? self.loadCSS(src) : self.loadScript(src);
                    });
                });
                deferred.resolve();
                return promise;
            };
            this.loadScript = function (src) {
                if (loaded[src]) return loaded[src].promise;
                var deferred = $q.defer();
                var script = $document[0].createElement('script');
                script.src = src;
                script.onload = function (e) {
                    $timeout(function () {
                        deferred.resolve(e);
                    });
                };
                script.onerror = function (e) {
                    $timeout(function () {
                        deferred.reject(e);
                    });
                };
                $document[0].body.appendChild(script);
                loaded[src] = deferred;
                return deferred.promise;
            };
            this.loadCSS = function (href) {
                if (loaded[href]) return loaded[href].promise;
                var deferred = $q.defer();
                var style = $document[0].createElement('link');
                style.rel = 'stylesheet';
                style.type = 'text/css';
                style.href = href;
                style.onload = function (e) {
                    $timeout(function () {
                        deferred.resolve(e);
                    });
                };
                style.onerror = function (e) {
                    $timeout(function () {
                        deferred.reject(e);
                    });
                };
                var main_stylesheet = document.getElementById("mainCss");
                if (main_stylesheet) {
                    var parent_style = main_stylesheet.parentNode;
                    parent_style.insertBefore(style, main_stylesheet);
                } else {
                    $document[0].head.appendChild(style);
                }
                loaded[href] = deferred;
                return deferred.promise;
            };
        }
    ]);


/* API Endpoint Service for API requests: Adapted from code base */

angular.module('dashboardappApp')
  .service('api', ['$http','$rootScope', '$cookies', function($http, $rootScope, $cookies) {

    if ($cookies.token !== undefined) {
      $http.defaults.headers.common['Authorization'] = 'Basic ' + $cookies.token;
    }

    this.get = function(endpoint, data, headers) {
        return $http({
            method: 'GET',
            url: endpoint,
            params: data ? data : '',
            headers: headers ? headers : ''
        })
    }
    this.post = function(endpoint, data) {
        $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded";
        return $http({
            method: 'POST',
            url: endpoint,
            data: $.param(data)
        })
    }
    this.postJson = function(endpoint, data) {
        return $http({
            method: 'POST',
            url: endpoint,
            data: data ? data : ''
        })
    }
    this.put = function(endpoint, data) {
        $http.defaults.headers.put["Content-Type"] = "application/x-www-form-urlencoded";
        return $http({
            method: 'PUT',
            url: endpoint,
            data: data ? data : ''
        })
    }
    this.putJson = function(endpoint, data) {
        return $http({
            method: 'PUT',
            url: endpoint,
            data: data ? data : ''
        })
    }
    this.delete = function(endpoint, data) {
      $http.defaults.headers.put["Content-Type"] = "application/x-www-form-urlencoded";
      return $http({
        method: 'DELETE',
        url: endpoint,
        data: data ? data : ''
      })
    }
    this.deleteJson = function(endpoint, data) {
        // had to explicitly set the content-type for the delete request to work, Why? I do not know yet
        $http.defaults.headers.common['Content-Type'] = "application/json";
        return $http({
            method: 'DELETE',
            url: endpoint,
            data: data ? data : ''
        })
    }
    this.authenticate = function(authData) {
        $http.defaults.headers.common['Authorization'] = 'Basic ' + authData;
        return $http({
            method: 'POST',
            url: "/v1/auth/login"
        })
    }
}])


/* Authentication API Endpoint Service, Extension for API requests for Signing In, Out, and Session validation. Adapted from code base */

angular.module('dashboardappApp')
  .service('authApi', ['api','$cookies','$state','$rootScope','$timeout','toastr', function(api, $cookies, $state, $rootScope, $timeout, toastr){

    this.getUser = function getUser(callback) {
        return api.get("/v1/auth/user").then(function(response) {
            return response
        })
    }

    // Authenticates clients.
    // authData is the base64 encoding of username and password
    // on authentication the $rootScope and $cookie is updated as necessary
    this.authenticate = function(authData) {
        // encode authData to base64 here, instead
        authData = btoa(authData.email + ":" + authData.password)
        //console.log(authData)
        return api.authenticate(authData).success(function(response) {
            //console.log(response)
            $cookies.isAuthenticated = true;
            $rootScope.isAuthenticated = true;
            $cookies.username = response.username;
            $rootScope.username = $cookies.username;
            // TODO maybe not. This is a security loop hole
            $cookies.token = authData;
            $rootScope.user = {
              username: $cookies.username,
              email: $cookies.email
            }
            response.roles.some(function(role) {
                if (role === "ROLE_ADMIN") {
                    $cookies.isAdmin = true;
                    $rootScope.isAdmin = true;
                    return true
                }
            })
            $rootScope.msg = {}
            $timeout(function(){
              $state.go('auth.home')
              toastr.success( "Hey " + $rootScope.username + ", you are now successfully logged in.", 'Login Successful!')
            }, 200)
        }).error(function(response) {
            $cookies.isAuthenticated = false;
            $cookies.isAdmin = false;
            $rootScope.user = null;
            $rootScope.isAuthenticated = false;
            $rootScope.isAdmin = false;
            toastr.error(response.message, 'Authentication Error')
        })
    }

    // Log out function
    this.logout = function(){
      console.log('running service.logout')
      $cookies.isAuthenticated = false;
      $cookies.isAdmin = false;
      $rootScope.isAuthenticated = false;
      $rootScope.isAdmin = false;
      $timeout(function(){
        $state.go('login')
      }, 200)
    }

}])

/* Names API Endpoint Service, Extension for API requests for Name Entries resources only. Adapted from code base */

angular.module('dashboardappApp')
  .service('namesApi', ['api', function(api) {

      /**
      * Adds a name to the database;
      * @param nameEntry
      */
      this.addName = function (name) {
        return api.postJson("/v1/names", name)
      }
      this.deleteName = function (name) {
        return api.deleteJson("/v1/names/" + name, name)
      }

      /**
       * Get a name
       * returns the one or zero result
       */
      this.getName = function getName(name, duplicate) {
        return api.get('/v1/names/' + name, { duplicates: duplicate })
      }

      this.getNames = function getNames(page, count, filter) {
        filter = !isEmptyObj(filter) ? filter : {};
        filter.page = page;
        filter.count = count;
        return api.get('/v1/names/', filter)
      }

      this.getSuggestedNames = function() {
        return api.get('/v1/suggest')
      }
      this.deleteSuggestedName = function(name) {
        return api.delete("/v1/suggest/" + name)
      }

      this.addNameToIndex = function (name) {
        return api.postJson('/v1/search/indexes/' + name)
      }
      this.removeNameFromIndex = function (name) {
        return api.deleteJson('/v1/search/indexes/' + name)
      }

      this.addNamesToIndex = function (namesJson) {
        return api.postJson('/v1/search/indexes/batch', namesJson)
      }
      this.removeNamesFromIndex = function (namesJson) {
        return api.deleteJson('/v1/search/indexes/batch', namesJson)
      }

      this.getFeedback = function(name) {
        return api.get('/v1/names/'+name+'/', { feedback: true })
      }
      this.deleteFeedback = function(name) {
        return api.deleteJson('/v1/'+name+'/feedback')
      }

      this.getGeoLocations = function(){
        return api.get('/v1/admin/geolocations')
      }

}])


angular.module('dashboardappApp')
  .service('uploader', ['api', 'FileUploader', 'toastr', function(api, FileUploader, toastr) {

    FileUploader.prototype.setEndpoint = function setEndpoint(endpoint){
      this.url = endpoint
    }

    var options = options || {},
        fileType = options.fileType || ['text/csv','text/xslx'],
        maxUpload = options.maxUpload || 1,
        invalidFileMsg = options.invalidFileMsg || 'File type is not supported',
        uploadLimitMsg = options.uploadLimitMsg || 'You may only upload one file at a time',
        onError = options.onErrorMsg || 'An error occur while uploading the file, please retry',
        onComplete = options.onCompleteMsg || 'File upload completed successfully',
        errorCallback = options.errorCallback || function(){},
        successCallback = options.successCallback || function(){}

    return function(endpoint, options) {

      var uploader = new FileUploader({
          url: endpoint
      })

      // FILTERS

      uploader.filters.push({
          name: 'customFilter',
          fn: function(item /*{File|FileLikeObject}*/, options) {
              return (fileType.indexOf(item.type) >= 0 && this.queue.length < maxUpload)
          }
      })

      // CALLBACKS
      uploader.onWhenAddingFileFailed = function (item /*{File|FileLikeObject}*/, filter, options) {
        console.info('onWhenAddingFileFailed', item, filter, options);
        if (fileType.indexOf(item.type) < 0) return toastr.warning('Invalid File' + invalidFileMsg);
        if (this.queue.length == maxUpload) return toastr.warning('Single upload' + uploadLimitMsg);
      }
      uploader.onAfterAddingFile = function (fileItem) {
        console.info('onAfterAddingFile', fileItem);
      }
      uploader.onAfterAddingAll = function (addedFileItems) {
        console.info('onAfterAddingAll', addedFileItems);
        if (!options.manualTrigger) addedFileItems[0].upload();
      }
      uploader.onBeforeUploadItem = function (item) {
        console.info('onBeforeUploadItem', item);
      }
      uploader.onProgressItem = function (fileItem, progress) {
        console.info('onProgressItem', fileItem, progress);
      }
      uploader.onProgressAll = function (progress) {
        console.info('onProgressAll', progress);
      }
      uploader.onSuccessItem = function (fileItem, response, status, headers) {
        console.info('onSuccessItem', fileItem, response, status, headers);
      }
      uploader.onErrorItem = function (fileItem, response, status, headers) {
        console.info('onErrorItem', fileItem, response, status, headers);
        toastr.error('Upload Error' + onError);
        fileItem.remove();
        errorCallback(response);
      }
      uploader.onCancelItem = function (fileItem, response, status, headers) {
        console.info('onCancelItem', fileItem, response, status, headers);
      }
      uploader.onCompleteItem = function (fileItem, response, status, headers) {
        console.info('onCompleteItem', fileItem, response, status, headers);
        toastr.success('Upload Complete' + onComplete);
        console.log('before running cb');
        successCallback(response);
        console.log('after running cb');
        fileItem.remove();
      }
      uploader.onCompleteAll = function () {
        console.info('onCompleteAll');
      }

      return uploader

    }

  }
])