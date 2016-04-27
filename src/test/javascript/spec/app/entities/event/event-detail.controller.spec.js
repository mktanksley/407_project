'use strict';

describe('Controller Tests', function() {

    describe('Event Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockEvent, MockUser, MockChore;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockEvent = jasmine.createSpy('MockEvent');
            MockUser = jasmine.createSpy('MockUser');
            MockChore = jasmine.createSpy('MockChore');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Event': MockEvent,
                'User': MockUser,
                'Chore': MockChore
            };
            createController = function() {
                $injector.get('$controller')("EventDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'tidyUpApp:eventUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
