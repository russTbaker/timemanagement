'use strict';

var app = angular.module('timesheetApp', ['ui.bootstrap','ui.bootstrap.datetimepicker', 'ngResource', 'ngRoute', 'hljs', 'spring-data-rest', 'ngMessages'])
    .config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
        $routeProvider.when('/', {
                templateUrl: 'main.html'
            })
            .when('/employee', {
                templateUrl: 'views/employee/employee.html'
            })
            .when('/customer', {
                templateUrl: 'views/customer/customer.html'
            })
            .when('/contract', {
                templateUrl: 'views/contract/contract.html'
            })
            .when('/timesheet', {
                templateUrl: 'views/timesheet/timesheet.html'
            })
            .when('/addUser', {
                templateUrl: 'views/user/addUserProfile.html'
            })
            .when('/editUser/:userId', {
                templateUrl: 'views/user/editUserProfile.html',
                controller: 'EditUserController',
                controllerAs: 'controller'
            })
            .when('/editUser', {
                templateUrl: 'views/user/editUserProfile.html',
                controller: 'UserSelfServiceController',
                controllerAs: 'controller'
            })
            .when('/login', {
                templateUrl: 'views/login.html',
                controller: 'NavigationController',
                controllerAs: 'controller'
            })
            .otherwise({
                redirectTo: '/'
            });
        $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    }]);