package cn.yizems.fileexplore.config;

import java.io.File;
import java.io.Serializable;

public class DirConfig implements Serializable {
    private String name;
    private String path;


    public DirConfig() {
    }

    public DirConfig(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public File getRealDir() {
        if ("/".equals(path)) {
            String rPath = new File("temp").getAbsolutePath();
            return new File(rPath.substring(0, rPath.lastIndexOf(File.separator)));
        }
        return new File(path);
    }
}
