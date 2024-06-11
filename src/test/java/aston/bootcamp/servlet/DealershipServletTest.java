package aston.bootcamp.servlet;

import aston.bootcamp.exceptions.NotFoundException;
import aston.bootcamp.service.DealershipService;
import aston.bootcamp.service.impl.DealershipServiceImpl;
import aston.bootcamp.servlet.dto.DealershipIncomingDto;
import aston.bootcamp.servlet.dto.DealershipUpdateDto;
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
public class DealershipServletTest {
    private static DealershipService mockDealershipService;
    private static DealershipServiceImpl oltDealershipServiceInstance;
    @InjectMocks
    private static DealershipServlet dealershipServlet;
    @Mock
    private static HttpServletRequest mockRequest;
    @Mock
    private static HttpServletResponse mockResponse;
    @Mock
    private static BufferedReader mockBufferedReader;

    private static void setMock(DealershipService mock) {
        try {
            Field instance = DealershipServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            oltDealershipServiceInstance = (DealershipServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockDealershipService = Mockito.mock(DealershipService.class);
        setMock(mockDealershipService);
        dealershipServlet = new DealershipServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = DealershipServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, oltDealershipServiceInstance);
    }

    @BeforeEach
    void beforeEach() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void afterEach() {
        Mockito.reset(mockDealershipService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("dealership/all").when(mockRequest).getPathInfo();
        dealershipServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockDealershipService).findAll();
    }

    @Test
    void doGetById() throws Exception {
        Mockito.doReturn("dealership/1").when(mockRequest).getPathInfo();
        dealershipServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockDealershipService).findById(Mockito.any(Long.class));
    }

    @Test
    void doGetNotFound() throws Exception {
        Mockito.doReturn("dealership/102").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("Not found")).when(mockDealershipService).findById(102L);
        dealershipServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("dealership/ebdvev").when(mockRequest).getPathInfo();
        dealershipServlet.doGet(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws Exception {
        Mockito.doReturn("dealership/2").when(mockRequest).getPathInfo();
        dealershipServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockDealershipService).delete(Mockito.any(Long.class));
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteNotFound() throws NotFoundException, IOException {
        Mockito.doReturn("dealership/102").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("Not found")).when(mockDealershipService).delete(102L);
        dealershipServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("dealership/ebdvev").when(mockRequest).getPathInfo();
        dealershipServlet.doDelete(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDeleteBike() throws IOException, NotFoundException {
        Mockito.doReturn("department/1/deleteBike/1").when(mockRequest).getPathInfo();
        dealershipServlet.doDelete(mockRequest, mockResponse);
        Mockito.doNothing().when(mockDealershipService).deleteBikeFromDealership(Mockito.any(Long.class), Mockito.any(Long.class));
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
        Mockito.verify(mockDealershipService).deleteBikeFromDealership(Mockito.any(Long.class), Mockito.any(Long.class));
    }

    @Test
    void doPost() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {
                "city":"Самара",
                "street":"Стара-Загора",
                "houseNum":31}""", null).when(mockBufferedReader).readLine();
        dealershipServlet.doPost(mockRequest, mockResponse);
        ArgumentCaptor<DealershipIncomingDto> captor = ArgumentCaptor.forClass(DealershipIncomingDto.class);
        Mockito.verify(mockDealershipService).save(captor.capture());
        Assertions.assertEquals(captor.getValue().getCity(), "Самара");
    }

    @Test
    void doPostBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {badrequest}""", null).when(mockBufferedReader).readLine();
        dealershipServlet.doPost(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("department/").when(mockRequest).getPathInfo();
        Mockito.doReturn("""
                {
                "id":1,
                "city":"Самара",
                "street":"Стара-Загора",
                "houseNum":31,
                "bikes":[]
                }
                """, null).when(mockBufferedReader).readLine();
        dealershipServlet.doPut(mockRequest, mockResponse);
        ArgumentCaptor<DealershipUpdateDto> captor = ArgumentCaptor.forClass(DealershipUpdateDto.class);
        Mockito.verify(mockDealershipService).update(captor.capture());
        Assertions.assertEquals(captor.getValue().getCity(), "Самара");
    }


    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("""
                {badrequest}""", null).when(mockBufferedReader).readLine();
        dealershipServlet.doPut(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPutNotFound() throws IOException, NotFoundException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("department/").when(mockRequest).getPathInfo();
        Mockito.doReturn("""
                {
                "id":1,
                "city":"Самара",
                "street":"Стара-Загора",
                "houseNum":31,
                "bikes":[]
                }
                """, null).when(mockBufferedReader).readLine();
        Mockito.doThrow(new NotFoundException("Not found")).
                when(mockDealershipService).update(Mockito.any(DealershipUpdateDto.class));
        dealershipServlet.doPut(mockRequest, mockResponse);
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        Mockito.verify(mockDealershipService).update(Mockito.any(DealershipUpdateDto.class));
    }

    @Test
    void doPutAddBike() throws IOException, NotFoundException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.doReturn("department/1/addBike/1").when(mockRequest).getPathInfo();
        dealershipServlet.doPut(mockRequest, mockResponse);
        Mockito.doNothing().when(mockDealershipService).addBikeToDealership(Mockito.any(Long.class), Mockito.any(Long.class));
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        Mockito.verify(mockDealershipService).addBikeToDealership(Mockito.any(Long.class), Mockito.any(Long.class));
    }
}
