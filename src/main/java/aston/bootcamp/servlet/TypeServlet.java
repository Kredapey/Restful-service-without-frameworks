package aston.bootcamp.servlet;

import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.service.TypeService;
import aston.bootcamp.service.impl.TypeServiceImpl;
import aston.bootcamp.servlet.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/type/*"})
public class TypeServlet extends HttpServlet {
    private final transient TypeService typeService = TypeServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public TypeServlet() {
        objectMapper = new ObjectMapper();
    }

    private static void setJsonContentType(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private static String getStringFromJson(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader postData = req.getReader();
        String line;
        while ((line = postData.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonContentType(resp);
        String answer = "";
        try {
            String[] address = req.getPathInfo().split("/");
            if (address[1].equals("all")) {
                List<TypeOutgoingDto> types = typeService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                answer = objectMapper.writeValueAsString(types);
            } else {
                TypeOutgoingDto typeOutgoingDto = typeService.findById(Long.valueOf(address[1]));
                resp.setStatus(HttpServletResponse.SC_OK);
                answer = objectMapper.writeValueAsString(typeOutgoingDto);
            }
        } catch (NotFoundException ex) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            answer = "Not found in servlet";
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            answer = "Bad request";
        }
        PrintWriter writer = resp.getWriter();
        writer.println(answer);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonContentType(resp);
        String answer = "";
        try {
            String[] address = req.getPathInfo().split("/");
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            typeService.delete(Long.valueOf(address[1]));
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            answer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            answer = "Bad request";
        }
        PrintWriter writer = resp.getWriter();
        writer.println(answer);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonContentType(resp);
        String request = getStringFromJson(req);
        String answer;
        Optional<TypeIncomingDto> typeIncomingDto;
        try {
            typeIncomingDto = Optional.ofNullable(objectMapper.readValue(request, TypeIncomingDto.class));
            TypeIncomingDto reqType = typeIncomingDto.orElseThrow(IllegalArgumentException::new);
            answer = objectMapper.writeValueAsString(typeService.save(reqType));
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            answer = "Bad request";
        }
        PrintWriter writer = resp.getWriter();
        writer.println(answer);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonContentType(resp);
        String request = getStringFromJson(req);
        String answer;
        Optional<TypeUpdateDto> typeUpdateDto;
        try {
            typeUpdateDto = Optional.ofNullable(objectMapper.readValue(request, TypeUpdateDto.class));
            TypeUpdateDto reqType = typeUpdateDto.orElseThrow(IllegalArgumentException::new);
            typeService.update(reqType);
            answer = "Success update";
        } catch (NotFoundException ex) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            answer = "Type not found";
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            answer = "Incorrect brand Object";
        }
        PrintWriter writer = resp.getWriter();
        writer.println(answer);
    }
}
