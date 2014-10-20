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
    .factory("AuthenticationService", function($http, $sanitize, $log, SessionService, FlashService, CSRF_TOKEN) {

        var sanitizeCredentials = function(credentials) {
            return {
                username: $sanitize(credentials.username),
                password: $sanitize(credentials.password),
                csrf_token: CSRF_TOKEN
            };
        };

        return {
            login: function(credentials) {
                var login = $http.post("/auth/login", sanitizeCredentials(credentials));
                login.success(function() {
                    SessionService.set('authenticated', true);
                    FlashService.clear();
                });
                login.error(function(response) {
                    $log.info("Unable to login user", response);
                    FlashService.show(response.flash);
                });
                return login;
            },
            logout: function() {
                var logout = $http.get("/auth/logout");
                logout.success(function() {
                    SessionService.set('authenticated', true);
                });
                return logout;
            },
            isLoggedIn: function() {
                return SessionService.get('authenticated');
            }
        };
    })

    /* ---------------------------------------------------------------------- */
    /* Hold message string to easily communicate highlight to the user        */
    /* ---------------------------------------------------------------------- */
    .factory("FlashService", function($rootScope) {
        return {
            show: function(message) {
                $rootScope.flash = message;
            },
            clear: function() {
                $rootScope.flash = "";
            }
        }
    })

    /* ---------------------------------------------------------------------- */
    /* Store key-value pair in the Browsers session store (per tab)           */
    /* ---------------------------------------------------------------------- */
    .factory("SessionService", function() {
        return {
            get: function(key) {
                return sessionStorage.getItem(key);
            },
            set: function(key, val) {
                return sessionStorage.setItem(key, val);
            },
            unset: function(key) {
                return sessionStorage.removeItem(key);
            }
        }
    })

;
