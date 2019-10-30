package launch;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.jasper.JspC;
import servlet.FileServlet;
import servlet.MatchServlet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

public class EmbeddedTomcatEx {

    public static void main(String[] args) throws LifecycleException {

        Tomcat tomcat = new Tomcat();
        int port = 8082;
        tomcat.setPort(port);

        String basedir = System.getProperty("user.dir") + "/tomcat." + port;
        tomcat.setBaseDir(basedir);

        System.out.println(new File(".").getAbsolutePath());

        Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());


        System.out.println(ctx.getRealPath("index.html"));

        System.out.println(ClassLoader.getSystemResourceAsStream("index.html"));

        Tomcat.addServlet(ctx, "index", new FileServlet("index.html"));
        ctx.addServletMappingDecoded("/", "index");

        Tomcat.addServlet(ctx, "file", new MatchServlet());
        ctx.addServletMappingDecoded("*.js", "file");
        ctx.addServletMappingDecoded("*.css", "file");

        Tomcat.addServlet(ctx, "jsp", new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {

                String fileName = "basic-arithmetic.jsp";
                File file = new File(basedir + "/" + fileName);

                if (!file.exists()) {
                    byte[] data = new byte[1024 * 1024 * 1024];
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
                    Class<?> xClass = loader.loadClass("org.apache.jsp.basic_002darithmetic_jsp");
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
        });

        ctx.addServletMappingDecoded("/jsp", "jsp");

        tomcat.start();
        tomcat.getServer().await();
    }
}