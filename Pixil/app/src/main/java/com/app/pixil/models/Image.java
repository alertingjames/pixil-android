package com.app.pixil.models;

import android.text.Layout;
import android.view.View;

import java.io.File;

public class Image {
    int idx = 0;
    String path = "";
    File file = null;
    boolean isSelected = false;
    int copyCount = 1;
    View view = null;

    public Image(){

    }

    public void setView(View view) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setCopyCount(int copyCount) {
        this.copyCount = copyCount;
    }

    public int getCopyCount() {
        return copyCount;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getIdx() {
        return idx;
    }

    public File getFile() {
        return file;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
