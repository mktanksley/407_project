(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('chore-event', {
            parent: 'entity',
            url: '/chore-event?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'tidyUpApp.choreEvent.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/chore-event/chore-events.html',
                    controller: 'ChoreEventController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('choreEvent');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('chore-event-detail', {
            parent: 'entity',
            url: '/chore-event/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'tidyUpApp.choreEvent.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/chore-event/chore-event-detail.html',
                    controller: 'ChoreEventDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('choreEvent');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ChoreEvent', function($stateParams, ChoreEvent) {
                    return ChoreEvent.get({id : $stateParams.id});
                }]
            }
        })
        .state('chore-event.new', {
            parent: 'chore-event',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/chore-event/chore-event-dialog.html',
                    controller: 'ChoreEventDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                dateTo: null,
                                dateDone: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('chore-event', null, { reload: true });
                }, function() {
                    $state.go('chore-event');
                });
            }]
        })
        .state('chore-event.edit', {
            parent: 'chore-event',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/chore-event/chore-event-dialog.html',
                    controller: 'ChoreEventDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ChoreEvent', function(ChoreEvent) {
                            return ChoreEvent.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('chore-event', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('chore-event.delete', {
            parent: 'chore-event',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/chore-event/chore-event-delete-dialog.html',
                    controller: 'ChoreEventDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ChoreEvent', function(ChoreEvent) {
                            return ChoreEvent.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('chore-event', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
