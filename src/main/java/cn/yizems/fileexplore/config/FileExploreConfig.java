package cn.yizems.fileexplore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "file-explore")
public class FileExploreConfig {

    private boolean open;
    private List<DirConfig> dirs;

    public FileExploreConfig() {
    }

    public FileExploreConfig(boolean open, List<DirConfig> dirs) {
        this.open = open;
        this.dirs = dirs;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public List<DirConfig> getDirs() {
        return dirs;
    }

    public void setDirs(List<DirConfig> dirs) {
        this.dirs = dirs;
    }

    public File getRealDir(String path) {
        for (DirConfig dir : dirs) {

            String matchPath = path;
            String relativePath = "";
            if (path.contains("/")) {
                matchPath = path.substring(0, path.indexOf("/"));
                relativePath = path.substring(path.indexOf("/") + 1);
            }

            if (matchPath.equals(dir.getName())) {
                if (relativePath.equals("")) {
                    return dir.getRealDir();
                } else {
                    return new File(dir.getRealDir(), relativePath);
                }
            }
        }
        return null;
    }

}
