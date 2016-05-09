(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('FlatDeleteController',FlatDeleteController);

    FlatDeleteController.$inject = ['$uibModalInstance', 'entity', 'Flat'];

    function FlatDeleteController($uibModalInstance, entity, Flat) {
        var vm = this;
        vm.flat = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            Flat.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
