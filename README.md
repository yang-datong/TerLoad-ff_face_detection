
## App设计

```mermaid
%%{init: {'theme': 'base','themeVariables': {'primaryBorderColor':'#fff','primaryTextColor': '#fff', 'primaryColor': '#383838'}}}%%
graph LR
%%--------------------------------------------------------------------
0("App启动")
1("登录管理员") 
	1-1("设置页面")
		1-1-1("绑定人脸登录") 
		1-1-2("更改管理员信息") 
		1-1-3("切换主题") 
		1-1-4("数据备份\恢复")
		1-1-5("退出登录")
	1-2("用户页面") 
		1-2-1("查看全部用户列表") 
			1-2-1-1("查看单个用户信息") 
			1-2-1-2("查看单个用户考勤状态") 
			1-2-1-3("向用户发送考勤信息提醒")
		1-2-2("发送添加用户申请")
	1-3("会话页面") 
		1-3-1("查看历史会话列表")
2("登录普通用户")
	2-1("设置界面")
		2-1-1("绑定人脸登录")
		2-1-2("更改账户信息")
		2-1-3("切换主题")
		2-1-4("退出登录")
	2-2("考勤界面")
		2-2-1("进行人脸考勤")
		2-2-2("上传人脸照片")
		2-2-3("当前考勤状态")
	2-3("会话界面")
		2-3-1("查看历史会话列表")

0 --> 1  &  2
			1 --> 1-1  &  1-2   & 1-3
						1-1 --> 1-1-1 & 1-1-2 & 1-1-3 & 1-1-4 & 1-1-5
						1-2 --> 1-2-2
						1-2 --> 1-2-1 --> 1-2-1-1 & 1-2-1-2 & 1-2-1-3
						1-3 --> 1-3-1
			2 --> 2-1 & 2-3 & 2-2
						2-1 --> 2-1-1 & 2-1-2 & 2-1-3 & 2-1-4
						2-2 --> 2-2-1 & 2-2-2 & 2-2-3
						2-3 --> 2-3-1


%%--------------------------------------------------------------------
```

## 数据库设计
>每天6点重置考勤数据

- `id`: 用户唯一标识，可以是自增长的整数类型或UUID字符串类型。
- `username`: 用户名，可以是字符串类型，用于登录和显示用户信息。
- `password`: 用户密码，可以是字符串类型，应该使用哈希加密存储。
- `name`: 用户真实姓名，可以是字符串类型，用于显示用户信息和报表。
- `email`: 用户电子邮件地址，可以是字符串类型，用于发送考勤提醒和报表。
- `phone`: 用户电话号码，可以是字符串类型，用于发送短信或电话提醒。
- `avatar`: 用户头像，可以是BLOB类型或图片文件路径，用于显示用户信息。
- `status`: 用户考勤状态，可以是整数类型或枚举类型，用于记录用户当前的考勤状态（如未考勤、正在考勤、完成考勤等）。
- `checkin_time`: 用户签到时间，可以是日期时间类型，用于记录用户签到时间。
- `checkout_time`: 用户签退时间，可以是日期时间类型，用于记录用户签退时间。
- `create_time`: 账号创建时间

```mermaid
%%{init: {'theme': 'base','themeVariables': {'primaryBorderColor':'#fff','primaryTextColor': '#fff', 'primaryColor': '#383838'}}}%%
graph TB
%%--------------------------------------------------------------------
0("user")
1("id")
	1-1("autoGenerate")
2("username")
3("password")
4("name")
5("email")
6("phone")
7("avatar")
8("status")
9("checkin_time")
10("checkout_time")
11("createTime")
0 --> 1 & 2 & 3 & 4 & 5 & 6 & 7 & 8 & 9 & 10 & 11
			1 --> 1-1
%%--------------------------------------------------------------------
```



## 默认用户
id |name| password
-|-|-
0 	|root	    |123
1 	|yangjing	|123
2 	|Atomu	  |111
3 	|LiangZhaoyang	|111
4 	|WuYiming	|111
5 	|ZhangXiangyu	|111
6 	|ChenWeijie	|111
7 	|LiuJiahui	|111
8 	|SunQianying	|111
9 	|WangJianfeng	|111
10	|ZhouXingyu	|111
11	|HuangYifan	|111
12	|LiMinghui	|111
13	|DengYuhan	|111
14	|TangZhengyang	|111
15	|LinQingyang	|111
16	|GaoXiaodong	|111
17	|HuQianwen	|111
18	|JinXinyi	|111
19	|FengYunlong	|111
20	|CaoXinran	|111
21	|LiJiaming	|111
22	|ZhouYifei	|111
23	|WuYufei	|111
24	|ChenJianyu	|111
25	|XuYuhang	|111
26	|ZhangXinyi	|111
27	|WangMengjie	|111
28	|LiuXiaowei	|111
29	|HuangZhihao	|111
30	|YangKaiwen	|111
31	|ShenZhihui	|111
32	|GuoYaqi	|111
33	|TangXueqin	|111
34	|DengYuting	|111
35	|JiangYingjie	|111
36	|HuShanshan	|111
37	|YaoZhijun	|111
38	|FanXiaojing	|111
39	|MeiXiaochen	|111
40	|CaiMengxuan	|111
