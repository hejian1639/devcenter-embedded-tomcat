package launch;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.jasper.JspC;
import org.apache.jasper.MyJavaCompiler;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

public class EmbeddedTomcatEx {

    public static void main(String[] args) throws LifecycleException, URISyntaxException {

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8082);


        System.out.println(new File(".").getAbsolutePath());

        Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());


        System.out.println(ctx.getRealPath("index.html"));

        System.out.println(ClassLoader.getSystemResourceAsStream("index.html"));

        Tomcat.addServlet(ctx, "index", new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {

                byte[] data = new byte[1024 * 1024];
                try (ServletOutputStream w = resp.getOutputStream();
                     InputStream stream = ClassLoader.getSystemResourceAsStream("index.html")) {

                    int len;
                    while ((len = stream.read(data)) > 0) {
                        w.write(data, 0, len);
                    }
                }
            }
        });

        ctx.addServletMappingDecoded("/", "index");

        Tomcat.addServlet(ctx, "jsp", new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {

                JspC jspc = new JspC();
                jspc.setUriroot("/Users/jianhe/devcenter-embedded-tomcat/src/main/webapp");
                jspc.setOutputDir("/Users/jianhe/devcenter-embedded-tomcat/tomcat.8082/work/Tomcat/localhost/ROOT");
                jspc.setJspFiles("basic-arithmetic.jsp");
                jspc.setCompile(true);
                jspc.execute();
                File filePath = new File("/Users/jianhe/devcenter-embedded-tomcat/tomcat.8082/work/Tomcat/localhost/ROOT");
                URL xUrl = filePath.toURI().toURL();
                try (URLClassLoader loader = new URLClassLoader(new URL[] { xUrl }, Thread.currentThread().getContextClassLoader())){
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