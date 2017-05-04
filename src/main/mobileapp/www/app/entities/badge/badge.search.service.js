(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .factory('BadgeSearch', BadgeSearch);

    BadgeSearch.$inject = ['$resource'];

    function BadgeSearch($resource) {
        var resourceUrl =  'api/_search/badges/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
