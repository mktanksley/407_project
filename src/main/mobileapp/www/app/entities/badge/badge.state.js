(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('badge', {
            parent: 'entity',
            url: '/badge',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'tidyUpApp.badge.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/badge/badges.html',
                    controller: 'BadgeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('badge');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('badge-detail', {
            parent: 'entity',
            url: '/badge/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'tidyUpApp.badge.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/badge/badge-detail.html',
                    controller: 'BadgeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('badge');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Badge', function($stateParams, Badge) {
                    return Badge.get({id : $stateParams.id});
                }]
            }
        })
        .state('badge.new', {
            parent: 'badge',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/badge/badge-dialog.html',
                    controller: 'BadgeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                earnedAt: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('badge', null, { reload: true });
                }, function() {
                    $state.go('badge');
                });
            }]
        })
        .state('badge.edit', {
            parent: 'badge',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/badge/badge-dialog.html',
                    controller: 'BadgeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Badge', function(Badge) {
                            return Badge.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('badge', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('badge.delete', {
            parent: 'badge',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/badge/badge-delete-dialog.html',
                    controller: 'BadgeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Badge', function(Badge) {
                            return Badge.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('badge', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
