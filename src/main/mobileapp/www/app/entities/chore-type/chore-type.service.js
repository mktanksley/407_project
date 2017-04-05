(function() {
    'use strict';
    angular
        .module('tidyUpApp')
        .factory('ChoreType', ChoreType);

    ChoreType.$inject = ['$resource'];

    function ChoreType ($resource) {
        var resourceUrl =  'api/chore-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
