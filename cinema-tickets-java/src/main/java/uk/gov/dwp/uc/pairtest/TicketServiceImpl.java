package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.HashMap;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private static final HashMap<TicketTypeRequest.Type, Integer> eachPrice = new HashMap<>();
    static {
        eachPrice.put(TicketTypeRequest.Type.ADULT, 25);
        eachPrice.put(TicketTypeRequest.Type.CHILD, 15);
        eachPrice.put(TicketTypeRequest.Type.INFANT, 0);
    }

    private TicketPaymentServiceImpl ticketPaymentService = new TicketPaymentServiceImpl();
    private SeatReservationServiceImpl seatReservationService = new SeatReservationServiceImpl();

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        validateAcctId(accountId);
        validatePurchase(ticketTypeRequests);

        int totalSeats = calculateSeats(ticketTypeRequests);
        int totalPrice = calculatePrice(ticketTypeRequests);

        ticketPaymentService.makePayment(accountId, totalPrice);
        seatReservationService.reserveSeat(accountId, totalSeats);

    }

    private void validateAcctId(Long accountID) {
        if(accountID <= 0) {
            //throw exception
        }

    }

    private void validatePurchase(TicketTypeRequest[] ticketTypeRequest) {

        int adults = 0;
        int totalTickets = 0;

        for(TicketTypeRequest req : ticketTypeRequest) {
            TicketTypeRequest.Type ticketType = req.getTicketType();
            int ticketsCount = req.getNoOfTickets();

            if(ticketsCount < 0) {
                //throw exception
            }

            if(ticketType == TicketTypeRequest.Type.ADULT) {
                adults += ticketsCount;
            }
            totalTickets += ticketsCount;
        }

        if(adults == 0) {
            //throw exception
        }
        if(totalTickets> 25) {
            //throw exception
        }
    }

    private int calculateSeats(TicketTypeRequest[] ticketTypeRequests) {
        int totalSeats = 0;
        for(TicketTypeRequest req : ticketTypeRequests) {
            TicketTypeRequest.Type ticketType = req.getTicketType();
            if(ticketType != TicketTypeRequest.Type.INFANT) {
               totalSeats += req.getNoOfTickets();
            }
        }
        return totalSeats;

    }

    private int calculatePrice(TicketTypeRequest[] ticketTypeRequests) {
        int price = 0;
        for(TicketTypeRequest req : ticketTypeRequests) {
            TicketTypeRequest.Type ticketType = req.getTicketType();
            int ticketCount = req.getNoOfTickets();
            price += ticketCount * eachPrice.get(ticketType);
        }

        return price;

    }

}
