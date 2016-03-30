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