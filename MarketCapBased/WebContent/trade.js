(function() {
	var app = angular.module('myApp', [ 'ui.router' ]);

	app.run(function($rootScope, $location, $state, LoginService) {

	});

	app.config([ '$stateProvider','$urlRouterProvider',
			function($stateProvider, $urlRouterProvider) {
				$urlRouterProvider.otherwise('/login');

				$stateProvider.state('login', {
					url : '/login',
					templateUrl : 'login.html',
					controller : 'LoginController'
				}).state('stock', {
					url : '/stock',
					templateUrl : 'stock.html',
					controller : 'StockController',
					params : {userDetails : ''}
				});
			} ]);

	app.controller('LoginController', function($scope, $rootScope,
			$stateParams, $state, LoginService) {
		$rootScope.title = "Login";

		$scope.formSubmit = function() {
		
		LoginService.login($scope.username, $scope.password).then(function(data){	
			console.log("login function response data is:" + data);
		    if (data) {
				$scope.error = '';
				$scope.username = '';
				$scope.password = '';
				//alert(data);
				$state.go('stock', {userDetails: data});
			} else {
				$scope.error = "Incorrect username/password !";
			}
		})		
		};

	});

	app.controller('StockController', function($scope, $rootScope,
			$stateParams, $state, LoginService) {
		$rootScope.title = " Stock Picker ";
		$rootScope.caps = [ "SmallCap", "MiddleCap", "LargeCap" ];
		
		$scope.showStockTable = false;
		$scope.stockList;
		$scope.savedStockList;
		$scope.userId = $stateParams.userDetails.userId;
		
		$scope.showStockAsPerCap = function() {
			console.log('market cap submit button clicked');
			var requestedCap = $scope.selectedCap;
			console.log('requestedCap:'+requestedCap);
			if(undefined != requestedCap)
			var stockDetails = LoginService.getStockDetails(requestedCap);

			setTimeout(function() {
				  //your code to be executed after 1 second
		   console.log('After timeout1 : '+stockDetails)	
		   console.log('After timeout2 : '+LoginService.getStockLists());
		   stockDetails = LoginService.getStockLists();
			if (undefined != stockDetails) {
				//$scope.stockList = stockDetails.query.results.quote;
				$scope.stockList = stockDetails.stocks;
				console.log('recommended stockList from server : '+$scope.stockList)
				$scope.showStockTable = true;
			}
			}, 5000);

		};
		$scope.saveStocks = function() {
			console.log('save stock button clicked : '+$scope.userId);
			var isSuccess = LoginService.saveStockData($scope.stockList, $scope.userId);

		};
		
		$scope.getSavedStocks = function() {
			console.log('fetch saved stock button clicked:'+$stateParams.userDetails.userId);
			var savedStocks = LoginService.fetchSavedStocks($scope.userId);
			if (undefined != savedStocks.stocks) {
				$scope.savedStockList = savedStocks.stocks;
			}

		};
		
	});

	app.factory('LoginService', function($http, $state, $q) {
		var isAuthenticated = false;
		var stockDetailsAsPerCap = '';
		var myStocks = [];
		var savedStockList = '';
		
		return {
			login : function(username, password) {
				var deferred = $q.defer();
				isAuthenticated = false;
				$http({
					method : 'POST',
					url : 'AuthenticationServlet',
					data : {
						user : username,
						pass : password
					}
				}).success(function(data, status, headers, config) {
					console.log("Login Success :"+data);
					isAuthenticated = true;
					$state.go('stock', {userDetails: data});
					//return isAuthenticated;
				}).error(function(data, status, headers, config) {
					console.log("Login failure :"+data);
					isAuthenticated = false;
					//return isAuthenticated;
				});
				deferred.resolve(isAuthenticated);
				return deferred.promise;
			},
			Authenticated : function() {
				console.log('Authenticated flag value:' + isAuthenticated);
				return isAuthenticated;
			},
			
			getStockLists : function() {
				console.log('stockDetailsAsPerCap in getStockLists:' + stockDetailsAsPerCap);
				return stockDetailsAsPerCap;
			},
			
			getStockDetails : function(requestedCap) {
				$http({
					method : 'GET',
					url : 'RecommendedStockServlet?selectedCap='+requestedCap
				}).success(function(data, status, headers, config) {
					console.log("success at stock details:" + data);
					stockDetailsAsPerCap = data;
				}).error(function(data, status, headers, config) {
					console.log("failure at stock details:" + data);
					stockDetailsAsPerCap = data;
				});
				return stockDetailsAsPerCap;
			},
			saveStockData : function(totalStocks, userId) {
				console.log('saveStockData service call is being made here');
				
				var selectedData = "";
				var selectedDataArr = [];
				for (i=0; i<totalStocks.length; i++) {
					if (totalStocks[i].selected == true) {
						quantity = document.getElementById("quantityField" + i).value;
						selectedData = selectedData + {stockName:totalStocks[i].stocksymbol, stockSymbol:totalStocks[i].stocksymbol, stockPrice:totalStocks[i].stockprice, stockQuantity:quantity} + ",";
						selectedDataArr.push({stockName:totalStocks[i].stocksymbol, stockSymbol:totalStocks[i].stocksymbol, stockPrice:totalStocks[i].stockprice, stockQuantity:quantity});
					}
				}
				
				console.log('selectedData:'+selectedData);
				console.log('selectedDataArr:'+selectedDataArr);
				$http({
					method : 'POST',
					url : 'SaveStockServlet?userId='+userId,
					data : {
						selectedStocks : selectedDataArr
					}
				}).success(function(data, status, headers, config) {
					console.log("success at stock details:" + data);
					stockDetailsAsPerCap = data;
				}).error(function(data, status, headers, config) {
					console.log("failure at stock details:" + data);
					stockDetailsAsPerCap = data;
				});
				return stockDetailsAsPerCap;
				
			},
			fetchSavedStocks : function(userId) {
				console.log('Inside fetchSavedStocks: '+userId);
				$http({
					method : 'GET',
					url : 'FetchSavedStockServlet?userId='+userId
				}).success(function(data, status, headers, config) {
					console.log("success at fetchSavedStocks:" + data);
					savedStockList = data;
				}).error(function(data, status, headers, config) {
					console.log("failure at fetchSavedStocks:" + data);
					savedStockList = data;
				});
				return savedStockList;
			}
				};

			});

})();