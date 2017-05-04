(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('BadgeDeleteController',BadgeDeleteController);

    BadgeDeleteController.$inject = ['$uibModalInstance', 'entity', 'Badge'];

    function BadgeDeleteController($uibModalInstance, entity, Badge) {
        var vm = this;
        vm.badge = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Badge.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
