(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
            .state('about', {
                parent: 'app',
                url: '/about',
                data: {
                    authorities: []
                    // pageTitle: 'tidyUpApp.about.title'
                },
                views: {
                    'content@': {
                        templateUrl: 'app/about/about.html'
                    }
                },
                resolve: {
                    translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                        // $translatePartialLoader.addPart('about');
                        $translatePartialLoader.addPart('global');
                        return $translate.refresh();
                    }]
                }
            });
    }

})();
