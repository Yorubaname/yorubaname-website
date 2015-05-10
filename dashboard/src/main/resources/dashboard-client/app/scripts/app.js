'use strict';

var dashboardappApp = angular.module('dashboardappApp', ['ui.router', 'ngCookies', 'ngTagsInput', 'ui.bootstrap', 'env'])
    .config(function($stateProvider, $urlRouterProvider) {
        $urlRouterProvider.otherwise('/home');

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

angular.module('dashboardappApp').controller('indexLogin', function($scope, $cookies) {

    $scope.logoutUser = function() {
        $cookies.isAuthenticated = false;
        $cookies.isAdmin = false;
        $scope.isAuthenticated = false;
        $scope.isAdmin = false;
    };

    if ($cookies.isAuthenticated && $cookies.isAuthenticated === 'true') {
        $scope.isAuthenticated = true;
    }

    if ($cookies.isAdmin && $cookies.isAdmin === 'true') {
        $scope.isAdmin = true;
    }

});


dashboardappApp.config(function(tagsInputConfigProvider) {
    /*Global configuration for tags inputs for text seperation*/
    tagsInputConfigProvider.setDefaults('tagsInput', {
        addOnSpace: true,
        addOnPaste: true,
        minLength: 1

    });
});
