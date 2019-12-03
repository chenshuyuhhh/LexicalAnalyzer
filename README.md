## Lexical 使用说明

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

## Syntax 使用说明

### 1. 执行

#### 1.1 进入jar包所在目录



#### 1.2 使用执行jar包的命令运行可执行文件 LexicalAndSyntax.jar

#### 1.3 出现三个选项，根据自己的情况选择执行，选择A或者B执行完毕之后会再次循环



### 2.选择

- A. 使用已有测试用例

会直接使用内置的文法和测试用例，输出词法分析的结果和语法分析的每一步

- B. 使用自己的测试用例

需要再输入测试用例所在绝对路径

- C. 结束

退出程序的执行。



### 3. 输出

- 会输出构造预测分析表的if else冲突和词法分析的结果
- 最后输出预测分析的每一步的符号栈和动作
  - 归约：用产生式
  - 移进：终结符匹配
  - 语法分析成功或者失败
  部分截图如下：
  

`ps` 除了使用命令行执行jar包，也可以用IDEA等JAVA IDE打开源代码运行
