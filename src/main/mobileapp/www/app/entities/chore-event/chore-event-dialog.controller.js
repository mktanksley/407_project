(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoreEventDialogController', ChoreEventDialogController);

    ChoreEventDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ChoreEvent', 'ChoreType', 'User'];

    function ChoreEventDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ChoreEvent, ChoreType, User) {
        var vm = this;
        vm.choreEvent = entity;
        vm.choretypes = ChoreType.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('tidyUpApp:choreEventUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.choreEvent.id !== null) {
                ChoreEvent.update(vm.choreEvent, onSaveSuccess, onSaveError);
            } else {
                ChoreEvent.save(vm.choreEvent, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.dateTo = false;
        vm.datePickerOpenStatus.dateDone = false;

        vm.openCalendar = function(date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
