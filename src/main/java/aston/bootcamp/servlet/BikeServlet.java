package aston.bootcamp.servlet;


import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.service.BikeService;
import aston.bootcamp.service.impl.BikeServiceImpl;
import aston.bootcamp.servlet.dto.BikeIncomingDto;
import aston.bootcamp.servlet.dto.BikeOutgoingDto;
import aston.bootcamp.servlet.dto.BikeUpdateDto;
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

@WebServlet(urlPatterns = {"/bike/*"})
public class BikeServlet extends HttpServlet {
    private final transient BikeService bikeService = BikeServiceImpl.getInstance();
    private final ObjectMapper objectMapper;
    public BikeServlet() {
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
                List<BikeOutgoingDto> bikes = bikeService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                answer = objectMapper.writeValueAsString(bikes);
            } else {
                BikeOutgoingDto bike = bikeService.findById(Long.valueOf(address[1]));
                resp.setStatus(HttpServletResponse.SC_OK);
                answer = objectMapper.writeValueAsString(bike);
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
            bikeService.delete(Long.valueOf(address[1]));
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
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
        Optional<BikeIncomingDto> bikeIncomingDto;
        try {
            bikeIncomingDto = Optional.ofNullable(objectMapper.readValue(request, BikeIncomingDto.class));
            BikeIncomingDto reqBike = bikeIncomingDto.orElseThrow(IllegalArgumentException::new);
            answer = objectMapper.writeValueAsString(bikeService.save(reqBike));
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
        Optional<BikeUpdateDto> bikeUpdateDto;
        try {
            bikeUpdateDto = Optional.ofNullable(objectMapper.readValue(request, BikeUpdateDto.class));
            BikeUpdateDto reqBike = bikeUpdateDto.orElseThrow(IllegalArgumentException::new);
            bikeService.update(reqBike);
            answer = "Success update";
        } catch (NotFoundException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            answer = "Bike not found";
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            answer = "Incorrect bike Object";
        }
        PrintWriter writer = resp.getWriter();
        writer.println(answer);
    }
}
