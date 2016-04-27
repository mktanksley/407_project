(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('FlatDetailController', FlatDetailController);

    FlatDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Flat', 'User'];

    function FlatDetailController($scope, $rootScope, $stateParams, entity, Flat, User) {
        var vm = this;
        vm.flat = entity;
        
        var unsubscribe = $rootScope.$on('tidyUpApp:flatUpdate', function(event, result) {
            vm.flat = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
