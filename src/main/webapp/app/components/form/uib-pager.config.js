(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .config(pagerConfig);

    function pagerConfig(uibPagerConfig, pagerConstants) {
        uibPagerConfig.itemsPerPage = pagerConstants.itemsPerPage;
        uibPagerConfig.previousText = '↑ Newer';
        uibPagerConfig.nextText = 'Older ↓';
    }
})();
