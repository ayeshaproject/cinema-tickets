import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class ValidationTest {

    private final TicketServiceImpl ticketService = new TicketServiceImpl();

    @Test
    void testInvalidAccountIDThrowsException() {
        TicketTypeRequest[] req = { new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)};
        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(0L, req));
        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(-0L, req));
    }

    @Test
    void testInvalidPurchaseNoAdult() {
        TicketTypeRequest[] req = {new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10)};
        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(12L, req));
    }

    @Test
    void testInvalidPurchaseMaxTickets() {
        TicketTypeRequest[] req = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 20)
        };
        assertThrows(InvalidPurchaseException.class, () -> ticketService.purchaseTickets(21L, req));
    }

    @Test
    void testValidPurchaseDoesntThrowException() {
        TicketTypeRequest[] req = {
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT,2)
        };
        assertDoesNotThrow(() -> ticketService.purchaseTickets(300L, req));
    }

}
