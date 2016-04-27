(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('TypeOfChoreController', TypeOfChoreController);

    TypeOfChoreController.$inject = ['$scope', '$state', 'TypeOfChore', 'TypeOfChoreSearch'];

    function TypeOfChoreController ($scope, $state, TypeOfChore, TypeOfChoreSearch) {
        var vm = this;
        vm.typeOfChores = [];
        vm.loadAll = function() {
            TypeOfChore.query(function(result) {
                vm.typeOfChores = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            TypeOfChoreSearch.query({query: vm.searchQuery}, function(result) {
                vm.typeOfChores = result;
            });
        };
        vm.loadAll();
        
    }
})();
