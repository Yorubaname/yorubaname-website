

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
    var result = nameEntryService.getSuggestedNames();
    result.then(function (response) {
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
  $scope.acceptSuggestedName = function(suggestedName) {
      console.log(suggestedName);
  };

  /**
   * Deletes the suggested name
   */
  $scope.deleteSuggestedName = function(suggestedName) {
    var result = nameEntryService.deleteSuggestedName(suggestedName);
    result.success(function(){
      getSuggestedNamesAndPutOnScope();
    });
  };
};

angular.module('dashboardappApp').controller("listSuggestedNamesController", listSuggestedNamesController);
