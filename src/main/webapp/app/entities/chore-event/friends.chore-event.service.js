(function () {
    'use strict';
    angular
        .module('tidyUpApp')
        .factory('FriendsChoreEvent', FriendsChoreEvent);

    FriendsChoreEvent.$inject = ['$resource', 'DateUtils'];

    function FriendsChoreEvent($resource, DateUtils) {
        var resourceUrl = 'api/friends-chore-events';

        return $resource(resourceUrl, {}, {
            'query': {method: 'GET', isArray: true}
        });
    }
})();
