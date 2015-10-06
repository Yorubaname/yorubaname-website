/* Controllers */
dashboardappApp

    .controller('mainCtrl', [
        '$scope',
        function ($scope) {
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
                            title: 'List Entries',
                            link: "auth.names.list_entries({status:'all'})"
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
                            title: 'Volunteers',
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
            });

            $rootScope.createScrollbar = function() {
                $("#main_menu .menu_wrapper").mCustomScrollbar({
                    theme: "minimal-dark",
                    scrollbarPosition: "outside"
                });
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
                    $rootScope.createScrollbar();
                }
            });
        }
    ])

    .controller('loginCtrl', [
        '$scope',
        'authApi',
        '$timeout',
        function ($scope, api, $timeout) {
            $scope.login = {}
            $scope.submit = function (){
                api.authenticate($scope.login, $scope)
            }
            
        }
    ])

    .controller('logoutCtrl', [
        '$scope',
        '$timeout',
        function ($scope, $timeout) {
            
            
        }
    ])

    .controller('dashboardCtrl', [
        '$scope',
        'files',
        'namesApi',
        function ($scope, files, namesApi) {
            // run scripts after state load
            $scope.$on('$stateChangeSuccess', function () {
                $('.countUpMe').each(function() {
                    var target = this,
                    endVal = parseInt($(this).attr('data-endVal')),
                    theAnimation = new countUp(target, 0, endVal, 0, 2.6, { useEasing : true, useGrouping : true, separator: ' ' });
                    theAnimation.start()
                })
            })

            $scope.names = []

            namesApi.getNames(1,5).success(function(responseData){
                responseData.forEach(function(name) {
                    $scope.names.push(name)
                })
            })

        }
    ])

    .controller('namesAddEntriesCtrl', [
        '$scope',
        'files',
        'FileUploader',
        function($scope, files, FileUploader) {

            var uploader = $scope.uploader = new FileUploader({
                url: 'js/controllers/upload.php'
            });

            // FILTERS

            uploader.filters.push({
                name: 'customFilter',
                fn: function(item /*{File|FileLikeObject}*/, options) {
                    return this.queue.length < 10;
                }
            });

            // CALLBACKS

            uploader.onWhenAddingFileFailed = function(item /*{File|FileLikeObject}*/, filter, options) {
                console.info('onWhenAddingFileFailed', item, filter, options);
            };
            uploader.onAfterAddingFile = function(fileItem) {
                console.info('onAfterAddingFile', fileItem);
            };
            uploader.onAfterAddingAll = function(addedFileItems) {
                console.info('onAfterAddingAll', addedFileItems);
            };
            uploader.onBeforeUploadItem = function(item) {
                console.info('onBeforeUploadItem', item);
            };
            uploader.onProgressItem = function(fileItem, progress) {
                console.info('onProgressItem', fileItem, progress);
            };
            uploader.onProgressAll = function(progress) {
                console.info('onProgressAll', progress);
            };
            uploader.onSuccessItem = function(fileItem, response, status, headers) {
                console.info('onSuccessItem', fileItem, response, status, headers);
            };
            uploader.onErrorItem = function(fileItem, response, status, headers) {
                console.info('onErrorItem', fileItem, response, status, headers);
            };
            uploader.onCancelItem = function(fileItem, response, status, headers) {
                console.info('onCancelItem', fileItem, response, status, headers);
            };
            uploader.onCompleteItem = function(fileItem, response, status, headers) {
                console.info('onCompleteItem', fileItem, response, status, headers);
            };
            uploader.onCompleteAll = function() {
                console.info('onCompleteAll');
            };

            console.info('uploader', uploader);




            $scope.$on('$stateChangeSuccess', function(){
                $('#slz_optgroups').selectize({
                    sortField: 'text'
                })
                $("select[rel='reg_select_multiple']").select2({
                    placeholder: "Select..."
                })
            })
        }
    ])


    .controller('namesEditEntryCtrl', [
        '$scope',
        'files',
        function($scope, files) {

            $scope.$on('$stateChangeSuccess', function(){
                $('#slz_optgroups').selectize({
                    sortField: 'text'
                })
                $("select[rel='reg_select_multiple']").select2({
                    placeholder: "Select..."
                })
            })
        }
    ])

    .controller('namesListEntriesCtrl', [
        '$scope',
        'namesApi',
        '$stateParams',
        '$window',
        function($scope, api, $stateParams, $window) {

            $scope.namesList = []

            $scope.status = $stateParams.status;

            api.getNames(1,3,$stateParams).success(function(responseData) {
                //console.log(responseData)
                $scope.namesListItems = responseData.length;
                responseData.forEach(function(name) {
                    $scope.namesList.push(name)
                })
            }).error(function(response) {
                console.log(response);
                // Notification.error(responseData.message)
            })



            $('#footable_demo').footable({
                toggleSelector: " > tbody > tr > td > span.footable-toggle"
            }).on({
                'footable_filtering': function (e) {
                    var selected = $scope.userStatus;
                    if (selected && selected.length > 0) {
                        e.filter += (e.filter && e.filter.length > 0) ? ' ' + selected : selected;
                        e.clear = !e.filter;
                    }
                }
            })
            $scope.clearFilters = function() {
                $('.filter-status').val('')
                $('#footable_demo').trigger('footable_clear_filter')
            }
            $scope.filterTable = function(userStatus) {
                $('#footable_demo').data('footable-filter').filter( $('#textFilter').val() )
            }
        }
    ])

    .controller('namesSearchCtrl', [
        '$scope',
        function($scope) {
            $scope.$on('$stateChangeSuccess', function(){
                
            })
        }
    ])

    .controller('userListCtrl', [
        '$scope',
        'api',
        //'Notification',
        '$window',
        function ($scope, api, $window) {

            $scope.userList = []
            
            api.get("/v1/auth/users").success(function(responseData) {
                console.log(responseData)
                $scope.userListItems = responseData.length;
                responseData.forEach(function(user) {
                    $scope.userList.push(user)
                })
            }).error(function(response) {
                console.log(response);
                // Notification.error(response.message)
            })

            $scope.$on('onRepeatLast', function (scope, element, attrs) {
                $('#user_list').listnav({
                    filterSelector: '.ul_lastName',
                    includeNums: false,
                    removeDisabled: true,
                    showCounts: false,
                    onClick: function(letter) {
                        $scope.userListItems = $window.document.getElementsByClassName("listNavShow").length;
                        $scope.$apply()
                    }
                })
            })
        }
    ])

    .controller('userAddCtrl', [
        '$scope',
        'api',
        // 'Notification',
        '$window',
        function ($scope, api, /* Notification, */ $window) {

            $scope.submit = function () {
                api.postJson("/v1/auth/create", $scope.user)
                   .success(function(responseData) {
                      console.log(responseData)
                      // clear form
                      // show notification, with option to add another
                      // Notification.success('')
                   })
                   .error(function(response) {
                     console.log(response)
                     // Notification.error('Can not create user due to:' + response.message)
                   })
            }
        }
    ])
    
    .controller('profileIndexCtrl', [
        '$scope',
        function ($scope) {
            
        }
    ])

    .controller('profileEditCtrl', [
        '$scope',
        function ($scope) {
            
        }
    ])


    .controller('topSearchCtrl', [
        '$scope',
        function ($scope) {
            $scope.selected = undefined;
            //$scope.states = ['Alabama', 'Alaska', 'Arizona', 'Arkansas', 'California', 'Colorado', 'Connecticut', 'Delaware', 'Florida', 'Georgia', 'Hawaii', 'Idaho', 'Illinois', 'Indiana', 'Iowa', 'Kansas', 'Kentucky', 'Louisiana', 'Maine', 'Maryland', 'Massachusetts', 'Michigan', 'Minnesota', 'Mississippi', 'Missouri', 'Montana', 'Nebraska', 'Nevada', 'New Hampshire', 'New Jersey', 'New Mexico', 'New York', 'North Dakota', 'North Carolina', 'Ohio', 'Oklahoma', 'Oregon', 'Pennsylvania', 'Rhode Island', 'South Carolina', 'South Dakota', 'Tennessee', 'Texas', 'Utah', 'Vermont', 'Virginia', 'Washington', 'West Virginia', 'Wisconsin', 'Wyoming'];
        }
    ])
;




