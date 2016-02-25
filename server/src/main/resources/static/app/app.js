'use strict';

var app = angular.module('timesheetApp', ['ui.bootstrap', 'ngResource', 'ngRoute', 'hljs', 'spring-data-rest'])
//app.config(function ($routeProvider) {
//        $routeProvider.when('/', {
//            templateUrl: '/js/app/app.html',
//            controller: 'appController'
//        })
//        $routeProvider.when('/employees', {
//            templateUrl: '/js/employees/employees.html',
//            controller: 'employeesController',
//            resolve: {
//                usersResource: function ($rootScope) {
//                    if ($rootScope.resource)
//                        return $rootScope.resource.$get('users', {
//                            linksAttribute: "_links",
//                            embeddedAttribute: "_embedded"
//                        });
//                    else
//                        return {};
//                }
//            }
//        }).when('/customers', {
//            templateUrl: '/js/customers/customers.html',
//            controller: 'customersController',
//            resolve: {
//                customersResource: function ($rootScope) {
//                    if ($rootScope.resource)
//                        return $rootScope.resource.$get('customers', {
//                            linksAttribute: "_links",
//                            embeddedAttribute: "_embedded"
//                        });
//                    else
//                        return {};
//                }
//            }
//        }).when('/timesheet', {
//            templateUrl: '/js/timesheet/timesheet.html',
//            controller: 'timesheetController',
//            resolve: {
//                customersResource: function ($rootScope) {
//                    if ($rootScope.resource)
//                        return $rootScope.resource.$get('timesheet', {
//                            linksAttribute: "_links",
//                            embeddedAttribute: "_embedded"
//                        });
//                    else
//                        return {};
//                }
//            }
//        }).when('/login', {
//            templateUrl: '/js/login/login.html',
//            controller: 'loginController'
//        }).when('/login', {
//            templateUrl: '/js/login/login.html',
//            controller: 'loginController'
//        }).otherwise({
//            redirectTo: '/'
//        });
//    })
    app.controller('NavigationController', function ($scope, $location) {
        $scope.navigateTo = function (path) {
            $location.path(path);
        };
    })
    .controller('employeesController', function ($scope, $http, SpringDataRestAdapter) {
        var httpPromise = $http.get('http://localhost:8080/api/employees').success(function (response) {
            $scope.response = angular.toJson(response, true);
        });

        SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
            $scope.employees = processedResponse._embeddedItems;
            $scope.processedResponse = angular.toJson(processedResponse, true);
        });
    })
    .controller('employeesController', function ($scope, $http, SpringDataRestAdapter) {
        $scope.isTimeSheetCollapsed = false;
        //$scope.isResponseCollapsed = true;
        //$scope.isProcessedResponseCollapsed = true;

        var httpPromise = $http.get('http://localhost:8080/api/employees').success(function (response) {
            $scope.response = angular.toJson(response, true);
        });

        SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
            $scope.employees = processedResponse._embeddedItems;
            $scope.processedResponse = angular.toJson(processedResponse, true);
        });

        $scope.onClickNavigateToTimeSheets = function(url){
            var httpPromise = $http.get(url).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
                $scope.timesheets = processedResponse._embeddedItems;
                $scope.processedResponse = angular.toJson(processedResponse, true);
            });
            $scope.isTimeSheetCollapsed = !$scope.isTimeSheetCollapsed;
        };

        $scope.onClickNavigateToAddresses = function(url){
            // go out and get items
        };

        $scope.onClickNavigateToPhones = function(url){
            // go out and get items
        };

        $scope.onClickNavigateToEmails = function(url){
            // go out and get items
        };

        $scope.onClickAdd = function (timeSheetEntry, url) {
            var httpPromiseTimesheet = $http.put(url, timeSheetEntry, 'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                });
//alert("content: " + timeSheetEntry) ;
//            var httpPromiseTimesheet =$http({
//                method: 'PUT',
//                url: url,
//                data: timeSheetEntry,
//                headers: {
//                    'Content-Type': 'application/json+hal',
//                    'Accept' : 'application/json+hal'
//                }}).success(
//                function (response) {
//                    $scope.response = angular.toJson(response, true);
//                });
//
            SpringDataRestAdapter.process(httpPromiseTimesheet).then(function (processedResponse) {
                $scope.timeSheetEntry = processedResponse._embeddedItems;
                $scope.processedResponse = angular.toJson(processedResponse, true);
            });
        };

    });
    //.controller('timesheetController', function ($scope, $http, SpringDataRestAdapter) {
    //    var postUrl;
    //    var httpPromiseTimesheet = $http.get('http://localhost:8080/hydrated/employees/').success(function (response) {
    //        $scope.response = angular.toJson(response, true);
    //    });
    //
    //    SpringDataRestAdapter.process(httpPromiseTimesheet).then(function (processedResponse) {
    //        $scope.timeSheetEntry = processedResponse._embeddedItems;
    //        $scope.processedResponse = angular.toJson(processedResponse, true);
    //    });
    //
    //    // from employees get timesheets http://localhost:8080/api/employees/1/timesheets
    //    // Pull out timeSheets._links.timeSheetEntries.href to get timesheeEntries
    //
    //
    //})
    //.controller('appController', function ($scope, $http, SpringDataRestAdapter) {
    //    //var httpPromise = $http.get('http://localhost:8080/api/').success(function (response) {
    //    //    $scope.response = angular.toJson(response, true);
    //    //});
    //    //
    //    //SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
    //    //    $scope.customers = processedResponse._embeddedItems;
    //    //    $scope.processedResponse = angular.toJson(processedResponse, true);
    //    //});
    //    //
    //    //var httpPromise = $http.get('http://localhost:8080/api/users/').success(function (response) {
    //    //    $scope.response = angular.toJson(response, true);
    //    //});
    //    //
    //    //SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
    //    //    $scope.users = processedResponse._embeddedItems;
    //    //    $scope.processedResponse = angular.toJson(processedResponse, true);
    //    //});
    //});
//(function(angular) {
//    angular.module("timesheetApp.controllers", []);
//    angular.module("timesheetApp.employeeServices", []);
//    angular.module("timesheetApp", ["ngResource", "timesheetApp.controllers", "timesheetApp.employeeServices"]);
//}(angular));