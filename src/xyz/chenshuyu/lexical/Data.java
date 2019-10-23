package xyz.chenshuyu.lexical;

public class Data {
    private String content;// 字符串内容
    private String kind; // 字符串类型

    public Data(String content, String kind) {
        this.content = content;
        this.kind = kind;
    }

    public String getContent() {
        return content;
    }

    public String getKind() {
        return kind;
    }
}
