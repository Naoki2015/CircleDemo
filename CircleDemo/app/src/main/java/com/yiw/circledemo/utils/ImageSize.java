package com.yiw.circledemo.utils;

import java.io.Serializable;

/**
 * Created by LewisMS on 7/23/2016.
 */
public class ImageSize implements Serializable {

    private int width;
    private int height;

    public ImageSize(int width, int height){
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
