(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('EventDetailController', EventDetailController);

    EventDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Event', 'User', 'Chore'];

    function EventDetailController($scope, $rootScope, $stateParams, entity, Event, User, Chore) {
        var vm = this;
        vm.event = entity;
        
        var unsubscribe = $rootScope.$on('tidyUpApp:eventUpdate', function(event, result) {
            vm.event = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
