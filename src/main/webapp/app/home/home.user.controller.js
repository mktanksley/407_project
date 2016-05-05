(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('HomeUserController', HomeUserController);

    HomeUserController.$inject = ['Principal'];

    function HomeUserController(Principal) {
        var vm = this;

        vm.account = null;
        vm.user = null;

        getAccount();

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
            });
        }

    }
})();
