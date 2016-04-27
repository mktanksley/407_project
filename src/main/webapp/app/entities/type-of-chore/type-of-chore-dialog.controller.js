(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('TypeOfChoreDialogController', TypeOfChoreDialogController);

    TypeOfChoreDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'TypeOfChore'];

    function TypeOfChoreDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, TypeOfChore) {
        var vm = this;
        vm.typeOfChore = entity;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('tidyUpApp:typeOfChoreUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.typeOfChore.id !== null) {
                TypeOfChore.update(vm.typeOfChore, onSaveSuccess, onSaveError);
            } else {
                TypeOfChore.save(vm.typeOfChore, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
