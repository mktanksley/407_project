(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoreEventDeleteController',ChoreEventDeleteController);

    ChoreEventDeleteController.$inject = ['$uibModalInstance', 'entity', 'ChoreEvent'];

    function ChoreEventDeleteController($uibModalInstance, entity, ChoreEvent) {
        var vm = this;
        vm.choreEvent = entity;
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
