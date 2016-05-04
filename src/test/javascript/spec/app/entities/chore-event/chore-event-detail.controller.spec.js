'use strict';

describe('Controller Tests', function() {

    describe('ChoreEvent Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockChoreEvent, MockChoreType, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockChoreEvent = jasmine.createSpy('MockChoreEvent');
            MockChoreType = jasmine.createSpy('MockChoreType');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'ChoreEvent': MockChoreEvent,
                'ChoreType': MockChoreType,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("ChoreEventDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'tidyUpApp:choreEventUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
