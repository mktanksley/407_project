(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .factory('ChoreEventSearch', ChoreEventSearch);

    ChoreEventSearch.$inject = ['$resource'];

    function ChoreEventSearch($resource) {
        var resourceUrl =  'api/_search/chore-events/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
