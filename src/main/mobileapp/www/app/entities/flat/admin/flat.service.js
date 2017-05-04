(function() {
    'use strict';
    angular
        .module('tidyUpApp')
        .factory('Flat', Flat);

    Flat.$inject = ['$resource', 'DateUtils'];

    function Flat ($resource, DateUtils) {
        var resourceUrl =  'api/flats/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.dateCreated = DateUtils.convertDateTimeFromServer(data.dateCreated);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
