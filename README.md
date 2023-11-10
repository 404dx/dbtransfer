# 数据库数据转换

#### 介绍
数据库数据转换工具  
一个小型的数据库迁移工具，目前只支持mysql与oracle 的数据库数据迁移,也可以mysql与oracle相互迁移数据，只能迁移已存在的表，未存在的不会创建  
因为公司产品升级客户需要升级版本，但是表结构与数据库都不一样，但是业务是一致需要老版本的数据，所以做了一个小工具,  
可以配置两个不同数据库表的表关系与字段关系，进行数据迁移。
#### 安装教程

mvn clean package

#### 使用说明

1. 将TransferConfig.xml按照说明配置好
2. 然后执行transfer脚本。(transfer TransferConfig.xml)  

#### 配置介绍
标签:TransferConfig
   
    from: 源数据的数据库数据源名称
    to:  源数据需要转至的目标数据源名称
    type: 转换类型，有两种：1. transfer 直接将数据转换至目标数据库 2. sql 将源数据的数据库数据转换成sql输出到文件
    path: 如果上面的type配置的是sql 则需要填输出路径，否则就不需要
    isAll: 是否转换源数据库所有的表，若只转换配置当中的数据则设置false反之true
标签:datasources 数据源配置
   
    source.name 数据源名称 用于from 与 to 参数的标识，不可重复
    source.type 数据源类型 只能是oracle 与 mysql
    source.url 数据库url
    source.driver 数据库驱动
    source.username 用户名 
    source.password 密码
标签:relations 两个数据源当中的表关系配置若表名称不一样，表字段不一样则可以在此处做匹配
    
    onlyConfig 改配置表示是否只是用已配置好关系的字段，若为false则配置了表字段关系则使用已配置的若未配置则与源表字段名称一样
    relation.source 源数据(from)的表名称
    relation.target 目标数据源(to)表名称
    relation.fields.field.source 源数据(from)的表字段名称
    relation.fields.field.target 目标数据源(to)的表字段名称
