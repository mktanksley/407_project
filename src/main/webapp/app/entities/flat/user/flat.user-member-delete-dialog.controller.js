(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('MemberDialogDeleteController', MemberDialogDeleteController);

    MemberDialogDeleteController.$inject = ['$uibModalInstance', 'entity', 'User', 'AlertService'];

    function MemberDialogDeleteController($uibModalInstance, entity, User, AlertService) {
        var vm = this;
        vm.user = entity;
        vm.clear = function () {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (login) {
            User.delete({login: login},
                function () {
                    $uibModalInstance.close(true);
                    AlertService.success("Successfully deleted user.")
                });
        };
    }
})();
