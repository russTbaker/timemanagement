'use strict';

var app = angular.module('timesheetApp', ['ui.bootstrap', 'ngResource', 'ngRoute', 'hljs', 'spring-data-rest'])
    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/', {
            templateUrl: 'main.html'
        }).when('/employee', {
                templateUrl: 'views/employee/employee.html'
            })
            .when('/addEmployee', {
                templateUrl: 'views/employee/addEmployee.html'
            })
            .when('/addEmployeeAddress/:employeeId', {
                templateUrl: 'views/employee/addEmployeeAddress.html'
            })
            .when('/addEmployeePhone/:employeeId', {
                templateUrl: 'views/employee/addEmployeePhone.html'
            })
            // .when('/samples/automatic-link-fetching', {
            //    templateUrl: 'partials/samples/automatic-link-fetching.html'
            //}).when('/samples/add-query-string-parameters', {
            //    templateUrl: 'partials/samples/add-query-string-parameters.html'
            //}).when('/samples/show-available-resources', {
            //    templateUrl: 'partials/samples/show-available-resources.html'
            //})
            .otherwise({
                redirectTo: '/'
            });
    }])
    .controller('NavigationController', function ($scope, $location) {

        $scope.navigateTo = function (path) {
            $location.path(path);
        };
    })
    .controller('addEmployeeAddressController', function ($scope, $http, SpringDataRestAdapter) {
        if (employee.address != null) {
            var httpPromise = $http.post(employee._links.address.href, employee.address, 'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                });
            SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
                alert("Address Added!");
            });
            $location.path('/employee');
        }
    })
    .controller('employeesController', function ($scope, $http, SpringDataRestAdapter, $location, $route) {
        $scope.isTimeSheetCollapsed = false;
        $scope.isTimeSheetEntryCollapsed = false;
        $scope.isAddressCollapsed = false;
        $scope.isAddAddressCollapsed = false;
        $scope.isPhonesCollapsed = false;
        $scope.isEmailsCollapsed = false;
        $scope.roles = ['administrator',
            'employee',
            'customer',
            'guest'];

        $scope.goToAddAddress = function (employee) {
            var href = employee._links.self.href;
            var employeeId = href.substr(href.lastIndexOf("/") + 1, href.length);
            $location.path('addEmployeeAddress/' + employeeId);
        };

        $scope.goToAddPhone = function (employee) {
            var href = employee._links.self.href;
            var employeeId = href.substr(href.lastIndexOf("/") + 1, href.length);
            $location.path('addEmployeePhone/' + employeeId);
        };

        var httpPromise = $http.get('http://localhost:8080/api/employees').success(function (response) {
            $scope.response = angular.toJson(response, true);
        });

        SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
            $scope.employees = processedResponse._embeddedItems;
            $scope.processedResponse = angular.toJson(processedResponse, true);
        });


        $scope.onClickNavigateToTimeSheets = function (url) {
            var httpPromise = $http.get(url).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
                $scope.timesheets = processedResponse._embeddedItems;
                $scope.processedResponse = angular.toJson(processedResponse, true);
            });
            $scope.isTimeSheetCollapsed = !$scope.isTimeSheetCollapsed;
        };

        $scope.onClickNavigateToTimeSheetEntries = function (url) {
            var httpPromise = $http.get(url).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
                $scope.timesheetEntries = processedResponse._embeddedItems;
                $scope.processedResponse = angular.toJson(processedResponse, true);
            });
            $scope.isTimeSheetEntryCollapsed = !$scope.isTimeSheetEntryCollapsed;
        };


        $scope.onClickNavigateToAddresses = function (url) {
            var httpPromise = $http.get(url).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
                $scope.addresses = processedResponse._embeddedItems;
                $scope.processedResponse = angular.toJson(processedResponse, true);
            });
            $scope.isAddressCollapsed = !$scope.isAddressCollapsed;
        };


        $scope.onClickNavigateToPhones = function (url) {
            var httpPromise = $http.get(url).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
                $scope.phones = processedResponse._embeddedItems;
                $scope.processedResponse = angular.toJson(processedResponse, true);
            });
            $scope.isPhonesCollapsed = !$scope.isPhonesCollapsed;
        };


        $scope.onClickNavigateToEmails = function (url) {
            var httpPromise = $http.get(url).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
                $scope.emails = processedResponse._embeddedItems;
                $scope.processedResponse = angular.toJson(processedResponse, true);
            });
            $scope.isEmailsCollapsed = !$scope.isEmailsCollapsed;
        };






        $scope.deleteEmployee = function (employee) {
            var httpPromiseTimesheet = $http.delete(employee._links.self.href, employee, 'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                });
            SpringDataRestAdapter.process(httpPromiseTimesheet).then(function (processedResponse) {
                console.log("Employee deleted.")
            });

            $route.reload();
        };

        $scope.deleteEmployeeAddress = function(employee,address) {
            var employeeHref = employee._links.self.href;
            var addressHref = address._links.self.href;
            var employeeId = employeeHref.substring(employeeHref.lastIndexOf('/')+1,employeeHref.length);
            var addressId = addressHref.substring(addressHref.lastIndexOf('/')+1,addressHref.length);

            var httpPromise = $http.delete('/hydrated/employees/' + employeeId + "/address/" + addressId).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            $route.reload();
        }

        $scope.deleteEmployeePhone = function(employee,phone) {
            var employeeHref = employee._links.self.href;
            var phoneHref = phone._links.self.href;
            var employeeId = employeeHref.substring(employeeHref.lastIndexOf('/')+1,employeeHref.length);
            var phoneId = phoneHref.substring(phoneHref.lastIndexOf('/')+1,phoneHref.length);

            var httpPromise = $http.delete('/hydrated/employees/' + employeeId + "/phones/" + phoneId).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            $route.reload();
        }

        $scope.deleteEmployeeEmail = function(employee,email) {
            var employeeHref = employee._links.self.href;
            var emailHref = email._links.self.href;
            var employeeId = employeeHref.substring(employeeHref.lastIndexOf('/')+1,employeeHref.length);
            var emailId = emailHref.substring(emailHref.lastIndexOf('/')+1,emailHref.length);

            var httpPromise = $http.delete('/hydrated/employees/' + employeeId + "/emails/" + emailId).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            $route.reload();
        }

    })
    .controller('EditEmployeeAddressController', function ($scope, $routeParams, $http, SpringDataRestAdapter) {
        var employeeId = $routeParams.employeeId;
        var httpPromise = $http.get('/api/employees/' + employeeId).success(function (response) {
            $scope.response = angular.toJson(response, true);
        });

        SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
            //$scope.employee = processedResponse._embeddedItems;
            $scope.processedResponse = angular.toJson(processedResponse, true);
            $scope.employee = processedResponse
        });

        $scope.addEmployeeAddress = function (employee) {
            var httpPromiseTimesheet = $http.post('/hydrated/employees/' + employeeId + '/address', employee.address,
                'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                });
            SpringDataRestAdapter.process(httpPromiseTimesheet).then(function (processedResponse) {
                $scope.processedResponse = angular.toJson(processedResponse, true);
                $scope.employee = processedResponse
                console.log("Address Added!");
            });

        };

    })
    .controller('EditEmployeePhoneController',function($scope, $http, SpringDataRestAdapter, $routeParams){
        var employeeId = $routeParams.employeeId;
        var httpPromise = $http.get('/api/employees/' + employeeId).success(function (response) {
            $scope.response = angular.toJson(response, true);
        });

        SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
            //$scope.employee = processedResponse._embeddedItems;
            $scope.processedResponse = angular.toJson(processedResponse, true);
            $scope.employee = processedResponse
        });

        $scope.addEmployeePhone = function (employee) {
            var httpPromise = $http.post('/hydrated/employees/' + employeeId + '/phones', employee.phone,
                'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                });
            SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
                $scope.processedResponse = angular.toJson(processedResponse, true);
                $scope.employee = processedResponse
                console.log("Phone Added!");
            });

        };
    });