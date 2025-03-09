import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

public class ServiceTest {

    private static final TicketServiceImpl ticketService = new TicketServiceImpl();

    @Test
    void testCalculateSeatsWithInfant() {
        TicketTypeRequest[] req = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        };
        ticketService.purchaseTickets(12L, req);
        /*
        Expected behaviour-> no exception thrown
        total seats calculated is 2*2 = 4
         */
    }

    @Test
    void testCalculatePrice() {
        TicketTypeRequest[] req = {
                new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2),
                new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
        };
        ticketService.purchaseTickets(12L,req);
        /*
        Expected behaviour -> no exception thrown
        total Price = (2*25) + (2*15) = 80
         */
    }
}
