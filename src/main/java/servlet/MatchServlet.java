package servlet;

import launch.Utility;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;

public class MatchServlet extends FiberHttpServlet {


    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getRequestURI();
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        System.out.println(path);

        String mimeType = Utility.probeContentType(path);

        resp.setContentType(mimeType);

        byte[] data = new byte[1024 * 1024];
        try (ServletOutputStream w = resp.getOutputStream();
             InputStream stream = ClassLoader.getSystemResourceAsStream(path)) {

            int len;
            while ((len = stream.read(data)) > 0) {
                w.write(data, 0, len);
            }
        }
    }

}
