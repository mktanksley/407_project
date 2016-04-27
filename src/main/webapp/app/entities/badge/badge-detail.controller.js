(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('BadgeDetailController', BadgeDetailController);

    BadgeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Badge', 'TypeOfBadge', 'User'];

    function BadgeDetailController($scope, $rootScope, $stateParams, entity, Badge, TypeOfBadge, User) {
        var vm = this;
        vm.badge = entity;
        
        var unsubscribe = $rootScope.$on('tidyUpApp:badgeUpdate', function(event, result) {
            vm.badge = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
