(function() {
	var app = angular.module('myApp', [ 'ui.router' ]);

	app.run(function($rootScope, $location, $state, LoginService) {
	});

	app.config([ '$stateProvider','$urlRouterProvider',
			function($stateProvider, $urlRouterProvider) {
				$urlRouterProvider.otherwise('/login');

				$stateProvider.state('login', {
					url : '/login',
					templateUrl : 'loginPage',
					controller : 'LoginController'
				}).state('stock', {
					url : '/stock',
					templateUrl : 'stockPage',
					controller : 'StockController',
					params : {userDetails : ''}
				});
			} ]);

	app.controller('LoginController', function($scope, $rootScope,
			$stateParams, $state, LoginService) {
		$rootScope.title = "Login";

		$scope.formSubmit = function() {
		
		LoginService.login($scope.username, $scope.password).then(function(data){	
			console.log("login function response data is : " + data);
		    if (data) {
				$scope.error = '';
				$scope.username = '';
				$scope.password = '';
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
		$rootScope.caps = [ "SmallCap", "MidCap", "LargeCap" ];
		$scope.showRecommendedStockTable = false;
		$scope.showloading=false;
		//$scope.recommendedStockList;
		//$scope.savedStockList;
		$scope.userId = $stateParams.userDetails.userId;
		
		$scope.showStockAsPerCap = function() {
			$scope.showloading=true;
			console.log('MarkeCap submit button is clicked');
			var requestedCap = $scope.selectedCap;
			console.log('RequestedCap : ' + requestedCap);
			if(undefined != requestedCap)
			var stockDetails = LoginService.getRecommendedStockLists(requestedCap);
			console.log('stockDetails after login service : ' + stockDetails)	
			
			setTimeout(function() 
		    {
		   console.log('After timeout stockDetails : ' + stockDetails)	
		   console.log('After timeout LoginService.getStockListForSelectedCAP() : ' + LoginService.getStockListForSelectedCAP());
		   stockDetails = LoginService.getStockListForSelectedCAP();
		   $scope.showloading=false;
			if (undefined != stockDetails || stockDetails != "") {
				$scope.recommendedStockList = stockDetails.stocks;
				console.log('recommendedStockList from server : '+ $scope.recommendedStockList)
				$scope.showloading=false;
				$scope.showRecommendedStockTable = true;
			}
		
			}, 3000);

		};
		$scope.saveStocks = function() {
			console.log('Save stock button clicked : '+ $scope.userId);
			var isSuccess = LoginService.saveStockData($scope.recommendedStockList, $scope.userId);

		};
		
		$scope.getSavedStocks = function() {
			console.log('Fetch saved stock button clicked : ' + $stateParams.userDetails.userId);
			var savedStocks = LoginService.fetchSavedStocks($scope.userId);
			if (undefined != savedStocks.stocks || savedStocks.stocks != "") {
				$scope.savedStockList = savedStocks.stocks;
			}
		};
		
		$scope.logout = function() {
			console.log('Logout button clicked:');
			$scope.showRecommendedStockTable = false;
			$scope.showloading = false;
			$scope.recommendedStockList = "";
			$scope.savedStockList = "";
			$scope.userId = ""
		};
	});

	app.factory('LoginService', function($http, $state, $q) {
		var isAuthenticated = false;
		var stockListForSelectedCAP = '';
		var userSavedStockList = '';
		
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
					console.log("Login Success : " + data);
					isAuthenticated = true;
					$state.go('stock', {userDetails: data});
				}).error(function(data, status, headers, config) {
					console.log("Login failure : " + data);
					isAuthenticated = false;
				});
				deferred.resolve(isAuthenticated);
				return deferred.promise;
			},
			
			Authenticated : function() {
				console.log('Authenticated flag value : ' + isAuthenticated);
				return isAuthenticated;
			},
			
			getStockListForSelectedCAP : function() {
				console.log('stockListForSelectedCAP in getStockListForSelectedCAP :' + stockListForSelectedCAP);
				return stockListForSelectedCAP;
			},
			
			getRecommendedStockLists : function(requestedCap) {
				$http({
					method : 'GET',
					url : 'RecommendedStockServlet?selectedCap='+requestedCap
				}).success(function(data, status, headers, config) {
					console.log("success at stock details: " + data);
					stockListForSelectedCAP = data;
				}).error(function(data, status, headers, config) {
					console.log("failure at stock details: " + data);
					stockListForSelectedCAP = data;
				});
				return stockListForSelectedCAP;
			},
			
			saveStockData : function(totalStocks, userId) {
				console.log('Inside saveStockData function');
				
				var selectedData = "";
				var selectedDataArr = [];
				for (i=0; i<totalStocks.length; i++) {
					if (totalStocks[i].selected == true) {
						quantity = document.getElementById("quantityField" + i).value;
						selectedData = selectedData + {stockName:totalStocks[i].stocksymbol, stocksymbol:totalStocks[i].stocksymbol, stockprice:totalStocks[i].stockprice, stockquantity:quantity} + ",";
						selectedDataArr.push({stockName:totalStocks[i].stocksymbol, stocksymbol:totalStocks[i].stocksymbol, stockprice:totalStocks[i].stockprice, stockquantity:quantity});
					}
				}
				
				console.log('selectedData: ' + selectedData);
				console.log('selectedDataArr: ' + selectedDataArr);
				$http({
					method : 'POST',
					url : 'SaveStockServlet?userId='+userId,
					data : {
						selectedStocks : selectedDataArr
					}
				}).success(function(data, status, headers, config) {
					console.log("success at stock details : " + data);
					stockListForSelectedCAP = data;
				}).error(function(data, status, headers, config) {
					console.log("failure at stock details : " + data);
					stockListForSelectedCAP = data;
				});
				return stockListForSelectedCAP;
				
			},
			
			fetchSavedStocks : function(userId) {
				console.log('Inside fetchSavedStocks: '+userId);
				$http({
					method : 'GET',
					url : 'FetchSavedStockServlet?userId='+userId
				}).success(function(data, status, headers, config) {
					console.log("success at fetchSavedStocks : " + data);
					userSavedStockList = data;
				}).error(function(data, status, headers, config) {
					console.log("failure at fetchSavedStocks : " + data);
					userSavedStockList = data;
				});
				return userSavedStockList;
			}
				};
			});
})();