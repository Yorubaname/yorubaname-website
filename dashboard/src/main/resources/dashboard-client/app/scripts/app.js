'use strict';

var dashboardappApp = angular.module('dashboardappApp', ['ui.router', 'ngCookies', 'ngTagsInput', 'ui.bootstrap', 'env', 'ui.bootstrap.showErrors', 'ui-notification', 'ngFileUpload'])
    .config(function($stateProvider, $urlRouterProvider, $httpProvider, tagsInputConfigProvider, NotificationProvider) {
        $urlRouterProvider.otherwise('/home');
        // $httpProvider.interceptors.push('authHttpResponseInterceptor');
        tagsInputConfigProvider.setDefaults('tagsInput', {
            addOnPaste: true,
            minLength: 1
        });

        NotificationProvider.setOptions({
            delay: 10000,
            startTop: 20,
            startRight: 10,
            verticalSpacing: 20,
            horizontalSpacing: 20,
            positionX: 'right',
            positionY: 'top'
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
            url: '/all?submittedBy',
            controller: 'listAllController',
            templateUrl: 'views/list.all.html'
        }).state('list.edit', {
            url: '/edit',
            controller: 'editNameController',
            templateUrl: 'views/list.edit.html'
        }).state('list.suggestedNames', {
            url: '/suggestedNames',
            controller: 'listSuggestedNamesController',
            templateUrl: 'views/list.suggestedNames.html'
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
    console.log($cookies.isAuthenticated)

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

    // Commenting this out as it was leading to 401 on login page. Needs further looking into
    //$http.defaults.headers.common['Authorization'] = 'Basic ' + $cookies.token;

    $rootScope.$on('$stateChangeSuccess', function(ev, to, toParams, from, fromParams) {
        $rootScope.previousState = from.name;
        $rootScope.currentState = to.name;
        $rootScope.previousParams = fromParams;
    });
});
