package aston.bootcamp.servlet;

import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.service.BrandService;
import aston.bootcamp.service.impl.BrandServiceImpl;
import aston.bootcamp.servlet.dto.BrandIncomingDto;
import aston.bootcamp.servlet.dto.BrandUpdateDto;
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
public class BrandServletTest {
    private static BrandService mockBrandService;
    private static BrandServiceImpl oldBrandServiceInstance;
    @InjectMocks
    private static BrandServlet brandServlet;
    @Mock
    private static HttpServletRequest mockRequest;
    @Mock
    private static HttpServletResponse mockResponse;
    @Mock
    private static BufferedReader mockBufferedReader;

    private static void setMock(BrandService mock) {
        try {
            Field instance = BrandServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldBrandServiceInstance = (BrandServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockBrandService = Mockito.mock(BrandService.class);
        setMock(mockBrandService);
        brandServlet = new BrandServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = BrandServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldBrandServiceInstance);
    }

    @BeforeEach
    void beforeEach() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void afterEach() {
        Mockito.reset(mockBrandService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("brand/all").when(mockRequest).getPathInfo();
        brandServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockBrandService).findAll();
    }

    @Test
    void doGetById() throws Exception {
        Mockito.doReturn("brand/1").when(mockRequest).getPathInfo();
        brandServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockBrandService).findById(Mockito.any(Long.class));
    }

    @Test
    void doGetNotFound() throws Exception {
        Mockito.doReturn("brand/102").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("Not found")).when(mockBrandService).findById(102L);
        brandServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("brand/ebdvev").when(mockRequest).getPathInfo();
        brandServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws Exception {
        Mockito.doReturn("brand/2").when(mockRequest).getPathInfo();
        brandServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockBrandService).delete(Mockito.any(Long.class));
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteNotFound() throws NotFoundException, IOException {
        Mockito.doReturn("bike/102").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("Not found")).when(mockBrandService).delete(102L);
        brandServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("brand/ebdvev").when(mockRequest).getPathInfo();
        brandServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {
                    "brand": "BMW"
                }""", null).when(mockBufferedReader).readLine();
        brandServlet.doPost(mockRequest, mockResponse);
        ArgumentCaptor<BrandIncomingDto> captor = ArgumentCaptor.forClass(BrandIncomingDto.class);
        Mockito.verify(mockBrandService).save(captor.capture());
        Assertions.assertEquals(captor.getValue().getBrand(), "BMW");
    }

    @Test
    void doPostBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {badrequest}""", null).when(mockBufferedReader).readLine();
        brandServlet.doPost(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {
                    "id": 1,
                    "brand": "BMW"
                }""", null).when(mockBufferedReader).readLine();
        brandServlet.doPut(mockRequest, mockResponse);
        ArgumentCaptor<BrandUpdateDto> captor = ArgumentCaptor.forClass(BrandUpdateDto.class);
        Mockito.verify(mockBrandService).update(captor.capture());
        Assertions.assertEquals(captor.getValue().getBrand(), "BMW");
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {badrequest}""", null).when(mockBufferedReader).readLine();
        brandServlet.doPut(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPutNotFound() throws IOException, NotFoundException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {
                    "id": 1,
                    "brand": "BMW"
                }""", null).when(mockBufferedReader).readLine();
        Mockito.doThrow(new NotFoundException("Not found"))
                .when(mockBrandService).update(Mockito.any(BrandUpdateDto.class));
        brandServlet.doPut(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        Mockito.verify(mockBrandService).update(Mockito.any(BrandUpdateDto.class));
    }
}
