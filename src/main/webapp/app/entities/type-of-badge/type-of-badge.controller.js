(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('TypeOfBadgeController', TypeOfBadgeController);

    TypeOfBadgeController.$inject = ['$scope', '$state', 'TypeOfBadge', 'TypeOfBadgeSearch'];

    function TypeOfBadgeController ($scope, $state, TypeOfBadge, TypeOfBadgeSearch) {
        var vm = this;
        vm.typeOfBadges = [];
        vm.loadAll = function() {
            TypeOfBadge.query(function(result) {
                vm.typeOfBadges = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            TypeOfBadgeSearch.query({query: vm.searchQuery}, function(result) {
                vm.typeOfBadges = result;
            });
        };
        vm.loadAll();
        
    }
})();
