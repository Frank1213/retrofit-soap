# retrofit-soap
retrofit优雅调用webservice接口
MainActivity里面有三个方法:
getSupportCityByFirst();// 通过省份获取城市代码,无封装
getSupportCityBySecond();// 通过省份获取城市代码,截取数据
getSupportCityByThrid()// 通过省份获取城市代码,截取数据,中文乱码de解决的方案-->入参赋值之前手动转换一次
