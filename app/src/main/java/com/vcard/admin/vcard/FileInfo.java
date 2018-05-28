package com.vcard.admin.vcard;

/**
 * explain
 * Created by mzw on 2017/4/6.
 */
public class FileInfo implements Comparable<FileInfo>{
    private String info;
    private String name;
    private String path;
    private int image;
    private int rank; //排序

    public FileInfo(String name, String path, int image,String info,int rank) {
        this.name = name;
        this.path = path;
        this.image = image;
        this.info = info;
        this.rank = rank;
    }

    public String getInfo() {
        return info;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getImage() {
        return image;
    }

    public int getRank() {
        return rank;
    }

    @Override
    public int compareTo(FileInfo o) {
        return this.getRank() - o.getRank();
    }
}
