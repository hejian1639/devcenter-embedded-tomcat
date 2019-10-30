package launch;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.jasper.JspC;
import servlet.FileServlet;
import servlet.JspServlet;
import servlet.MatchServlet;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;

public class EmbeddedTomcatEx {

    public static void main(String[] args) throws LifecycleException {

        Tomcat tomcat = new Tomcat();
        int port = 8180;
        tomcat.setPort(port);

        String basedir = System.getProperty("user.dir") + "/tomcat." + port;
        tomcat.setBaseDir(basedir);

        System.out.println(new File(".").getAbsolutePath());

        Context ctx = tomcat.addContext("/", new File(".").getAbsolutePath());




        Tomcat.addServlet(ctx, "index", new FileServlet("index.html"));
        ctx.addServletMappingDecoded("/", "index");

        Tomcat.addServlet(ctx, "file", new MatchServlet());
        ctx.addServletMappingDecoded("*.js", "file");
        ctx.addServletMappingDecoded("*.css", "file");


        Tomcat.addServlet(ctx, "jsp", new JspServlet(basedir, "basic-arithmetic.jsp"));
        ctx.addServletMappingDecoded("/jsp", "jsp");

        tomcat.start();
        tomcat.getServer().await();
    }
}