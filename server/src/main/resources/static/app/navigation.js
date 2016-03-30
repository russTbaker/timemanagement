app.controller('NavigationController', function ($rootScope, $scope, $http, $location) {

    var authenticate = function (credentials, callback) {

        var headers = credentials ? {
            authorization: "Basic "
            + btoa(credentials.username + ":" + credentials.password)
        } : {};

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

    };


    $scope.logout = function () {
        $http.post('logout', {}).success(function () {
            $rootScope.authenticated = false;
            $scope.admin = false;
            $location.path("/");
        }).error(function (data) {
            $rootScope.authenticated = false;
            $scope.admin = false;
        });
    };

    authenticate();
    $scope.credentials = {};
    $scope.login = function () {
        authenticate($scope.credentials, function () {
            if ($rootScope.authenticated) {
                $location.path("/");
                $scope.error = false;
            } else {
                $location.path("/login");
                $scope.error = true;
            }
        });
    };

    $scope.logout = function () {
        $http.post('logout', {}).finally(function () {
            $rootScope.authenticated = false;
            $location.path("/");
        });
    };
    $scope.navigateTo = function (path) {
        $location.path(path);
    };
});