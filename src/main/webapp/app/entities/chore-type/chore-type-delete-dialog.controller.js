(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoreTypeDeleteController',ChoreTypeDeleteController);

    ChoreTypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'ChoreType'];

    function ChoreTypeDeleteController($uibModalInstance, entity, ChoreType) {
        var vm = this;
        vm.choreType = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            ChoreType.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
