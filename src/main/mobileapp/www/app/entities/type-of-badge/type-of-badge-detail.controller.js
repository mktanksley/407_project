(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('TypeOfBadgeDetailController', TypeOfBadgeDetailController);

    TypeOfBadgeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'TypeOfBadge'];

    function TypeOfBadgeDetailController($scope, $rootScope, $stateParams, entity, TypeOfBadge) {
        var vm = this;
        vm.typeOfBadge = entity;
        
        var unsubscribe = $rootScope.$on('tidyUpApp:typeOfBadgeUpdate', function(event, result) {
            vm.typeOfBadge = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
