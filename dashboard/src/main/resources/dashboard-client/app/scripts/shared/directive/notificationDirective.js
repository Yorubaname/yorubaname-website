'use strict';

/**
 * Service for Auth related tasks
 */

var noticeDirective = function() {
    return {
        restrict: "E",
        template: '<div id="msg-container" class="{{msg.type}}">{{msg.text}}</div>',
        scope: {
            notify: "&",
            msg: "="
        },
        controller: function($scope) {
            this.show = function(msg, type) {
                console.log(type)
                $scope.msg.text = msg
                $scope.msg.type = type;
                setTimeout(function() {
                    $scope.$apply(function() {
                        $scope.msg.text = "";
                        $scope.msg.type = "";
                    });
                }, 5000);
            };
        }

    }


};

angular.module('dashboardappApp').directive('notification', noticeDirective);
