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
  .service('authApi', ['api','$cookies','$state','$rootScope','$timeout','growl', function(api, $cookies, $state, $rootScope, $timeout, growl){

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
            $cookies.isAuthenticated = true;
            $rootScope.isAuthenticated = true;
            $cookies.username = response.username;
            $rootScope.username = $cookies.username;
            // TODO maybe not. This is a security loop hole
            $cookies.token = authData;
            response.roles.some(function(role) {
                if (role === "ROLE_ADMIN") {
                    $cookies.isAdmin = true;
                    $rootScope.isAdmin = true;
                    return true
                }
            })
            $rootScope.msg = {}
            $timeout(function(){
              growl.success("Hey " + $rootScope.username + ", you are now successfully logged in.", {})
              $state.go('auth.home')
            }, 200)
        }).error(function(response) {
            console.log(growl)
            console.log(response)
            $cookies.isAuthenticated = false;
            $cookies.isAdmin = false;
            $rootScope.isAuthenticated = false;
            $rootScope.isAdmin = false;
            growl.error(response.message, {})
            // currentScope.msg.type = "msg-error";
            // currentScope.msg.text = "Can not login with the credentials provided";
        })
    }

    // Logs out
    this.signout = function(){
        return api.delete('/v1/sessions/'+$scope.app.session._id).success(function(){
          // $scope.app.session = {};
          $cookies.isAuthenticated = false;
          $cookies.isAdmin = false;
          currentScope.isAuthenticated = false;
          currentScope.isAdmin = false;
          $timeout(function(){
            $state.go('index.signin')
          }, 200)
        })
      }
}])

/* Names API Endpoint Service, Extension for API requests for Name Entries resources only. Adapted from code base */

angular.module('dashboardappApp')
  .service('namesApi', ['api', function(api) {

      /**
      * Adds a name to the database;
      * @param nameEntry
      */
      this.addName = function (nameToAdd) {
        return api.postJson("/v1/names", nameToAdd)
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

      this.deleteSuggestedName = function(suggestedName) {
        return api.delete("/v1/suggest/" + suggestedName)
      }

      this.indexName = function (name) {
        return api.postJson('/v1/search/indexes/' + name)
      }

      this.removeNameFromIndex = function (name) {
        return api.deleteJson('/v1/search/indexes/' + name)
      }

      this.getFeedback = function(name) {
        return api.get('/v1/names/'+name+'/', { feedback: true })
      }

      this.deleteFeedback = function(name) {
        return api.deleteJson('/v1/'+name+'/feedback')
      }

}])