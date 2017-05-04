(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoresDialogController', ChoresDialogController);

    ChoresDialogController.$inject = ['$timeout', '$scope', 'Principal', '$uibModalInstance', 'entity', 'ChoreEvent', 'ChoreType', 'Flat'];

    function ChoresDialogController($timeout, $scope, Principal, $uibModalInstance, entity, ChoreEvent, ChoreType, Flat) {
        var vm = this;
        vm.choreEvent = entity;
        vm.choretypes = ChoreType.query({repeatable: true});
        vm.account = null;
        vm.flat = null;
        vm.loadData = loadData;

        vm.loadData();

        function loadData() {
            Principal.identity(true).then(function (account) {
                vm.account = account;

                Flat.get({id: vm.account.memberOf.id}, function (flat) {
                    vm.flat = flat;
                }, function (error) {
                    AlertService.error(error.data.message);
                })

            });
        }

        $timeout(function () {
            angular.element('.form-group:eq(0)>input').focus();
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

        vm.clear = function () {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.dateTo = false;

        vm.openCalendar = function (date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
