package aston.bootcamp.servlet;

import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.service.BrandService;
import aston.bootcamp.service.impl.BrandServiceImpl;
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

@WebServlet(urlPatterns = {"/brand/*"})
public class BrandServlet extends HttpServlet {
    private final transient BrandService brandService = BrandServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public BrandServlet() {
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
                List<BrandOutgoingDto> brands = brandService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                answer = objectMapper.writeValueAsString(brands);
            } else {
                BrandOutgoingDto brand = brandService.findById(Long.valueOf(address[1]));
                resp.setStatus(HttpServletResponse.SC_OK);
                answer = objectMapper.writeValueAsString(brand);
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
            brandService.delete(Long.valueOf(address[1]));
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
        Optional<BrandIncomingDto> brandIncomingDto;
        try {
            brandIncomingDto = Optional.ofNullable(objectMapper.readValue(request, BrandIncomingDto.class));
            BrandIncomingDto reqBrand = brandIncomingDto.orElseThrow(IllegalArgumentException::new);
            answer = objectMapper.writeValueAsString(brandService.save(reqBrand));
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            answer = ex.getMessage();
        }
        PrintWriter writer = resp.getWriter();
        writer.println(answer);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonContentType(resp);
        String request = getStringFromJson(req);
        String answer;
        Optional<BrandUpdateDto> brandUpdateDto;
        try {
            brandUpdateDto = Optional.ofNullable(objectMapper.readValue(request, BrandUpdateDto.class));
            BrandUpdateDto reqBrand = brandUpdateDto.orElseThrow(IllegalArgumentException::new);
            brandService.update(reqBrand);
            answer = "Success update";
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            answer = "Incorrect brand Object";
        }
        PrintWriter writer = resp.getWriter();
        writer.println(answer);
    }
}
