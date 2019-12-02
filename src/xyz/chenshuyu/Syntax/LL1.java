package xyz.chenshuyu.Syntax;


import xyz.chenshuyu.lexical.Attribute;

import java.io.*;
import java.util.*;

public class LL1 {

    // 待规约的文本
    private String text;

    // 文法的开始符号
    private String start;

    // 文法的空字符串表示方法
    private String nullString;

    private static Set<String> terminators = new HashSet<String>() {
        {
            addAll(Attribute.getKeywords());
            addAll(Attribute.getBoundarys());
            addAll(Attribute.getOperators());
            add("IDN");
            add("INT");
            add("FLOAT");
            //add(nullString);
        }
    };// 终结符集合

    private TreeMap<String, ArrayList<String>> expression = new TreeMap<>(); // 记录文法
    private TreeMap<String, Boolean> hasrecord = new TreeMap<>();
    private Map<String, Set<String>> first = new HashMap<>(); //  记录 first 集合
    private Map<String, Set<String>> follow = new HashMap<>(); //  记录 follow 集合

    private Map<String, Map<String, String>> table = new HashMap<>(); // 记录预测分析表

    private ArrayList<String> grammer = new ArrayList<>(); // 使用 ArrayList 来存储每行读取到的 grammer

    private boolean change = false;// 用来记录follow集合是否改变

    /**
     * 读取 filename 中的文法
     *
     * @param filename 文件名
     */
    public void inputGrammer(String filename) {
        grammer.clear();
        File file = new File(filename);

        try {
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(inputReader);
            // 按行读取字符串
            String str;
            while ((str = bf.readLine()) != null) {
                grammer.add(str);
            }
            bf.close();
            inputReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获得所有表达式
    void initExpress() {
        for (String line : grammer) { // 按行处理
            String[] strs = line.split(" -> ");
            ArrayList<String> exp;
            if (expression.get(strs[0]) == null) {
                // 没有存过这个非终结符的表达式
                exp = new ArrayList<String>() {
                    {
                        add(strs[1]);
                    }
                };
            } else { // 存过就需要把之前的拿出来
                exp = expression.get(strs[0]);
                exp.add(strs[1]);
            }
            expression.put(strs[0], exp);
        }
    }

    // 获得所有非终结符的 first 集合
    void firstAll() {
        Set<String> lefts = expression.keySet();

        for (String left : lefts) {
            if (hasrecord.get(left) == null) {  // 找过了就不用
                firstExps(left);
            }
        }
    }

    //  获得非终结符的所有first集合
    private Set<String> firstExps(String left) {
        // 从文法右边的第一个字符开始看
        ArrayList<String> strs = expression.get(left); // 用空格分割
        for (String exp : strs) {
            firstOneExp(left, exp, 0);
        }
        hasrecord.put(left, true);
        return first.get(left);
    }

    // 获得右边的产生式第一个字符串为终结符,left左边的非终结符
    private void firstOneExp(String left, String exp, int i) {

        String[] items = exp.split(" ");

        if (i >= items.length) { // 超过范围，不能再后看
            System.out.println("error");
            new HashSet<String>() {
            };
            return;
        }

        String right = items[i];   // 右边的产生式第一个字符串

        Set<String> firstList; // 非终结符号对应的first集合
        if (first.get(left) == null) { // 没有记录过这个非终结符
            firstList = new HashSet<String>();
        } else { // 记录过这个终结符
            firstList = first.get(left);
        }

        if (terminators.contains(right)) {  // 文法右边的产生式第一个字符串是终结符
            firstList.add(right);
        } else { // 文法右边的产生式第一个字符串是非终结符
            Set<String> temps;
            if (hasrecord.get(right) == null) {
                temps = new HashSet<>(firstExps(right));
            } else { // 已经找过非终结符，就不用再找
                temps = first.get(right);
            }
            boolean hasnull = false; // first 集合是否存在空字符串 $
            Iterator<String> it = temps.iterator();
            while (it.hasNext()) {
                String temp = it.next();
                if (temp.equals(nullString)) {
                    hasnull = true;
                    it.remove(); // 要除开这个空字符串
                }
            }
            // 将右边第一个非终结符的fisrt集合的，除开开始符号加入
            firstList.addAll(temps);
            if (hasnull) {
                if (i + 1 < items.length) {
                    // 存在空字符串则要向下看下一个字符串
                    firstOneExp(left, exp, i + 1);
                } else {
                    // 表达式中最后一个字符串仍然有空字符串，则将空字符串加入
                    firstList.add(nullString);
                }
            }
        }
        first.put(left, firstList);

        // return first.get(left);
    }

    // 获得所有的 follow 集合
    void followAll() {
        Set<String> lefts = expression.keySet();

        while (true) {
            for (String left : lefts) { // 遍历
                followExps(left);
            }
            if (!change) {
                break;
            } else { //发生了改变，将change置位于未改变
                change = false;
            }
        }
    }

    /**
     * 找到非终结符 str 的 follow 集合
     *
     * @param str 非终结符
     * @return str 的 follow 集合
     */
    private void followExps(String str) {
        Set<String> sets;
        if (follow.get(str) == null) {
            sets = new HashSet<>();
        } else {
            sets = follow.get(str);
        }
        Set<String> setcmp = new HashSet<>(sets);// 使用setcmp用来比较前后是否变化。

        for (String left : expression.keySet()) { // 根据非终结符遍历所有表达式
            ArrayList<String> exps = expression.get(left); // 获得表达式
            if (left.equals(start)) {// 对于文法的开始符号S, 置#于FOLLOW(S)中;
                sets.add("#");
                follow.put(str, sets);
            }

            for (String exp : exps) { // 表达式
                String[] items = exp.split(" ");
                for (int i = 0; i < items.length; i++) {
                    String item = items[i];
                    if (item.equals(str)) { // 表达式的右边有这个非终结符B
                        Boolean hasA = Boolean.FALSE;
                        if (i + 1 == items.length) {
                            hasA = true;
                        } else { // 若A→αBβ是一个产生式, 则把FIRST(β)\{ε}加至FOLLOW(B)中;
                            hasA = firstofStrs(exp, i + 1, sets, hasA);
                        }
                        if (hasA && !str.equals(left)) { // follow 不是它自己
                            // 若A→αB是一个产生式, 或A→αBβ是一个产生式而β => ε (即 ε∈FIRST(β) ), 则把FOLLOW(A)加至FOLLOW(B)中.
                            if (follow.get(left) != null) {
                                sets.addAll(follow.get(left));// 记录下依赖集
                            }
                        }
                        // 匹配上非终结符则不用再往后遍历
                        break;
                    }
                }
            }
        }
        if (!equals(sets, setcmp)) {
            change = true; // 发生了改变
            follow.put(str, sets);
        }
    }

    /**
     * 获得line这个字符串组S1S2S3...Sn中第i个字符串开始的SiSi+1...Sn的 first 集合
     * <p>
     * 对文法G的任何符号串α=X1X2...Xn构造集合FIRST(α)
     * (1)首先置FIRST(α)= FIRST(X1)\{}.
     * (2)如果对任何j, j∈[1, i-1], ∈FIRST (Xj), 则把FIRST(Xj)\{}加入到FIRST(α)
     * 中.特别是,若所有的FIRST(Xj)均含有 , j=1,2,...,n, 则把加到FIRST(α)中.
     *
     * @param line
     * @param i
     * @param sets
     * @param hasA
     * @return
     */
    private Boolean firstofStrs(String line, int i, Set<String> sets, Boolean hasA) {
        //Set<String> sets = new HashSet<>();
        // 若A→αBβ是一个产生式, 则把FIRST(β)\{ε}加至FOLLOW(B)中
        String[] items = line.split(" ");
        int j;
        for (j = i; j < items.length; j++) {
            // B有下一位
            String next = items[j];
            if (next.equals(nullString)) { // 如果是空字符串
                sets.add(next);
                return true;
            } else if (terminators.contains(next)) { // 终结符的 first 集合是自己
                sets.add(next);
                return hasA;// 遇到终结符不用再往下看
            } else { // 非终结符的 first 集合通过之前求的 first 集合获得

                Set<String> temps = new HashSet<>(first.get(next));
                boolean hasnull = false; // first 集合是否存在空字符串 $
                Iterator<String> it = temps.iterator();
                while (it.hasNext()) {
                    String temp = it.next();
                    if (temp.equals(nullString)) {
                        hasnull = true;
                        it.remove(); // 要除开这个空字符串
                    }
                }
                // 首先置FIRST(α)= FIRST(X1)\{$}.
                sets.addAll(temps);
                if (hasnull) { // 如果对任何j, j∈[1, i-1], $ ∈FIRST (Xj), 则把FIRST(Xj)\{$}加入到FIRST(α)
                    if (j + 1 == items.length) {
                        // 表达式中最后一个字符串仍然有空字符串，则将follow(A)加入
                        hasA = true;
                    }
                } else {
                    // 没有空，就不用看下一个字符串
                    break;
                }
            }
        }
        return hasA;
    }

    /**
     * 获得预测分析表
     */
    void getTable() {
        Set<String> lefts = expression.keySet();

        for (String left : lefts) {
            getOneline(left);
        }
    }

    /**
     * 获得某一个非终结符的预测分析表的那一行
     * @param left 非终结符
     */
    private void getOneline(String left) {
        ArrayList<String> exps = expression.get(left); // 获得所有表达式
        Map<String, String> line = new HashMap<>();
        for (String exp : exps) { // 对于每一个表达式
            Set<String> sets = new HashSet<>();
            Boolean hasnull = firstofStrs(exp, 0, sets, false);
            if (hasnull || exp.equals(nullString)) { // 空字符串，则看左边字符串的follow集合
                sets.addAll(follow.get(left));
            }
            for (String key : sets) {
                if (line.containsKey(key)) { // 已经存过这个终结符，冲突
                    System.out.println(left + "|" + key + "|" + exp + "|" + line.get(key));
                } else { // 没有存过，存进去
                    line.put(key, exp);
                }
            }
        }
        table.put(left, line);
    }

    /**
     * 根据预测分析表进行文法分析
     */
    void analyze() {
        Stack<String> signs = new Stack<>();
        signs.push("#");
        signs.push(start);
        String[] strs = text.split(" |\r\n");
        for (int i = 0; i < strs.length; i++) {
            String str = strs[i];
            String top = signs.peek(); // 获得栈顶元素
            if (top.isEmpty()) {
                System.out.println("栈已空");
            }
            System.out.print("\33[29;4m符号栈：\33[34;4m" + stacktoStr(signs) + "    ");
            if (top.equals(str)) {
                if (top.equals("#")) {
                    System.out.println("\33[28;4m大公告成，匹配成功辽～");
                    if (i < strs.length - 1) {
                        System.out.println("但是后面还有内容");
                        break;
                    }
                }
                System.out.println("\33[33;4m移进：" + top);
                signs.pop(); // 推出栈顶
            } else if (table.get(top) == null) { // 语法错误
                System.out.println("\33[31;4m错误：栈顶：" + top + " 输入串的第一个字符串：" + str);
                break;
            } else if (table.get(top).get(str) == null) { // 获得空项
                System.out.println("\33[31;4m错误：栈顶：" + top + " 输入串的第一个字符串：" + str);
                break;
            } else {
                String exp = table.get(top).get(str); // 获得产生式
                System.out.println("\33[28;4m所用产生式：\33[35;4m" + top + " -> " + exp);
                signs.pop(); // 先将栈顶推出
                String[] items = exp.split(" ");
                for (int j = items.length - 1; j >= 0; j--) {
                    String item = items[j]; // 逆序推入
                    if (!item.equals(nullString)) {
                        // 不是空字符串才入栈
                        signs.push(item);
                    }
                }
                --i; // 并没有移动
            }
        }
    }

    private String stacktoStr(Stack<String> stack) {
        String stackstr = stack.toString();
        String[] items = stackstr.split(",");
        StringBuffer result = new StringBuffer("");
        for (String item : items) {
            result.append(item);
        }
        return result.toString();
    }

    /**
     * 比较集合的是否相同，如果包含相同元素则相同
     *
     * @param set1
     * @param set2
     * @return
     */
    private boolean equals(Set<?> set1, Set<?> set2) {
        if (set1 == null || set2 == null) {//null就直接不比了
            return false;
        }
        if (set1.size() != set2.size()) {//大小不同也不用比了
            return false;
        }
        return set1.containsAll(set2);//最后比containsAll
    }

    public Set<String> getTerminators() {
        return terminators;
    }


    public String getStart() {
        return start;
    }

    void setStart(String start) {
        this.start = start;
    }

    public String getNullString() {
        return nullString;
    }

    void setNullString(String nullString) {
        terminators.remove(this.nullString);
        this.nullString = nullString;
        terminators.add(nullString);
    }

    void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
