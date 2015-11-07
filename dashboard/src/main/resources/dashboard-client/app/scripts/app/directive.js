/* Directives */

dashboardappApp
    // change page title
    .directive('updateTitle', function ($rootScope) {
        return {
            link: function (scope, element) {
                var listener = function (event, toState, toParams, fromState, fromParams) {
                    var title = 'Yoruba Names Admin';
                    if (toState.page_title) {
                        title = toState.page_title;
                    }
                    if($rootScope.appVer) {
                        element.text(title +' ('+$rootScope.appVer+')');
                    } else {
                        element.text(title);
                    }
                };
                $rootScope.$on('$stateChangeStart', listener);
            }
        }
    })
    // page preloader
    .directive('pageLoader', [
        '$timeout',
        function ($timeout) {
            return {
                restrict: 'AE',
                template: '<div class="rect1"></div><div class="rect2"></div><div class="rect3"></div><div class="rect4"></div><div class="rect5"></div>',
                link: function (scope, el, attrs) {
                    el.addClass('pageLoader hide');
                    scope.$on('$stateChangeStart', function (event) {
                        el.toggleClass('hide animate');
                    });
                    scope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState) {
                        event.targetScope.$watch('$viewContentLoaded', function () {
                            $timeout(function () {
                                el.toggleClass('hide animate')
                            }, 600);
                        })
                    });
                }
            };
        }
    ])
    // show/hide side menu
    .directive('menuToggle', [
        '$rootScope',
        '$localStorage',
        '$window',
        '$timeout',
        function ($rootScope, $localStorage, $window, $timeout) {
            return {
                restrict: 'E',
                template: '<span class="menu_toggle" ng-click="toggleSidebar()"><span class="icon_menu_toggle" ><i class="arrow_carrot-2left" ng-class="sideNavCollapsed ? \'hide\' : \'\'"></i><i class="arrow_carrot-2right" ng-class="sideNavCollapsed ? \'\' : \'hide\'"></i></span></span>',
                link: function (scope, el, attrs) {
                    var mobileView = 992;
                    $rootScope.getWidth = function () {
                        return window.innerWidth;
                    };
                    $rootScope.$watch($rootScope.getWidth, function (newValue, oldValue) {
                        if (newValue >= mobileView) {
                            if (angular.isDefined($localStorage.sideNavCollapsed)) {
                                if($localStorage.sideNavCollapsed == false) {
                                    $rootScope.sideNavCollapsed = false;
                                } else {
                                    $rootScope.sideNavCollapsed = true;
                                }
                            } else {
                                $rootScope.sideNavCollapsed = false;
                            }
                        } else {
                            $rootScope.sideNavCollapsed = true;
                        }
                        $timeout(function() {
                            $(window).resize();
                        });
                    });
                    scope.toggleSidebar = function () {
                        $rootScope.sideNavCollapsed = !$rootScope.sideNavCollapsed;
                        $localStorage.sideNavCollapsed = $rootScope.sideNavCollapsed;
                        if(!$rootScope.fixedLayout) {
                            if(window.innerWidth > 991) {
                                $timeout(function () {
                                    $(window).resize();
                                });
                            }
                        }
                        if(!$rootScope.sideNavCollapsed && !$rootScope.topMenuAct) {
                            $rootScope.createScrollbar();
                        } else {
                            $rootScope.destroyScrollbar();
                        }
                    };
                }
            };
        }
    ])
    // update datatables fixedHeader position
    .directive('updateFixedHeaders', function ($window) {
        return function (scope, element) {
            var w = angular.element($window);
            scope.getElDimensions = function () {
                return {
                    'w': element.width(),
                    'h': element.height()
                };
            };
            scope.$watch(scope.getElDimensions, function (newValue, oldValue) {
                if(typeof oFH != 'undefined') {
                    oFH._fnUpdateClones( true );
                    oFH._fnUpdatePositions();
                }
            }, true);
            w.bind('resize', function () {
                scope.$apply();
            });
        };
    })
    // ng-repeat after render callback
    .directive('onLastRepeat', function ($timeout) {
        return function (scope, element, attrs) {

            console.log("running lastRepeat directive")

            if (!scope.$last) return false;

            var footableTable = element.parents('table')

            scope.$evalAsync(function(){

                if (! footableTable.hasClass('footable-loaded')) {
                    footableTable.footable()
                }
                footableTable.trigger('footable_initialized')
                footableTable.trigger('footable_resize')
                footableTable.data('footable').redraw()
            })
            /*if (scope.$last) {
                $timeout(function () {
                    scope.$emit('onRepeatLast', element, attrs);
                })
            }*/
        };
    })
    // add width/height properities to Image
    .directive('addImageProp', function () {
        return {
            restrict: 'A',
            link: function (scope, elem, attr) {
                elem.on('load', function () {
                    var w = !scope.isHighDensity() ? $(this).width() : $(this).width()/2,
                        h = !scope.isHighDensity() ? $(this).height() : $(this).height()/2;
                    $(this).attr('width',w).attr('height',h);
                });
            }
        };
    })

;