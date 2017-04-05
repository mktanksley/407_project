(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoreTypeDialogController', ChoreTypeDialogController);

    ChoreTypeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ChoreType'];

    function ChoreTypeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ChoreType) {
        var vm = this;
        vm.choreType = entity;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('tidyUpApp:choreTypeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.choreType.id !== null) {
                ChoreType.update(vm.choreType, onSaveSuccess, onSaveError);
            } else {
                ChoreType.save(vm.choreType, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
