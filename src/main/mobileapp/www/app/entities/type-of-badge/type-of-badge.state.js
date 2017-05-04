(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('type-of-badge', {
            parent: 'entity',
            url: '/type-of-badge',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'tidyUpApp.typeOfBadge.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/type-of-badge/type-of-badges.html',
                    controller: 'TypeOfBadgeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('typeOfBadge');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('type-of-badge-detail', {
            parent: 'entity',
            url: '/type-of-badge/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'tidyUpApp.typeOfBadge.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/type-of-badge/type-of-badge-detail.html',
                    controller: 'TypeOfBadgeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('typeOfBadge');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'TypeOfBadge', function($stateParams, TypeOfBadge) {
                    return TypeOfBadge.get({id : $stateParams.id});
                }]
            }
        })
        .state('type-of-badge.new', {
            parent: 'type-of-badge',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/type-of-badge/type-of-badge-dialog.html',
                    controller: 'TypeOfBadgeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                description: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('type-of-badge', null, { reload: true });
                }, function() {
                    $state.go('type-of-badge');
                });
            }]
        })
        .state('type-of-badge.edit', {
            parent: 'type-of-badge',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/type-of-badge/type-of-badge-dialog.html',
                    controller: 'TypeOfBadgeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['TypeOfBadge', function(TypeOfBadge) {
                            return TypeOfBadge.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('type-of-badge', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('type-of-badge.delete', {
            parent: 'type-of-badge',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/type-of-badge/type-of-badge-delete-dialog.html',
                    controller: 'TypeOfBadgeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['TypeOfBadge', function(TypeOfBadge) {
                            return TypeOfBadge.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('type-of-badge', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
