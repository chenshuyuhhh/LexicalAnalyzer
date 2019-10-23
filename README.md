### 使用说明

#### 1. 命令行执行
`LexicalAnalyzer.jar` 包放在本项目的out/artifacts/LexicalAnalyzer_jar目录下。
在 terminal 中执行如下语句
```shell
java -jar LexicalAnalyzer.jar file1 file2 ... filen
```
- file1、file2、filen 为文件路径参数，本项目将会对 file1、file2、filen 进行词法分析。

#### 2. 执行效果截图
![ref]("scr/xyz/chenshuyu/data/make.jpg)

#### 3. IDE中运行执行
找到 scr/xyz/chenshuyu/lexical目录下的Attribute类，第一个psvm方法即为程序入口，可以用scr/xyz/chenshuyu/data目录下的input.txt和test.txt作为测试文件。
在main方法中添加如下代码即可。
```shell
  testFile("src/xyz/chenshuyu/data/test.txt");
  testFile("src/xyz/chenshuyu/data/test.txt");
```
默认为文件作为执行jar包的参数传入运行。

#### 4.其他文档 
作业要求见根目录下的文档：`大作业第一部分 词法分析.pdf`

程序设计文档见根目录下的文档 `词法分析器器的完整设计`