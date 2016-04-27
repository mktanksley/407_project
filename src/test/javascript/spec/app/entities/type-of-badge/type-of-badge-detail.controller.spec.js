'use strict';

describe('Controller Tests', function() {

    describe('TypeOfBadge Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockTypeOfBadge;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockTypeOfBadge = jasmine.createSpy('MockTypeOfBadge');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'TypeOfBadge': MockTypeOfBadge
            };
            createController = function() {
                $injector.get('$controller')("TypeOfBadgeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'tidyUpApp:typeOfBadgeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
