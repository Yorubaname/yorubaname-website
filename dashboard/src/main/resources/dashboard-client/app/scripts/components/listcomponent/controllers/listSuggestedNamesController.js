

/**
 * Controller for the view that lists all suggested names
 * @param $scope angular scope
 * @param $location angular's location service
 * @param nameEntryService service for getting names from end point
 * @param Notification service for showing client side notifications
 */
var listSuggestedNamesController = function($scope, $location, nameEntryService, Notification) {

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
  var getSuggestedNamesAndPutOnScope = function() {
    var names = nameEntryService.getSuggestedNames();
    names.then(function (response) {
      $scope.suggestedNames = response.data;

    }, function (error) {
      Notification.error({
        title: 'An error occurred',
        message: error
      })
    });

  };

  getSuggestedNamesAndPutOnScope();

  /**
   * Adds the suggested name to the list of names eligible to be added to search index
   */
  var acceptSuggestedName = function(suggestedName) {

  };

  /**
   * Delets the suggested name
   */
  var deleteSuggestedName = function(suggestedName) {

  };

};

angular.module('dashboardappApp').controller("listSuggestedNamesController", listSuggestedNamesController);
