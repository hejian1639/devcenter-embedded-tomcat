package servlet;

import org.apache.jasper.JspC;
import org.apache.jasper.compiler.JspUtil;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

public class JspServlet extends FiberHttpServlet {
    String basedir;
    String fileName;

    public JspServlet(String basedir, String fileName) {
        this.basedir = basedir;
        this.fileName = fileName;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        File file = new File(basedir + "/" + fileName);

        if (!file.exists()) {
            byte[] data = new byte[1024 * 1024];
            try (OutputStream w = new FileOutputStream(file);
                 InputStream stream = ClassLoader.getSystemResourceAsStream(fileName)) {

                int len;
                while ((len = stream.read(data)) > 0) {
                    w.write(data, 0, len);
                }
            }

        }

        JspC jspc = new JspC();
        jspc.setUriroot(basedir);
        jspc.setOutputDir(basedir + "/work/Tomcat/localhost/ROOT");
        jspc.setJspFiles(fileName);
        jspc.setCompile(true);
        jspc.execute();
        File filePath = new File(basedir + "/work/Tomcat/localhost/ROOT");
        URL xUrl = filePath.toURI().toURL();
        try (URLClassLoader loader = new URLClassLoader(new URL[]{xUrl}, Thread.currentThread().getContextClassLoader())) {
            String className = JspUtil.makeJavaIdentifier(fileName);
            Class<?> xClass = loader.loadClass("org.apache.jsp." + className);
            Servlet servlet = (Servlet) xClass.newInstance();
            servlet.init(getServletConfig());
            servlet.service(req, resp);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();

        }
    }

}
