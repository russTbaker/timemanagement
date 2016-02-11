var app = angular.module('gsRoleBasedUI', ['ui.bootstrap', 'ngResource', 'ngRoute', 'hljs', 'spring-data-rest']);

app.config(function ($routeProvider) {
        $routeProvider.when('/', {
            templateUrl: '/js/app/app.html',
            controller: 'appController'
        })
        $routeProvider.when('/users', {
            templateUrl: '/js/users/users.html',
            controller: 'SamplesListEmbeddedItemsController',
            resolve: {
                usersResource: function ($rootScope) {
                    if ($rootScope.resource)
                        return $rootScope.resource.$get('users', {
                            linksAttribute: "_links",
                            embeddedAttribute: "_embedded"
                        });
                    else
                        return {};
                }
            }
        }).when('/login', {
            templateUrl: '/js/login/login.html',
            controller: 'loginController'
        }).otherwise({
            redirectTo: '/'
        });
    })
    //.controller('appController', ['$rootScope',
    //    '$scope',
    //    'spring-data-rest',
    //    function ($rootScope, $scope, SpringDataRestAdapter) {
    //
    //        $scope.root = function () {
    //            $http.$get('http://localhost:8080/', {
    //                linksAttribute: "_links"
    //            }).then(function (resource) {
    //                $rootScope.resource = resource;
    //            });
    //        };
    //
    //        $scope.root();
    //
    //    }])
    .controller('SamplesListEmbeddedItemsController', function ($scope, $http, SpringDataRestAdapter) {
        var httpPromise = $http.get('http://localhost:8080/customers/').success(function (response) {
            $scope.response = angular.toJson(response, true);
        });

        SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
            $scope.customers = processedResponse._embeddedItems;
            $scope.processedResponse = angular.toJson(processedResponse, true);
        });
    })
    .controller('appController', function ($scope, $http, SpringDataRestAdapter) {
    var httpPromise = $http.get('http://localhost:8080/').success(function (response) {
        $scope.response = angular.toJson(response, true);
    });

    SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
        $scope.customers = processedResponse._embeddedItems;
        $scope.processedResponse = angular.toJson(processedResponse, true);
    });
});
