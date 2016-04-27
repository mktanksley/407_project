(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoreDialogController', ChoreDialogController);

    ChoreDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Chore', 'TypeOfChore', 'User'];

    function ChoreDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Chore, TypeOfChore, User) {
        var vm = this;
        vm.chore = entity;
        vm.typeofchores = TypeOfChore.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('tidyUpApp:choreUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.chore.id !== null) {
                Chore.update(vm.chore, onSaveSuccess, onSaveError);
            } else {
                Chore.save(vm.chore, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.date = false;

        vm.openCalendar = function(date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
