'use strict';


/**
 * Directive that is used to create html form to create/update
 * Name entry
 */
// TODO move the dependent template into a directory with the directive This involves updating the grunt task to deal with when views are not in the root views dir
  // TODO update checks to also cater for other HTTP actions. POST etc
// TODO this todo is not perculair to this file. Standardize either to use toneMark or tonalMark
var nameEntry = function ($http, $location, endpointService) {

  var populateForm = function (scope, getDuplicates) {
      // reach out to the endpoint to get data for this name
      var request = endpointService.get('/v1/names/'+scope.currentName, {'duplicates':'yes'});
      request.success(function(data) {
        //TODO write a utility function that does this extraction
        scope.formEntry.name = data.mainEntry.name;
        scope.formEntry.meaning = data.mainEntry.meaning;
        scope.formEntry.geoLocation = data.mainEntry.geoLocation;
        //scope.formEntry.toneMark = data.mainEntry.tone.toneMark;
        scope.formEntry.morphology = data.mainEntry.morphology;

        scope.formEntry.pronunciation = data.mainEntry.pronunciation;
        scope.formEntry.ipaNotation = data.mainEntry.ipaNotation;
        scope.formEntry.syllables = data.mainEntry.syllables;
        scope.formEntry.extendedMeaning = data.mainEntry.extendedMeaning;

        scope.formEntry.etymology = data.mainEntry.etymology;
        scope.formEntry.variants = data.mainEntry.variants;
        scope.formEntry.famousPeople = data.mainEntry.famousPeople;
        scope.formEntry.inOtherLanguages = data.mainEntry.inOtherLanguages;

        scope.formEntry.media = data.mainEntry.media;
        scope.formEntry.tags = data.mainEntry.tags;

        if (getDuplicates && getDuplicates === "true") {
            scope.duplicates = data.duplicates;
        }

      }).error(function(data){
        console.log(data);
      });
  };


  return {
    restrict: 'E',
    templateUrl: 'views/nameentry.directive.template.html',
    replace: true,
    link: function (scope, element, attrs) {
      var request;
      scope.formEntry = {};
      scope.msg = {};
      scope.buttonAction = "Create Entry";
      scope.duplicateView = attrs.duplicates == "true" ? true : false;

      var resetAfterPost = function (element, scope) {
        element.children('form')[0].reset();
        scope.msg.text = "Successfully added name";
        scope.msg.type = "msg-success";
      };

      var resetAfterPut = function (element, sope) {
        scope.msg.text = "Successfully updated name";
        scope.msg.type = "msg-success";
      };

      if (attrs.action === 'put') {
        scope.buttonAction = "Update Entry";
        scope.currentName = $location.search().name;
        populateForm(scope, attrs.duplicates);
      };


      scope.create = function () {

        if (attrs.action === 'put') {
          request = endpointService.put('/v1/name', scope.formEntry);
        } else {
          request = endpointService.post('/v1/name', scope.formEntry);
        }

        request.success(function (data) {
          if (data === "success") {

            if (attrs.action === "put") {
              resetAfterPut(element, scope);
            } else {
              resetAfterPost(element, scope);
            }
            setTimeout(function () {
              scope.$apply(function () {
                scope.msg.text = "";
                scope.msg.type = "";
              });
            }, 5000);
          }
        }).error(function (data, status, headers, config) {
          scope.msg.text = "Error in adding name:" + data;
          scope.msg.type = "msg-error";
          setTimeout(function () {
            scope.$apply(function () {
              scope.msg.text = "";
              scope.msg.type = "";
            });
          }, 5000);
        });
      };
    }
  };
};


angular.module('dashboardappApp').directive('nameentry', nameEntry);
