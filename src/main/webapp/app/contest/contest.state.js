(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('contest', {
                parent: 'app',
                url: '/contest',
                data: {
                    authorities: []
                    // pageTitle: 'tidyUpApp.about.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/contest/contest.html',
                        controller: 'ContestController',
                        controllerAs: 'vm'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    }

})();
