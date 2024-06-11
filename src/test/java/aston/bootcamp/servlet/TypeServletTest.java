package aston.bootcamp.servlet;

import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.service.TypeService;
import aston.bootcamp.service.impl.TypeServiceImpl;
import aston.bootcamp.servlet.dto.TypeIncomingDto;
import aston.bootcamp.servlet.dto.TypeUpdateDto;
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
public class TypeServletTest {
    private static TypeService mockTypeService;
    private static TypeServiceImpl oldTypeServiceInstance;
    @InjectMocks
    private static TypeServlet typeServlet;
    @Mock
    private static HttpServletRequest mockRequest;
    @Mock
    private static HttpServletResponse mockResponse;
    @Mock
    private static BufferedReader mockBufferedReader;

    private static void setMock(TypeService mock) {
        try {
            Field instance = TypeServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oldTypeServiceInstance = (TypeServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockTypeService = Mockito.mock(TypeService.class);
        setMock(mockTypeService);
        typeServlet = new TypeServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = TypeServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oldTypeServiceInstance);
    }

    @BeforeEach
    void beforeEach() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void afterEach() {
        Mockito.reset(mockTypeService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("type/all").when(mockRequest).getPathInfo();
        typeServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockTypeService).findAll();
    }

    @Test
    void doGetById() throws Exception {
        Mockito.doReturn("type/1").when(mockRequest).getPathInfo();
        typeServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockTypeService).findById(Mockito.any(Long.class));
    }

    @Test
    void doGetNotFound() throws Exception {
        Mockito.doReturn("type/102").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("Not found")).when(mockTypeService).findById(102L);
        typeServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("type/ebdvev").when(mockRequest).getPathInfo();
        typeServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws Exception {
        Mockito.doReturn("type/2").when(mockRequest).getPathInfo();
        typeServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockTypeService).delete(Mockito.any(Long.class));
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteNotFound() throws NotFoundException, IOException {
        Mockito.doReturn("type/102").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("Not found")).when(mockTypeService).delete(102L);
        typeServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("type/ebdvev").when(mockRequest).getPathInfo();
        typeServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {
                    "type": "custom"
                }""", null).when(mockBufferedReader).readLine();
        typeServlet.doPost(mockRequest, mockResponse);
        ArgumentCaptor<TypeIncomingDto> captor = ArgumentCaptor.forClass(TypeIncomingDto.class);
        Mockito.verify(mockTypeService).save(captor.capture());
        Assertions.assertEquals(captor.getValue().getType(), "custom");
    }

    @Test
    void doPostBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {badrequest}""", null).when(mockBufferedReader).readLine();
        typeServlet.doPost(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {
                    "id": 1,
                    "type": "custom"
                }""", null).when(mockBufferedReader).readLine();
        typeServlet.doPut(mockRequest, mockResponse);
        ArgumentCaptor<TypeUpdateDto> captor = ArgumentCaptor.forClass(TypeUpdateDto.class);
        Mockito.verify(mockTypeService).update(captor.capture());
        Assertions.assertEquals(captor.getValue().getType(), "custom");
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {badrequest}""", null).when(mockBufferedReader).readLine();
        typeServlet.doPut(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPutNotFound() throws IOException, NotFoundException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {
                    "id": 1,
                    "type": "custom"
                }""", null).when(mockBufferedReader).readLine();
        Mockito.doThrow(new NotFoundException("Not found"))
                .when(mockTypeService).update(Mockito.any(TypeUpdateDto.class));
        typeServlet.doPut(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        Mockito.verify(mockTypeService).update(Mockito.any(TypeUpdateDto.class));
    }
}
