function getUserAddresses(SpringDataRestAdapter, $http, usersUri, $scope) {
    SpringDataRestAdapter.process($http.get(usersUri + '/addresses').success(function (response) {
        $scope.response = angular.toJson(response, true);
    })).then(function (processedResponse) {
        $scope.user.addresses = processedResponse._embeddedItems;
        console.log("Addresses found.")
    });
}
function getUserPhones(SpringDataRestAdapter, $http, usersUri, $scope) {
    SpringDataRestAdapter.process($http.get(usersUri + '/phones').success(function (response) {
        $scope.response = angular.toJson(response, true);
    })).then(function (processedResponse) {
        $scope.user.phones = processedResponse._embeddedItems;

    });
}
function getUserEmails(SpringDataRestAdapter, $http, usersUri, $scope) {
    SpringDataRestAdapter.process($http.get(usersUri + '/emails').success(function (response) {
        $scope.response = angular.toJson(response, true);
    })).then(function (processedResponse) {
        $scope.user.emails = processedResponse._embeddedItems;
    });
}
function refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri) {
    setTimeout(function () {
        $route.reload();
        init(SpringDataRestAdapter, $http, $scope, usersUri);
    }, delay);
}
function init(SpringDataRestAdapter, $http, $scope, usersUri) {
    SpringDataRestAdapter.process($http.get(usersUri).success(function (response) {
        $scope.response = angular.toJson(response, true);
    })).then(function (processedResponse) {
        $scope.processedResponse = angular.toJson(processedResponse, true);
        $scope.user = processedResponse;
        getUserAddresses(SpringDataRestAdapter, $http, usersUri, $scope);
        getUserPhones(SpringDataRestAdapter, $http, usersUri, $scope);
        getUserEmails(SpringDataRestAdapter, $http, usersUri, $scope);

    });
}
function openModal($uibModal, ModalInstanceCtrl, $scope, $log, templateUrl) {
    var modalInstance = $uibModal.open({
        templateUrl: templateUrl,
        controller: ModalInstanceCtrl,
        scope: $scope,
        resolve: {
            form: function () {
                return $scope.form;
            }
        }
    });

    modalInstance.result.then(function (selectedItem) {
        $scope.selected = selectedItem;
    }, function () {
        $log.info('Modal dismissed at: ' + new Date());
    });
}
function getCurrentUser($http, headers, $rootScope, $scope, callback) {
    $http.get('user', {headers: headers}).success(function (data) {
        if (data.name) {
            $rootScope.authenticated = true;
            $scope.user = data.name;
            $scope.admin = data && data.roles && data.roles.indexOf("ROLE_ADMINISTRATOR") != -1;
        } else {
            $rootScope.authenticated = false;
            $scope.admin = false;
        }
        callback && callback();
    }).error(function () {
        $rootScope.authenticated = false;
        callback && callback();
    });
}