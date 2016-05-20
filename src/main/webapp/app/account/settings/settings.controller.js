(function () {
    'use strict';

    angular
        .module('tidyUpApp')
        .controller('SettingsController', SettingsController);

    SettingsController.$inject = ['$scope', 'Principal', 'Auth', 'JhiLanguageService', '$translate', 'DataUtils'];

    function SettingsController($scope, Principal, Auth, JhiLanguageService, $translate, DataUtils) {
        var vm = this;

        vm.error = null;
        vm.save = save;
        vm.settingsAccount = null;
        vm.success = null;

        /**
         * Store the "settings account" in a separate variable, and not in the shared "account" variable.
         */
        var copyAccount = function (account) {
            return {
                activated: account.activated,
                email: account.email,
                firstName: account.firstName,
                langKey: account.langKey,
                lastName: account.lastName,
                login: account.login,
                points: account.points,
                avatar: account.avatar,
                avatarContentType: account.avatarContentType
            };
        };

        Principal.identity().then(function (account) {
            vm.settingsAccount = copyAccount(account);
        });

        vm.setAvatar = function ($file, user) {
            if ($file && $file.$error === 'pattern') {
                return;
            }
            if ($file) {
                DataUtils.toBase64($file, function(base64Data) {
                    $scope.$apply(function() {
                        user.avatar = base64Data;
                        user.avatarContentType = $file.type;
                    });
                });
            }
        };

        function save() {
            Auth.updateAccount(vm.settingsAccount).then(function () {
                vm.error = null;
                vm.success = 'OK';
                Principal.identity(true).then(function (account) {
                    vm.settingsAccount = copyAccount(account);
                });
                JhiLanguageService.getCurrent().then(function (current) {
                    if (vm.settingsAccount.langKey !== current) {
                        $translate.use(vm.settingsAccount.langKey);
                    }
                });
            }).catch(function () {
                vm.success = null;
                vm.error = 'ERROR';
            });
        }
    }
})();
