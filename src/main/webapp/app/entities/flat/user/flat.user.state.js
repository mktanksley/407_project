(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('registerFlat', {
                parent: 'app',
                url: '/add/flat',
                data: {
                    authorities: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/flat/user/flat.user-dialog.html',
                        controller: 'FlatUserDialogController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('flat');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }],
                    entity: function () {
                        return {
                            name: null,
                            dateCreated: null,
                            id: null
                        };
                    }
                }
            })
            .state('userFlat', {
                parent: 'app',
                url: '/flat',
                data: {
                    authorities: ['ROLE_FLAT_ADMIN']
                },
                views: {
                    'content@': {
                        templateUrl: 'app/entities/flat/user/flat.user.html',
                        controller: 'FlatUserController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('flat');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }],
                    entity: function () {
                        return {
                            id: null, login: null, firstName: null, lastName: null, email: null,
                            activated: true, langKey: null, createdBy: null, createdDate: null,
                            lastModifiedBy: null, lastModifiedDate: null, resetDate: null,
                            resetKey: null, authorities: null,
                            points: null,
                            avatar: null,
                            avatarContentType: null
                        };
                    }
                }
            })
            .state('userFlat.newMember', {
                parent: 'userFlat',
                url: '/member/new',
                data: {
                    authorities: ['ROLE_FLAT_ADMIN']
                },
                params: {
                    flat: null
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/flat/user/flat.user-member-dialog.html',
                        controller: 'MemberDialogController',
                        controllerAs: 'vm',
                        backdrop: 'static',
                        size: 'lg',
                        resolve: {
                            entity: function () {
                                return {
                                    name: null,
                                    dateCreated: null,
                                    id: null
                                };
                            }
                        }
                    }).result.then(function () {
                        $state.go('userFlat', null, {reload: true});
                    }, function () {
                        $state.go('userFlat');
                    });
                }]
            })
            .state('userFlat.delete', {
                parent: 'userFlat',
                url: '/member/{login}/delete',
                data: {
                    authorities: ['ROLE_FLAT_ADMIN']
                },
                onEnter: ['$stateParams', '$state', '$uibModal', function ($stateParams, $state, $uibModal) {
                    $uibModal.open({
                        templateUrl: 'app/entities/flat/user/flat.user-member-delete-dialog.html',
                        controller: 'MemberDialogDeleteController',
                        controllerAs: 'vm',
                        size: 'md',
                        resolve: {
                            entity: ['User', function (User) {
                                return User.get({login: $stateParams.login});
                            }]
                        }
                    }).result.then(function () {
                        $state.go('userFlat', null, {reload: true});
                    }, function () {
                        $state.go('^');
                    });
                }]
            });
    }

})();
