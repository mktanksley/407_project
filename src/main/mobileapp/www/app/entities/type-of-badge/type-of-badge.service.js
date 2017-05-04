(function() {
    'use strict';
    angular
        .module('tidyUpApp')
        .factory('TypeOfBadge', TypeOfBadge);

    TypeOfBadge.$inject = ['$resource'];

    function TypeOfBadge ($resource) {
        var resourceUrl =  'api/type-of-badges/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
