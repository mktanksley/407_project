(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('EventDialogController', EventDialogController);

    EventDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Event', 'User', 'Chore'];

    function EventDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Event, User, Chore) {
        var vm = this;
        vm.event = entity;
        vm.users = User.query();
        vm.chores = Chore.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('tidyUpApp:eventUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.event.id !== null) {
                Event.update(vm.event, onSaveSuccess, onSaveError);
            } else {
                Event.save(vm.event, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.dateTill = false;
        vm.datePickerOpenStatus.dateDone = false;

        vm.openCalendar = function(date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
