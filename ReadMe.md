# MyAgent

## how to use

```shell
mvn assembly:assembly
```

然后在idea里面添加vmoptions

```
-javaagent:/Users/LvSheng/code/github/MyAgent/target/MyAgent-1.0-SNAPSHOT-jar-with-dependencies.jar
-Dmyagent.base.package=com.alibaba.ls
```

myagent.base.package用于指定代码增强的路径

javassist开发文档 [javassist tutorial](http://www.javassist.org/tutorial/tutorial.html)