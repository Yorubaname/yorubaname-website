'use strict';

var dashboardappApp = angular.module('dashboardappApp', ['ui.router'])
  .config(function ($stateProvider, $urlRouterProvider) {
  $urlRouterProvider.otherwise('/home');

  $stateProvider.state('home', {
    url: '/home',
    controller: 'homeController',
    templateUrl: 'views/home.html'
  });

  $stateProvider.state('entry', {
    url: '/entry',
    controller: 'entryController',
    templateUrl:  'views/entry.html'
  }).state('entry.single', {
    url: '/single',
    templateUrl: 'views/entry.single.html'
  }).state('entry.upload', {
    url: '/upload',
    templateUrl: 'views/entry.upload.html'
  });

   $stateProvider.state('list', {
     url: '/list',
     controller: 'listController',
     templateUrl: 'views/list.html'
   }).state('list.all', {
     url: '/all',
     controller: 'listAllController',
     templateUrl: 'views/list.all.html'
   }).state('list.edit', {
     url: '/edit',
     controller: 'editNameController',
     templateUrl: 'views/list.edit.html'
   });


  });


angular.module('dashboardappApp').controller("search", function($scope, $location){
  $scope.search = function () {
    window.location.href = "#/list/edit?name="+$scope.searchTerm;
};
});

dashboardappApp.run(function($rootScope) {
  $rootScope.dashboardEndpoint = "http://localhost:8081/dashboard";
  $rootScope.appEndpoint = "http://localhost:8081";
});
