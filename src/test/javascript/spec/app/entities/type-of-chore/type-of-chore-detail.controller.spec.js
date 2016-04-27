'use strict';

describe('Controller Tests', function() {

    describe('TypeOfChore Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockTypeOfChore;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockTypeOfChore = jasmine.createSpy('MockTypeOfChore');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'TypeOfChore': MockTypeOfChore
            };
            createController = function() {
                $injector.get('$controller')("TypeOfChoreDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'tidyUpApp:typeOfChoreUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
