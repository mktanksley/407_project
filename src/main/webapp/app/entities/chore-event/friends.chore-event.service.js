(function () {
    'use strict';
    angular
        .module('tidyUpApp')
        .factory('FriendsChoreEvent', FriendsChoreEvent);

    FriendsChoreEvent.$inject = ['$resource'];

    function FriendsChoreEvent($resource) {
        var resourceUrl = 'api/friends-chore-events';

        return $resource(resourceUrl, {}, {
            'query': {
                method: 'GET',
                isArray: true}
        });
    }
})();
