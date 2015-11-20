/* States */
dashboardappApp

    .config(['$stateProvider', '$urlRouterProvider',
        function ($stateProvider, $urlRouterProvider) {

            // Use $urlRouterProvider to configure any redirects (when) and invalid urls (otherwise).
            $urlRouterProvider
                .when('/home', '/')
                .otherwise('/login');

            // State Configurations
            $stateProvider

                // Login Page
                .state("login", {
                    page_title: 'Yoruba Names - Admin - Login',
                    url: "/login",
                    templateUrl: 'tmpls/login.html',
                    controller: 'loginCtrl',
                    data:{
                      requiresLogout:true
                    }
                })
                // Errors
                .state("error", {
                    url: "/error",
                    abstract: true,
                    template: '<div ui-view autoscroll="false" class="mainView-animate"></div>'
                })
                // Errors > 404
                .state("error.404", {
                    page_title: 'Yoruba Names - Admin - Error 404',
                    url: "/404",
                    templateUrl: 'tmpls/error.404.html'
                })
                // Authenticated
                .state("auth", {
                    abstract: true,
                    // this state url
                    url: "",
                    templateUrl: 'tmpls/authenticated.html',
                    data:{
                      requiresLogin:true,
                      requiresAdminPriviledge:true
                    }
                })
                // Dashboard
                .state("auth.home", {
                    // this state page title
                    page_title: 'Yoruba Names - Admin - Dashboard',
                    // this state url
                    url: "/",
                    templateUrl: 'tmpls/dashboard.html',
                    ncyBreadcrumb: {
                        label: 'Home'
                    },
                    controller: 'dashboardCtrl'
                })
                // Names (parent state)
                .state('auth.names', {
                    // With abstract set to true, that means this state can not be explicitly activated.
                    abstract: true,
                    url: '/names',
                    template: '<div ui-view autoscroll="false" class="mainView-animate"></div>'
                })
                // Names > New Entries
                .state('auth.names.add_entries', {
                    page_title: 'Yoruba Names - Admin - Add Name Entries',
                    ncyBreadcrumb: {
                        label: 'Add Name Entries'
                    },
                    url: '/new',
                    templateUrl: 'tmpls/names/new.html',
                    controller:'namesAddEntriesCtrl'
                })

                // edit Name Entry
                .state('auth.names.edit_entries', {
                    page_title: 'Yoruba Names - Admin - Edit Entry',
                    ncyBreadcrumb: {
                        label: 'Edit Entry'
                    },
                    url: '/edit/:entry',
                    templateUrl: 'tmpls/names/edit.html',
                    controller:'namesEditEntryCtrl'
                })


                // Names > Published Names
                .state('auth.names.list_entries', {
                    page_title: 'Yoruba Names - Admin - Names',
                    ncyBreadcrumb: {
                        label: 'Names Entries'
                    },
                    url: '/lists/:status?:submmittedBy',
                    templateUrl: 'tmpls/names/lists.html',
                    controller: 'namesListEntriesCtrl'
                })

                .state('auth.names.own_entries', {
                    page_title: 'Yoruba Names - Admin - Own Name Entries',
                    ncyBreadcrumb: {
                        label: 'Your Entries'
                    },
                    url: '/your_entries',
                    templateUrl: 'tmpls/names/sorted_lists.html',
                    controller: 'namesByUserListCtrl'
                })

                /*.state('auth.names.suggested_names', {
                    page_title: 'Yoruba Names - Suggested Names',
                    ncyBreadcrumb: {
                        label: 'Suggested Names'
                    },
                    url: '/suggested',
                    templateUrl: 'tmpls/names/suggested.html',
                    controller: 'namesSuggestedEntriesCtrl'
                })*/
                
                // Names > Name Search
                .state('auth.names.search', {
                    page_title: 'Yoruba Names - Admin - Name Search',
                    ncyBreadcrumb: {
                        label: 'Search'
                    },
                    url: '/search/:entry',
                    templateUrl: 'tmpls/names/search.html',
                    controller: 'namesSearchCtrl'
                })
                
                // Pages (parent state)
                .state('auth.profile', {
                    abstract: true,
                    url: '/profile',
                    template: '<div ui-view autoscroll="false" class="mainView-animate"></div>'
                })
                
                // Pages > Contact List
                .state('auth.profile.index', {
                    page_title: 'Yourba Names Admin - User Profile',
                    ncyBreadcrumb: {
                        label: 'User Profile'
                    },
                    url: '/',
                    templateUrl: 'tmpls/profile/index.html',
                    controller: 'profileIndexCtrl'
                })

                // Pages > Faq/Help
                .state('auth.profile.edit', {
                    page_title: 'Yoruba Names Admin - Update User Profile',
                    ncyBreadcrumb: {
                        label: 'Update Profile'
                    },
                    url: '/edit',
                    templateUrl: 'tmpls/profile/edit.html',
                    controller: 'profileEditCtrl'
                })

                // Pages (parent state)
                .state('auth.users', {
                    abstract: true,
                    url: '/users',
                    template: '<div ui-view autoscroll="false" class="mainView-animate"></div>'
                })

                // Pages > Faq/Help > Categories
                .state('auth.users.add_user', {
                    page_title: 'Yoruba Names Admin - Add User',
                    ncyBreadcrumb: {
                        label: 'Add User'
                    },
                    url: '/new',
                    templateUrl: 'tmpls/users/new.html',
                    controller: 'userAddCtrl'
                })

                .state('auth.users.edit_user', {
                    page_title: 'Yoruba Names Admin - Edit User',
                    ncyBreadcrumb: {
                        label: 'Edit User Info'
                    },
                    url: '/edit/:id',
                    templateUrl: 'tmpls/users/edit.html',
                    controller: 'userEditCtrl'
                })

                .state('auth.users.list_users', {
                    page_title: 'Yoruba Names Admin - All Users',
                    ncyBreadcrumb: {
                        label: 'All Users'
                    },
                    url: '/lists/:role',
                    templateUrl: 'tmpls/users/lists.html',
                    controller: 'userListCtrl'
                })

        }
    ])