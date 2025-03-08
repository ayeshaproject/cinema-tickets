package uk.gov.dwp.uc.pairtest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.HashMap;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private static final Logger log = LogManager.getLogger(TicketServiceImpl.class);
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

        try {
            log.info("The validation checks begins");
            validateAcctId(accountId);
            validatePurchase(ticketTypeRequests);

            log.info("Calculating the total seats and total price");
            int totalSeats = calculateSeats(ticketTypeRequests);
            int totalPrice = calculatePrice(ticketTypeRequests);

            log.info("Making payment of amount {} for accountID {}", totalPrice, accountId);
            ticketPaymentService.makePayment(accountId, totalPrice);
            log.info("Reserving {} seats for accountID {}", totalSeats, accountId);
            seatReservationService.reserveSeat(accountId, totalSeats);
        }
        catch (InvalidPurchaseException e) {
            log.error("Invalid purchase for account ID {}, error: {}", accountId,e.getMessage());
            throw e;
        } catch(Exception e) {
          log.error("Invalid process of purchasing ticket for accountID {}, error {}",accountId, e.getMessage(), e);
          throw new RuntimeException("Purchase terminated due to error. Please try again", e);
        }


    }

    private void validateAcctId(Long accountID) {
        log.info("Validating accountID for {}", accountID);
        if(accountID <= 0) {
            log.warn("Invalid account ID {}", accountID);
            throw new InvalidPurchaseException("The accountID should be greater than 0");
        }

    }

    // validation logic for ticket type and total number of tickets
    private void validatePurchase(TicketTypeRequest[] ticketTypeRequest) {

        log.info("Validating the ticket type and total number of tickets");
        int adults = 0;
        int totalTickets = 0;

        for(TicketTypeRequest req : ticketTypeRequest) {
            TicketTypeRequest.Type ticketType = req.getTicketType();
            int ticketsCount = req.getNoOfTickets();

            if(ticketsCount < 0) {
                log.warn("Invalid number of tickets : {}", ticketsCount);
                throw new InvalidPurchaseException("The total tickets cannot be negative");
            }

            if(ticketType == TicketTypeRequest.Type.ADULT) {
                adults += ticketsCount;
            }
            totalTickets += ticketsCount;
        }

        if(adults == 0) {
            log.warn("Adult count is {}",adults);
            throw new InvalidPurchaseException("Atleast one adult is needed with children and/or infants");
        }
        if(totalTickets> 25) {
            log.warn("Max no. of tickets reached : {}", totalTickets);
            throw new InvalidPurchaseException("Maximum no. of tickets purchased at a time cannot exceed 25");
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
        log.debug("Total seats booked : {}", totalSeats);
        return totalSeats;

    }

    private int calculatePrice(TicketTypeRequest[] ticketTypeRequests) {
        int price = 0;
        for(TicketTypeRequest req : ticketTypeRequests) {
            TicketTypeRequest.Type ticketType = req.getTicketType();
            int ticketCount = req.getNoOfTickets();
            price += ticketCount * eachPrice.get(ticketType);
        }

        log.debug("Total price of the ticket : {}", price);
        return price;

    }

}
