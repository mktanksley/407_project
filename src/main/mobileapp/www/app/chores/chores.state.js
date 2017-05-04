(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('chores', {
                parent: 'app',
                url: '/chores',
                data: {
                    authorities: ['ROLE_FLAT_ADMIN']
                },
                views: {
                    'content@': {
                        templateUrl: 'app/chores/chores.html',
                        controller: 'ChoresController',
                        controllerAs: 'vm'
                    }
                },
                params: {
                    page: {
                        value: '1',
                        squash: true
                    },
                    sort: {
                        value: 'dateDone,desc',
                        squash: true
                    }
                },
                resolve: {
                    pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                        return {
                            page: PaginationUtil.parsePage($stateParams.page),
                            sort: $stateParams.sort,
                            predicate: PaginationUtil.parsePredicate($stateParams.sort),
                            ascending: PaginationUtil.parseAscending($stateParams.sort)
                        };
                    }],
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('choreEvent');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            })
            .state('chores.new', {
                parent: 'chores',
                url: '/new',
                data: {
                    authorities: ['ROLE_FLAT_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/chores/chores-dialog.html',
                        controller: 'ChoresDialogController',
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
                    }).result.then(function () {
                        $state.go('chores', null, {reload: true});
                    }, function () {
                        $state.go('chores');
                    });
                }]
            })
            .state('chores.edit', {
                parent: 'chores',
                url: '/{id}/edit',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/chores/chores-dialog.html',
                        controller: 'ChoresDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: ['ChoreEvent', function(ChoreEvent) {
                                return ChoreEvent.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function() {
                        $state.go('chores', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    });
                }]
            })
            .state('chores.delete', {
                parent: 'chores',
                url: '/{id}/delete',
                data: {
                    authorities: ['ROLE_USER']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/chores/chores-delete-dialog.html',
                        controller: 'ChoresDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['ChoreEvent', function(ChoreEvent) {
                                return ChoreEvent.get({id : $stateParams.id});
                            }]
                        }
                    }).result.then(function() {
                        $state.go('chores', null, { reload: true });
                    }, function() {
                        $state.go('^');
                    });
                }]
            });
    }

})();
