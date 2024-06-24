# UserCenter_Backend用户中心系统-后端部分
鱼皮知识星球项目《用户中心系统》和《伙伴匹配系统》中的后端部分

## 功能：
- 登录/注册/注销
- 用户信息增删改查
- 队伍信息增删改查
- 加入/退出队伍
- 标签匹配机制
- 推荐用户列表
## 技术亮点/学习心得
- 一些好用的插件/库：
	- 用MybatisX根据数据库自动生成实体类
	- 调用Mybatis-plus中简单的sql操作
	- sonarLint代码规范检查插件
	- apache commons utils工具类，加密工具、检查是否为空函数
	- auto filling java call arguments插件自动填充请求参数
    - DigestUtils密码加密工具库
    - easyexcel好用的excel读写库
    - gson json解析库
    - swagger2接口文档生成工具
- IDEA使用技巧
	- 自动生成测试类的快捷键：alt+enter **create test**
	- 自动生成实现类的快捷键：alt+enter
- 性能调优策略
	- 尽量减少非必要的数据库检索
 	- 实践对比在sql检索数据还是在内存中检索数据	 
- 功能亮点：
	- Redis存储Session实现分布式登录
   	- 定时任务将热点信息redis预缓存，并使用分布式锁防止重复执行
	- KNN编辑距离算法 	
 ## 写在最后
 学习之路、任重道远、继续加油吧！
