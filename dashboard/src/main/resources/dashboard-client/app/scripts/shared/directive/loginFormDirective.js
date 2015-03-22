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
        var data = {
          email: scope.loginForm.email,
          password: scope.loginForm.password
        };

        var response = endpointService.postJson("/auth/login", data);
        response.success(function(response) {
          $cookies.isAuthenticated = true;
          $cookies.isAdmin = response.admin;
          $cookies.userName = response.username;

          scope.isAuthenticated = true;
          scope.isAdmin = response.admin;
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
