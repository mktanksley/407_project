(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoreEventDetailController', ChoreEventDetailController);

    ChoreEventDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'ChoreEvent', 'ChoreType', 'User'];

    function ChoreEventDetailController($scope, $rootScope, $stateParams, entity, ChoreEvent, ChoreType, User) {
        var vm = this;
        vm.choreEvent = entity;
        
        var unsubscribe = $rootScope.$on('tidyUpApp:choreEventUpdate', function(event, result) {
            vm.choreEvent = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
