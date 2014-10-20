'use strict';

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
    /* Intercept unauthenticated routes                                       */
    /* ---------------------------------------------------------------------- */
    .run(function($rootScope, $location, AuthenticationService, FlashService) {
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
    })

;