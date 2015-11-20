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

dashboardappApp

  .service('api', ['$http', '$rootScope', function($http, $rootScope) {

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
            //type: 'json',
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

dashboardappApp

  .service('authApi', ['api', 'usersApi', '$localStorage', '$state', '$rootScope', '$timeout', 'toastr', 'baseUrl', function(api, usersApi, $localStorage, $state, $rootScope, $timeout, toastr, baseUrl){

    this.getUser = function(callback) {
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
            $localStorage.isAuthenticated = true;
            $rootScope.isAuthenticated = true;
            $localStorage.id = response.id;
            $localStorage.username = response.username;
            $localStorage.email = response.email;
            $rootScope.username = $localStorage.username;
            // TODO maybe not. This is a security loop hole
            $localStorage.token = authData;

            $rootScope.user = {
              username: $localStorage.username,
              email: $localStorage.email,
              id: $localStorage.id
            }

            $rootScope.baseUrl = baseUrl;
            $localStorage.baseUrl = baseUrl;

            response.roles.some(function(role) {
                // Check ROLE_ADMIN first, since it supercedes all
                if (role === "ROLE_ADMIN") {
                    $localStorage.isAdmin = true;
                    $rootScope.isAdmin = true;
                    return true
                }
                else if (role === "ROLE_LEXICOGRAPHER") {
                    $localStorage.isLexicographer = true;
                    $rootScope.isLexicographer = true;
                    return true
                }
            })
            
            $rootScope.msg = {}
            $timeout(function(){
              $state.go('auth.home')
              toastr.success( "Hey " + $rootScope.username + ", you are now successfully logged in.", 'Login Successful!')
            }, 200)
        }).error(function(response) {
            $localStorage.isAuthenticated = false;
            $localStorage.isAdmin = false;
            $localStorage.isLexicographer = false;
            $localStorage.id = null;
            $rootScope.user = null;
            $rootScope.isAuthenticated = false;
            $rootScope.isAdmin = false;
            toastr.error(response.message, 'Sign In Error')
        })
    }

    // Log out function
    this.logout = function(){
      $localStorage.isAuthenticated = false;
      $localStorage.isAdmin = false;
      $rootScope.isAuthenticated = false;
      $rootScope.isAdmin = false;
      $rootScope.user = null;
      $localStorage.user = null;
      $timeout(function(){
        $state.go('login')
        toastr.info('You are now logged out.')
      }, 200)
    }

}])

/* Users API Endpoint Service */

dashboardappApp

  .service('usersApi', ['api', '$state', 'toastr', function(api, $state, toastr){

    this.getUser = function(userId){
      return api.get('/v1/users/'+userId)
    }

    this.addUser = function(user){
      return api.postJson("/v1/auth/create", user)
               .success(function(resp) {
                  toastr.success('User account with email '+user.email+' successfully created.')
               })
               .error(function(resp) {
                 console.log(resp)
                 toastr.error('User account could not be created. Try again.')
               })
    }

    /* updated user information */
    this.updateUser = function(user){
      return api.putJson("/v1/auth/users", user)
               .success(function(resp) {
                  toastr.success('User account with email '+user.email+' successfully updated.')
                  $state.go('auth.users.list_users')
               })
               .error(function(resp) {
                 console.log(resp)
                 toastr.error('User account could not be updated. Try again.')
               })
    }

  }])

/* Names API Endpoint Service, Extension for API requests for Name Entries resources only. Adapted from code base */

dashboardappApp

  .service('namesApi', ['api', 'toastr', '$state', '$localStorage', '$timeout', '_', function(api, toastr, $state, $localStorage, $timeout, _) {

      var _this = this;

      /**
      * Adds a name to the database;
      * @param nameEntry
      */
      this.addName = function (name, fn) {
        // include logged in user's details
        name.submittedBy = $localStorage.email;
        name.geoLocation = JSON.parse( name.geoLocation || '{}' )
        return api.postJson("/v1/names", name).success(function(resp){
          toastr.success(name.name + ' was successfully added. <br>Add another name')
          fn()
          cacheNames()
        }).error(function(resp){
          toastr.error(name.name + ' could not be added. Please try again.')
        })
      }

      this.prevAndNextNames = function(name, fn){
        if(! $localStorage.entries.length )
        {
          api.get('/v1/names').success(function(resp){
            $localStorage.entries = resp
          })
        }

        return $timeout(function(){
          // find the index of the current name in the names
          var index = _.findIndex($localStorage.entries, { name: name }),
             prev = $localStorage.entries[index - 1],
             next = $localStorage.entries[index + 1]
          return fn(prev, next)
        }, 2500)
      }

      var cacheNames = function(){
        return api.get('/v1/names').success(function(resp){
          $localStorage.entries = resp
        })
      }

      this.getCachedNames = function(fn){
        if(! $localStorage.entries.length )
        {
          api.get('/v1/names').success(function(resp){
            $localStorage.entries = resp
          })
        }
        return $timeout(function(){
          return fn($localStorage.entries)
        }, 500)
      }

      /**
      * Updates an existing name in the database;
      * @param nameEntry
      */
      this.updateName = function(originalName, nameEntry){
        nameEntry = angular.copy( nameEntry )
        nameEntry.geoLocation = JSON.parse( nameEntry.geoLocation || '{}' )
        return api.putJson("/v1/names/" + originalName, nameEntry).success(function(resp){
          toastr.success(nameEntry.name + ' was successfully updated.')
          cacheNames()
        }).error(function(resp){
          toastr.error(nameEntry.name + ' could not be updated. Please try again.')
        })
      }

      /**
      * Deletes a name from the database;
      * @param nameEntry
      */
      this.deleteName = function (name) {
        return api.deleteJson("/v1/names/" + name.name, name).success(function(resp){
          toastr.success(name.name + ' has been deleted successfully')
          $state.go('auth.names.list_entries', {status:"all"})
          cacheNames()
        }).error(function(resp){
          toastr.error(name.name + ' could not be deleted. Please try again.')
        })
      }

      /**
       * Get a name
       * returns the one or zero result
       */
      this.getName = function (name, duplicate) {
        return api.get('/v1/names/' + name, { duplicates: duplicate })
      }

      this.getNames = function (filter) {
        filter = !isEmptyObj(filter) ? filter : {}
        if (filter.status == 'suggested') return api.get('/v1/suggest')
        if (filter.status == 'published') filter.indexed = true;
        // page, count, 
        // filter.page = page || 1
        // filter.count = count || 10
        filter.orderBy = 'createdAt'
        return api.get('/v1/names', filter)
      }

      this.countNames = function(status, fn){
        return _this.getNames({ status: status }).success(function(resp){
          return fn(resp.length)
        })
      }

      this.deleteSuggestedName = function(name) {
        return api.delete("/v1/suggest/" + name).success(function(resp){
          toastr.success(name + ' has been deleted successfully')
          //$state.go('auth.names.suggested')
        }).error(function(resp){
          toastr.error(name + ' could not be deleted. Please try again.')
        })
      }

      this.addNameToIndex = function (name) {
        return api.postJson('/v1/search/indexes/' + name)
      }
      this.removeNameFromIndex = function (name) {
        return api.deleteJson('/v1/search/indexes/' + name)
      }

      this.addNamesToIndex = function (namesJsonArray) {
        var names = _.pluck(namesJsonArray, 'name')
        return api.postJson('/v1/search/indexes/batch', names)
      }
      this.removeNamesFromIndex = function (namesJsonArray) {
        var names = _.pluck(namesJsonArray, 'name')
        return api.deleteJson('/v1/search/indexes/batch', names)
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


dashboardappApp

  .service('uploader', ['FileUploader', 'baseUrl', 'toastr', '$localStorage', '$state', function(FileUploader, baseUrl, toastr, $localStorage, $state) {

    FileUploader.prototype.setEndpoint = function setEndpoint(endpoint){
      this.url = baseUrl + endpoint
    }

    return function(endpoint, options) {

      var uploader = new FileUploader({ 
          url: baseUrl + endpoint, 
          alias:'nameFiles',
          headers:{
            Authorization: 'Basic ' + $localStorage.token
          }
        }),
        options = options || {},
        fileType = options.fileType || ['text/csv','application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'],
        maxUpload = options.maxUpload || 1,
        invalidFileMsg = options.invalidFileMsg || 'File type is not supported',
        uploadLimitMsg = options.uploadLimitMsg || 'You may only upload one file at a time',
        onError = options.onErrorMsg || 'An error occur while uploading the file, please retry',
        onComplete = options.onCompleteMsg || 'File upload completed successfully',
        errorCallback = options.errorCallback || function(){},
        successCallback = options.successCallback || function(){}

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
        //console.info('onErrorItem', fileItem, response, status, headers);
        toastr.error('Upload Error' + onError);
        fileItem.remove();
        errorCallback(response);
      }
      uploader.onCancelItem = function (fileItem, response, status, headers) {
        console.info('onCancelItem', fileItem, response, status, headers);
      }
      uploader.onCompleteItem = function (fileItem, response, status, headers) {
        //console.info('onCompleteItem', fileItem, response, status, headers);
        toastr.success('Upload Complete' + onComplete);
        //console.log('before running cb');
        successCallback(response)
        //console.log('after running cb');
        fileItem.remove()
        $state.go("auth.names.list_entries", {status:'all'})
      }
      uploader.onCompleteAll = function () {
        console.info('onCompleteAll');
      }

      return uploader

    }

  }
])