package aston.bootcamp.servlet;

import aston.bootcamp.db.ConnectionManager;
import aston.bootcamp.db.impl.ConnectionManagerImpl;
import aston.bootcamp.utils.CreateSqlSchema;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/database")
public class DatabaseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
        CreateSqlSchema.initSchema(connectionManager);
        String result = "SQL schema initialized";
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        resp.setStatus(HttpServletResponse.SC_OK);
        writer.println(result);
    }
}
