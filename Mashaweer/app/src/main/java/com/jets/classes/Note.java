package com.jets.classes;

/**
 * Created by toqae on 3/24/2017.
 */

public class Note {

    private String content;
    private boolean checked;

    public Note(String content, boolean checked) {
        this.content = content;
        this.checked = checked;
    }

    public Note(String content) {
        this.content = content;
        this.checked = false;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "Note{" +
                "content='" + content + '\'' +
                ", checked=" + checked +
                '}';
    }
}
