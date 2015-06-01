'use strict';

//TODO refactor
var loginForm = function (endpointService, $cookies) {

  return {
    restrict: 'E',
    templateUrl: 'views/loginform.directive.template.html',
    replace: true,
    link: function (scope, element, attrs) {
      scope.loginForm = {};
      scope.msg = {};
      scope.buttonAction = "Login";
      scope.login = function () {

        var authData = btoa(scope.loginForm.email + ":" + scope.loginForm.password);
        var response = endpointService.authenticate(authData);
        response.success(function(response) {
          $cookies.isAuthenticated = true;
          scope.isAuthenticated = true;
          $cookies.userName = response.username;
          $cookies.token = authData;

          response.roles.every(function(role) {
            if (role === "ROLE_ADMIN") {
              $cookies.isAdmin = true;
              scope.isAdmin = response.admin;
              return false;
            }
          });

          scope.msg = {};
          window.location.href = "#/home";

        }).error(function(response){
          $cookies.isAuthenticated = false;
          $cookies.isAdmin = response.isAdmin;

          scope.isAuthenticated = false;
          scope.isAdmin = response.isAdmin;
          scope.msg.type = "msg-error";
          scope.msg.text = "Can not login with the credentials provided";
        });

      };
    }
  };
};

angular.module('dashboardappApp').directive('loginForm', loginForm);
