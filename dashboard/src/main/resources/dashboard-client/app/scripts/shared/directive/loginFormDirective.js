'use strict';

//TODO refactor
var loginForm = function(authService, $rootScope) {

    return {
        restrict: 'E',
        templateUrl: 'views/loginform.directive.template.html',
        replace: true,
        link: function(scope, element, attrs) {
            scope.loginForm = {};
            scope.msg = {};
            scope.buttonAction = "Login";
            scope.login = function() {
                var authData = btoa(scope.loginForm.email + ":" + scope.loginForm.password);
                console.log(authData)
                authService.authenticate(authData, $rootScope);

            };
        }
    };
};

angular.module('dashboardappApp').directive('loginForm', loginForm);
