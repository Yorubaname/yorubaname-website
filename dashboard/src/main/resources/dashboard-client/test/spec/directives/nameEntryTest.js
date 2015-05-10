'use strict';


/*Test for name entry directory forms inputs*/


describe('Directive: nameEntry', function() {
    var scope,
        elem,
        compiled,
        html;
    beforeEach(angular.mock.module('ngMockE2E'));
    beforeEach(function() {
        //load the module
        module('dashboardappApp');

        //set our view html.
        html = '<nameentry></nameentry>';

        inject(function($compile, $rootScope) {
            scope = $rootScope.$new();

            //get the jqLite or jQuery element
            elem = angular.element(html);

            //compile the element into a function to
            // process the view.
            compiled = $compile(elem);

            //run the compiled view.
            compiled(scope);

            //call digest on the scope!
            scope.$digest();
        });
    });

    it('Autocomplete should serve name', function() {
        //set a value (the same one we had in the html)
        scope.formEntry = {};
        scope.formEntry.pronunciation = '';

        //check to see if it's blank first.
        expect(elem.find('pronunciation').text()).toBe('');
        scope.formEntry.pronunciation = 'odu to la';
        //test to see if it was updated.
        console.log(elem.find('pronunciation'));
        expect(elem.find('pronunciation').text()).toBe(['odu', 'to', 'la']);
    });
});
