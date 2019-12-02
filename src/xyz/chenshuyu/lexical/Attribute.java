package xyz.chenshuyu.lexical;

import xyz.chenshuyu.Syntax.LL1;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

public class Attribute {

    public static void main(String[] args) {

        // 多个参数输入
//        for (String file : args) {
//            testFile(file);
//        }
        //   testFiletoStr("src/xyz/chenshuyu/data/test.txt");
        testFile("src/xyz/chenshuyu/data/test.txt");
//        testFile("src/xyz/chenshuyu/data/input.txt");
    }


    private static final Set<String> keywords = new HashSet<String>() {
        {
            add("while");
            add("break");
            add("continue");
            add("for");
            add("if");
            add("else");
            add("float");
            add("int");
            add("void");
            add("return");
            add("main");
        }
    }; // 关键字集合

    private static final Set<String> operators = new HashSet<String>() {
        {
            add("+");
            add("-");
            add("*");
            add("/");
            add("=");
            add(">=");
            add("==");
            add("<=");
        }
    }; // 算符集合

    private static Set<String> boundarys = new HashSet<String>() {
        {
            add("(");
            add(")");
            add("{");
            add("}");
            add(";");
        }
    }; // 界符集合

    private static Set<String> terminators = new HashSet<String>() {
        {
            addAll(Attribute.getKeywords());
            addAll(Attribute.getBoundarys());
            addAll(Attribute.getOperators());
            //add(nullString);
        }
    };// 终结符集合


    /**
     * C语言规定，标识符只能由字母（A~Z, a~z）、数字（0~9）和下划线（_）组成，
     * 并且第一个字符必须是字母或下划线，不能是数字。
     */
    private static final String isIdentifer = "IDN"; // 标识符

//    private static final String isConstant = "constant"; // 常数

    private static final String isError = "isError"; // 非法表达

    private static final String isFloat = "FLOAT"; // 浮点数

    private static final String isInt = "INT"; // 整数

    private static final String isBoundary = "boundary"; // 界符

    private static final String isOperator = "operator";  // 算符

    private static final String isKeyword = "keyword";  // 关键字


    /**
     * 根据字符数组进行词法分析
     *
     * @param chars 文本文件对应的字符数组，在外面做去空格
     * @return
     */
    public static ArrayList<Data> analyzer(char[] chars) {
        // 初始化分析结果map
        ArrayList<Data> result = new ArrayList<Data>();

        int ln = chars.length;
        for (int i = 0; i < ln; i++) {

            // 取出字符
            char c = chars[i];
            StringBuilder str = new StringBuilder("" + c);

            // 先判断是否是常数
            if (isNumber(c) || c == '.') { // 如果是数字，可能是整数或者浮点数

                /*
                 * 浮点数的判断最为复杂
                 * 指数形式，如3.45e6,7.e-6 7e-6;
                 * 十进制小数形式，如1.08，.98，18.；保证“.”前或者后必然有数字。
                 * 指数形式可以在十进制形式后面加en。
                 * 最后加f/F，只有带前面两种形式的才能加这个尾缀
                 * float a = 10 这个10算int
                 * 10f 、10d、10.0d 、.算词法错误
                 */
                // 浮点数.号前后的数字部分，至少有一个部分有数字
                boolean beforedian = false; // 小数点前是否有数字，有数字则true
                boolean afterdian = false; // 小数点后是否有数字

                boolean hase = false; // 是否有e
                boolean aftere = true; // e后面应当有数字,默认合法
                boolean hasDian = false;// 是否有小数点

                // 浮点数小数点前面的数字部分
                if (isNumber(c)) {
                    // 如果c是一个数字，小数点前面肯定有数字
                    beforedian = true;
                    // 继续往后找数字
                    while (true) {
                        if (++i >= ln) break; // 如果越界，直接跳出循环
                        c = chars[i]; // 没有越界，向后访问
                        if (isNumber(c)) {
                            // 是数字再加入字符串
                            str.append(c);
                        } else {
                            // 不是数字跳出循环
                            break;
                        }
                    }
                }
                // 浮点数.
                // 保证“.”前或者后必然有数字
                // 这个c可能是第一次读到的，也可能是刚开始是数字，往后读
                if (c == '.') {
                    if (beforedian) str.append(c);
                    hasDian = true;
                    // 往后继续识别数字，可能没有
                    while (true) {
                        if (++i >= ln) break; // 如果越界，直接跳出循环
                        c = chars[i]; // 没有越界，向后访问
                        if (isNumber(c)) {
                            afterdian = true;
                            // 是数字再加入字符串
                            str.append(c);
                        } else {
                            // 不是数字跳出循环
                            break;
                        }
                    }
                    // 回退
                }

                // 浮点数应当考虑.指数形式，如3.45e6
                // 这个c=='e' 可能是经过了第二个if (c == '.') ，也没有经过小数点
                // 如果没有经过小数点，需要前面有数字
                if ((beforedian || afterdian) && c == 'e') {
                    str.append(c);
                    hase = true;
                    // 往后继续识别数字，有才合法
                    boolean judge = false; // 标示是否合法，默认不合法
                    if (++i < ln) {
                        c = chars[i];
                    }
                    if (c == '-') {
                        // 指数可能是负数，就往后读
                        str.append(c);
                    } else {
                        --i;
                    }
                    while (true) {
                        // 往后识别数字
                        if (++i >= ln) break; // 如果越界，直接跳出循环
                        c = chars[i]; // 没有越界，向后访问
                        if (isNumber(c)) {
                            // e后面是否合法
                            judge = true;
                            // 是数字再加入字符串
                            str.append(c);
                        } else {
                            // 不是数字跳出循环
                            break;
                        }
                    }
                    if (!judge) {
                        aftere = false;
                    }
                    // 数字读取完毕，回退
                }

                // 没有点，有数字，没有e，则是整数
                if (!hasDian && beforedian && !hase) {
                    result.add(new Data(str.toString(), isInt));
                }
                // 保证e的合法性的前提下
                // 小数点后面有数字也可以
                // 小数点后面没有数字，则需要前面有数字和小数点
                // 小数点后面没有数字，也没有小数点，则需要有e
                else if (aftere && (afterdian || (beforedian && hasDian) || (beforedian && hase))) {
                    if ((c == 'f' || c == 'F')) {
                        str.append(c);
                        ++i;
                    }
                    result.add(new Data(str.toString(), isFloat));
                } else {
                    result.add(new Data(str.toString(), isError));
                }
                // 整体回退一格，便于进行下一个字符识别
                --i;
            }
            /*
             * 标识符的判断
             * 标识符由字母(A-Z,a-z)、数字(0-9)、下划线“_”组成,
             * 并且首字符不能是数字,但可以是字母或者下划线
             * 字符串可能是关键字，所以再满足标识符组成元素的条件下，先判断是否为关键字
             */
            else if (isCharAnd_(c)) {

                while (true) {
                    if (++i >= ln) {
                        --i;
                        break;
                    } // 越界直接跳出
                    c = chars[i];
                    // 如果是字母(A-Z,a-z)、数字(0-9)、下划线“_”
                    if (isCharAnd_(c) || isNumber(c)) {
                        str.append(c);
                    } else {
                        --i;
                        break;
                    }
                }
                String temp = str.toString();
                if (keywords.contains(temp)) {
                    result.add(new Data(temp, isKeyword));
                } else {
                    result.add(new Data(temp, isIdentifer));
                }
                // 回退
            }
            //  判断是运算符还是界符
            else if (c != ' ') {
                // 先判断运算符
                // 先从特殊的开始判断
                if (c == '=' || c == '>' || c == '<') {

                    // special：">=" "==" "<="
                    if (++i < ln) {
                        // 如果能往后取一位
                        c = chars[i];
                        // 是不是=，因为有==运算符和=运算符
                        if (c == '=') {
                            // >=,<=,==
                            str.append(c);
                            result.add(new Data(str.toString(), isOperator));
                            // 实际往后取了，需要加上去
                            ++i;
                        } else if (chars[i - 1] == '=') { // i-1为上一位
                            // = 运算符，不是==
                            result.add(new Data(str.toString(), isOperator));
                        } else {
                            // 不是>=,<= ,只有>或者<
                            result.add(new Data(str.toString(), isError));
                        }
                        // 再减回去
                        --i;
                    } else {
                        // 不能就直接是=
                        result.add(new Data(str.toString(), isOperator));
                    }
                } else if (operators.contains("" + c)) { // 别的操作符，均为一位，可直接判断
                    result.add(new Data(str.toString(), isOperator));
                }
                // 不是算符，就可能是界符
                else if (boundarys.contains("" + c)) {
                    result.add(new Data(str.toString(), isBoundary));
                } else {
                    result.add(new Data(str.toString(), isError));
                }
            }
        }

        return result;
    }


    /**
     * c是否是数字
     *
     * @param c 待判断的字符
     * @return 如果是数字返回true
     */
    private static boolean isNumber(char c) {
        return '0' <= c && c <= '9';
    }

    /**
     * 判断c是否是字母或者下划线
     *
     * @param c 待判断的字符
     * @return 如果是字母或者下划线返回true
     */
    private static boolean isCharAnd_(char c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || c == '_';
    }

    public static String processFile(String content) {
        //去掉/* */型注释
        String[] strs = content.split("/\\*");
        StringBuilder newContent = new StringBuilder(strs[0]);
        for (int i = 1; i < strs.length; i++) {
            String[] temp = strs[i].split("\\*/");
            // 只有第一个*/ 算
            for (int j = 1; j < temp.length; j++) {
                newContent.append(temp[j]);
            }

        }

        // 去掉//型注释，去掉空格,换行
        return newContent.toString().
                replaceAll("//.*\r\n", "").
                replaceAll("\n", " ").
                replaceAll("\r", " ");
    }

    public static void testFile(String filename) {
        File file = new File(filename);
        try {
            InputStream is = new FileInputStream(file);
            int iAvail = is.available();
            byte[] bytes = new byte[iAvail];
            is.read(bytes);
            // 获取文件内容
            String content = new String(bytes);
            content = processFile(content);
            ArrayList<Data> datas = analyzer(content.toCharArray());
            for (Data data : datas) {
                System.out.println(data.getContent() + ": " + data.getKind());
            }
            System.out.println();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String testFiletoStr(String filename) {
        File file = new File(filename);
        StringBuffer out = new StringBuffer("");
        try {
            InputStream is = new FileInputStream(file);
            int iAvail = is.available();
            byte[] bytes = new byte[iAvail];
            is.read(bytes);
            // 获取文件内容
            String content = new String(bytes);
            content = processFile(content);
            ArrayList<Data> datas = analyzer(content.toCharArray());
            for (Data data : datas) {
                if (terminators.contains(data.getContent())) {
                    if (data.getContent().equals("main")) {
                        out.append("IDN ");
                    } else {
                        out.append(data.getContent() + " ");
                    }
                } else {
                    out.append(data.getKind() + " ");
                }
                System.out.println(data.getContent() + ": " + data.getKind());
            }
            System.out.println();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return out.toString();
    }

    public static Set<String> getKeywords() {
        return keywords;
    }

    public static Set<String> getOperators() {
        return operators;
    }

    public static Set<String> getBoundarys() {
        return boundarys;
    }

    /**
     * 测试浮点数的识别
     */
    public static void testFloat() {
        ArrayList<String> arrayList = new ArrayList<String>() {
            {
//                add(".");
//                add("007.");
//                add(".78");
//                add("0.1969");
//                add("1973");
//                add("78e-6");
//                add("9e");
//                add("129e9");
//                add("");
//                add("293.e-1");
//                add("249.e10");
//                add("291.e");
//                add(".829e12");
//                add(".29e-2");
//                add(".927e");
//                add("29.9e;");
//                add("292.9e");
//                add("193.29e-19");
//                add("8.1e2");
//                add("190");
//                add(".f");
//                add("007.F");
//                add(".78f");
//                add("0.1969F");
//                add("1973f");
//                add("78e-6F");
//                add("9ef");
//                add("129e9f");
//                add("f");
//                add("293.e-1F");
//                add("249.e10f");
//                add("291.eF");
//                add(".829e12f");
//                add(".29e-2FF");
//                add(".927ef");
//                add("29.9e;f");
//                add("292.9ef");
//                add("193.29e-19F");
//                add("8.1e2F");
//                add("190f");
//                add(".e-1");
                add("10f");
            }
        };
        System.out.println(analyzer("10f".toCharArray()).get(1).getKind());

        for (String temp : arrayList) {
            // System.out.println(analyzer(temp.toString()));
        }
    }
}
