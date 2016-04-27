(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('FlatController', FlatController);

    FlatController.$inject = ['$scope', '$state', 'Flat', 'FlatSearch'];

    function FlatController ($scope, $state, Flat, FlatSearch) {
        var vm = this;
        vm.flats = [];
        vm.loadAll = function() {
            Flat.query(function(result) {
                vm.flats = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            FlatSearch.query({query: vm.searchQuery}, function(result) {
                vm.flats = result;
            });
        };
        vm.loadAll();
        
    }
})();
