'use strict';


/**
 * Directive to get the files in a form into $scope
 * @param $parse
 * @returns {{restrict: string, link: Function}}
 */
var fileModel = function ($parse) {
  return {
    restrict: 'A',
    link: function(scope, element, attrs) {
      var model = $parse(attrs.fileModel);
      var modelSetter = model.assign;

      element.bind('change', function(){
        scope.$apply(function(){
          modelSetter(scope, element[0].files[0]);
        });
      });
    }
  };
};



angular.module('dashboardappApp').directive('fileModel', fileModel);
