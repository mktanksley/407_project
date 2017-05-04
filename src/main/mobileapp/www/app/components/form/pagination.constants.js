(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .constant('paginationConstants', {
            'itemsPerPage': 10
        })
        .constant('pagerConstants', {
            'itemsPerPage': 3
        });
})();
