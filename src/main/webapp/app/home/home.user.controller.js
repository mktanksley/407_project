(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('HomeUserController', HomeUserController);

    HomeUserController.$inject = ['Principal', 'ChoreType', 'ChoreEvent', 'AlertService'];

    function HomeUserController(Principal, ChoreType, ChoreEvent, AlertService) {
        var vm = this;

        vm.account = null;
        vm.user = null;

        vm.choreTypes = null;

        vm.isEventSaving = false;
        vm.saveChoreEvent = saveChoreEvent;

        getAccount();
        getChoreTypes();

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
            });
        }

        function getChoreTypes() {
            ChoreType.query(function (result) {
                vm.choreTypes = result;
            });
        }

        // methods for saving one-time chore event
        var onSaveSuccess = function (result) {
            // add event update listeners?
            // $scope.$emit('tidyUpApp:choreEventUpdate', result);
            vm.isEventSaving = false;
            AlertService.success('home.choreEvent.success');
        };
        var onSaveError = function () {
            vm.isEventSaving = false;
            AlertService.error('home.choreEvent.error');
        };
        function saveChoreEvent(choreType) {
            vm.isEventSaving = true;
            
            var choreEvent = {
                id: null,
                dateTo: null,
                dateDone: null,
                isType: {id: choreType.id}
            };

            ChoreEvent.save(choreEvent, onSaveSuccess, onSaveError);
        }

    }
})();
