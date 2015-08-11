/**
 * Controller for the view that lists all suggested names
 * @param $scope angular scope
 * @param $location angular's location service
 * @param nameEntryService service for getting names from end point
 * @param Notification service for showing client side notifications
 */
var listSuggestedNamesController = function ($scope, $location, nameEntryService, Notification) {

  /**
   * An Array of {
   * name: string,
   * details: string,
   * geoLocation: {
   *  place: string,
   *  region: string
   * },
   * email: string
   * }
   * @type {Array}
   */
  $scope.suggestedNames = [];

  /**
   * Get all suggested names and put in scope
   */
  var getSuggestedNamesAndPutOnScope = function () {
    var result = nameEntryService.getSuggestedNames();
    result.then(function (response) {
      $scope.suggestedNames = response.data;

    }, function (error) {
      Notification.error({
        title: 'An error occurred',
        message: error
      });
    });
  };

  getSuggestedNamesAndPutOnScope();

  /**
   * Adds the suggested name to the list of names eligible to be added to search index
   */
  $scope.acceptSuggestedName = function (suggestedName) {
    var nameToAccept = {};
    $scope.suggestedNames.some(function (entry) {
      if (entry.name === suggestedName) {

        nameToAccept = {
          name: entry.name,
          meaning: entry.details,
          geoLocation: {
            place: entry.geoLocation.place
          },
          submittedBy: entry.email
        };

        return true; // breaks the iteration
      }
    });

    if (!$.isEmptyObject(nameToAccept)) {
      var result = nameEntryService.addName(nameToAccept);
      result.success(function () {
        Notification.success("Name successfully added to database");
        // Name added then delete from the suggested name store
        $scope.deleteSuggestedName(nameToAccept.name, false);
      }).error(function (error) {
        Notification.error(error.message);
      });
    }
  };

  /**
   * Deletes the suggested name
   */
  $scope.deleteSuggestedName = function (suggestedName, showNotification) {
    var showNotif = (typeof showNotification === 'undefined')? false: showNotification;
    var result = nameEntryService.deleteSuggestedName(suggestedName);
    result.success(function () {
      if (showNotif) {
        Notification.success("Name deleted");
      }
      getSuggestedNamesAndPutOnScope();
    }).error(function (error) {
      if (showNotif) {
        Notification.error("An error while attempting to delete suggested name");
      }
    });
  };
};

angular.module('dashboardappApp').controller("listSuggestedNamesController", listSuggestedNamesController);
