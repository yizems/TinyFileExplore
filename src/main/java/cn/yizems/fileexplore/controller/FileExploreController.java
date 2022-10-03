package cn.yizems.fileexplore.controller;

import cn.yizems.fileexplore.config.DirConfig;
import cn.yizems.fileexplore.config.FileExploreConfig;
import cn.yizems.fileexplore.util.ServletUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/file-explore")
public class FileExploreController {
    @Autowired
    private FileExploreConfig config;


    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String res(@RequestParam(value = "path", required = false) String path) throws IOException {

        if (!config.isOpen()) {
            return "文件浏览器已关闭";
        }

        if (!StringUtils.hasLength(path)) {
            return responseConfigDir();
        }

        if (!"/".equals(path)) {
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
        }

        File realDir = config.getRealDir(path);

        if (realDir == null || !realDir.exists()) {
//            ServletUtils.getResponse().sendError(404, "path is not exists");
            return "path is not exists";
        }
        // 如果是文件,直接下载
        if (realDir.isFile()) {
            return ServletUtils.download(realDir);
        }
        // 如果是目录,则展示目录
        String outHtml = getOutHtml();
        outHtml = setCurrentDir(path, outHtml);

        // 获取父级目录,如果是根目录,则显示本身
        String parentDir = "";
        if (path.contains("/")) {
            parentDir = path.substring(0, path.lastIndexOf("/"));
        } else {
            parentDir = "";
        }

        outHtml = setParentDir(outHtml, parentDir);

        outHtml = setDirHtml(getDirContent(path, realDir), outHtml);
        return outHtml;
    }

    private static String setCurrentDir(String path, String outHtml) {
        outHtml = outHtml.replace("{CURRENT_DIR}", path);
        return outHtml;
    }

    private static String setParentDir(String outHtml, String parentDir) {
        return outHtml.replace("{PARENT_DIR}", ServletUtils.getRequest().getRequestURL() + "?path=" + parentDir);
    }

    private static String setDirHtml(String html, String outHtml) {
        return outHtml.replace("{FILES_REPLACE}", html);
    }

    /**
     * 获取目录内容html
     */
    private static String getDirContent(String path, File realDir) {
        StringBuilder fileTable = new StringBuilder();

        for (File file : realDir.listFiles()) {
            String filePath = path + "/" + file.getName();
            fileTable.append("<tr>");
            fileTable.append("<td>");
            fileTable.append("<font color=\"blue\"><a href=\"");
            fileTable.append(ServletUtils.getRequest().getRequestURL() + "?path=" + filePath);
            fileTable.append("\">");
            fileTable.append(file.getName());
            fileTable.append("</a></font>");
            fileTable.append("</td>");
            fileTable.append("<td>");
            if (file.isDirectory()) {
                fileTable.append("--");
            } else {
                fileTable.append(file.length());
            }
            fileTable.append("</td>");
            fileTable.append("<td>");
            fileTable.append(file.isDirectory() ? "dir" : "file");
            fileTable.append("</td>");
            fileTable.append("<td>").append(filePath).append("</td>");
            fileTable.append("</tr>");
        }
        String tableHtml = fileTable.toString();
        return tableHtml;
    }

    /**
     * 返回配置的目录
     *
     * @return
     */
    private String responseConfigDir() throws IOException {
        String outHtml = getOutHtml();
        outHtml = setCurrentDir("/", outHtml);
        outHtml = setParentDir(outHtml, "");
        StringBuilder fileTable = new StringBuilder();
        for (DirConfig dir : config.getDirs()) {
            fileTable.append("<tr>");
            fileTable.append("<td>");
            fileTable.append("<font color=\"blue\"><a href=\"");
            fileTable.append(ServletUtils.getRequest().getRequestURL() + "?path=" + dir.getName());
            fileTable.append("\">");
            fileTable.append(dir.getName());
            fileTable.append("</a></font>");
            fileTable.append("</td>");
            fileTable.append("<td>");
            fileTable.append("--");
            fileTable.append("</td>");
            fileTable.append("<td>");
            fileTable.append("dir");
            fileTable.append("</td>");
            fileTable.append("<td>").append(dir.getName()).append("</td>");
            fileTable.append("</tr>");
        }
        outHtml = setDirHtml(fileTable.toString(), outHtml);
        return outHtml;
    }

    private static String getOutHtml() throws IOException {
        ClassPathResource classPathResource = new ClassPathResource("/static/file-expolore.html");
        InputStream inputStream = classPathResource.getInputStream();
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        String outHtml = new String(bytes);
        return outHtml;
    }
}
