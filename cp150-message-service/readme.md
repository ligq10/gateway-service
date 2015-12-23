# CP150 RabbitMQ消息服务 #

## Features ##
    1. 处理CP150网关发出的事件
    2. 监控中心直属分组统计 
    3. 监控中心直属及子组分组统计  
    4. 根据组查询终端在线数量 
    5. 根据组查询终端报警数量 
    6. 根据终端IMEI查询在线的终端
    7. 根据电话号码查询相关终端
    8. 查询终端最新状态信息

----------


## 监控中心直属分组统计 ##

	1. curl http://172.28.4.220:8086/monitorCount/bySelfId?groupId=8a89aa4a4ff2ea93014ff37ec23b0000


	2.  成功后将在response里面返回在线数量
	    {
		    "total": 1,
		    "warning": 1,
		    "offline": 1,
		    "online": 0
		}
        
## 监控中心直属及子组分组统计 ##

	1. curl http://172.28.4.220:8086/monitorCount/byParentId?groupId=guanhutong


	2.  成功后将在response里面返回在线数量
		{
		    "total": 3,
		    "warning": 3,
		    "offline": 2,
		    "online": 1
		}
        
## 根据组查询终端在线数量 ##

	1. curl http://172.28.4.220:8086/onlineTerminals/byGroupIds/count?groupIds=guanhutong,234234


	2.  成功后将在response里面返回在线数量
	    {
            "count": 1
        }
        
## 根据组查询终端报警数量 ##

	1. curl http://172.28.4.220:8086/warningTerminals/byGroupIds/count?groupIds=guanhutong,8a89aa4a4ff2ea93014ff37ec23b0000


	2.  成功后将在response里面返回在线数量
	    {
            "count":3
        }


## 根据终端IMEI查询在线的终端:
	1. curl http://172.28.4.220:8086/onlineTerminals/byImeis?imeis=357718860201288,357718860201289
	
	
	2.  成功后将在response里面返回在线的终端列表
		[
            "357718860201288"
        ]

## 根据电话号码查询相关终端:
	1. curl http://localhost:29219/cp150s/view/byContactAndPrentGroup?telNum=18682753329&groupid=guanhutong&page=0&size=20
	
	
	2.  成功后将在response终端列表
		{
		  "_links" : {
		    "self" : {
		      "href" : "http://localhost:29219/cp150s/view/byContactAndPrentGroup?telNum=18682753329&groupid=guanhutong&page=0&size=20"
		    }
		  },
		  "_embedded" : {
		    "terminals" : [ {
		      "sn" : "305015",
		      "modelNumber" : "CP150",
		      "imei" : "357718860201288",
		      "sim" : "",
		      "ownerName" : null,
		      "checkCode" : "5015",
		      "version" : null,
		      "status" : "未激活",
		      "activateTime" : null,
		      "description" : null,
		      "groupId" : "guanhutong",
		      "uuid" : "ff8081814e4d80fa014e75c69a0400bd"
		    }, {
		      "sn" : "222222222",
		      "modelNumber" : "2222",
		      "imei" : "232342342333333",
		      "sim" : "18682753329",
		      "ownerName" : null,
		      "checkCode" : "3222",
		      "version" : "22222",
		      "status" : "未激活",
		      "activateTime" : null,
		      "description" : "22222",
		      "groupId" : "guanhutong",
		      "uuid" : "ff8081814e4d80fa014e52a6ca8700b2"
		    } ]
		  },
		  "page" : {
		    "size" : 20,
		    "totalElements" : 2,
		    "totalPages" : 1,
		    "number" : 0
		  }
		}


## 查询终端最新状态 ##

	1. curl http://172.28.4.220:8086/terminals/latestInfo?imei=656584020305001


	2.  成功后response
	    {
            "sim": "18682753322",
            "gpsStatus": 1,
            "longitude": 116.777,
            "latitude": 39.1428,
            "locationTime": "2015-08-18 15:03:27",
            "expireDate": "2020-12-30 00:00:00",
            "batteryLevel": "60%",
            "onLine": false
        }