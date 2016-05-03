(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('UserManagementDetailController', UserManagementDetailController);

    UserManagementDetailController.$inject = ['$stateParams', 'User', 'DataUtils'];

    function UserManagementDetailController ($stateParams, User, DataUtils) {
        var vm = this;

        vm.load = load;
        vm.user = {};

        vm.load($stateParams.login);

        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        function load (login) {
            User.get({login: login}, function(result) {
                vm.user = result;
            });
        }
    }
})();
