(function() {
    'use strict';
    angular
        .module('tidyUpApp')
        .factory('ChoreEvent', ChoreEvent);

    ChoreEvent.$inject = ['$resource', 'DateUtils'];

    function ChoreEvent ($resource, DateUtils) {
        var resourceUrl =  'api/chore-events/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.dateTo = DateUtils.convertDateTimeFromServer(data.dateTo);
                    data.dateDone = DateUtils.convertDateTimeFromServer(data.dateDone);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
