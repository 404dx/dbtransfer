数据库数据转换工具

根据不同环境执行不同的脚本
脚本执行需要在后面携带路径参数，
由于时间原因该工具没有做太多的配置文件校验，所以谨慎使用，尽量按照规则来使用，当然数据不会出现太大的问题，
若某表出现转换异常则会停止该表的数据转换并抛出对应的异常信息在logs文件夹当中

配置介绍:
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