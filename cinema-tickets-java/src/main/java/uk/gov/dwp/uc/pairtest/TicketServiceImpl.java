package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;

import java.util.Arrays;

import static java.util.Objects.nonNull;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */
    private Integer amount;
    private Integer quantity;
    private TicketPaymentService ticketPaymentService;
    private SeatReservationService seatReservationService;

    public TicketServiceImpl() {
        seatReservationService = new SeatReservationServiceImpl();
        ticketPaymentService = new TicketPaymentServiceImpl();
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        if(nonNull(accountId) && accountId == 0){
            throw new IllegalStateException("Invalid account");
        }

        Arrays.stream(ticketTypeRequests).forEach(ticketTypeRequest -> {
            processRequest(ticketTypeRequest);
        });

        if(quantity > 25){
            throw new IllegalStateException(" ");
        }

        ticketPaymentService.makePayment(accountId, amount);
        seatReservationService.reserveSeat(accountId, quantity);
    }

    private void processRequest(TicketTypeRequest ticketTypeRequest){
        switch (ticketTypeRequest.getTicketType()){
            case INFANT -> quantity += ticketTypeRequest.getNoOfTickets();

            case CHILD -> {
                amount += 15;
                quantity += ticketTypeRequest.getNoOfTickets();
            }

            case ADULT -> {
                amount += 25;
                quantity += ticketTypeRequest.getNoOfTickets();
            }

            default -> throw new IllegalStateException("Unexpected value: " + ticketTypeRequest.getTicketType());
        }
    }
}
