package aston.bootcamp.servlet;

import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.service.DealershipService;
import aston.bootcamp.service.impl.DealershipServiceImpl;
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

@WebServlet(urlPatterns = {"/dealership/*"})
public class DealershipServlet extends HttpServlet {
    private final transient DealershipService dealershipService = DealershipServiceImpl.getInstance();
    private final ObjectMapper objectMapper;
    public DealershipServlet() {
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
                List<DealershipOutgoingDto> dealerships = dealershipService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                answer = objectMapper.writeValueAsString(dealerships);
            } else {
                DealershipOutgoingDto dealershipOutgoingDto = dealershipService.findById(Long.valueOf(address[1]));
                resp.setStatus(HttpServletResponse.SC_OK);
                answer = objectMapper.writeValueAsString(dealershipOutgoingDto);
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
            if (req.getPathInfo().contains("/deleteBike/")) {
                if (address[2].equals("deleteBike")) {
                    dealershipService.deleteBikeFromDealership(Long.valueOf(address[1]),
                            Long.valueOf(address[3]));
                }
            } else {
                dealershipService.delete(Long.valueOf(address[1]));
            }
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
        Optional<DealershipIncomingDto> dealershipIncomingDto;
        try {
            dealershipIncomingDto = Optional.ofNullable(objectMapper.readValue(request, DealershipIncomingDto.class));
            DealershipIncomingDto reqDealer = dealershipIncomingDto.orElseThrow(IllegalArgumentException::new);
            answer = objectMapper.writeValueAsString(dealershipService.save(reqDealer));
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
        Optional<DealershipUpdateDto> dealershipUpdateDto;
        try {
            if (req.getPathInfo().contains("/addBike/")) {
                String[] address = req.getPathInfo().split("/");
                if (address.length > 3 && address[2].equals("addBike")) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    dealershipService.addBikeToDealership(Long.valueOf(address[1]),
                            Long.valueOf(address[3]));
                }
            } else {
                dealershipUpdateDto = Optional.ofNullable(objectMapper.readValue(request, DealershipUpdateDto.class));
                DealershipUpdateDto reqDealer = dealershipUpdateDto.orElseThrow(IllegalArgumentException::new);
                dealershipService.update(reqDealer);
            }
            answer = "Success update";
        } catch (NotFoundException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            answer = "Dealership not found";
        } catch (Exception ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            answer = "Incorrect dealership Object";
        }
        PrintWriter writer = resp.getWriter();
        writer.println(answer);
    }
}
