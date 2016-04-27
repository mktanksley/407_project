(function() {
    'use strict';

    angular
        .module('tidyUpApp')
        .factory('TypeOfChoreSearch', TypeOfChoreSearch);

    TypeOfChoreSearch.$inject = ['$resource'];

    function TypeOfChoreSearch($resource) {
        var resourceUrl =  'api/_search/type-of-chores/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
