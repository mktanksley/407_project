(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('HomeUserController', HomeUserController);

    HomeUserController.$inject = ['$state', 'Principal', 'ChoreType', 'ChoreEvent', 'FriendsChoreEvent', 'AlertService', 'ParseLinks', 'pagerConstants', 'User', 'TodoChore'];

    function HomeUserController($state, Principal, ChoreType, ChoreEvent, FriendsChoreEvent, AlertService, ParseLinks, pagerConstants, User, TodoChore) {
        var vm = this;

        vm.account = null;
        vm.user = null;
        vm.choreTypes = null;
        vm.choreEvents = null;
        vm.friendChoreEvents = [];
        vm.totalItems = 0;

        vm.isEventSaving = false;
        vm.saveChoreEvent = saveChoreEvent;
        vm.onError = onError;

        vm.loadChoreEvents = loadChoreEvents;
        vm.loadFriendChoreEvents = loadFriendChoreEvents;
        vm.getAccount = getAccount;
        vm.getChoreTypes = getChoreTypes;

        vm.todoChore = null;
        vm.getTodoEvent = getTodoEvent;
        vm.fulfillTodoChore = fulfillTodoChore;

        vm.getTodoEvent();

        // pagination for events
        vm.loadPage = loadPage;
        vm.page = $state.params.page;
        vm.transition = transition;
        vm.clear = clear;

        vm.loadChoreEvents();
        vm.loadFriendChoreEvents();
        vm.getAccount();
        vm.getChoreTypes();

        function getAccount() {
            Principal.identity().then(function (account) {
                vm.account = account;
            });
        }

        function getChoreTypes() {
            ChoreType.query({repeatable: false}, function (result) {
                vm.choreTypes = result;
            });
        }

        // methods for saving one-time chore event
        var onSaveSuccess = function (result) {
            vm.isEventSaving = false;
            AlertService.success('home.choreEvent.success');
            loadChoreEvents();
        };

        var onSaveError = function () {
            vm.isEventSaving = false;
            AlertService.error('home.choreEvent.error');
        };

        /**
         * Loads current User entity, creates new choreEvent and updates User with choreType points.
         *
         * @param choreType
         */
        function saveChoreEvent(choreType) {
            vm.isEventSaving = true;
            vm.isUserSaving = true;

            // load User
            User.get({login: vm.account.login}, function (result) {
                vm.user = result;
                var choreEvent = {
                    id: null,
                    dateTo: null,
                    dateDone: new Date(),
                    isType: {id: choreType.id},
                    doneBy: vm.user
                };

                // update User
                var userToUpdate = vm.user;
                userToUpdate.points += choreType.points;
                User.update(userToUpdate, function (result) {
                    vm.isUserSaving = false;
                    // reload User points
                    vm.account.points = result.points;

                    // create ChoreEvent
                    ChoreEvent.save(choreEvent, onSaveSuccess, onSaveError);
                }, onSaveError);
            });
        }

        // methods to handle list of events:
        function loadChoreEvents() {
            ChoreEvent.query({
                page: vm.page - 1,
                size: pagerConstants.itemsPerPage,
                sort: ['dateDone,desc']
            }, onSuccess, onError);

            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.choreEvents = data;
                // vm.page = pagingParams.page;
            }
        }

        function loadFriendChoreEvents() {
            FriendsChoreEvent.query(function (result) {
                vm.friendChoreEvents = result;
            });
        }

        function onError(error) {
            AlertService.error(error.data.message);
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page
            });
        }

        function clear() {
            vm.links = null;
            vm.page = 0;
            vm.transition();
        }

        function getTodoEvent() {
            vm.todoChore = TodoChore.get({}, function (data) {
                vm.todoChore = data;
            }, function () {
                vm.todoChore = null;
            });
        }

        function fulfillTodoChore() {
            vm.isUserSaving = true;

            // load User
            User.get({login: vm.account.login}, function (result) {
                vm.user = result;
                vm.todoChore.dateDone = new Date();

                // update User
                var userToUpdate = vm.user;
                userToUpdate.points += vm.todoChore.isType.points;
                User.update(userToUpdate, function (result) {
                    vm.isUserSaving = false;
                    // reload User points
                    vm.account.points = result.points;

                    // update chore
                    vm.todoChore.dateDone = new Date();
                    ChoreEvent.update(vm.todoChore, function () {
                        AlertService.success('home.choreEvent.success');
                        vm.getTodoEvent();
                        vm.loadChoreEvents();
                    }, onSaveError);

                }, onSaveError);
            });
        }
    }
})();
