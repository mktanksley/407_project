(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('BadgeDialogController', BadgeDialogController);

    BadgeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Badge', 'TypeOfBadge', 'User'];

    function BadgeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Badge, TypeOfBadge, User) {
        var vm = this;
        vm.badge = entity;
        vm.typeofbadges = TypeOfBadge.query();
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('tidyUpApp:badgeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.badge.id !== null) {
                Badge.update(vm.badge, onSaveSuccess, onSaveError);
            } else {
                Badge.save(vm.badge, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.earnedAt = false;

        vm.openCalendar = function(date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
