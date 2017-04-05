(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('ChoresController', ChoresController);

    ChoresController.$inject = ['$state', 'Principal', 'AlertService', 'pagingParams', 'paginationConstants', 'ChoreEvent', 'ParseLinks'];

    function ChoresController($state, Principal, AlertService, pagingParams, paginationConstants, ChoreEvent, ParseLinks) {
        var vm = this;
        vm.account = null;
        vm.loadData = loadData;
        vm.loadPage = loadPage;
        vm.transition = transition;

        vm.loadData();

        function loadData() {
            Principal.identity(true).then(function (account) {
                vm.account = account;
            });

            ChoreEvent.query({
                page: pagingParams.page - 1,
                size: paginationConstants.itemsPerPage,
                sort: pagingParams.sort,
                repeatable: true
            }, onSuccess, onError);

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.choreEvents = data;
                vm.page = pagingParams.page;
            }

            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage (page) {
            vm.page = page;
            vm.transition();
        }

        function transition () {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: pagingParams.sort
            });
        }
    }
})();
