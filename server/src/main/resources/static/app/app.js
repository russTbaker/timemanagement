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
                controller: 'NavigationController',
                controllerAs: 'controller'
            })
            .otherwise({
                redirectTo: '/'
            });
        $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    }]);


app.factory('UserService', function (SpringDataRestAdapter, $http) {
    var user;
    var response;
    var processedResponse;
    var error;
    return {

        addUser: function (user, $scope, form) {
            var httpPromise = $http.post('api/' + user.role +'s', user, 'Content-Type:application/json+hal').success(
                function (response) {
                    $scope.response = angular.toJson(response, true);
                })
                .error(function (processedResponse) {
                    $scope.error = processedResponse.messages;
                    _.each(processedResponse.messages, function (errors, key) {
                        form.$dirty = true;
                        form.$setValidity(errors, false);
                        form.$error.userExists = true;
                    });
                });
            SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
                $scope.user = processedResponse;
                var userHref = processedResponse._links.self.href;
                console.log(userHref);
                $scope.userId = userHref.substring(userHref.lastIndexOf('/') + 1, userHref.length);
                console.log("User Added ");
            });
        },

        addAddress: function (user, address) {
            this.addAssociation(address, 'addresses', user);
        },

        updateAddress: function (address) {
            $http.put(address._links.self.href, address)
                .success(function (response) {
                    console.log("Address updated!");
                });
        },

        addPhone: function (user, incomingPhone) {
            this.addAssociation(incomingPhone, 'phones', user);
        },

        updatePhone: function (incomingPhone) {
            this.updateEntity(incomingPhone, "Phone");
        },

        addEmail: function (user, incomingEmail) {
            this.addAssociation(incomingEmail, 'emails', user);
        },

        updateEmail: function (incomingEmail) {
            this.updateEntity(incomingEmail, "Email");
        },

        updateEntity: function (entity, type) {
            $http.put(entity._links.self.href, entity)
                .success(function (response) {
                    console.log(type + " updated!");
                });
        },

        addAssociation: function (association, type, user) {
            var userHref = user._links.self.href;
            var userId = userHref.substring(userHref.lastIndexOf('/') + 1, userHref.length);
            var httpPromise = $http.post('/api/' + type + '/', association).success(
                function (incomingResponse) {
                    var associationStringData = '';
                    var userAssociations = eval('user.' + type);
                    _.each(userAssociations, function (_association, key) {
                        if(associationStringData.indexOf(_association._links.self.href) == -1){
                            associationStringData += _association._links.self.href;
                        }
                        associationStringData += '\n';
                    });
                    associationStringData += incomingResponse._links.self.href;
                    $http({
                        method: 'PUT',
                        url: '/api/users/' + userId + "/" + type,
                        data: associationStringData,
                        headers: {
                            'Content-Type': 'text/uri-list'
                        }
                    }).success(function (addAssociationResponse) {
                    }).error(function (errorAddAddressToUserResponse) {
                        error = errorAddAddressToUserResponse.message;
                        console.log("An error has occurred." + error.message)
                    });
                }).error(function (response) {
                error = response.message;
                console.log("An error has occurred." + error.message);
            });

            SpringDataRestAdapter.process(httpPromise).then(function (processedResponse) {
                console.log(type + " Added!");
            });
        },

        updateAssociation: function (association, userId, type) {
            SpringDataRestAdapter.process($http.put('/api/' + type + '/', association)
                .success(
                    function (incomingResponse) {
                        angular.toJson(incomingResponse, true);
                    })
                .error(function (response) {
                    error = response.message;
                    console.log("An error has occurred." + error.message);
                }))
                .then(function (processedResponse) {
                    console.log(type + " updated!");
                });
        },

        removeAssociation: function (uriList, user, type, associationUrl) {
            var userHref = user._links.self.href;
            var userId = userHref.substring(userHref.lastIndexOf('/') + 1, userHref.length);
            $http({
                method: 'PUT',
                url: '/api/users/' + userId + "/" + type,
                data: uriList,
                headers: {
                    'Content-Type': 'text/uri-list'
                }
            }).success(function (addAssociationResponse) {
                $http.delete(associationUrl).success(function(response){
                    console.log("Deleted entity");
                }).error(function(response){
                    console.error("Could not delete entity");
                });
            }).error(function (errorAddAddressToUserResponse) {
                error = errorAddAddressToUserResponse.message;
                console.log("An error has occurred." + error.message)
            });
        },

        editUser: function (user) {
            var userHref = user._links.self.href;
            var userId = userHref.substring(userHref.lastIndexOf('/') + 1, userHref.length);

            var roles = user.roles;
            var index;
            for (index = 0; index < roles.length; index++) {
                SpringDataRestAdapter.process($http.put('/api/' + user.roles[0].role + 's/' + userId,
                    user, 'Content-Type:application/json+hal').success(function (response) {
                    angular.toJson(response, true);
                })).then(function (processedResponse) {
                    console.log("User updated.")
                });
            }
        },

        getUser: function () {
            return user;
        },

        getResponse: function () {
            return response;
        },

        getProcessedResponse: function () {
            return processedResponse;
        },

        getError: function () {
            return error;
        }
    };
});



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
    .controller('AddUserController', function ($scope, $http, SpringDataRestAdapter, UserService) {
        $scope.roles = [
            {name: 'administrator', value: 'administrator'},
            {name: 'employee', value: 'employee'},
            {name: 'customer', value: 'customer'},
            {name: 'guest', value: 'guest'}
        ];

        $scope.emailTypes = ['billing',
            'business',
            'both'];

        $scope.phoneTypes = ['fax',
            'mobile',
            'office'];

        $scope.isCustomer = true;


        $scope.enableFields = function (role) {
            $scope.isCustomer = role == 'customers';
        };

        $scope.addUser = function (user) {
            UserService.addUser(user, $scope, this.addUserForm);
            $scope.user = UserService.getUser();
            $scope.error = UserService.getError();
            console.log($scope.error);
        };

        $scope.addAddress = function (address) {
            UserService.addAddress($scope.user, address);
        };

        $scope.addEmail = function (email) {
            UserService.addEmail($scope.user, email)
        };

        $scope.addPhone = function (phone) {
            UserService.addPhone($scope.user, phone);
        };
    })
    .controller('EditUserController', function ($scope, $routeParams, $http, SpringDataRestAdapter, UserService, $uibModal, $log, $route) {
        // Constants/Variables
        var usersUri = '/api/users/' + $routeParams.userId;
        const delay = 100;



        // Local function definitions

        $scope.emailTypes = ['billing',
            'business',
            'both'];

        $scope.phoneTypes = ['fax',
            'mobile',
            'office'];

        $scope.roles = ['administrator'
            , 'employee'
            , 'customer'
            , 'guest'];


        init(SpringDataRestAdapter, $http, $scope, usersUri);


        $scope.showAddAddressForm = function () {
            $scope.message = "Show Form Button Clicked";
            console.log($scope.message);
            openModal($uibModal, AddAddressModalInstanceCtrl, $scope, $log, 'views/user/modal/addAddressModal.html');
        };

        var AddAddressModalInstanceCtrl = function ($scope, $uibModalInstance, UserService) {
            $scope.submitForm = function (address) {
                if ($scope.form.$valid) {
                    console.log('user form is in scope');
                    UserService.addAddress($scope.user, address);
                    $scope.response = UserService.getResponse();
                    $scope.processedResponse = UserService.getProcessedResponse();
                    $uibModalInstance.close('closed');
                } else {
                    console.log('userform is not in scope');
                }
            };

            $scope.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            };
        };


        $scope.showAddPhoneForm = function () {
            $scope.message = "Show Form Button Clicked";
            console.log($scope.message);
            openModal($uibModal, AddPhoneModalInstanceCtrl, $scope, $log, 'views/user/modal/addPhoneModal.html');
        };

        var AddPhoneModalInstanceCtrl = function ($scope, $uibModalInstance, UserService) {
            $scope.submitForm = function (phone) {
                if ($scope.form.$valid) {
                    console.log('user form is in scope');
                    UserService.addPhone($scope.user, phone);
                    $scope.response = UserService.getResponse();
                    $scope.processedResponse = UserService.getProcessedResponse();
                    $uibModalInstance.close('closed');
                } else {
                    console.log('userform is not in scope');
                }
            };

            $scope.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            };
        };

        $scope.showAddEmailForm = function () {
            $scope.message = "Show Form Button Clicked";
            console.log($scope.message);
            openModal($uibModal, AddEmailModalInstanceCtrl, $scope, $log, 'views/user/modal/addEmailModal.html');
        };

        var AddEmailModalInstanceCtrl = function ($scope, $uibModalInstance, UserService) {
            $scope.submitForm = function (email) {
                if ($scope.form.$valid) {
                    console.log('user form is in scope');
                    UserService.addEmail($scope.user, email);
                    $scope.response = UserService.getResponse();
                    $scope.processedResponse = UserService.getProcessedResponse();
                    $uibModalInstance.close('closed');
                } else {
                    console.log('userform is not in scope');
                }
            };

            $scope.cancel = function () {
                $uibModalInstance.dismiss('cancel');
            };
        };







        $scope.checkRole = function (role, user) {
            if (user != null) {
                var roles = user.role;
                return roles == role;
            }
        };

        $scope.editUser = function (user) {
            UserService.editUser(user);
            refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
        };


        $scope.addAddress = function(address){
            UserService.addAddress($scope.user, address);
            refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
        };

        $scope.updateAddress = function (address) {
            UserService.updateAddress(address);
            refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
        };



        $scope.deleteAddress = function (address) {
            // Take address URI and remove it from the list, then update the user with the new uri-list
            var remainingUris = '';
            _.each($scope.user.addresses, function (addr, key) {
                if (addr.street1 != address.street1) {
                    remainingUris += addr._links.self.href;
                    remainingUris += '\n';
                }
            });
            UserService.removeAssociation(remainingUris, $scope.user, 'addresses', address._links.self.href);
            init(SpringDataRestAdapter, $http, $scope, usersUri);
        };


        $scope.addPhone = function (phone) {
            UserService.addPhone($scope.user, phone);
            refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
        };

        $scope.updatePhone = function (phone) {
            UserService.updatePhone(phone);
            refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
        };

        $scope.deletePhone = function (phone) {
            // Take phone URI and remove it from the list, then update the user with the new uri-list
            var remainingUris = '';
            _.each($scope.user.phones, function (pho, key) {
                if (pho.phone != phone.phone) {
                    remainingUris += pho._links.self.href;
                    remainingUris += '\n';
                }
            });
            UserService.removeAssociation(remainingUris, $scope.user, 'phones', phone._links.self.href);
            refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
        };

        $scope.addEmail = function (email) {
            UserService.addEmail($scope.user, email);
            refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);

        };

        $scope.updateEmail = function (email) {
            UserService.updateEmail(email);
            refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
        };

        $scope.deleteEmail = function (email) {
            // Take emails URI and remove it from the list, then update the user with the new uri-list
            var remainingUris = '';
            _.each($scope.user.emails, function (eml, key) {
                if (eml.email != email.email) {
                    remainingUris += eml._links.self.href;
                    remainingUris += '\n';
                }
            });
            UserService.removeAssociation(remainingUris, $scope.user, 'emails', email._links.self.href);
            refreshUserProfilePage($route, delay, $http, $scope, SpringDataRestAdapter, usersUri);
        };



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

