'use strict';

describe('Controller Tests', function() {

    describe('Chore Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockChore, MockTypeOfChore, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockChore = jasmine.createSpy('MockChore');
            MockTypeOfChore = jasmine.createSpy('MockTypeOfChore');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Chore': MockChore,
                'TypeOfChore': MockTypeOfChore,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("ChoreDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'tidyUpApp:choreUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
