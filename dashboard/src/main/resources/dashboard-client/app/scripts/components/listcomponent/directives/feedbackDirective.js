'use strict';

/**
 * Directive to display the feedback messages
 * Pass in the name to display the atribute for via
 * the "name" attribute of the feedback.
 *
 */
var feedback = function (nameEntryService, Notification, $timeout) {

  return {
    restrict: 'E',
    templateUrl: 'views/list.feedback.html',
    link: function (scope, element, attributes) {
      scope.feedbacks = [];
      scope.showDeleteButton = false;

      $timeout(function () {

        //TODO investigate more "Angularish" way to only perform a function when the directive is rendered
        //TODO instead of the timeout that is being currently used

        var result = nameEntryService.getFeedback(attributes["name"]);
        result.success(function (data) {
          scope.feedbacks = data.feedback;

          if (scope.feedbacks.length != 0) {
            scope.showDeleteButton = true;
          }

        }).error(function (error) {
          Notification.error(error);
        });

      }, 500);

      scope.deleteFeedback = function () {
        var result = nameEntryService.deleteFeedback(attributes["name"]);
        result.success(function () {
          scope.feedbacks = [];
          scope.showDeleteButton = false;
        }).error(function (error) {
          Notification.error(error);
          scope.showDeleteButton = true;
        });
      };
    }
  };
};

angular.module('dashboardappApp').directive('feedback', feedback);
