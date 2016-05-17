(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .config(pagerConfig);

    pagerConfig.$inject = ['uibPagerConfig', 'pagerConstants'];

    function pagerConfig(uibPagerConfig, pagerConstants) {
        uibPagerConfig.itemsPerPage = pagerConstants.itemsPerPage;
        uibPagerConfig.previousText = '↑ Newer';
        uibPagerConfig.nextText = 'Older ↓';
    }
})();
