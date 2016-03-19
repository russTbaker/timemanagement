'use strict';

var app = angular.module('timesheetApp', ['ui.bootstrap', 'ui.bootstrap.datetimepicker', 'ngResource', 'ngRoute', 'hljs', 'spring-data-rest'])
    .config(['$routeProvider', '$httpProvider',function ($routeProvider,$httpProvider) {
        $routeProvider.when('/', {
                templateUrl: 'main.html'
            })
            .when('/employee', {
                templateUrl: 'views/employee/employee.html'
            })
            .when('/customer', {
                templateUrl: 'views/customer/customer.html'
            })
            .when('/addCustomer', {
                templateUrl: 'views/user/addUserProfile.html'
            })
            .when('/addUser', {
                templateUrl: 'views/user/addUserProfile.html'
            })
            .when('/editUser/:userId', {
                templateUrl: 'views/user/editUserProfile.html'
            })
            .when('/editEmail/:userId', {
                templateUrl: 'views/user/email.html'
            })
            .when('/addPhone/:userId', {
                templateUrl: 'views/user/addPhone.html'
            })
            .when('/addAddress/:userId', {
                templateUrl: 'views/user/addAddress.html'
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
            .when('/login', {
                templateUrl: 'views/login.html',
                controller : 'NavigationController',
                controllerAs: 'controller'
            })
            .otherwise({
                redirectTo: '/'
            });
        $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    }]);

    angular.module('timesheetApp').factory('UserService',function(SpringDataRestAdapter,$http){
        var user;
        var response;
        var processedResponse;
        var error;
        return {
            addAddress:function (incomingAddress, userId) {
                this.addAssociation(incomingAddress,userId,'address','addresses');
            },

            addPhone:function (incomingPhone, userId) {
                this.addAssociation(incomingPhone,userId,'phone','phones');
            },

            addEmail:function (incomingEmail, userId) {
                this.addAssociation(incomingEmail,userId,'email','emails');
            },

            addAssociation:function (association, userId,type,pluralType) {
                var httpPromise = $http.post('/api/'+pluralType+'/', association).success(
                    function (incomingResponse) {
                        $http({method: 'PUT',
                            url: '/api/users/' + userId + "/" + type,
                            data: incomingResponse._links.self.href,
                            headers: {
                                'Content-Type':'text/uri-list'
                            }
                        }).success(function(addAddressToUserResponse){
                        }).error(function(errorAddAddressToUserResponse){
                            error = errorAddAddressToUserResponse.message;
                            console.log("An error has occurred." + error.message)
                        });
                    }).error(function(response){
                    error = response.message;
                    console.log("An error has occurred." + error.message);
                });

                SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
                    console.log(type + " Added!");
                });
            },

            getUser: function(){
                return user;
            },

            getResponse: function(){
                return response;
            },

            getProcessedResponse: function(){
                return processedResponse;
            }
        };
    });

    app.controller('NavigationController', function ($rootScope, $scope, $http, $location) {

        var authenticate = function(credentials, callback) {

            var headers = credentials ? {authorization : "Basic "
            + btoa(credentials.username + ":" + credentials.password)
            } : {};

            $http.get('user', {headers : headers}).success(function(data) {
                if (data.name) {
                    $rootScope.authenticated = true;
                    $scope.user = data.name;
                    $scope.admin = data && data.roles && data.roles.indexOf("ROLE_ADMINISTRATOR")!=-1;
                } else {
                    $rootScope.authenticated = false;
                    $scope.admin = false;
                }
                callback && callback();
            }).error(function() {
                $rootScope.authenticated = false;
                callback && callback();
            });

        };



        $scope.logout = function() {
            $http.post('logout', {}).success(function() {
                $rootScope.authenticated = false;
                $scope.admin = false;
                $location.path("/");
            }).error(function(data) {
                $rootScope.authenticated = false;
                $scope.admin = false;
            });
        };

        authenticate();
        $scope.credentials = {};
        $scope.login = function() {
            authenticate($scope.credentials, function() {
                if ($rootScope.authenticated) {
                    $location.path("/");
                    $scope.error = false;
                } else {
                    $location.path("/login");
                    $scope.error = true;
                }
            });
        };

        $scope.logout = function() {
            $http.post('logout', {}).finally(function() {
                $rootScope.authenticated = false;
                $location.path("/");
            });
        }
        $scope.navigateTo = function (path) {
            $location.path(path);
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


        $scope.addUserToContract = function (contract, user) {
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
            loadContracts();
            $route.reload();
        };


        $scope.addJobToEmployee = function (user, job) {
            var jobId = job.substring(job.lastIndexOf('/') + 1, job.length);
            var userId = user.substring(user.lastIndexOf('/') + 1, user.length);
            SpringDataRestAdapter.process($http.post('/hydrated/employees/' + userId + '/jobs/' + jobId,
                'Content-Type:application/json+hal').success(
                function (response) {
                    console.log("Added job to contract!");
                }));
            loadEmployees();
            loadJobs();
            $route.reload();
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

            $scope.terms = [{name: 'Net 15', value: 'net15'},
                {name: 'Net 30', value: 'net30'},
                {name: 'Net 45', value: 'net45'}];
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
                $scope.users = processedResponse._embeddedItems;
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
        console.log("");
        angular.element(document).ready(function () {
            getEmployeeId();
            console.log('page loading completed');
        });


        getJobsForEmployee();

        $scope.changeJob = function (job) {
            var i = 0
            for (i = 0; i < $scope.timesheetEntries.length; i++) {
                $scope.timesheetEntries[i].jobId = job;
            }
        };

        // TODO: This is hardcoded
        $scope.onClickCreateTimesheet = function (jobId) {
            SpringDataRestAdapter.process($http.put('/hydrated/employees/2/timesheets/' + jobId).success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                })).then(function (processedResponse) {
                console.log("Timesheet created.")
            });
        }

        // TODO: This is hardcoded
        $scope.updateTimesheet = function (jobs) {
            var i;
            for (i = 0; i < jobs.length; i++) {
                var url = '/hydrated/employees/timesheets/' + jobs[i].jobId + '/timesheetentries';
                console.log("Trying to update timesheet entries" + url);
                SpringDataRestAdapter.process($http.put(url, jobs[i].timeEntries,
                    'Content-Type:application/json+hal').success(
                    function (response) {
                        $scope.response = angular.toJson(response, true);
                    })).then(function (processedResponse) {
                    console.log("Time Entry updated.")
                });
            }
            //loadTimesheets(map);
            //loadTimesheetEntries();
            $route.reload();
        };

        // Functions

        function loadTimesheetEntries() {
            SpringDataRestAdapter.process($http.get('/api/jobs/1/timeEntries').success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                $scope.timesheetEntries = processedResponse._embeddedItems;
                //$scope.timesheetEntries = angular.toJson(processedResponse, true);
            });
        }

        function getJobsForEmployee() {
            SpringDataRestAdapter.process($http.get('/hydrated/employees/jobs').success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                $scope.jobs = processedResponse._embeddedItems;

                //var index;
                //for(index =0;index<processedResponse._embeddedItems.length;index++){
                //    console.log(processedResponse._embeddedItems[index].job.name);
                //}
                console.log("Got jobs ")
            });
        }

        function getEmployeeId() {
            var employeeId;
            SpringDataRestAdapter.process($http.get('/employee').success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                console.log("Processed response: " + processedResponse.dba);
                //employeeId = processedResponse.id;
                $scope.employeeId = processedResponse.id;
                //console.log("Got jobs ")
            });
            return employeeId;
        }

        function getEmployeeIdCallback(employeeId) {

        }
    })
    .controller('AddUserController', function ($scope, $http, SpringDataRestAdapter,UserService) {
        $scope.roles = [
            {name: 'administrator', value: 'administrators'},
            {name: 'employee', value: 'employees'},
            {name: 'customer', value: 'customers'},
            {name: 'guest', value: 'guests'}
        ];

        $scope.emailTypes = ['billing',
            'business',
            'both'];

        $scope.addUser = function (user) {
            var httpPromise = $http.post('api/' + user.role, user, 'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                });
            SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
                var userHref = processedResponse._links.self.href;
                console.log(userHref);
                $scope.userId = userHref.substring(userHref.lastIndexOf('/') + 1, userHref.length);
                console.log("User Added ");
            });
        };
        //$scope.addAddress = function (address, userId) {
        //    var httpPromiseTimesheet = $http.post('/hydrated/employees/' + userId + '/address', address,
        //        'Content-Type:application/json+hal').success(
        //        function (response) {
        //            $scope.response = angular.toJson(response, true);
        //        });
        //    SpringDataRestAdapter.process(httpPromiseTimesheet).then(function (processedResponse) {
        //        $scope.processedResponse = angular.toJson(processedResponse, true);
        //        $scope.employee = processedResponse
        //        console.log("Address Added!");
        //    });
        //};

        $scope.addAddress = function(address,userId){
            UserService.addAddress(address,userId);
        };

        $scope.addEmail = function (email, userId) {
            var httpPromiseTimesheet = $http.post('/hydrated/employees/' + userId + '/emails', email,
                'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                });
            SpringDataRestAdapter.process(httpPromiseTimesheet).then(function (processedResponse) {
                $scope.processedResponse = angular.toJson(processedResponse, true);
                $scope.employee = processedResponse
                console.log("Email Added!");
            });
        };


        $scope.addPhone = function (phone, userId) {
            var httpPromise = $http.post('/hydrated/employees/' + userId + '/phones', phone,
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
    .controller('EditUserController', function ($scope, $routeParams, $http, SpringDataRestAdapter,UserService) {
        $scope.emailTypes = ['billing',
            'business',
            'both'];

        $scope.roles = ['administrator'
            ,'employee'
            ,'customer'
            ,'guest']

        var userId = $routeParams.userId;
        init();

        $scope.checkRole = function (role, user) {
            if (user != null) {
                var roles = user.roles;
                var index;
                for (index = 0; index < roles.length; index++) {
                    if (roles[index].role == role) {
                        return true;
                    }
                }
            }
        };

        $scope.editUser = function (user) {
            var userHref = user._links.self.href;
            var userId = userHref.substring(userHref.lastIndexOf('/') + 1, userHref.length);

            var roles = user.roles;
            var index;
            for (index = 0; index < roles.length; index++) {
                SpringDataRestAdapter.process($http.put('/api/'+ user.roles[0].role + 's/' + userId ,
                    user, 'Content-Type:application/json+hal').success(function (response) {
                    $scope.response = angular.toJson(response, true);
                })).then(function (processedResponse) {
                    console.log("User updated.")
                });
            }



        };
        
        $scope.addAddress = function(address){
            UserService.addAddress(address,userId);
            $scope.response = UserService.getResponse();
            $scope.processedResponse = UserService.getProcessedResponse();
        };


        $scope.updateAddress = function (employee, address) {
            var employeeHref = employee._links.self.href;
            var addressHref = address._links.self.href;
            var userId = employeeHref.substring(employeeHref.lastIndexOf('/') + 1, employeeHref.length);
            var addressId = addressHref.substring(addressHref.lastIndexOf('/') + 1, addressHref.length);

            SpringDataRestAdapter.process($http.put('/hydrated/employees/' + userId + '/address/' + addressId,
                address, 'Content-Type:application/json+hal').success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                console.log("Address updated for user.")
            });
        };

        $scope.addEmail = function (employee) {
            var httpPromiseTimesheet = $http.post('/hydrated/employees/' + userId + '/emails', employee.email,
                'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                });
            SpringDataRestAdapter.process(httpPromiseTimesheet).then(function (processedResponse) {
                $scope.processedResponse = angular.toJson(processedResponse, true);
                $scope.employee = processedResponse
                console.log("Email Added!");
            });
        };

        $scope.updateEmail = function (employee, email) {
            var employeeHref = employee._links.self.href;
            var emailHref = email._links.self.href;
            var userId = employeeHref.substring(employeeHref.lastIndexOf('/') + 1, employeeHref.length);
            var emailId = emailHref.substring(emailHref.lastIndexOf('/') + 1, emailHref.length);

            SpringDataRestAdapter.process($http.put('/hydrated/employees/' + userId + '/emails/' + emailId,
                email, 'Content-Type:application/json+hal').success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                console.log("Email updated for user.")
            });
        };

        $scope.addPhone = function (employee) {
            var httpPromise = $http.post('/hydrated/employees/' + userId + '/phones', employee.phone,
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

        $scope.updatePhone = function (employee, phone) {
            var employeeHref = employee._links.self.href;
            var phoneHref = phone._links.self.href;
            var userId = employeeHref.substring(employeeHref.lastIndexOf('/') + 1, employeeHref.length);
            var phoneId = phoneHref.substring(phoneHref.lastIndexOf('/') + 1, phoneHref.length);

            SpringDataRestAdapter.process($http.put('/hydrated/employees/' + userId + '/phones/' + phoneId,
                phone, 'Content-Type:application/json+hal').success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                console.log("Phone updated for user.")
            });
        };

        function init() {
            const usersUri = '/api/users/' + userId;
            SpringDataRestAdapter.process($http.get(usersUri).success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                $scope.processedResponse = angular.toJson(processedResponse, true);
                $scope.user = processedResponse;

            });


            SpringDataRestAdapter.process($http.get(usersUri + '/address').success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                $scope.address = processedResponse._embeddedItems;
            });


            SpringDataRestAdapter.process($http.get(usersUri + '/phones').success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                $scope.phones = processedResponse._embeddedItems;

            });

            SpringDataRestAdapter.process($http.get(usersUri + '/emails').success(function (response) {
                $scope.response = angular.toJson(response, true);
            })).then(function (processedResponse) {
                $scope.emails = processedResponse._embeddedItems;
            });
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

        $scope.goToEditUser = function (user) {
            var href = user._links.self.href;
            var userId = href.substr(href.lastIndexOf("/") + 1, href.length);
            $location.path('editUser/' + userId);
        };

        $scope.goToAddAddress = function (user) {
            var href = user._links.self.href;
            var employeeId = href.substr(href.lastIndexOf("/") + 1, href.length);
            $location.path('addAddress/' + employeeId);
        };

        $scope.goToAddPhone = function (user) {
            var href = user._links.self.href;
            var userId = href.substr(href.lastIndexOf("/") + 1, href.length);
            $location.path('addPhone/' + userId);
        };

        $scope.goToAddEmail = function (user) {
            var href = user._links.self.href;
            var userId = href.substr(href.lastIndexOf("/") + 1, href.length);
            $location.path('addEmail/' + userId);
        };


        var httpPromise = $http.get('/api/employees').success(function (response) {
            $scope.response = angular.toJson(response, true);
        });

        SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
            $scope.users = processedResponse._embeddedItems;
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
            console.log("Requesting :" + url);
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

        $scope.deleteUser = function (employee) {
            var httpPromiseTimesheet = $http.delete(employee._links.self.href, employee, 'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                });
            SpringDataRestAdapter.process(httpPromiseTimesheet).then(function (processedResponse) {
                console.log("Employee deleted.")
            });

            $route.reload();
        };

        $scope.deleteUserAddress = function (employee, address) {
            var employeeHref = employee._links.self.href;
            var addressHref = address._links.self.href;
            var employeeId = employeeHref.substring(employeeHref.lastIndexOf('/') + 1, employeeHref.length);
            var addressId = addressHref.substring(addressHref.lastIndexOf('/') + 1, addressHref.length);

            var httpPromise = $http.delete('/hydrated/employees/' + employeeId + "/address/" + addressId).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            $route.reload();
        };

        $scope.deleteUserPhone = function (employee, phone) {
            var employeeHref = employee._links.self.href;
            var phoneHref = phone._links.self.href;
            var employeeId = employeeHref.substring(employeeHref.lastIndexOf('/') + 1, employeeHref.length);
            var phoneId = phoneHref.substring(phoneHref.lastIndexOf('/') + 1, phoneHref.length);

            var httpPromise = $http.delete('/hydrated/employees/' + employeeId + "/phones/" + phoneId).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            $route.reload();
        };

        $scope.deleteUserEmail = function (employee, email) {
            var employeeHref = employee._links.self.href;
            var emailHref = email._links.self.href;
            var employeeId = employeeHref.substring(employeeHref.lastIndexOf('/') + 1, employeeHref.length);
            var emailId = emailHref.substring(emailHref.lastIndexOf('/') + 1, emailHref.length);

            var httpPromise = $http.delete('/hydrated/employees/' + employeeId + "/emails/" + emailId).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            $route.reload();
        };

        $scope.onClickNavigateToEdit = function (employee) {
            var employeeHref = employee._links.self.href;
            var employeeId = employeeHref.substring(employeeHref.lastIndexOf('/') + 1, employeeHref.length);
            console.log("Navigating to :" + "/editUser/" + employeeId);
            $location.path("/editUser/" + employeeId);
        }

    })
    .controller('CustomersController', function ($scope, $http, SpringDataRestAdapter, $route, $location) {
        $scope.isAddressCollapsed = false;
        $scope.isAddAddressCollapsed = false;
        $scope.isPhonesCollapsed = false;
        $scope.isEmailsCollapsed = false;
        $scope.roles = ['administrator',
            'employee',
            'customer',
            'guest'];
        init();
        function init() {

        var httpPromise = $http.get('/api/customers').success(function (response) {
            $scope.response = angular.toJson(response, true);
        });

        SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
            $scope.users = processedResponse._embeddedItems;
            $scope.processedResponse = angular.toJson(processedResponse, true);
        });
    }


        $scope.goToEditUser = function (user) {
            var href = user._links.self.href;
            var userId = href.substr(href.lastIndexOf("/") + 1, href.length);
            $location.path('editUser/' + userId);
        };


        var httpPromise = $http.get('/api/customers').success(function (response) {
            $scope.response = angular.toJson(response, true);
        });

        SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
            $scope.customers = processedResponse._embeddedItems;
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
            console.log("Requesting :" + url);
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

        $scope.deleteUser = function (customer) {
            var httpPromiseTimesheet = $http.delete(customer._links.self.href, customer, 'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                });
            SpringDataRestAdapter.process(httpPromiseTimesheet).then(function (processedResponse) {
                console.log("Customer deleted.")
            });

            init();
        };

        $scope.deleteUserAddress = function (customer, address) {
            var customerHref = customer._links.self.href;
            var addressHref = address._links.self.href;
            var customerId = customerHref.substring(customerHref.lastIndexOf('/') + 1, customerHref.length);
            var addressId = addressHref.substring(addressHref.lastIndexOf('/') + 1, addressHref.length);

            var httpPromise = $http.delete('/hydrated/customers/' + customerId + "/address/" + addressId).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            $route.reload();
        };

        $scope.deleteUserPhone = function (customer, phone) {
            var customerHref = customer._links.self.href;
            var phoneHref = phone._links.self.href;
            var customerId = customerHref.substring(customerHref.lastIndexOf('/') + 1, customerHref.length);
            var phoneId = phoneHref.substring(phoneHref.lastIndexOf('/') + 1, phoneHref.length);

            var httpPromise = $http.delete('/hydrated/customers/' + customerId + "/phones/" + phoneId).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            $route.reload();
        };

        $scope.deleteUserEmail = function (customer, email) {
            var customerHref = customer._links.self.href;
            var emailHref = email._links.self.href;
            var customerId = customerHref.substring(customerHref.lastIndexOf('/') + 1, customerHref.length);
            var emailId = emailHref.substring(emailHref.lastIndexOf('/') + 1, emailHref.length);

            var httpPromise = $http.delete('/hydrated/customers/' + customerId + "/emails/" + emailId).success(function (response) {
                $scope.response = angular.toJson(response, true);
            });

            $route.reload();
        };

        $scope.onClickNavigateToEdit = function (customer) {
            var customerHref = customer._links.self.href;
            var customerId = customerHref.substring(customerHref.lastIndexOf('/') + 1, customerHref.length);
            console.log("Navigating to :" + "/editUser/" + customerId);
            $location.path("/editUser/" + customerId);
        }

    });

