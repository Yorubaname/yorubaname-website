/* Controllers */
dashboardappApp

    .controller('mainCtrl', [
        '$scope',
        function ($scope){
        }
    ])

    .controller('headerCtrl', [
        '$scope',
        'authApi',
        function ($scope, api){
            $scope.logout = function (){
                return api.logout()
            }
        }
    ])
    
    .controller('sideMenuCtrl', [
        '$rootScope',
        '$scope',
        '$state',
        '$stateParams',
        '$timeout',
        function ($rootScope, $scope, $state, $stateParams, $timeout) {
            $scope.sections = [
                {
                    id: 0,
                    title: 'Dashboard',
                    icon: 'fa fa-home first_level_icon',
                    link: 'auth.home'
                },
                {
                    id: 1,
                    title: 'Names Entries',
                    icon: 'fa fa-list-ol first_level_icon',
                    submenu_title: 'Names Entries',
                    submenu: [
                        {
                            title: 'Add Entries',
                            link: 'auth.names.add_entries'
                        },
                        {
                            title: 'All Name Entries',
                            link: "auth.names.list_entries({status:'all'})"
                        },
                        {
                            title: 'Published Names',
                            link: "auth.names.list_entries({status:'published'})"
                        },
                        {
                            title: 'Suggested Names',
                            link: "auth.names.list_entries({status:'suggested'})"
                        },
                        {
                            title: 'Your Entries',
                            link: "auth.names.own_entries"
                        },
                        {
                            title: 'Search',
                            link: 'auth.names.search'
                        }
                    ]
                },
                {
                    id: 2,
                    title: 'Users',
                    icon: 'fa fa-users first_level_icon',
                    submenu_title: 'Users',
                    submenu: [
                        {
                            title: 'Add User',
                            link: 'auth.users.add_user'
                        },
                        {
                            title: 'Users',
                            link: "auth.users.list_users({role:'all'})"
                        }
                    ]
                }
            ];

            // accordion menu
            $(document).off('click', '.side_menu_expanded #main_menu .has_submenu > a').on('click', '.side_menu_expanded #main_menu .has_submenu > a', function () {
                if($(this).parent('.has_submenu').hasClass('first_level')) {
                    var $this_parent = $(this).parent('.has_submenu'),
                        panel_active = $this_parent.hasClass('section_active');

                    if (!panel_active) {
                        $this_parent.siblings().removeClass('section_active').children('ul').slideUp('200');
                        $this_parent.addClass('section_active').children('ul').slideDown('200');
                    } else {
                        $this_parent.removeClass('section_active').children('ul').slideUp('200');
                    }
                } else {
                    var $submenu_parent = $(this).parent('.has_submenu'),
                        submenu_active = $submenu_parent.hasClass('submenu_active');

                    if (!submenu_active) {
                        $submenu_parent.siblings().removeClass('submenu_active').children('ul').slideUp('200');
                        $submenu_parent.addClass('submenu_active').children('ul').slideDown('200');
                    } else {
                        $submenu_parent.removeClass('submenu_active').children('ul').slideUp('200');
                    }
                }
            })

            $rootScope.createScrollbar = function() {
                $("#main_menu .menu_wrapper").mCustomScrollbar({
                    theme: "minimal-dark",
                    scrollbarPosition: "outside"
                })
            }

            $rootScope.destroyScrollbar = function() {
                $("#main_menu .menu_wrapper").mCustomScrollbar('destroy');
            }

            $timeout(function() {
                if(!$rootScope.sideNavCollapsed && !$rootScope.topMenuAct) {
                    if(!$('#main_menu .has_submenu').hasClass('section_active')) {
                        $('#main_menu .has_submenu .act_nav').closest('.has_submenu').children('a').click();
                    } else {
                        $('#main_menu .has_submenu.section_active').children('ul').show();
                    }
                    // init scrollbar
                    $rootScope.createScrollbar()
                }
            })
        }
    ])

    .controller('loginCtrl', [
        '$scope',
        'authApi',
        function ($scope, api) {
            $scope.login = {}
            $scope.submit = function (){
                return api.authenticate($scope.login, $scope)
            }
        }
    ])

    .controller('dashboardCtrl', [
        '$scope',
        'namesApi',
        function ($scope, api) {

            api.countNames('published', function(num){
                $scope.count_published_names = num
                $('.countUpMe .published_names').each(function() {
                    var target = this,
                    endVal = num,
                    theAnimation = new countUp(target, 0, endVal, 0, 2.6, { useEasing : true, useGrouping : true, separator: ' ' });
                    theAnimation.start()
                })

            })

            api.countNames('suggested', function(num){
                $scope.count_suggested_names = num
                $('.countUpMe .suggested_names').each(function() {
                    var target = this,
                    endVal = num,
                    theAnimation = new countUp(target, 0, endVal, 0, 2.6, { useEasing : true, useGrouping : true, separator: ' ' });
                    theAnimation.start()
                })

            })

            api.countNames('all', function(num){
                $scope.count_all_names = num
                $('.countUpMe .all_names').each(function() {
                    var target = this,
                    endVal = num,
                    theAnimation = new countUp(target, 0, endVal, 0, 2.6, { useEasing : true, useGrouping : true, separator: ' ' });
                    theAnimation.start()
                })

            })

            $scope.latestNames = []
            api.getNames(1,5).success(function(responseData){
                responseData.forEach(function(name) {
                    $scope.latestNames.push(name)
                })
            })

        }
    ])

    .controller('namesAddEntriesCtrl', [
        '$scope',
        'uploader',
        'namesApi',
        function($scope, Uploader, api) {

            $scope.uploader = Uploader('/v1/names/upload')

            $scope.new = true
            $scope.name = {}

            $scope.submit = function(){
                return api.addName($scope.name, function(){
                    // reset the form models fields
                    $scope.name = {}
                })
            }

        }
    ])

    .controller('namesEditEntryCtrl', [
        '$scope',
        '$stateParams',
        'namesApi',
        function($scope, $stateParams, api) {

            var originalName = null

            api.prevAndNextNames($stateParams.entry, function(prev, next){
                $scope.prev = prev
                $scope.next = next
            })

            api.getName($stateParams.entry).success(function(resp){
                $scope.name = resp
                originalName = resp.name
                $scope.name.geoLocation = JSON.stringify( $scope.name.geoLocation )
            })

            $scope.submit = function(){
                return api.updateName(originalName, $scope.name)
            }

            $scope.delete = function(){
                if (confirm("Are you sure you want to delete " + $scope.name.name + "?")) {
                    return api.deleteName($scope.name)
                }
            }
        }
    ])

    .controller('namesListEntriesCtrl', [
        '$scope',
        'namesApi',
        '$stateParams',
        'toastr',
        function($scope, api, $stateParams, toastr) {

            $scope.namesList = []

            $scope.status = $stateParams.status;

            api.getNames($stateParams).success(function(responseData) {
                $scope.namesListItems = responseData.length;
                responseData.forEach(function(name) {
                    $scope.namesList.push(name)
                })
            }).error(function(response) {
                console.log(response)
            })

            $scope.delete = function(entry){

                if (entry && $window.confirm('Are you sure you want to delete '+ entry.name + '?')) {
                    return api.deleteSuggestedName(entry.name)
                }

                var entries = $.map( $('input[name="selected_name"]:checked') , function(elem){
                    return  $scope.namesList[ $(elem).val() ]
                })

                if (!entries.length) return toastr.warning('Select names to delete');

                if (!$window.confirm('Are you sure you want to delete the selected name/s?')) return;
                
            }

            $scope.clearFilters = function() {
                $('.filter-status').val('')
                $('#names_table').trigger('footable_clear_filter')
            }

            $scope.filterTable = function(userStatus) {
                $('#names_table').data('footable-filter').filter( $('#textFilter').val() )
            }

            $scope.indexName = function(entry){
                return (!entry.indexed) ? api.addNameToIndex(entry.name).success(function(response){
                    return entry.indexed = true
                }) : api.removeNameFromIndex(entry.name).success(function(response){
                    entry.indexed = false
                })
            }

            $scope.indexNames = function(action){
                var entries = $.map( $('input[name="selected_name"]:checked') , function(elem){
                    return  $scope.namesList[ $(elem).val() ]
                })

                if (entries.length > 0) {

                    (!action || action == 'add') ? api.addNamesToIndex(entries).success(function(response){
                        $.map(entries, function(entry) { entry.indexed = true })
                        toastr.success(entries.length + ' names have been added to index')
                    }).error(function(){
                        toastr.error('Selected names could not be added to index')
                    })

                    :

                    api.removeNamesFromIndex(entries).success(function(response){
                        $.map(entries, function(entry) { entry.indexed = false })
                        toastr.success(entries.length + ' names have been removed from index')
                    }).error(function(){
                        toastr.error('Selected names could not be removed from index')
                    })

                    // then deselect all
                    return $('input[name="selected_name"]:checked').removeAttr('checked')

                }

                else toastr.warning('No names selected')
            }

            /**
             * Adds the suggested name to the list of names eligible to be added to search index
             */
              $scope.acceptSuggestedName = function (suggestedName) {
                var name = {}
                $scope.namesList.some(function (entry) {
                  if (entry.name === suggestedName) {
                    name = {
                      name: entry.name,
                      meaning: entry.details,
                      geoLocation: {
                        place: entry.geoLocation.place
                      },
                      submittedBy: entry.email
                    }
                    return true // breaks the iteration
                  }
                })
                if (!$.isEmptyObject(name)) {
                  return api.addName(name, function () {
                    // Name added then delete from the suggested name store
                    return api.deleteSuggestedName(name.name)
                  })
                }
              }



        }
    ])

    .controller('namesSearchCtrl', [
        '$scope',
        function($scope) {
        }
    ])

    .controller('namesByUserListCtrl', [
        '$scope',
        'namesApi',
        '$window',
        function ($scope, api, $window) {

            $scope.namesList = []

            api.getNames({ submittedBy: $scope.user.email }).success(function(responseData) {
                $scope.namesListItems = responseData.length;
                responseData.forEach(function(name) {
                    $scope.namesList.push(name)
                })
            }).error(function(response) {
                console.log(response)
            })
            
            $scope.$on('onRepeatLast', function (scope, element, attrs) {
                $('#user_list').listnav({
                    filterSelector: '.ul_name',
                    includeNums: false,
                    removeDisabled: true,
                    showCounts: false,
                    onClick: function(letter) {
                        $scope.namesListItems = $window.document.getElementsByClassName("listNavShow").length
                        $scope.$apply()
                    }
                })
            })
        }
    ])

    .controller('userListCtrl', [
        '$scope',
        'api',
        '$window',
        function ($scope, api, $window) {
            $scope.usersList = []

            api.get("/v1/auth/users").success(function(response) {
                $scope.usersListItems = response.length;
                response.forEach(function(user) {
                    $scope.usersList.push(user)
                })
            }).error(function(response) {
                console.log(response)
            })

            $scope.clearFilters = function() {
                $('.filter-status').val('')
                $('#users_table').trigger('footable_clear_filter')
            }

            $scope.filterTable = function(userStatus) {
                $('#users_table').data('footable-filter').filter( $('#textFilter').val() )
            }
        }
    ])

    .controller('userAddCtrl', [
        '$scope',
        'usersApi',
        'toastr',
        '$window',
        function ($scope, api, toastr, $window) {
            $scope.user = {}
            $scope.roles = {
                BASIC: 'ROLE_BASIC',
                LEXICOGRAPHER: 'ROLE_LEXICOGRAPHER',
                DASHBOARD: 'ROLE_DASHBOARD',
                ADMIN: 'ROLE_ADMIN'
            }

            $scope.submit = function () {
                $scope.user.roles = $.map( $scope.roles, function( value, key ) {
                  if (value != false) return value
                })
                return api.addUser($scope.user)
            }
        }
    ])

    .controller('userEditCtrl', [
        '$scope',
        'usersApi',
        'toastr',
        '$state',
        '$stateParams',
        '$window',
        function ($scope, api, toastr, $state, $stateParams, $window) {
            
            api.getUser($stateParams.id).success(function(user){
                $scope.user = user
            }).error(function(resp){
                console.log(resp)
            })

            $scope.roles = {
                BASIC: 'ROLE_BASIC',
                LEXICOGRAPHER: 'ROLE_LEXICOGRAPHER',
                DASHBOARD: 'ROLE_DASHBOARD',
                ADMIN: 'ROLE_ADMIN'
            }

            $scope.submit = function () {
              $scope.user.roles = $.map( $scope.roles, function( value, key ) {
                if (value != false) return value
              })
              return api.updateUser($scope.user)
            }
        }
    ])

    .controller('profileIndexCtrl', [
        '$scope',
        '$localStorage',
        'usersApi',
        function ($scope, $localStorage, api) {
            api.getUser($localStorage.id).success(function(user){
                $scope.user = user
            }).error(function(resp){
                console.log(resp)
            })
        }
    ])

    .controller('profileEditCtrl', [
        '$scope',
        '$localStorage',
        'usersApi',
        function ($scope, $localStorage, api) {
            api.getUser($localStorage.id).success(function(user){
                $scope.user = user
            }).error(function(resp){
                console.log(resp)
            })
        }
    ])

    .controller('searchCtrl', [
        '$scope',
        '$state',
        'namesApi',
        'toastr',
        '_',
        function ($scope, $state, api, toastr,  _) {
            $scope.search = {}
            api.getCachedNames(function(names){
                $scope.names = names
            })
            $scope.exec_search = function(){
                var name = $scope.names.some(function(entry){
                    if ($scope.search.entry == entry.name) {
                        return $state.go('auth.names.edit_entries', { entry: $scope.search.entry })
                    }
                })

                if (!name) {
                    toastr.warning( "No entries found for "+ $scope.search.entry )
                    return $state.go('auth.names.search', { entry: $scope.search.entry })
                }

                if ($scope.search.authorsEntry) {
                    console.log('will be searching my entries only')

                }
            }
        }
    ])

    .controller('searchPageCtrl', [
        '$scope',
        'namesApi',
        '$stateParams',
        function ($scope, api, $stateParams) {

            // 
            
        }
    ])
;