(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .factory('TypeOfBadgeSearch', TypeOfBadgeSearch);

    TypeOfBadgeSearch.$inject = ['$resource'];

    function TypeOfBadgeSearch($resource) {
        var resourceUrl =  'api/_search/type-of-badges/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
