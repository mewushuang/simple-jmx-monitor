package com.van.monitor.systemInfo;

/**
 * Created by van on 2016/12/1.
 */
public class LogLine {
    private int lineOffset;
    private String content;

    public LogLine(int lineOffset, String content) {
        this.lineOffset = lineOffset;
        this.content = content;
    }

    public int getLineOffset() {
        return lineOffset;
    }

    public void setLineOffset(int lineOffset) {
        this.lineOffset = lineOffset;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
