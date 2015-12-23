# 语音发布服务 #

## Features ##
    1. 查询语音发布群组列表
    2. 查询群组详情
    3. 创建群组
    4. 把终端加入群组
    5. 从群组中移除终端
    6. 删除群组
    7. 新增语音播报
    8. 修改语音播报
    9. 语音播报记录列表
    10. 语音播报记录详情
    11. 语音播报下载链接列表
    12. 语音播报下载

###1. 查询语音发布群组列表 :

	1. curl localhost:29216/remindgroups?groupId=guanhutong

	2.  response
	   {
		    "content": [
		        {
		            "id": "8a89aa05506a253a01506a25e6360000",
		            "name": null,
		            "terminalCount": 0,
		            "searchKeywords": null,
		            "ownerGroupId": "guanhutong"
		        },
		        {
		            "id": "8a89aa685051115b0150511191ce0000",
		            "name": "9927",
		            "terminalCount": 11,
		            "searchKeywords": null,
		            "ownerGroupId": "guanhutong"
		        }
		    ],
		    "last": false,
		    "totalElements": 11,
		    "totalPages": 6,
		    "size": 2,
		    "number": 0,
		    "sort": null,
		    "first": true,
		    "numberOfElements": 2
		}

###2. 查询语音发布群组包含终端详情 :

	1. curl localhost:29216/remindgroups/8a89aa685051115b0150511191ce0000?page=1&size=2

	2.  response
    {
        "remindGroup": {
            "id": "8a89aa68506f746501506f74b77a0000",
            "name": "test",
            "terminalCount": 0,
            "searchKeywords": null,
            "ownerGroupId": "guanhutong"
        },
        "size": 50,
        "number": 0,
        "content": [
            {
                "id": "8a89aa4a5003680601500379f6890000",
                "imei": "656584020305001",
                "sim": "",
                "ownerName": null,
                "groupId": "guanhutong"
            }
        ],
        "first": true,
        "last": true,
        "totalElements": 1,
        "numberOfElements": 1,
        "totalPages": 1
    }


###3. 创建群组:

	1. curl -H "Content-Type:application/json" localhost:29216/remindgroups -d
	{
	"name" : "9927",
	"ownerGroupId": "guanhutong",
	"groupIds": ["guanhutong"],
	"terminalIds": ["8a89aa4a5003680601500379f6890000"],
	"searchKeywords": ["男", "神经病"],
	"searchParams": "minAge=25&maxAge=26&keyword=186&medicalHistory=糖尿病&ownerGroupId=guanhutong"
	}


###4. 添加终端到群组:

	1. curl -H "Content-Type:application/json" localhost:29216/remindgroups/8aa5c6b2504f987201504f98eb4e0000/addterminals -d
	{
	"groupIds": ["guanhutong"],
	"terminalIds": ["8a89aa4a5003680601500379f6890000"],
	"searchKeywords": ["男", "神经病"],
	"searchParams": ""
	}


###5. 从群组中移除终端:
	1. curl -H "Content-Type:application/json"http://localhost:29216/remindgrops/8aa5c6b2504f987201504f98eb4e0000/deleteterminals -d
	["8a89aa4a4feda9ed014ff3866b5b0000","8a89aa4a5003680601500379f6890000"]


###6. 删除群组：
	1. curl -X DELETE http://localhost:29216/remindgrops/8aa5c6b2504f987201504f98eb4e0000
	
	
###7. 新增语音播报:
	1. curl -H "Content-Type:application/json" POST http://localhost:29216/reminders -d
	
	{
	    "ownerGroupId": "guanhutong",
	    "creator": "creator",
	    "searchKeywords": ["男", "神经病"],
		"searchParams": "";
	    "reminderGroupName":"登山",
	    "groupIds": [
	        "8a89aa4a4ff2ea93014ff37ec23b0000"
	    ],
	    "terminalIds": [
	        "8a89aa4a5003680601500379f6890000"
	    ],
	    "mode": "OneTime",
	    "reminderTime": "2015-10-21 11:00:23",
	    "content": "早上好"
	}
	
###8. 修改语音播报:
	1. curl -H "Content-Type:application/json" PATCH http://localhost:29216/reminders/{id} -d
	
	{
	    "saveToRemindGroup": "true",
	    "reminderGroupName":"登山",
	    "reminderGroupName": "OneTime",
	    "reminderTime": "2015-10-21 11:00:23",
	    "content": "早上好好"
	}

### 9. 语音播报记录列表
	1. curl  http://localhost:29216/reminders?status=false&groupId=guanhutong
	
	{
	    "content": [
	        {
	            "id": "8a89aa055093db93015093dc2d800007",
	            "terminalCount": 1,
	            "ownerGroupId": "guanhutong",
	            "creator": null,
	            "needIssue": false,
	            "needExport": false,
	            "searchKeywords": null,
	            "content": "早上好",
	            "remindGroupId": "8a89aa055093db93015093dc2ce60000",
	            "remindGroupName": "登山",
	            "active": true,
	            "mode": "OneTime",
	            "reminderTime": "2015-10-21 11:00:23"
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
	
### 10. 语音播报记录详情
	1. curl   http://localhost:29216/reminders/{id}
	
	{
	    "reminder": {
	        "id": "8a89aa055093db93015093dc2d800007",
	        "terminalCount": 0,
	        "ownerGroupId": "guanhutong",
	        "creator": null,
	        "needIssue": false,
	        "needExport": false,
	        "searchKeywords": null,
	        "content": "早上好",
	        "remindGroupId": "8a89aa055093db93015093dc2ce60000",
	        "remindGroupName": "登山",
	        "active": true,
	        "mode": "OneTime",
	        "reminderTime": "2015-10-21 11:00:23"
	    },
	    "size": 50,
	    "number": 0,
	    "content": [
	        {
	            "id": "8a89aa4a5003680601500379f6890000",
	            "imei": "656584020305001",
	            "sim": "",
	            "ownerName": null,
	            "groupId": "guanhutong"
	        }
	    ],
	    "numberOfElements": 1,
	    "totalElements": 1,
	    "first": true,
	    "totalPages": 1,
	    "last": true
	}

### 11. 语音播报下载链接列表
	1.curl get http://10.9.42.203:8087/reminders/8a89aa055093db93015093dc2d800007/exports
	{
    "count": 1,
    "listSize": 1,
    "reminderExports": [
        "8a89aa055093db93015093dc9c830009"
    ]
}

### 12. 语音播报下载  
	1. curl get http://10.9.42.202:29216/exports/8a89aa055093db93015093dc9c830009
	
### 13. 删除语音播报 
	1. curl delete http://localhost:29216/reminders/{id}
	
	
    
