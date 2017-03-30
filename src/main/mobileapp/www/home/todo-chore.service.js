(function () {
    'use strict';
    angular
        .module('tidyUpApp')
        .factory('TodoChore', TodoChore);

    TodoChore.$inject = ['$resource', 'DateUtils'];

    function TodoChore($resource, DateUtils) {
        var resourceUrl = 'api/to-do/';

        return $resource(resourceUrl, {}, {
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dateTo = DateUtils.convertDateTimeFromServer(data.dateTo);
                        data.dateDone = DateUtils.convertDateTimeFromServer(data.dateDone);
                    }
                    return data;
                }
            },
            'update': {method: 'PUT'}
        });
    }
})();
