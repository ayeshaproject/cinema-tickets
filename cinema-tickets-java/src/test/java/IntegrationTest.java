import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class IntegrationTest {

    private TicketServiceImpl ticketService;
    private TicketPaymentServiceImpl paymentService;
    private SeatReservationServiceImpl reservationService;

    @BeforeEach
    void setUp() {
        paymentService = mock(TicketPaymentServiceImpl.class);
        reservationService = mock(SeatReservationServiceImpl.class);
        ticketService = new TicketServiceImpl();
    }

    @Test
    void testValidPurchase() {
        TicketTypeRequest[] req = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        };
        ticketService.purchaseTickets(11L, req);

        verify(paymentService).makePayment(1L, 80);
        verify(reservationService).reserveSeat(1L, 4);
    }

    @Test
    void testNoExternalCallsForInvalidAccountID() {
        TicketTypeRequest[] req = {new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2)};
        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(0L, req));

        //to make sure no external calls happen for invalid account numbers
        verify(paymentService, never()).makePayment(anyLong(), anyInt());
        verify(reservationService,never()).reserveSeat(anyLong(), anyInt());

    }
}
