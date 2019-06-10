phBill

																																									2019-6-9 ver. 1

本工程旨在实现一个易于操作的个人记账软件，当前版本 ver. 1.0

实现功能如下：

- 账号密码登陆
- 柱状图分析一个月内的收支情况
- 基于数据库的添加/删除收支记录
- 自动定位
- 账单附加日期，说明，(可选图片)。

技术背景

环境配置

- 操作系统环境：windows
- 开发环境：android studio
- 代码运行环境：AVD Nexus 5X API 29 x86
- JDK ≥ 1.8
- 使用 androidx
- SDK ≥ 25，推荐使用 28
- 使用国产手机，避免出现定位相关功能无法正常使用的情况。

组件列表

- Hellocharts library：提供图表支持 
- Andpermission：提供国产手机的权限管理
- Smartfresh：提供刷新的界面效果
- BRVAH：提供了列表上下拉动，下拉获取更多的机制
- FlexibleDivider：控制 RecycleView 中的 item
- Bomb：提供了android手机的云服务相关 API

模块设计说明

activity设计

  模块名             	简要介绍                                    
  sign up activity	提供登陆时的用户/密码名输入，以及各个情况的错误输出              
  login activity  	提供注册时的界面以及密码重复，用户名为空等错误的警告              
  main activity   	作为APP的核心 activity，作为主界面                 
  my fragment     	继承 class Fragment，附加：自动隐藏无用模块的功能，一组关于进程互锁的变量
                  	                                        
                  	                                        
                  	                                        

main activity：


