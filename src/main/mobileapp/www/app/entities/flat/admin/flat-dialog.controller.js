(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('FlatDialogController', FlatDialogController);

    FlatDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Flat', 'User'];

    function FlatDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Flat, User) {
        var vm = this;
        vm.flat = entity;
        vm.users = User.query();
        vm.flats = Flat.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('tidyUpApp:flatUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.flat.id !== null) {
                Flat.update(vm.flat, onSaveSuccess, onSaveError);
            } else {
                Flat.save(vm.flat, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.dateCreated = false;
    }
})();
