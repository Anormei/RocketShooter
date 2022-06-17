package com.ar_co.androidgames.rocketshooter.framework.assetmanager;

import android.graphics.Rect;

public class Node{

    private final Node[] child;
    private Texture t;
    Rect r;

    public Node() {
        child = new Node[2];
    }

    public Node(int x, int y, int width, int height) {
        r = new Rect(x, y, width, height);
        child = new Node[2];

    }

    public Node[] insert(Texture t, int padding) {
        if(r.width() < t.width + padding * 2  || r.height() < t.height + padding * 2) {
            return null;
        }

        this.t = t;
        t.x = r.left + padding;
        t.y = r.top + padding;

        int dw = r.width() - t.width;
        int dh = r.height() - t.height;

        if(dw > dh) {
            child[0] = new Node(t.x + t.width, r.top, r.right, r.bottom);
            child[1] = new Node(r.left, t.y + t.height, t.x + t.width, r.bottom);
        }else {
            child[0] = new Node(t.x + t.width, r.top, r.right, t.y + t.height);
            child[1] = new Node(r.left, t.y + t.height, r.right, r.bottom);
        }

        return child;
    }


    public boolean isFilled() {
        return child[0] != null && child[1] != null;
    }
}