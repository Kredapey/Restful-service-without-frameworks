package aston.bootcamp.servlet;

import aston.bootcamp.entity.Bike;
import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.repository.BikeRepository;
import aston.bootcamp.repository.impl.BikeRepositoryImpl;
import aston.bootcamp.service.BikeService;
import aston.bootcamp.service.impl.BikeServiceImpl;
import aston.bootcamp.servlet.dto.BikeIncomingDto;
import aston.bootcamp.servlet.dto.BikeUpdateDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

@ExtendWith(
        MockitoExtension.class
)
public class BikeServletTest {
    private static BikeService mockBikeService;
    private static BikeServiceImpl oldBikeServiceInstance;
    @InjectMocks
    private static BikeServlet bikeServlet;
    @Mock
    private static HttpServletRequest mockRequest;
    @Mock
    private static HttpServletResponse mockResponse;
    @Mock
    private static BufferedReader mockBufferedReader;
    private static void setMock(BikeService mock) {
        try {
            Field instance = BikeServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldBikeServiceInstance = (BikeServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockBikeService = Mockito.mock(BikeService.class);
        setMock(mockBikeService);
        bikeServlet = new BikeServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = BikeServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldBikeServiceInstance);
    }

    @BeforeEach
    void beforeEach() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void afterEach() {
        Mockito.reset(mockBikeService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("bike/all").when(mockRequest).getPathInfo();
        bikeServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockBikeService).findAll();
    }

    @Test
    void doGetById() throws Exception {
        Mockito.doReturn("bike/1").when(mockRequest).getPathInfo();
        bikeServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockBikeService).findById(Mockito.any(Long.class));
    }

    @Test
    void doGetNotFound() throws Exception {
        Mockito.doReturn("bike/102").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("Not found")).when(mockBikeService).findById(102L);
        bikeServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("bike/ebdvev").when(mockRequest).getPathInfo();
        bikeServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws Exception {
        Mockito.doReturn("bike/2").when(mockRequest).getPathInfo();
        bikeServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockBikeService).delete(Mockito.any(Long.class));
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteNotFound() throws NotFoundException, IOException {
        Mockito.doReturn("bike/102").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("Not found")).when(mockBikeService).delete(102L);
        bikeServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doDeleteBaRequest() throws IOException {
        Mockito.doReturn("bike/ebdvev").when(mockRequest).getPathInfo();
        bikeServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {
                        "type": {
                            "id": 1,
                            "type": "chopper"
                        },
                        "brand": {
                            "id": 3,
                            "brand": "Honda"
                        },
                        "model": "Shadow 150",
                        "cost": 299900
                    }""", null).when(mockBufferedReader).readLine();
        bikeServlet.doPost(mockRequest, mockResponse);
        ArgumentCaptor<BikeIncomingDto> captor = ArgumentCaptor.forClass(BikeIncomingDto.class);
        Mockito.verify(mockBikeService).save(captor.capture());
        Assertions.assertEquals(captor.getValue().getBrand().getBrand(), "Honda");
        Assertions.assertEquals(captor.getValue().getType().getType(), "chopper");
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {
                        "id": 1,
                        "type": {
                            "id": 1,
                            "type": "chopper"
                        },
                        "brand": {
                            "id": 3,
                            "brand": "Honda"
                        },
                        "model": "Shadow 150",
                        "cost": 299900,
                        "dealerships": []
                    }""", null).when(mockBufferedReader).readLine();
        bikeServlet.doPut(mockRequest, mockResponse);
        ArgumentCaptor<BikeUpdateDto> captor = ArgumentCaptor.forClass(BikeUpdateDto.class);
        Mockito.verify(mockBikeService).update(captor.capture());
        Assertions.assertEquals(captor.getValue().getBrand().getBrand(), "Honda");
        Assertions.assertEquals(captor.getValue().getType().getType(), "chopper");
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {badrequest}""", null).when(mockBufferedReader).readLine();
        bikeServlet.doPut(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}
