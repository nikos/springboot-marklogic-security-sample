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
                controller: 'AppController'
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
                redirectTo: '/products'
            });
    })

    /* ---------------------------------------------------------------------- */
    /* Intercept HTTP errors on response                                      */
    /* ---------------------------------------------------------------------- */
    .config(function($httpProvider) {
        var interceptor = function($rootScope, $q, $location) {

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

            return function(promise) {
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
            if ($rootScope.user === undefined || $rootScope.user.roles[role] === undefined) {
                return false;
            }
            return $rootScope.user.roles[role];
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
            $log.info("No user found in session, URI: '" + originalPath + "' -> redirecting to login");
        }
    })

;