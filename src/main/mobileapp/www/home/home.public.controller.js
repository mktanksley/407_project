(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('HomePublicController', HomePublicController);

    HomePublicController.$inject = ['$scope', '$state', 'LoginService'];

    function HomePublicController ($scope, $state, LoginService) {
        var vm = this;

        vm.register = register;
        vm.login = LoginService.open;

        function register () {
            $state.go('register');
        }

        $scope.$on('authenticationSuccess', function() {
            $state.go('home');
        });
    }
})();
