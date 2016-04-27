(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoreDetailController', ChoreDetailController);

    ChoreDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Chore', 'TypeOfChore', 'User'];

    function ChoreDetailController($scope, $rootScope, $stateParams, entity, Chore, TypeOfChore, User) {
        var vm = this;
        vm.chore = entity;
        
        var unsubscribe = $rootScope.$on('tidyUpApp:choreUpdate', function(event, result) {
            vm.chore = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
