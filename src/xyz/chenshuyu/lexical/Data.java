package xyz.chenshuyu.lexical;

public class Data {
    private String content;
    private String kind;

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
