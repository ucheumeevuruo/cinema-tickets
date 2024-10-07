package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.paymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;

import static java.util.Objects.isNull;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private Integer totalPrice = 0;
    private Integer infantQuantity = 0;
    private Integer childQuantity = 0;
    private Integer adultQuantity = 0;
    private paymentService paymentService; // In spring, we can adopt dependency injection like @Autowire
    private SeatReservationService seatReservationService; // In spring, we can adopt dependency injection like @Autowire

    public TicketServiceImpl(paymentService paymentService, SeatReservationService seatReservationService) {
        this.seatReservationService = seatReservationService;
        this.paymentService = paymentService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        if(isNull(accountId) || accountId.equals(Constant.INVALID_ACCOUNT_ID)){
            throw new IllegalArgumentException("The account id not valid");
        }

        // Process all the requests (infant, child, and adult) and compute amount, quantity
        Arrays
                .stream(ticketTypeRequests)
                .forEach(this::processRequest);

        if(adultQuantity == 0 && (infantQuantity > 0 || childQuantity > 0)){
            throw new IllegalArgumentException("Child and Infant tickets cannot be purchased without an Adult ticket.");
        }

        // Compute total quantity. Infant(s) are not allocated a seat
        var totalQuantity = childQuantity + adultQuantity;

        if(totalQuantity <= Constant.NO_QUANTITY){
            throw new IllegalArgumentException("Minimum quantity: The required quantity should be at least 1");
        }

        if(totalQuantity > Constant.MAX_FEE) {
            throw new IllegalArgumentException("Maximum exceeded: Cannot purchase more than 25 tickets.");
        }

        // Make payment and reserve seats
        paymentService.makePayment(accountId, totalPrice);
        seatReservationService.reserveSeat(accountId, totalQuantity);
    }

    private void processRequest(TicketTypeRequest request){

        if(isNull(request) || isNull(request.getTicketType())){
            throw new IllegalArgumentException("Ticket type expected");
        }

        switch (request.getTicketType()){
            case INFANT -> infantQuantity += request.getNoOfTickets();

            case CHILD -> {
                totalPrice += request.getNoOfTickets() * Constant.FEE_15;
                childQuantity += request.getNoOfTickets();
            }

            case ADULT -> {
                totalPrice += request.getNoOfTickets() * Constant.FEE_25;
                adultQuantity += request.getNoOfTickets();
            }

            default -> throw new IllegalArgumentException("Invalid ticket request");
        }
    }

    private interface Constant{
        Long INVALID_ACCOUNT_ID = 0L;
        Integer NO_QUANTITY = 0;
        Integer MAX_FEE = 25;
        Integer FEE_15 = 15;
        Integer FEE_25 = 25;
    }
}
