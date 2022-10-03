package cn.yizems.fileexplore.util;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 客户端工具类
 *
 * @author ruoyi
 */
public class ServletUtils {

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        return getRequestAttributes().getRequest();
    }

    /**
     * 获取response
     */
    public static HttpServletResponse getResponse() {
        return getRequestAttributes().getResponse();
    }

    /**
     * 获取session
     */
    public static HttpSession getSession() {
        return getRequest().getSession();
    }

    public static ServletRequestAttributes getRequestAttributes() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return (ServletRequestAttributes) attributes;
    }

    public static void renderString(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void renderHtml(HttpServletResponse response, String string) {
        try {
            response.setStatus(200);
            response.setContentType("text/html");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String download(File realDir) {
        // 返回文件流
        try {
            HttpServletResponse response = ServletUtils.getResponse();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + realDir.getName());
            response.setHeader("Content-Length", String.valueOf(realDir.length()));
            response.getOutputStream().write(readFileToByteArray(realDir));
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] readFileToByteArray(File realDir) {
        try {
            FileInputStream fileInputStream = new FileInputStream(realDir);
            byte[] bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            fileInputStream.close();
            return bytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
