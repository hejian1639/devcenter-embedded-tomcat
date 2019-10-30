package servlet;

import launch.Utility;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;

public class FileServlet extends HttpServlet {
    String fileName;

    public FileServlet(String fileName) {
        this.fileName = fileName;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String mimeType = Utility.probeContentType(fileName);

        resp.setContentType(mimeType);

        byte[] data = new byte[1024 * 1024];
        try (ServletOutputStream w = resp.getOutputStream();
             InputStream stream = ClassLoader.getSystemResourceAsStream(fileName)) {

            int len;
            while ((len = stream.read(data)) > 0) {
                w.write(data, 0, len);
            }
        }
    }

}
