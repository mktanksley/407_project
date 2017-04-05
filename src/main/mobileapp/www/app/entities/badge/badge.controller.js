(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('BadgeController', BadgeController);

    BadgeController.$inject = ['$scope', '$state', 'Badge', 'BadgeSearch'];

    function BadgeController ($scope, $state, Badge, BadgeSearch) {
        var vm = this;
        vm.badges = [];
        vm.loadAll = function() {
            Badge.query(function(result) {
                vm.badges = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            BadgeSearch.query({query: vm.searchQuery}, function(result) {
                vm.badges = result;
            });
        };
        vm.loadAll();
        
    }
})();
