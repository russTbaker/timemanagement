app.controller('EmployeesController', function ($scope, $http, SpringDataRestAdapter, $location, $route) {
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

});