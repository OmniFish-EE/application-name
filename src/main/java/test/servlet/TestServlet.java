package test.servlet;

import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import test.ejb.CallbackIF;

@WebServlet(name = "TestServlet", urlPatterns = { "/servlet_vehicle" })
public class TestServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    CallbackIF callbackBean;

    @Resource(lookup = "java:app/AppName")
    String appName;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {
        res.setHeader("X-AppName", appName);

        PrintWriter writer = res.getWriter();
        writer.println("Injected appName: " + appName);
        writer.println("Ran callbackBean.getAppName: " + callbackBean.getAppName());
        writer.flush();

        res.setStatus(200);
    }
}
