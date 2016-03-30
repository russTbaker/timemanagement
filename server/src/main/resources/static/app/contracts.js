app.controller('ContractsController', function ($scope, $http, SpringDataRestAdapter, $location, $route) {
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

});