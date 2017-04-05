(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ContestController', ContestController);

    ContestController.$inject = ['$scope', 'Principal', 'Flat', '$state'];

    function ContestController($scope, Principal, Flat, $state) {
        var vm = this;

        vm.account = null;
        vm.flat = null;
        vm.loadData = loadData;

        vm.loadData();

        function loadData() {
            Principal.identity(true).then(function (account) {
                vm.account = account;

                Flat.get({id: vm.account.memberOf.id}, function (flat) {
                    vm.flat = flat;

                    // sort users by points
                    vm.flat.residents.sort(function (a, b) {
                        if (a.points > b.points) {
                            return -1;
                        }
                        if (a.points < b.points) {
                            return 1;
                        }
                        // a must be equal to b
                        return 0;
                    });
                }, function (error) {
                    AlertService.error(error.data.message);
                })
            });
        };

    }
})();
