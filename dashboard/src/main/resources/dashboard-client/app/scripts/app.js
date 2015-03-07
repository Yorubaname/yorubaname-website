'use strict';

var dashboardappApp = angular.module('dashboardappApp', ['ui.router'])
  .config(function ($stateProvider, $urlRouterProvider) {
  $urlRouterProvider.otherwise('/home');

  $stateProvider.state('home', {
    url: '/home',
    controller: 'homeController',
    templateUrl: 'scripts/components/homecomponent/views/home.html'
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
  });

dashboardappApp.run(function($rootScope) {
  $rootScope.dashboardEndpoint = "http://localhost:8081/dashboard";
  $rootScope.appEndpoint = "http://localhost:8081";
});
