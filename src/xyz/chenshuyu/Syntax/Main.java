package xyz.chenshuyu.Syntax;

import xyz.chenshuyu.lexical.Attribute;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // System.out.println(Main.class.getResource(""));
        String content = "c--grammer";
        while (true) {
            System.out.println("\33[29;4mA. 使用已有测试用例\r\nB. 使用自己的测试用例\r\nC. 结束");
            Scanner sc = new Scanner(System.in);
            String answer = sc.next();
            if (answer.equals("A")) {
                testMediu();
            } else if (answer.equals("B")) {
                System.out.println("请输入你的测试用例文件所在绝对路径");
                content = sc.next();
                testMediuFile(content);
            } else if (answer.equals("C")) {
                break;
            } else {
                System.out.println("你的输入错误");
            }
            System.out.print("\33[29;4m\r\n");
        }
        // lextosyn("
        // ");
    }


    private static void test() {
        LL1 ll1 = new LL1();
        Scanner sc = new Scanner(System.in);
//        System.out.println("请输入你的文法所在路径");
//        String gram = sc.next();
        String gram = "/Users/chenshuyu/IdeaProjects/LexicalAnalyzer/src/xyz/chenshuyu/Syntax/c--grammer";
        getMsg(gram, ll1);
//        System.out.println("请输入你想要匹配的文件路径：");
////        String filename = sc.next();
        String filename = "src/xyz/chenshuyu/data/test.txt";
        ll1.setText(Attribute.testFiletoStr(filename));
        ll1.analyze();
        System.out.println("hhhhhh");
    }

    private static void testMediu() {
        LL1 ll1 = new LL1();
        String gram = "c--grammer";
        getMsg(gram, ll1);
        String content = "test.txt";
        ll1.setText(Attribute.testFiletoStr(content) + "#");
        ll1.analyze();
    }

    private static void testMediuFile(String content) {
        LL1 ll1 = new LL1();
        String gram = "c--grammer";
        getMsg(gram, ll1);
        ll1.setText(Attribute.tFiletoStr(content));
        ll1.analyze();
    }


    private static void testSmall() {
        LL1 ll1 = new LL1();
        String gram = "/Users/chenshuyu/IdeaProjects/LexicalAnalyzer/src/xyz/chenshuyu/Syntax/test";
        getMsg(gram, ll1);
        ll1.setText("IDN * IDN + IDN #");
        ll1.analyze();
        System.out.println("hhhhhh");
    }

    private static String lextosyn(String filename) {
        return Attribute.testFiletoStr(filename) + "#";
    }


    private static void getMsg(String file, LL1 ll1) {
        ll1.inputGrammer(file);
        ll1.setStart("S");
        ll1.setNullString("$");
        ll1.initExpress();
        ll1.firstAll();
        ll1.followAll();
        ll1.getTable();
    }
}
