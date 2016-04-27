(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoreDeleteController',ChoreDeleteController);

    ChoreDeleteController.$inject = ['$uibModalInstance', 'entity', 'Chore'];

    function ChoreDeleteController($uibModalInstance, entity, Chore) {
        var vm = this;
        vm.chore = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Chore.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
