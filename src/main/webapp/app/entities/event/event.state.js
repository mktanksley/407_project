(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('event', {
            parent: 'entity',
            url: '/event?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'tidyUpApp.event.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/event/events.html',
                    controller: 'EventController',
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
                    $translatePartialLoader.addPart('event');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('event-detail', {
            parent: 'entity',
            url: '/event/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'tidyUpApp.event.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/event/event-detail.html',
                    controller: 'EventDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('event');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Event', function($stateParams, Event) {
                    return Event.get({id : $stateParams.id});
                }]
            }
        })
        .state('event.new', {
            parent: 'event',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/event/event-dialog.html',
                    controller: 'EventDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                dateTill: null,
                                dateDone: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('event', null, { reload: true });
                }, function() {
                    $state.go('event');
                });
            }]
        })
        .state('event.edit', {
            parent: 'event',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/event/event-dialog.html',
                    controller: 'EventDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Event', function(Event) {
                            return Event.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('event', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('event.delete', {
            parent: 'event',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/event/event-delete-dialog.html',
                    controller: 'EventDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Event', function(Event) {
                            return Event.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('event', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
