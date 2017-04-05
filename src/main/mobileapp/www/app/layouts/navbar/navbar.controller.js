(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('NavbarController', NavbarController);

    NavbarController.$inject = ['$scope', '$state', 'Auth', 'Principal', 'ProfileService', 'LoginService'];

    function NavbarController ($scope, $state, Auth, Principal, ProfileService, LoginService) {
        var vm = this;

        vm.isNavbarCollapsed = true;
        vm.isAuthenticated = Principal.isAuthenticated;

        ProfileService.getProfileInfo().then(function(response) {
            vm.inProduction = response.inProduction;
            vm.swaggerDisabled = response.swaggerDisabled;
        });

        vm.login = login;
        vm.logout = logout;
        vm.toggleNavbar = toggleNavbar;
        vm.collapseNavbar = collapseNavbar;
        vm.$state = $state;
        vm.isFlatAdmin = false;
        vm.setAdmin = setAdmin;

        setAdmin();

        function login() {
            collapseNavbar();
            LoginService.open();
        }

        function logout() {
            collapseNavbar();
            Auth.logout();
            $state.go('home');
        }

        function toggleNavbar() {
            vm.isNavbarCollapsed = !vm.isNavbarCollapsed;
        }

        function collapseNavbar() {
            vm.isNavbarCollapsed = true;
        }

        function setAdmin() {
            Principal.hasAuthority("ROLE_FLAT_ADMIN")
                .then(function (result) {
                    if (result) {
                        vm.isFlatAdmin = true;
                    } else {
                        vm.isFlatAdmin = false;
                    }
                });
        }

        $scope.$on('tidyUpApp:registeredNewFlat', function (event) {
            Principal.identity(true).then(setAdmin());
        });
        $scope.$on('authenticationSuccess', function (event) {
            Principal.identity(true).then(setAdmin());
        });
    }
})();
