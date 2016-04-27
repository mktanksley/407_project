(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('TypeOfChoreDeleteController',TypeOfChoreDeleteController);

    TypeOfChoreDeleteController.$inject = ['$uibModalInstance', 'entity', 'TypeOfChore'];

    function TypeOfChoreDeleteController($uibModalInstance, entity, TypeOfChore) {
        var vm = this;
        vm.typeOfChore = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            TypeOfChore.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
