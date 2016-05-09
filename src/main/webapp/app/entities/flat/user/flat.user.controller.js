(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('FlatUserController', FlatUserController);

    FlatUserController.$inject = ['entity', 'Flat', 'Principal', 'AlertService'];

    function FlatUserController(entity, Flat, Principal, AlertService) {
        var vm = this;
        vm.flat = entity;
        vm.account = null;

        loadData();

        function loadData() {
            Principal.identity().then(function (account) {
                vm.account = account;

                Flat.get({id : vm.account.memberOf.id}, function (flat) {
                    vm.flat = flat;
                }, function (error) {
                    AlertService.error(error.data.message);
                })

            });
        }

    }
})();
