(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('MemberDialogController', MemberDialogController);

    MemberDialogController.$inject = ['$state', '$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'User', 'AlertService'];

    function MemberDialogController($state, $timeout, $scope, $stateParams, $uibModalInstance, entity, User, AlertService) {
        var vm = this;
        vm.user = entity;

        $timeout(function () {
            angular.element('.form-group:eq(0)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('tidyUpApp:flatUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
            AlertService.success("Flat member has been added!");
            $state.go('userFlat');
        };

        var onSaveError = function (error) {
            vm.isSaving = false;
            AlertService.error(error.data.message);
        };

        vm.save = function () {
            vm.isSaving = true;
            vm.user.memberOf = $stateParams.flat;
            vm.user.authorities = ['ROLE_USER'];
            User.save(vm.user, onSaveSuccess, onSaveError);
        };

        vm.clear = function () {
            $uibModalInstance.dismiss('cancel');
        };

    }
})();
