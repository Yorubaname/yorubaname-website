'use strict';

//TODO refactor. Especially the role selector
var createUserForm = function (endpointService, $cookies) {

  return {
    restrict: 'E',
    templateUrl: 'views/createuserform.directive.template.html',
    replace: true,
    link: function (scope, element, attrs) {
      scope.createUserForm = {};
      scope.msg = {};
      scope.buttonAction = "Create New User";
      scope.createUser = function () {


        var response = endpointService.post("/auth/create", scope.createUserForm);
        response.success(function(response) {
          scope.msg.type = "msg-success";
          scope.msg.text = "User successfully created";
          setTimeout(function () {
            scope.$apply(function () {
              scope.msg.text = "";
              scope.msg.type = "";
            });
          }, 5000);

        }).error(function(response) {

          scope.msg.type = "msg-error";
          scope.msg.text = "Can not create user due to:" + response;

          setTimeout(function () {
            scope.$apply(function () {
              scope.msg.text = "";
              scope.msg.type = "";
            });
          }, 5000);
        });
      }
    }
  };
};

angular.module('dashboardappApp').directive('createUserForm', createUserForm);
