'use strict';

/**
 * @ngdoc function
 * @name dashboardappApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the dashboardappApp
 */
angular.module('dashboardappApp')
  .controller('MainCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
