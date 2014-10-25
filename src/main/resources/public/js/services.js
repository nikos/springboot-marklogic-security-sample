'use strict';

/* ~~ Services ~~ */

angular.module('MarkLogicSampleApp.services', [])

    /* ---------------------------------------------------------------------- */
    /* Provide access to REST API from Spring-Boot application                */
    /* ---------------------------------------------------------------------- */
    .factory('MarkLogicService', function ($resource) {
        var service = $resource('/products/:sku.json', {id: '@sku'}, {
            getProduct:    {method: 'GET'},
            /* Note: search results contain facets beside the products */
            getProducts:   {method: 'GET',  url: '/products.json', isArray: false},
            addProduct:    {method: 'POST', url: '/products' },
            removeProduct: {method: 'DELETE'}
        });
        service.prototype.isNew = function() {
            return (typeof(this.id) === 'undefined');
        };
        return service;
    })

    /* ---------------------------------------------------------------------- */
    /* Authenticate user on login                                             */
    /* ---------------------------------------------------------------------- */
    .factory("AuthenticationService", function($http, $sanitize, $log, FlashService, CSRF_TOKEN) {

        var sanitizeCredentials = function(credentials) {
            return {
                username: $sanitize(credentials.username),
                password: $sanitize(credentials.password),
                csrf_token: CSRF_TOKEN
            };
        };

        return {
            authenticate: function(credentials) {
                var auth = $http.post("/auth/authenticate", sanitizeCredentials(credentials));
                auth.success(function() {
                    $log.info("Successfully authenticated user: " + credentials.username);
                    FlashService.clear();
                });
                auth.error(function(response) {
                    $log.info("Unable to authenticate user: " + credentials.username, response);
                    FlashService.show("Wrong username or password provided");
                });
                return auth;
            }
        };
    })

    /* ---------------------------------------------------------------------- */
    /* Hold message string to easily communicate highlight to the user        */
    /* ---------------------------------------------------------------------- */
    .factory("FlashService", function($rootScope, $log) {
        return {
            show: function(message) {
                $rootScope.flash = message;
            },
            clear: function() {
                $rootScope.flash = "";
            }
        }
    })

;
