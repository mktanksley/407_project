(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('FlatUserDialogController', FlatUserDialogController);

    FlatUserDialogController.$inject = ['$scope', '$state', 'entity', 'Flat', 'User'];

    function FlatUserDialogController ($scope, $state, entity, Flat, User) {
        var vm = this;
        vm.flat = entity;
        vm.users = User.query();
        vm.flats = Flat.query();

        var onSaveSuccess = function (result) {
            $scope.$emit('tidyUpApp:flatUpdate', result);
            vm.isSaving = false;
            $state.go('home');
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
    }
})();
