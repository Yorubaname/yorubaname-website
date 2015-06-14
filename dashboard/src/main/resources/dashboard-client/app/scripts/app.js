'use strict';

var dashboardappApp = angular.module('dashboardappApp', ['ui.router', 'ngCookies', 'ngTagsInput', 'ui.bootstrap', 'env', 'ui.bootstrap.showErrors'])
    .config(function($stateProvider, $urlRouterProvider, $httpProvider, tagsInputConfigProvider) {
        $urlRouterProvider.otherwise('/home');
        // $httpProvider.interceptors.push('authHttpResponseInterceptor');
        tagsInputConfigProvider.setDefaults('tagsInput', {
            addOnSpace: true,
            addOnPaste: true,
            minLength: 1

        });

        $stateProvider.state('home', {
            url: '/home',
            controller: 'homeController',
            templateUrl: 'views/home.html'
        });
        $stateProvider.state('entry', {
            url: '/entry',
            controller: 'entryController',
            templateUrl: 'views/entry.html'
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

        $stateProvider.state('admin', {
            url: '/admin',
            controller: 'adminController',
            templateUrl: 'views/admin.html'
        }).state('admin.createuser', {
            url: '/createuser',
            controller: 'adminCreateUserController',
            templateUrl: 'views/admin.createuser.html'
        }).state('admin.allusers', {
            url: '/allusers',
            controller: 'adminAllUsersController',
            templateUrl: 'views/admin.allusers.html'
        });

    });


angular.module('dashboardappApp').controller('search', function($scope) {
    $scope.search = function() {
        window.location.href = '#/list/edit?name=' + $scope.searchTerm;
    };
});

angular.module('dashboardappApp').controller('indexLogin', function($scope, $cookies, $rootScope) {

    $scope.logoutUser = function() {
        $cookies.isAuthenticated = false;
        $cookies.isAdmin = false;
        $rootScope.isAuthenticated = false;
        $rootScope.isAdmin = false;
    };
    console.log($cookies.isAdmin)

    if ($cookies.isAuthenticated && $cookies.isAuthenticated === 'true') {
        $rootScope.isAuthenticated = true;
    }
    if ($cookies.isAdmin && $cookies.isAdmin === 'true') {
        $rootScope.isAdmin = true;
    }



});

dashboardappApp.run(function($rootScope, $cookies, $http) {
    $rootScope.dashboardEndpoint = "http://localhost:8081/dashboard";
    $rootScope.appEndpoint = "http://localhost:8081";
    $rootScope.previousState;
    $rootScope.previousParams;
    $rootScope.currentState;
    $rootScope.state

    $http.defaults.headers.common['Authorization'] = 'Basic ' + $cookies.token;

    $rootScope.$on('$stateChangeSuccess', function(ev, to, toParams, from, fromParams) {
        $rootScope.previousState = from.name;
        $rootScope.currentState = to.name;
        $rootScope.previousParams = fromParams;
    });
});
