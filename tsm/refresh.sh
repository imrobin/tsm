echo '清理工作'
mvn clean eclipse:clean

echo '构建eclipse工程环境,下载JAR源代码,将项目订制为web项目'
mvn eclipse:eclipse -DdownloadSources=false -Dwtpversion=2.0

echo '生成工程依赖JAR包,放到WEB-INF/lib'
mvn dependency:copy-dependencies

echo 'MAVEN工程构建完成'
