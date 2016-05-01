(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('home', {
                parent: 'app',
                url: '/',
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        controller: 'HomeController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('home');
                        return $translate.refresh();
                    }]
                }
            })
            .state('home.user', {
                data: {
                    authorities: ['ROLE_USER']
                },
                views: {
                    'content@': {
                        templateUrl: 'app/home/home-user.html'
                    }
                }
            })
            .state('home.public', {
                data: {
                    authorities: []
                },
                views: {
                    'content@': {
                        templateUrl: 'app/home/home-public.html',
                        controller: 'HomePublicController'
                    },
                    'login@home.public': {
                        templateUrl: 'app/components/login/login-home.html',
                        // controller: 'LoginController',
                        // controllerAs: 'coto'
                    },
                    'register@home.public': {
                        templateUrl: 'app/account/register/register.html',
                        // controller: 'LoginController',
                        // controllerAs: 'coto'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('login');
                        $translatePartialLoader.addPart('register');
                        return $translate.refresh();
                    }]
                }
            });
    }
})();
