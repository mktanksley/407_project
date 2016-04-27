'use strict';

describe('Controller Tests', function() {

    describe('Badge Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockBadge, MockTypeOfBadge, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockBadge = jasmine.createSpy('MockBadge');
            MockTypeOfBadge = jasmine.createSpy('MockTypeOfBadge');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Badge': MockBadge,
                'TypeOfBadge': MockTypeOfBadge,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("BadgeDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'tidyUpApp:badgeUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
