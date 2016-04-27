(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('TypeOfBadgeDialogController', TypeOfBadgeDialogController);

    TypeOfBadgeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'TypeOfBadge'];

    function TypeOfBadgeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, TypeOfBadge) {
        var vm = this;
        vm.typeOfBadge = entity;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('tidyUpApp:typeOfBadgeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.typeOfBadge.id !== null) {
                TypeOfBadge.update(vm.typeOfBadge, onSaveSuccess, onSaveError);
            } else {
                TypeOfBadge.save(vm.typeOfBadge, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
