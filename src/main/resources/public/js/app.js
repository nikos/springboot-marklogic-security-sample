'use strict';

var XAUTH_TOKEN_HEADER = 'x-auth-token';

angular.module('MarkLogicSampleApp',
    // Declare app level module dependencies
    [   'ngResource',
        'ngRoute',
        'ngSanitize',
        'toastr',
        'ui.bootstrap',
        'ngTagsInput',
        'MarkLogicSampleApp.services',
        'MarkLogicSampleApp.controllers'
    ])

    /* ---------------------------------------------------------------------- */
    /* Application Routes                                                     */
    /* ---------------------------------------------------------------------- */
    .config(function($routeProvider) {
        $routeProvider
            .when('/login', {
                templateUrl: '/public/views/login.html',
                controller: 'UserController'
            })
            .when('/products', {
                templateUrl: '/public/views/list.html',
                controller: 'ProductListController'
            })
            .when('/search', {
                templateUrl: '/public/views/search.html',
                controller: 'ProductSearchController'
            })
            .when('/products/new', {
                templateUrl: '/public/views/create.html',
                controller: 'ProductCreateController'
            })
            .when('/products/:sku', {
                templateUrl: '/public/views/detail.html',
                controller: 'ProductDetailController'
            })
            .otherwise({
                redirectTo: '/login'
            });
    })

    /* ---------------------------------------------------------------------- */
    /* Intercept HTTP errors on response                                      */
    /* ---------------------------------------------------------------------- */
    .config(function($httpProvider) {
        var interceptor = function ($rootScope, $q, $location) {

            function success(response) {
                return response;
            }

            function error(response) {

                var status = response.status;
                var config = response.config;
                var method = config.method;
                var url = config.url;

                if (status == 401) {
                    $location.path("/login");
                } else {
                    $rootScope.error = method + " on " + url + " failed with status " + status;
                }

                return $q.reject(response);
            }

            return function (promise) {
                return promise.then(success, error);
            };
        };
        $httpProvider.responseInterceptors.push(interceptor);
    })

    /* ---------------------------------------------------------------------- */
    /* Intercept unauthenticated routes on request time                       */
    /* ---------------------------------------------------------------------- */
    .run(function($rootScope, $location, $http, $window, $log) {

        /* Reset error when a new view is loaded */
        $rootScope.$on('$viewContentLoaded', function() {
            delete $rootScope.error;
        });

        $rootScope.hasRole = function(role) {

            if ($rootScope.user === undefined) {
                return false;
            }

            if ($rootScope.user.roles[role] === undefined) {
                return false;
            }

            return $rootScope.user.roles[role];
        };

        $rootScope.logout = function() {
            $log.info("Logging out user " + $rootScope.username);
            delete $rootScope.username;
            delete $http.defaults.headers.common[XAUTH_TOKEN_HEADER];
            delete $window.sessionStorage.usertoken;
            $log.info("Forward to login...");
            $location.path("/login");
        };

        /* Try getting valid user from cookie or go to login page */
        var originalPath = $location.path();
        $location.path("/login");
        var usertoken = $window.sessionStorage.usertoken;
        if (usertoken !== undefined) {
            var username = usertoken.substring(0, usertoken.indexOf(':'));
            $rootScope.username = username;
            $rootScope.usertoken = usertoken;
            $log.info("User " + username + " found in session " + originalPath);
            // For all out-going HTTP calls add the auth token to the header
            $http.defaults.headers.common[XAUTH_TOKEN_HEADER] = usertoken;

            $location.path(originalPath);
        } else {
            $log.info("No user found in session for " + originalPath + " redirecting to login");
        }

        /*
        var routesThatRequireAuth = ['/products'];

        $rootScope.$on('$routeChangeStart', function(event, next, current) {
            if (routesThatRequireAuth.indexOf($location.path())>=0 && !AuthenticationService.isLoggedIn()) {
                $location.path('/login');
                FlashService.show("Please log in to continue.");
            }
            if ($location.path().indexOf("/login") >= 0 && AuthenticationService.isLoggedIn()) {
                $location.path('/products');
            }
        });
        */
    })

;