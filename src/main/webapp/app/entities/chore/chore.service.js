(function() {
    'use strict';
    angular
        .module('tidyUpApp')
        .factory('Chore', Chore);

    Chore.$inject = ['$resource', 'DateUtils'];

    function Chore ($resource, DateUtils) {
        var resourceUrl =  'api/chores/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.date = DateUtils.convertDateTimeFromServer(data.date);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
