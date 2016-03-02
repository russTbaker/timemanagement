'use strict';

var app = angular.module('timesheetApp', ['ui.bootstrap', 'ui.bootstrap.datetimepicker', 'ngResource', 'ngRoute', 'hljs', 'spring-data-rest'])
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
            .when('/contract', {
                templateUrl: 'views/contract/contract.html'
            })
            .when('/timesheet', {
                templateUrl: 'views/timesheet/timesheet.html'
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
    .controller('EmployeesController', function ($scope, $http, SpringDataRestAdapter, $location, $route) {
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

        var httpPromise = $http.get('/api/employees').success(function (response) {
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

        $scope.deleteEmployeeAddress = function (employee, address) {
            var employeeHref = employee._links.self.href;
            var addressHref = address._links.self.href;
            var employeeId = employeeHref.substring(employeeHref.lastIndexOf('/') + 1, employeeHref.length);
            var addressId = addressHref.substring(addressHref.lastIndexOf('/') + 1, addressHref.length);

            var httpPromise = $http.delete('/hydrated/employees/' + employeeId + "/address/" + addressId).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            $route.reload();
        }

        $scope.deleteEmployeePhone = function (employee, phone) {
            var employeeHref = employee._links.self.href;
            var phoneHref = phone._links.self.href;
            var employeeId = employeeHref.substring(employeeHref.lastIndexOf('/') + 1, employeeHref.length);
            var phoneId = phoneHref.substring(phoneHref.lastIndexOf('/') + 1, phoneHref.length);

            var httpPromise = $http.delete('/hydrated/employees/' + employeeId + "/phones/" + phoneId).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            $route.reload();
        }

        $scope.deleteEmployeeEmail = function (employee, email) {
            var employeeHref = employee._links.self.href;
            var emailHref = email._links.self.href;
            var employeeId = employeeHref.substring(employeeHref.lastIndexOf('/') + 1, employeeHref.length);
            var emailId = emailHref.substring(emailHref.lastIndexOf('/') + 1, emailHref.length);

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
    .controller('EditEmployeePhoneController', function ($scope, $http, SpringDataRestAdapter, $routeParams) {
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
    })
    .controller('ContractsController', function ($scope, $http, SpringDataRestAdapter, $location, $route) {


        init();

        //---------- Contracts

        $scope.addContract = function (contract) {
            SpringDataRestAdapter.process($http.post('/hydrated/contracts/', contract,
                'Content-Type:application/json+hal').success(
                function (response) {
                    console.log("Added contract!")
                }));
        };


        $scope.deleteContract = function (contract) {
            SpringDataRestAdapter.process($http.delete(contract._links.self.href, contract,
                'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                })).then(function (processedResponse) {
                console.log("Contract deleted.")
            });

            $route.reload();
        };


        $scope.editContract = function (contract) {
            SpringDataRestAdapter.process($http.put(contract._links.self.href, contract,
                'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                })).then(function (processedResponse) {
                console.log("Contract updated.")
            });

            $route.reload();
        };


        $scope.addEmployeeToContract = function (contract, user) {
            addContract(contract, user, "employees");
            $route.reload();

        };

        $scope.addCustomerToContract = function (contract, user) {
            addContract(contract, user, "customers");
            $route.reload();
        };

        //------------------------ Jobs

        $scope.addJob = function (job) {
            SpringDataRestAdapter.process($http.post('/api/jobs/', job,
                'Content-Type:application/json+hal').success(
                function (response) {
                    console.log("Job added!")
                }));
            loadJobs();
            $route.reload();
        };

        $scope.deleteJob = function (job) {
            if (job._links.contract.href != null) {
                SpringDataRestAdapter.process($http.get(job._links.contract.href)
                    .success(function (response) {
                    })).then(function (processedResponse) {
                    console.log("Got jobs!");
                    var links = processedResponse._links.contract.href;
                    SpringDataRestAdapter.process($http.get(links).success(function (response) {
                        }))
                        .then(findContractAndDeleteJob(processedResponse, job));
                });

                loadJobs();
                $route.reload();
            }
        };

        $scope.addJobToContract = function (contract, job) {
            var contractId = contract.substring(contract.lastIndexOf('/') + 1, contract.length);
            //var jobHref = job._links.self.href;
            var jobId = job.substring(job.lastIndexOf('/') + 1, job.length);
            SpringDataRestAdapter.process($http.post('/hydrated/contracts/' + contractId + '/jobs/' + jobId).success(
                function (response) {
                    console.log("Added job to contract!");
                }));
        };


        $scope.addJobToEmployee = function (user, job) {
            var jobId = job.substring(job.lastIndexOf('/') + 1, job.length);
            var userId = user.substring(user.lastIndexOf('/') + 1, user.length);
            SpringDataRestAdapter.process($http.post('/hydrated/employees/' + userId + '/jobs/' + jobId,
                'Content-Type:application/json+hal').success(
                function (response) {
                    console.log("Added job to contract!");
                }));
        };

        $scope.editJob = function () {

        };
        // ------------------------- Functions

        function init() {
            refreshPage();
            $scope.startDate = {
                opened: false
            };
            $scope.endDate = {
                opened: false
            };

            $scope.openStartDate = function () {
                $scope.startDate.opened = true;
            };
            $scope.openEndDate = function () {
                $scope.endDate.opened = true;
            };

            $scope.terms = ['net15',
                'net30',
                'net45'];
        }

        function refreshPage() {
            loadContracts();
            loadEmployees();
            loadCustomers();
            loadJobs();
        }

        // Preload contracts
        function loadContracts() {
            SpringDataRestAdapter.process($http.get('/api/contracts').success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                $scope.contracts = processedResponse._embeddedItems;
                $scope.processedResponse = angular.toJson(processedResponse, true);
            });
        }

        // Preload Employees
        function loadEmployees() {
            SpringDataRestAdapter.process($http.get('/api/employees').success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                $scope.employees = processedResponse._embeddedItems;
                $scope.processedResponse = angular.toJson(processedResponse, true);
            });
        }

        // Preload Customers
        function loadCustomers() {
            SpringDataRestAdapter.process($http.get('/api/customers').success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                $scope.customers = processedResponse._embeddedItems;
                $scope.processedResponse = angular.toJson(processedResponse, true);
            });
        }

        // Preload Jobs
        function loadJobs() {
            SpringDataRestAdapter.process($http.get('/api/jobs').success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                $scope.jobs = processedResponse._embeddedItems;
                $scope.processedResponse = angular.toJson(processedResponse, true);
            });
        }

        function addContract(contract, user, path) {
            var contractId = contract.substring(contract.lastIndexOf('/') + 1, contract.length);
            var userId = user.substring(user.lastIndexOf('/') + 1, user.length);
            SpringDataRestAdapter.process($http.put('/hydrated/' + path + '/' + userId + '/contracts/' + contractId,
                'Content-Type:application/json+hal').success(
                function (response) {
                    console.log("Added " + path + " to contract!");
                }));
        }

        function findContractAndDeleteJob(processedResponse, job) {
            var jobHref = job._links.self.href;
            var jobId = jobHref.substring(jobHref.lastIndexOf('/') + 1, jobHref.length);
            var contractHref = processedResponse._links.contract.href;
            var contractId = contractHref.substring(contractHref.lastIndexOf('/') + 1, contractHref.length);
            SpringDataRestAdapter.process($http.delete('/hydrated/contracts/' + contractId + '/jobs/' + jobId,
                'Content-Type:application/json+hal').success(
                function (response) {
                    killJob(job);
                    console.log("Job deleted!")
                }));
        }

        function killJob(job) {
            SpringDataRestAdapter.process($http.delete(job._links.job.href).success(function (response) {
                $scope.response = angular.toJson(response, true);
            }), true).then(function (processedResponse) {
                $scope.categories = processedResponse._embeddedItems;
                $scope.processedResponse = angular.toJson(processedResponse, true);
            });
        }

        function getContractFromJob(job) {
            SpringDataRestAdapter.process($http.get(job._links.contract.href).success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                console.log("Got jobs!");
                var links = processedResponse._links.contract.href;
                SpringDataRestAdapter.process($http.get(links).success(function (response) {
                    $scope.response = angular.toJson(response, true);
                })).then(function (processedResponse) {
                    console.log("Returning Contracts " + processedResponse._links);
                    return processedResponse._links;
                });
            });
        }

    })
    .controller('TimesheetController', function ($scope, $http, SpringDataRestAdapter, $location, $route) {
            loadTimesheets();
            loadTimesheetEntries();
            getJobsForEmployee();

$scope.changeJob = function (job) {
    var i=0
    for( i=0;i<$scope.timesheetEntries.length;i++){
        $scope.timesheetEntries[i].jobId = job;
    }
};
        $scope.updateTimesheet = function(timesheetEntries){
            console.log("Trying to update timesheet entries" + '/hydrated/employees/2/timesheets/' + timesheetEntries[0].timesheetId);
            SpringDataRestAdapter.process($http.put('/hydrated/employees/2/timesheets/' + timesheetEntries[0].timesheetId + "/timesheetentries", timesheetEntries,
                'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                })).then(function (processedResponse) {
                console.log("Timesheet updated.")
            });

            $route.reload();
        };

        // Functions
            // Preload timesheets
            function loadTimesheets() {
                // TODO: Hard coded for now, get this from the userPricipal
                SpringDataRestAdapter.process($http.get('/api/employees/2/timesheets').success(function (response) {
                    $scope.response = angular.toJson(response, true);
                })).then(function (processedResponse) {
                    $scope.timesheets = processedResponse._embeddedItems;
                    //$scope.processedResponse = angular.toJson(processedResponse, true);
                });
            }

            function loadTimesheetEntries() {
                // TODO: Hard coded for now, get this from the userPricipal
                SpringDataRestAdapter.process($http.get('/api/timesheets/1/timeSheetEntries').success(function (response) {
                    $scope.response = angular.toJson(response, true);
                })).then(function (processedResponse) {
                    $scope.timesheetEntries = processedResponse._embeddedItems;
                    //$scope.timesheetEntries = angular.toJson(processedResponse, true);
                });
            }

            function getJobsForEmployee() {
                SpringDataRestAdapter.process($http.get('/hydrated/employees/2/jobs').success(function (response) {
                    var response2 =
                    $scope.response = angular.toJson(response, true);
                })).then(function (processedResponse) {
                    $scope.jobs =  processedResponse._embeddedItems;
                    console.log("Got jobs ")
                });
            }
        }
    );