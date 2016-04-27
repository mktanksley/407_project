(function() {
    'use strict';
    angular
        .module('tidyUpApp')
        .factory('Event', Event);

    Event.$inject = ['$resource', 'DateUtils'];

    function Event ($resource, DateUtils) {
        var resourceUrl =  'api/events/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.dateTill = DateUtils.convertDateTimeFromServer(data.dateTill);
                    data.dateDone = DateUtils.convertDateTimeFromServer(data.dateDone);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
