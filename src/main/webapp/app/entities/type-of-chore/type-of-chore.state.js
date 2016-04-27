(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('type-of-chore', {
            parent: 'entity',
            url: '/type-of-chore',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'tidyUpApp.typeOfChore.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/type-of-chore/type-of-chores.html',
                    controller: 'TypeOfChoreController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('typeOfChore');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('type-of-chore-detail', {
            parent: 'entity',
            url: '/type-of-chore/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'tidyUpApp.typeOfChore.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/type-of-chore/type-of-chore-detail.html',
                    controller: 'TypeOfChoreDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('typeOfChore');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'TypeOfChore', function($stateParams, TypeOfChore) {
                    return TypeOfChore.get({id : $stateParams.id});
                }]
            }
        })
        .state('type-of-chore.new', {
            parent: 'type-of-chore',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/type-of-chore/type-of-chore-dialog.html',
                    controller: 'TypeOfChoreDialogController',
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
                    $state.go('type-of-chore', null, { reload: true });
                }, function() {
                    $state.go('type-of-chore');
                });
            }]
        })
        .state('type-of-chore.edit', {
            parent: 'type-of-chore',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/type-of-chore/type-of-chore-dialog.html',
                    controller: 'TypeOfChoreDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['TypeOfChore', function(TypeOfChore) {
                            return TypeOfChore.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('type-of-chore', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('type-of-chore.delete', {
            parent: 'type-of-chore',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/type-of-chore/type-of-chore-delete-dialog.html',
                    controller: 'TypeOfChoreDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['TypeOfChore', function(TypeOfChore) {
                            return TypeOfChore.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('type-of-chore', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
