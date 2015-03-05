'use strict';

var dashboardappApp = angular.module('dashboardappApp', ['ui.router']).config(function ($stateProvider,
                                                                                        $urlRouterProvider) {
  $urlRouterProvider.otherwise('/home');

  //$stateProvider.state('home', {
  //  url: '/home',
  //  controller: 'homeController',
  //  templateUrl: 'scripts/components/homecomponent/views/home.html'
  //})
  //  .state('home.single', {
  //   url: '/single',
  //   templateUrl: 'scripts/components/homecomponent/views/single.html'
  //  })
  //  .state('home.upload', {
  //   url: '/upload',
  //   template: 'it works'
  //  });

  $stateProvider.state('entry', {
    url: '/entry',
    controller: 'entryController',
    templateUrl:  'views/entry.html'
  }).state('entry.single', {
    url: '/single',
    templateUrl: 'views/single.html'
  }).state('entry.upload', {
    url: '/upload',
    templateUrl: 'views/upload.html'
  });
  });
