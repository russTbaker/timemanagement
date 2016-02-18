var app = angular.module('gsRoleBasedUI', ['ui.bootstrap', 'ngResource', 'ngRoute', 'hljs', 'spring-data-rest']);

app.config(function ($routeProvider) {
        $routeProvider.when('/', {
            templateUrl: '/js/app/app.html',
            controller: 'appController'
        })
        $routeProvider.when('/employees', {
            templateUrl: '/js/employees/employees.html',
            controller: 'employeesController',
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
        }).when('/customers', {
            templateUrl: '/js/customers/customers.html',
            controller: 'customersController',
            resolve: {
                customersResource: function ($rootScope) {
                    if ($rootScope.resource)
                        return $rootScope.resource.$get('customers', {
                            linksAttribute: "_links",
                            embeddedAttribute: "_embedded"
                        });
                    else
                        return {};
                }
            }
        }).when('/timesheet', {
            templateUrl: '/js/timesheet/timesheet.html',
            controller: 'timesheetController',
            resolve: {
                customersResource: function ($rootScope) {
                    if ($rootScope.resource)
                        return $rootScope.resource.$get('timesheet', {
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
        }).when('/login', {
            templateUrl: '/js/login/login.html',
            controller: 'loginController'
        }).otherwise({
            redirectTo: '/'
        });
    })
    .controller('customersController', function ($scope, $http, SpringDataRestAdapter) {
        var httpPromise = $http.get('http://localhost:8080/api/customers/').success(function (response) {
            $scope.response = angular.toJson(response, true);
        });

        SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
            $scope.customers = processedResponse._embeddedItems;
            $scope.processedResponse = angular.toJson(processedResponse, true);
        });
    })
    .controller('employeesController', function ($scope, $http) {
        $http.get('http://localhost:8080/hydrated/employee/').success(function (data, status, headers, config) {
            $scope.employees = data;
        }).error(function (data, status, headers, config) {
            console.log(data);
        })

        $scope.onClickAdd = function (timeSheetEntry, url, SpringDataRestAdapter) {
            var httpPromiseTimesheet = $http.post('http://localhost:8080/' + url, timeSheetEntry).success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
            });

            //SpringDataRestAdapter.process(httpPromiseTimesheet).then(function (processedResponse) {
            //    $scope.timeSheetEntry = processedResponse._embeddedItems;
            //    $scope.processedResponse = angular.toJson(processedResponse, true);
            //});
        };

    })
    .controller('timesheetController', function ($scope, $http, SpringDataRestAdapter) {
        var postUrl;
        var httpPromiseTimesheet = $http.get('http://localhost:8080/hydrated/employees/').success(function (response) {
            $scope.response = angular.toJson(response, true);
        });

        SpringDataRestAdapter.process(httpPromiseTimesheet).then(function (processedResponse) {
            $scope.timeSheetEntry = processedResponse._embeddedItems;
            $scope.processedResponse = angular.toJson(processedResponse, true);
        });

        // from employees get timesheets http://localhost:8080/api/employees/1/timesheets
        // Pull out timeSheets._links.timeSheetEntries.href to get timesheeEntries



    })
    .controller('appController', function ($scope, $http, SpringDataRestAdapter) {
        //var httpPromise = $http.get('http://localhost:8080/api/').success(function (response) {
        //    $scope.response = angular.toJson(response, true);
        //});
        //
        //SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
        //    $scope.customers = processedResponse._embeddedItems;
        //    $scope.processedResponse = angular.toJson(processedResponse, true);
        //});
        //
        //var httpPromise = $http.get('http://localhost:8080/api/users/').success(function (response) {
        //    $scope.response = angular.toJson(response, true);
        //});
        //
        //SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
        //    $scope.users = processedResponse._embeddedItems;
        //    $scope.processedResponse = angular.toJson(processedResponse, true);
        //});
    });
