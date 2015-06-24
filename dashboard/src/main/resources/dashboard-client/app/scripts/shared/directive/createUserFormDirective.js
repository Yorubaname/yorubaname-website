'use strict';

//TODO refactor. Especially the role selector
var createUserForm = function(endpointService, $cookies, Notification) {

    return {
        restrict: 'E',
        templateUrl: 'views/createuserform.directive.template.html',
        replace: true,
        link: function(scope, element, attrs) {
            scope.createUserForm = {};
            scope.msg = {};
            scope.buttonAction = "Create New User";
            scope.createUser = function() {

                var response = endpointService.postJson("/v1/auth/create", scope.createUserForm);
                response.success(function(response) {
                    Notification.success('User successfully created')

                }).error(function(response) {
                    Notification.error('Can not create user due to:' + response.message)
                });
            }
        }
    }
};

angular.module('dashboardappApp').directive('createUserForm', createUserForm);
