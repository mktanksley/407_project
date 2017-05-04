(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoreTypeDetailController', ChoreTypeDetailController);

    ChoreTypeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'ChoreType'];

    function ChoreTypeDetailController($scope, $rootScope, $stateParams, entity, ChoreType) {
        var vm = this;
        vm.choreType = entity;
        
        var unsubscribe = $rootScope.$on('tidyUpApp:choreTypeUpdate', function(event, result) {
            vm.choreType = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
