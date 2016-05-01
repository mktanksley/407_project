(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('HomePublicController', HomePublicController);

    HomePublicController.$inject = ['$scope', '$state'];

    function HomePublicController ($scope, $state) {
        $scope.$on('authenticationSuccess', function() {
            $state.go('home.user');
        });
    }
})();
