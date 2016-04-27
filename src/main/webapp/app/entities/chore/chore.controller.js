(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoreController', ChoreController);

    ChoreController.$inject = ['$scope', '$state', 'Chore', 'ChoreSearch'];

    function ChoreController ($scope, $state, Chore, ChoreSearch) {
        var vm = this;
        vm.chores = [];
        vm.loadAll = function() {
            Chore.query(function(result) {
                vm.chores = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ChoreSearch.query({query: vm.searchQuery}, function(result) {
                vm.chores = result;
            });
        };
        vm.loadAll();
        
    }
})();
