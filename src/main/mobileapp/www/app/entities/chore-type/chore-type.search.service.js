(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .factory('ChoreTypeSearch', ChoreTypeSearch);

    ChoreTypeSearch.$inject = ['$resource'];

    function ChoreTypeSearch($resource) {
        var resourceUrl =  'api/_search/chore-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
