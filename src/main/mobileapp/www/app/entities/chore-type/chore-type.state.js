(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('chore-type', {
            parent: 'entity',
            url: '/chore-type',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'tidyUpApp.choreType.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/chore-type/chore-types.html',
                    controller: 'ChoreTypeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('choreType');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('chore-type-detail', {
            parent: 'entity',
            url: '/chore-type/{id}',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'tidyUpApp.choreType.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/chore-type/chore-type-detail.html',
                    controller: 'ChoreTypeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('choreType');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ChoreType', function($stateParams, ChoreType) {
                    return ChoreType.get({id : $stateParams.id});
                }]
            }
        })
        .state('chore-type.new', {
            parent: 'chore-type',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/chore-type/chore-type-dialog.html',
                    controller: 'ChoreTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                repeatable: false,
                                interval: null,
                                points: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('chore-type', null, { reload: true });
                }, function() {
                    $state.go('chore-type');
                });
            }]
        })
        .state('chore-type.edit', {
            parent: 'chore-type',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/chore-type/chore-type-dialog.html',
                    controller: 'ChoreTypeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ChoreType', function(ChoreType) {
                            return ChoreType.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('chore-type', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('chore-type.delete', {
            parent: 'chore-type',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/chore-type/chore-type-delete-dialog.html',
                    controller: 'ChoreTypeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ChoreType', function(ChoreType) {
                            return ChoreType.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('chore-type', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
