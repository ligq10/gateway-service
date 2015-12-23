
# cp150-gateway-web-service
	> 关护通终端下发指令服务
## Features


	
# DEV Env
## Run 
	> run as gradle build -clean build
	> java -jar cp150-gateway-web-service-(version).jar
	
	服务健康检查
	http://localhost:29212/health 
	
	>1 联络人名单

	curl -header {"Content-Type:application/vnd.jiahua.commands.settingContactList.v1+json"} http://localhost:19212/cp150s/commands/contacts
	-----------------------------------------------------------------------------------
		post	{
				  "imei" : "357718860201288",
				  "message" : [{
				    "position" : 0,
				    "telNum" : "18030511595",
				    "name" : "彭良"
				  }]
				}
	-----------------------------------------------------------------------------------

	curl -header {"Content-Type:application/json"} http://localhost:19212/cp150s/commands/contactupdate
    	-----------------------------------------------------------------------------------
    		post	{
    				  "imei" : "357718860201288",
    				  "message" : {
    				    "position" : 0,
    				    "telNum" : "18030511595",
    				    "name" : "彭良"
    				  }
    				}
    	-----------------------------------------------------------------------------------

	>2 白名单

	curl -header {"Content-Type:application/vnd.jiahua.commands.settingWhiteList.v1+json"} http://localhost:19212/cp150s/commands/whitelist
	-----------------------------------------------------------------------------------
		post	{
				  "imei" : "357718860201288",
				  "message" : [{
				    "position" : 0,
				    "telNum" : "18030511595",
				    "name" : "彭良"
				  }]
				}
	-----------------------------------------------------------------------------------

	curl -header {"Content-Type:application/json"} http://localhost:19212/cp150s/commands/whitelistadd
    	-----------------------------------------------------------------------------------
    		post	{
    				  "imei" : "357718860201288",
    				  "message" : {
    				    "telNum" : "18030511595",
    				    "name" : "彭良"
    				  }
    				}


    		返回值：
    		{
                "position": 11,
                "telNum": "18030511595",
                "name": "彭良"
            }
    	-----------------------------------------------------------------------------------

    curl -header {"Content-Type:application/json"} http://localhost:19212/cp150s/commands/whitelistupdate
        	-----------------------------------------------------------------------------------
        		post	{
        				  "imei" : "357718860201288",
        				  "message" : {
        				    "position" : 0,
        				    "telNum" : "18030511595",
        				    "name" : "彭良"
        				  }
        				}
        	-----------------------------------------------------------------------------------

     curl -header {"Content-Type:application/json"} http://localhost:19212/cp150s/commands/whitelistdelete
            	-----------------------------------------------------------------------------------
            		post	{
            				  "imei" : "357718860201288",
            				  "message" : 0
            				 }
            	-----------------------------------------------------------------------------------

	>3 恢复出厂设置 curl -header {"Content-Type:application/vnd.jiahua.commands.settingInitialization.v1+json"} http://localhost:19212/cp150s/commands/initialization
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"param":"1"
							}

				}
	-----------------------------------------------------------------------------------
	
	>4 电子围栏设置 curl -header {"Content-Type:application/vnd.jiahua.commands.settingProtectedCircle.v1+json"} http://localhost:19212/cp150s/commands/protectedcircle
	-----------------------------------------------------------------------------------
		type防护圈类型标志（0关闭 1、圆形2、方形）
		post	{									
					"imei": "357718860201288",
					"message":{
								"type":1,
								"centreLongt":"40.3",
								"centreLat":"30.5",
								"radius":"1",
								"eastLongt":"",
								"westLongt":"",
								"southLat":"",
								"northLat":"",
								"contactFlag":2
							}

				}
	-----------------------------------------------------------------------------------
	>5 来电设置 curl -header {"Content-Type:application/vnd.jiahua.commands.settingAnswer.v1+json"} http://localhost:19212/cp150s/commands/answer
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"param":"1"
							}

				}
	-----------------------------------------------------------------------------------
	>6 服务器设置 curl -header {"Content-Type:application/vnd.jiahua.commands.settingServerConf.v1+json"} http://localhost:19212/cp150s/commands/serverconf
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"param":"225.225.225.225:9990"
							}

				}
	-----------------------------------------------------------------------------------
	>7 自动接听 curl -header {"Content-Type:application/vnd.jiahua.commands.settingAutoAnswer.v1+json"} http://localhost:19212/cp150s/commands/autoanswer
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"param":"1"
							}

				}
	-----------------------------------------------------------------------------------
	
	>8 计步器步数归零间隔天数 curl -header {"Content-Type:application/vnd.jiahua.commands.settingPedometerInterval.v1+json"} http://localhost:19212/cp150s/commands/pedometerinterval
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"param":"3"
							}

				}
	-----------------------------------------------------------------------------------
	>9 心跳包间隔 curl -header {"Content-Type:application/vnd.jiahua.commands.settingHeartbeatInterval.v1+json"} http://localhost:19212/cp150s/commands/heartbeatinterval
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"param":"3"
							}

				}
	-----------------------------------------------------------------------------------
	>10 SOS模式 curl -header {"Content-Type:application/vnd.jiahua.commands.settingSos.v1+json"} http://localhost:19212/cp150s/commands/sos
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"sendShortMessage":"1",
								"sentToC1":"0",
								"sentToC2":"1",
								"sentToC3":"0",
								"sentToC4":"1",
								"sentC5Tell":"18682753329",
								"call":"1",
								"callC1":"1",
								"callC2":"0",
								"callC3":"1",
								"callC4":"0",
								"callC5Tell":"123456789",
								"smPrefix":"baojing"
							}

				}
	-----------------------------------------------------------------------------------
	>11 终端所在城市行政区划代码 curl -header {"Content-Type:application/vnd.jiahua.commands.settingArea.v1+json"} http://localhost:19212/cp150s/commands/area
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"param":"110106"
							}

				}
	-----------------------------------------------------------------------------------
	>12 修改密码 curl -header {"Content-Type:application/vnd.jiahua.commands.settingPassword.v1+json"} http://localhost:19212/cp150s/commands/password
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"param":"123456"
							}

				}
	-----------------------------------------------------------------------------------
	>13 GPS模块电源开关 curl -header {"Content-Type:application/vnd.jiahua.commands.settingGpsPower.v1+json"} http://localhost:19212/cp150s/commands/gpspower
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"param":"1"
							}

				}
	-----------------------------------------------------------------------------------
	>14 终端闲置报警 curl -header {"Content-Type:application/vnd.jiahua.commands.settingIdleWarning.v1+json"} http://localhost:19212/cp150s/commands/idlewarning
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"param":"5"
							}

				}
	-----------------------------------------------------------------------------------
	>15 代发短信指令 curl -header {"Content-Type:application/vnd.jiahua.commands.shortMessage.v1+json"} http://localhost:19212/cp150s/commands/shortmessage
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"telNum":"123456789",
								"content":"短信内容"
							}

				}
	-----------------------------------------------------------------------------------
	>16 代拨电话指令 curl -header {"Content-Type:application/vnd.jiahua.commands.dialingCall.v1+json"} http://localhost:19212/cp150s/commands/dialingcall
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"telNum":"123456789",
								"type":1
							}

				}
	-----------------------------------------------------------------------------------
	>17 事件语音提醒指令
	

	curl -header {"Content-Type:application/json"} http://localhost:19212/cp150s/commands/voicereminderadd
	-----------------------------------------------------------------------------------
	        备注： repeatMode 代表重复选择重复时周一到周日是否提醒，true提醒，false不提醒
    		post	{
    					"imei": "357718860201288",
    					"message":{
    								"active" : "true",
    								"mode" : "OneTime | EveryDay | Repeat",
    								"repeatMode" : ["true", "false", "true", "true", "true", "true", "false"],
    								"reminderTime" : "1912-01-01 00:04:12",
    								"content":"息技术中心中国铁道科学研究院"
    							}

    				}

    				返回值：
                    {
                        "index" : 0,
                        "active" : "true",
                        "mode" : "OneTime",
                        "repeatMode" : ["true", "false", "true", "true", "true", "true", "false"],
                        "reminderTime" : "1912-01-01 00:04:12",
                        "content":"息技术中心中国铁道科学研究院"
                    }
    	-----------------------------------------------------------------------------------

    curl -header {"Content-Type:application/json"} http://localhost:19212/cp150s/commands/voicereminderupdate
    	-----------------------------------------------------------------------------------
    	        备注： repeatMode 代表重复选择重复时周一到周日是否提醒，true提醒，false不提醒
        		post	{
        					"imei": "357718860201288",
        					"message":{
        								"index" : 3,
        								"active" : "true",
        								"mode" : "OneTime | EveryDay | Repeat",
        								"repeatMode" : ["true", "false", "true", "true", "true", "true", "false"],
        								"reminderTime" : "1912-01-01 00:04:12",
        								"content":"息技术中心中国铁道科学研究院"
        							}

        				}
        	-----------------------------------------------------------------------------------

    curl -header {"Content-Type:application/json"} http://localhost:19212/cp150s/commands/voicereminderdelete
        	-----------------------------------------------------------------------------------
            		post	{
            					"imei": "357718860201288",
            					"message": 5
            				}
            	-----------------------------------------------------------------------------------

	>18 实时语音播报指令 curl -header {"Content-Type:application/vnd.jiahua.commands.realTimeBroadcast.v1+json"} http://localhost:19212/cp150s/commands/realtimebroadcast
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"param":"天气不错"
							}

				}
	-----------------------------------------------------------------------------------
	>19 代发彩信指令 curl -header {"Content-Type:application/vnd.jiahua.commands.multimediaMessage.v1+json"} http://localhost:19212/cp150s/commands/multimediamessage
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"telNum":"123456789",
								"content":"天气污染严重,避免外出^http://m4.auto.itc.cn/car/120/95/90/Img1579095_120.jpg"
							}

				}
	-----------------------------------------------------------------------------------
	>20 用户生理信息配置指令 curl -header {"Content-Type:application/vnd.jiahua.commands.settingPhysiologicalInformation.v1+json"} http://localhost:19212/cp150s/commands/physiologicalinformation
	-----------------------------------------------------------------------------------
		post	{									
					"imei": "357718860201288",
					"message":{
								"gender":0,
								"age":25,
								"height":160,
								"weight":100,
								"mode":2,
								"stepDistance":80
							}

				}
	-----------------------------------------------------------------------------------
	>21 终端设置链接 curl http://localhost:29212/cp150s/357718860201288/settings
	-----------------------------------------------------------------------------------
		get	{
			  "imei" : "357718860201288",
			  "_links" : {
			    "self" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings"
			    },
			    "contactList" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/contacts"
			    },
			    "whiteList" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/whitelist"
			    },
			    "initialization" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/initialization"
			    },
			    "protectedCircle" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/protectedcircle"
			    },
			    "answer" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/answer"
			    },
			    "serverConf" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/serverconf"
			    },
			    "autoAnswer" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/autoanswer"
			    },
			    "pedometerInterval" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/pedometerinterval"
			    },
			    "heartbeatInterval" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/heartbeatinterval"
			    },
			    "sos" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/sos"
			    },
			    "area" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/area"
			    },
			    "password" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/password"
			    },
			    "gpsPower" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/gpspower"
			    },
			    "idleWarning" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/settings/idlewarning"
			    }
			  }
			}
	-----------------------------------------------------------------------------------
	>22 终端事件提醒 curl http://localhost:29212/cp150s/357718860201288/voicereminders
	-----------------------------------------------------------------------------------
	备注  action:（添加，修改，删除，开，关分别对应0,1,2,3,4）
		get	{
			  "voiceReminders" : {
			    "0": {
            "index": 0,
            "active": true,
            "mode": "OneTime",
            "reminderTime": "1912-01-01 00:04:12",
            "repeatMode": [
                true,
                false,
                true,
                true,
                true,
                true,
                false
            ],
            "content": "息技术中心中国铁道科学研究院"
        },
        "2": {
            "index": 2,
            "active": true,
            "mode": "OneTime",
            "reminderTime": "1912-01-01 00:04:12",
            "repeatMode": [
                true,
                false,
                true,
                true,
                true,
                true,
                false
            ],
            "content": "息技术中心中国铁道科学研究院"
        	},
			  "imei" : "357718860201288",
			  "_links" : {
			    "self" : {
			      "href" : "http://localhost:29212/cp150s/357718860201288/voicereminders"
			    }
			  }
			}
	-----------------------------------------------------------------------------------
	

	
# Developer 主要研发人员
	> hanlin@changhongit.com ; pengliang@changhongit.com