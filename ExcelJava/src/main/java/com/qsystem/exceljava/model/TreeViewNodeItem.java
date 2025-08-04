package com.qsystem.exceljava.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TreeViewNodeItem implements Serializable {
    private String name;
    private List<TreeViewNodeItem> children;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TreeViewNodeItem> getChildren() {
        return children;
    }

    public TreeViewNodeItem() {
        children = new ArrayList<>();
    }
}