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
                        templateUrl: 'app/home/home.html',
                        controller: 'HomeController',
                        controllerAs: 'vm'
                    },
                    'login@home': {
                        templateUrl: 'app/components/login/login-home.html',
                        // controller: 'LoginController',
                        // controllerAs: 'coto'
                    },
                    'register@home': {
                        templateUrl: 'app/account/register/register.html',
                        // controller: 'LoginController',
                        // controllerAs: 'coto'
                    }
                },
                resolve: {
                    mainTranslatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('home');
                        $translatePartialLoader.addPart('login');
                        $translatePartialLoader.addPart('register');
                        return $translate.refresh();
                    }]
                }
            })
            .state('none', {});
    }
})();
