(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('FlatUserDialogController', FlatUserDialogController);

    FlatUserDialogController.$inject = ['$scope', '$state', 'entity', 'Flat', 'AlertService'];

    function FlatUserDialogController($scope, $state, entity, Flat, AlertService) {
        var vm = this;
        vm.flat = entity;
        vm.flats = Flat.query();
        //TODO check if user already has a flat > go to homepage

        var onSaveSuccess = function (result) {
            $scope.$emit('tidyUpApp:flatUpdate', result);
            vm.isSaving = false;
            AlertService.success("tidyUpApp.flat.registered");
            $state.go('userFlat.newMember', {flat: result});
        };

        var onSaveError = function () {
            vm.isSaving = false;
            AlertService.error("Could not save flat, try again, please.");
        };

        vm.save = function () {
            vm.isSaving = true;
                Flat.save(vm.flat, onSaveSuccess, onSaveError);
        };
    }
})();
