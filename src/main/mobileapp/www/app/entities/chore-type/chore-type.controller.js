(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoreTypeController', ChoreTypeController);

    ChoreTypeController.$inject = ['$scope', '$state', 'ChoreType', 'ChoreTypeSearch'];

    function ChoreTypeController ($scope, $state, ChoreType, ChoreTypeSearch) {
        var vm = this;
        vm.choreTypes = [];
        vm.loadAll = function() {
            ChoreType.query(function(result) {
                vm.choreTypes = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ChoreTypeSearch.query({query: vm.searchQuery}, function(result) {
                vm.choreTypes = result;
            });
        };
        vm.loadAll();
        
    }
})();
