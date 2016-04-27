(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .factory('ChoreSearch', ChoreSearch);

    ChoreSearch.$inject = ['$resource'];

    function ChoreSearch($resource) {
        var resourceUrl =  'api/_search/chores/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
