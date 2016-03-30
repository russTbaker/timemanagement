app.controller('CustomersController', function ($scope, $http, SpringDataRestAdapter, $route, $location) {
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