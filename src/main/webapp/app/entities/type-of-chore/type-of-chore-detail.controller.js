(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('TypeOfChoreDetailController', TypeOfChoreDetailController);

    TypeOfChoreDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'TypeOfChore'];

    function TypeOfChoreDetailController($scope, $rootScope, $stateParams, entity, TypeOfChore) {
        var vm = this;
        vm.typeOfChore = entity;
        
        var unsubscribe = $rootScope.$on('tidyUpApp:typeOfChoreUpdate', function(event, result) {
            vm.typeOfChore = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
