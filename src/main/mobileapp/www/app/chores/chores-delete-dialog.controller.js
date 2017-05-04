(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoresDeleteController',ChoresDeleteController);

    ChoresDeleteController.$inject = ['$uibModalInstance', 'entity', 'ChoreEvent'];

    function ChoresDeleteController($uibModalInstance, entity, ChoreEvent) {
        var vm = this;
        vm.chore = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            ChoreEvent.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
