(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['Principal', '$state'];

    function HomeController(Principal, $state) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;

        getAccount();

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
                switchState();
            });
        }

        function register() {
            $state.go('register');
        }

        function switchState() {
            if (vm.isAuthenticated()) {
                var isAdmin = vm.account.authorities.indexOf('ROLE_ADMIN') != -1;
                if (vm.account.memberOf == null && !isAdmin) {
                    $state.go('registerflat');
                } else {
                    $state.go('home.user');
                }
            } else {
                $state.go('home.public');
            }
        }
    }
})();
