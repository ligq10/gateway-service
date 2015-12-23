
# cp150-terminal-rest
	> 关护通终端管理
## Features
    > 新建终端
    > 修改终端
    > 删除终端     
    > 终端导入
    > 根据组绑定终端
    > 终端分配
    > 查询终端
    > 根据Imei查询终端 
    > 根据Imei&checkCode查询终端 
    > 根据分组查询终端列表
    > 根据分组查询分组及子组所有终端
    > 终端列表
    > 终端搜索
    > 终端用户搜索
    > 根据Imei查询报警列表
    > 根据id查询报警详情

	
# DEV Env
## Run 
	> run as gradle build -clean build
	> java -jar cp150-terminal-rest-(version).jar
	
	服务健康检查
	http://localhost:29210/health 
	
	> 新建终端 curl -header {"Content-Type:application/json"} http://localhost:29210/cp150s
	-----------------------------------------------------------------------------------
		post	{	
					"sn": "123456",     
					"modelNumber": "6001",
					"imei": "83275647512612345678",
					"sim":"123456789",
					"checkCode": "5126",
					"version":"0001",
					"status":"激活",
					"description":"description",
					"groupId":"8a89aa054b2e7a23014b2e7ea82f0001"
				}
	-----------------------------------------------------------------------------------
	> 删除终端 curl http://localhost:29210/cp150s/8a89aa054b2e7a23014b2e7ea82f0001
	-----------------------------------------------------------------------------------
		delete
	-----------------------------------------------------------------------------------
	> 修改终端 curl -header {"Content-Type:application/json"} http://localhost:29210/cp150s/ff8081814cc15286014cc61b49290010
	-----------------------------------------------------------------------------------
		patch 	{	
					"sn": "123456",     
					"modelNumber": "6001",
					"imei": "832756475126",
					"sim":"123456789",
					"checkCode": "5126",
					"version":"0001",
					"status":"激活",
					"description":"description"
				}
	-----------------------------------------------------------------------------------
	> 终端导入 curl {"Content-Type:multipart/form-data"} http://localhost:29210/cp150s/import
	-----------------------------------------------------------------------------------
		post	
				groupid:123456789
				file:.xls或.xlsx的Excel文件
	-----------------------------------------------------------------------------------
	> 终端分配 curl  http://localhost:29210/cp150s/distribution
	-----------------------------------------------------------------------------------
		post	{
				    "fromGroupIds": [
				        "ff8081814b39cea9014c217560ae0090",
				        "ff8081814b39cea9014c217560ae0015"
				    ],
				    "fromTerminalIds": [
				        "ff8081814b39cea9014c217560ae0013","ff8081814b39cea9014c217560ae0013"
				    ],
				    "toGroupId": "guanhutong"
				}
	-----------------------------------------------------------------------------------
	> 根据组绑定终端 {"Content-Type:multipart/form-data"} curl http://localhost:29210/cp150s/groupbind
	-----------------------------------------------------------------------------------
		post 
			groupid:123456789
			parentid:123456789
		 	file:.xls或.xlsx的Excel文件
	-----------------------------------------------------------------------------------
	> 查询终端 curl  http://localhost:29210/cp150s/8a89aa054b2e7a23014b2e7ea82f0001
	-----------------------------------------------------------------------------------
		get		{
				  "sn": "ff808181491cfa9b01491d2658ac0024",
				  "modelNumber": "ff808181491cfa9b01491d1b014a001b",
				  "imei": "ff808181491cfa9b01491d1b014a001b",
				  "checkCode": "ff808181491cfa9b01491d1b014a001b",
				  "version": null,
				  "status": null,
				  "activateTime": null,
				  "description": null,
				  "_links": {
				    "self": {
				      "href": "http://localhost:29210/terminal/8a89aa054b2e7a23014b2e7ea82f0001"
				    }
				  }
				}
	-----------------------------------------------------------------------------------
	> 根据Imei查询终端 curl  http://localhost:29210/cp150s/search/findByImei?imei=656584020305009  （可用来校验Imei号唯一性）
	-----------------------------------------------------------------------------------
		get		{
				  "_embedded" : {
				    "cp150s" : [ {
				      "sn" : "305009",
				      "modelNumber" : "CP158",
				      "imei" : "656584020305009",
				      "checkCode" : "5009",
				      "version" : null,
				      "status" : null,
				      "activateTime" : null,
				      "description" : null,
				      "groupId" : null,
				      "_links" : {
				        "self" : {
				          "href" : "http://localhost:29210/cp150s/8a89aa054c26908d014c2691e1450007"
				        }
				      }
				    } ]
				  }
				}
	-----------------------------------------------------------------------------------
	> 根据Imei&checkCode查询终端 curl  http://localhost:29210/cp150s/search/findByImeiAndCheckCode?imei=656584020305009&checkcode=5009  （可用来校验Imei号唯一性）
	-----------------------------------------------------------------------------------
		get		{
				  "_embedded" : {
				    "cp150s" : [ {
				      "sn" : "305009",
				      "modelNumber" : "CP158",
				      "imei" : "656584020305009",
				      "checkCode" : "5009",
				      "version" : null,
				      "status" : null,
				      "activateTime" : null,
				      "description" : null,
				      "groupId" : null,
				      "_links" : {
				        "self" : {
				          "href" : "http://localhost:29210/cp150s/8a89aa054c26908d014c2691e1450007"
				        }
				      }
				    } ]
				  }
				}
	-----------------------------------------------------------------------------------
	> 终端列表(1) curl  http://localhost:29210/cp150s
	-----------------------------------------------------------------------------------
		get	{
			  "_links": {
			    "self": {
			      "href": "http://localhost:29210/cp150s{?page,size,sort}",
			      "templated": true
			    }
			  },
			  "_embedded": {
			    "terminal": [
			      {
			        "sn": "ff808181491cfa9b01491d2658ac0024",
			        "modelNumber": "ff808181491cfa9b01491d1b014a001b",
			        "imei": "ff808181491cfa9b01491d1b014a001b",
			        "checkCode": "ff808181491cfa9b01491d1b014a001b",
			        "version": null,
			        "status": null,
			        "activateTime": null,
			        "description": null,
			        "_links": {
			          "self": {
			            "href": "http://localhost:29210/cp150s/8a89aa054b2e7a23014b2e7ea82f0001"
			          }
			        }
			      }
			    ]
			  },
			  "page": {
			    "size": 20,
			    "totalElements": 1,
			    "totalPages": 1,
			    "number": 0
			  }
			}	
	-----------------------------------------------------------------------------------
	> 终端列表(2) curl  http://localhost:29210/cp150s/withterminaluser?&page=0&size=2
	-----------------------------------------------------------------------------------
		get	{
			  "_links" : {
			    "next" : {
			      "href" : "http://localhost:29210/cp150s/withterminaluser?&page=1&size=2"
			    },
			    "self" : {
			      "href" : "http://localhost:29210/cp150s/withterminaluser?&page=0&size=2"
			    }
			  },
			  "_embedded" : {
			    "terminalResponses" : [ {
			      "id" : "8a89aa054c2ac950014c2acc02fc0002",
			      "sn" : "305001",
			      "modelNumber" : "CP150",
			      "terminalUserName" : "老张",
			      "imei" : "656584020305001",
			      "sim" : null,
			      "checkCode" : "5001",
			      "version" : null,
			      "status" : null,
			      "activateTime" : "",
			      "description" : null,
			      "groupId" : "8a89aa054b53417b014b534480b10000"
			    }, {
			      "id" : "8a89aa054c2ac950014c2acc02fd0005",
			      "sn" : "305005",
			      "modelNumber" : "CP154",
			      "terminalUserName" : null,
			      "imei" : "656584020305005",
			      "sim" : null,
			      "checkCode" : "5005",
			      "version" : null,
			      "status" : null,
			      "activateTime" : "",
			      "description" : null,
			      "groupId" : "8a89aa054b53417b014b534480b10000"
			    } ]
			  },
			  "page" : {
			    "size" : 2,
			    "totalElements" : 40,
			    "totalPages" : 20,
			    "number" : 0
			  }
			}	
	-----------------------------------------------------------------------------------
	> 根据分组查询终端 curl  http://localhost:29210/cp150s/search/findByGroupId?groupid=ff8081814b39cdfd014b7213f0820039&page=0&size=1
	-----------------------------------------------------------------------------------
		get	{
			{
			  "_links" : {
			    "next" : {
			      "href" : "http://localhost:29210/cp150s/search/findByGroupId?groupid=ff8081814b39cdfd014b7213f0820039&page=1&size=1"
			    },
			    "self" : {
			      "href" : "http://localhost:29210/cp150s/search/findByGroupId?groupid=ff8081814b39cdfd014b7213f0820039&page=0&size=1{&sort}",
			      "templated" : true
			    }
			  },
			  "_embedded" : {
			    "cp150s" : [ {
			      "sn" : "123456",
			      "modelNumber" : "6001",
			      "imei" : "832756475955",
			      "checkCode" : "5126",
			      "version" : "0001",
			      "status" : "激活",
			      "activateTime" : null,
			      "description" : "description",
			      "groupId" : "ff8081814b39cdfd014b7213f0820039",
			      "_links" : {
			        "self" : {
			          "href" : "http://localhost:29210/cp150s/ff8081814b39cea9014b493d3e770002"
			        }
			      }
			    } ]
			  },
			  "page" : {
			    "size" : 1,
			    "totalElements" : 2,
			    "totalPages" : 2,
			    "number" : 0
			  }
			}
	-----------------------------------------------------------------------------------
	> 根据分组查询分组及子组所有终端 curl  http://localhost:29210/cp150s/search/findByGroupIdAndChildGroups?groupid=guanhutong&page=0&size=2
	-----------------------------------------------------------------------------------
			get		{
					  "_links" : {
					    "next" : {
					      "href" : "http://localhost:29210/cp150s/search/findByGroupIdAndChildGroups?groupid=guanhutong&page=1&size=2"
					    },
					    "self" : {
					      "href" : "http://localhost:29210/cp150s/search/findByGroupIdAndChildGroups?groupid=guanhutong&page=0&size=2"
					    }
					  },
					  "_embedded" : {
					    "cp150s" : [ {
					      "sn" : "2222222",
					      "modelNumber" : "wwwww",
					      "imei" : "848484848484848",
					      "sim" : null,
					      "ownerName" : null,
					      "checkCode" : "111",
					      "version" : null,
					      "status" : null,
					      "activateTime" : null,
					      "description" : null,
					      "groupId" : "guanhutong",
					      "uuid" : "ff8081814dbd08db014dfb60e7f70003"
					    }, {
					      "sn" : "啊啊啊啊啊啊啊啊啊啊啊啊啊",
					      "modelNumber" : "啊啊啊啊啊啊啊啊啊啊啊啊啊",
					      "imei" : "666666666666655",
					      "sim" : "12345678900",
					      "ownerName" : null,
					      "checkCode" : "5555",
					      "version" : "啊啊啊啊啊啊啊啊啊",
					      "status" : null,
					      "activateTime" : null,
					      "description" : "啊啊啊啊啊啊啊啊啊啊啊",
					      "groupId" : "guanhutong",
					      "uuid" : "ff8081814dbd08db014dfb4546410002"
					    } ]
					  },
					  "page" : {
					    "size" : 2,
					    "totalElements" : 11,
					    "totalPages" : 6,
					    "number" : 0
					  }
					}
	-----------------------------------------------------------------------------------
	> 终端轨迹 curl  http://localhost:29210/cp150s/ff8081814cc63456014cc6a0fd0d0001/locations?from=2015-04-08+06:42:18&page=0&size=20&to=2015-04-08+18:42:18
	-----------------------------------------------------------------------------------
	get		{
                "_links": {
                    "self": {
                        "href": "http://localhost:29210/cp150s/ff8081814cc63456014cc6a0fd0d0001/locations?from=2015-04-08 06:42:18&to=2015-04-08 18:42:18&page=0&size=20"
                    }
                },
                "_embedded": {
                    "locations": [
                        {
                            "date": "2015-04-08 13:55:33",
                            "gpsStatus": 0,
                            "mcc": "460",
                            "mnc": "0",
                            "lac": "33033",
                            "cell": "19074"
                        },
                        {
                            "date": "2015-04-08 14:17:58",
                            "gpsStatus": 1,
                            "longitude": 104.069,
                            "latitude": 30.585
                        },
                        ...
                    ]
                 },
                  "page": {
                         "size": 20,
                         "totalElements": 9,
                         "totalPages": 1,
                         "number": 0
                  }
             }

    id不存在返回 404， 没有位置信息返回204， 时间格式错误返回400

	-----------------------------------------------------------------------------------
	 > 终端搜索  curl http://localhost:29210/cp150s/search/byKeywordAndGroup?keyword=27&groupid=guanhutong&page=0&size=2
	-----------------------------------------------------------------------------------
		get {
			  "_links" : {
			    "next" : {
			      "href" : "http://localhost:29210/cp150s/search/byKeywordAndGroup?keyword=27&groupid=guanhutong&page=1&size=2"
			    },
			    "self" : {
			      "href" : "http://localhost:29210/cp150s/search/byKeywordAndGroup?keyword=27&groupid=guanhutong&page=0&size=2"
			    }
			  },
			  "_embedded" : {
			    "cp150s" : [ {
			      "sn" : "123456",
			      "modelNumber" : "6001",
			      "imei" : "832756475122",
			      "sim" : "123456789",
			      "ownerName" : null,
			      "checkCode" : "5126",
			      "version" : "0001",
			      "status" : "激活",
			      "activateTime" : "2015-06-17 13:14:56",
			      "description" : "description",
			      "groupId" : "guanhutong",
			      "uuid" : "8a89aa054dfff0e9014dfff10adb0000"
			    }, {
			      "sn" : "123456",
			      "modelNumber" : "6001",
			      "imei" : "832756475121",
			      "sim" : "123456789",
			      "ownerName" : null,
			      "checkCode" : "5126",
			      "version" : "0001",
			      "status" : "激活",
			      "activateTime" : "2015-06-17 11:24:28",
			      "description" : "description",
			      "groupId" : "guanhutong",
			      "uuid" : "8a89aa054dff8b4e014dff8be6710000"
			    } ]
			  },
			  "page" : {
			    "size" : 2,
			    "totalElements" : 4,
			    "totalPages" : 2,
			    "number" : 0
			  }
			}
	-----------------------------------------------------------------------------------


	-----------------------------------------------------------------------------------
    	 > 终端用户搜索  curl localhost:29210/terminalusers/advancesearch?minAge=25&maxAge=26&keyword=186&medicalHistory=糖尿病&ownerGroupId=guanhutong&gender=男
    -----------------------------------------------------------------------------------
        说明：ownerGroupId参数必需，其它参数至少有一个，minAge, maxAge为一组参数

    		{
                "content": [
                    {
                        "id": "8a89aa0550032129015003249a540000",
                        "telNum": "18682753329",
                        "realName": "老人1",
                        "terminal": {
                            "id": "8a89aa4a5003680601500379f6890000",
                            "sn": "305001",
                            "modelNumber": "CP150",
                            "imei": "656584020305001",
                            "type": "Cp150",
                            "sim": "",
                            "checkCode": "5001",
                            "status": "未激活",
                            "groupId": "guanhutong",
                            "uuid": "8a89aa4a5003680601500379f6890000"
                        }
                    }
                ],
                "totalPages": 1,
                "totalElements": 1,
                "last": true,
                "size": 20,
                "number": 0,
                "sort": null,
                "first": true,
                "numberOfElements": 1
            }
    	-----------------------------------------------------------------------------------


----------


## 报警API ##

### 1. 根据IMEI查询报警列表 ###

	curl -X POST -H "X-Token:74b00ff8-2116-4ba4-96cf-11a7220f308b" localhost:29210/warnings/view/byimeis?page=0&size=1 -d ["658956989456784"]

	返回值：
	{
        "_embedded": {
            "warningResponses": [
                {
                    "id": "8a89aa0550fe6d4c0150fe6e238f0000",
                    "name": "老张",
                    "warning": "低电报警",
                    "date": "2015-11-13 09:20:39"
                }
            ]
        },
        "page": {
            "size": 1,
            "totalElements": 12,
            "totalPages": 12,
            "number": 0
        }
    }
    
### 2. 根据imei、type、时间###

	curl -X POST -H "X-Token:74b00ff8-2116-4ba4-96cf-11a7220f308b" localhost:29210/warnings/view/byconditions?imei=658956989456784&type=SOS&start=2015-11-20 22:23:33&end=2015-12-08 22:23:33
	type:SOS\LOW_BATTERY\PROTECTED_CIRCLE_CONTACT_IN\PROTECTED_CIRCLE_CONTACT_OUT\ALL
	返回值：
	{
	    "_embedded": {
	        "warningResponses": [
	            {
	                "id": "e7a3495a-997a-11e5-b684-28d5b6102eb7",
	                "name": "老张",
	                "warning": "SOS报警",
	                "type": "SOS",
	                "date": "2015-12-03 13:01:24"
	            },
	            {
	                "id": "da1a8ef7-997a-11e5-b684-28d5b6102eb7",
	                "name": "老张",
	                "warning": "SOS报警",
	                "type": "SOS",
	                "date": "2015-12-03 13:01:01"
	            }
	        ]
	    },
	    "page": {
	        "size": 20,
	        "totalElements": 2,
	        "totalPages": 1,
	        "number": 0
	    }
	}   


### 2. 根据Id查询报警详情 ###

	curl -H "X-Token:74b00ff8-2116-4ba4-96cf-11a7220f308b" localhost:29210/warnings/view/byid?id=23

    返回值：
    {
        "id": "8a89aa0550fe6d4c0150fe6e238f0000",
        "name": "老张",
        "warning": "低电报警",
        "date": "2015-11-13 09:20:39",
        "type": "SOS",
        "gpsStatus": 1,
        "longitude": 116.466,
        "latitude": 39.0857,
        "mcc": "460",
        "mnc": "00",
        "lac": "4461",
        "cell": "26747",
        "batteralLevel": 3
    }

	
# Developer 主要研发人员
	> hanlin@changhongit.com ; pengliang@changhongit.com