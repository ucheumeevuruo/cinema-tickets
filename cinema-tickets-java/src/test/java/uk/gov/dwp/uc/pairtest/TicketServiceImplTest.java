package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.enums.Type;

import static org.junit.jupiter.api.Assertions.*;


public class TicketServiceImplTest {

    private thirdparty.paymentgateway.paymentService paymentService;
    private SeatReservationService seatReservationService;
    private TicketService ticketService;

    @BeforeEach
    void setup(){
        // Mock the external services
        paymentService = Mockito.mock(thirdparty.paymentgateway.paymentServiceImpl.class);
        seatReservationService = Mockito.mock(SeatReservationServiceImpl.class);

        // Create an instance of TicketServiceImpl with mocked services
        ticketService = new TicketServiceImpl(paymentService, seatReservationService);

    }

    @Test
    public void testPurchaseWithValidAdultAndInfantTickets() {
        // Given
        TicketTypeRequest adultTicket = new TicketTypeRequest(Type.ADULT, 1);
        TicketTypeRequest infantTicket = new TicketTypeRequest(Type.INFANT, 1);

        // When
        assertDoesNotThrow(() ->
                ticketService.purchaseTickets(123L, adultTicket, infantTicket));
    }

    @Test
    public void testPurchaseWithValidAdultAndChildTickets() {
        // Given
        TicketTypeRequest adultTicket = new TicketTypeRequest(Type.ADULT, 2);
        TicketTypeRequest childTicket = new TicketTypeRequest(Type.CHILD, 1);

        // When
        assertDoesNotThrow (() ->
                ticketService.purchaseTickets(123L, adultTicket, childTicket));

    }

    @Test
    public void testPurchaseWithInvalidNoAdult() {
        // Given
        TicketTypeRequest childTicket = new TicketTypeRequest(Type.CHILD, 1);
        TicketTypeRequest infantTicket = new TicketTypeRequest(Type.INFANT, 1);

        // When / Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> ticketService.purchaseTickets(123L, childTicket, infantTicket));

        assertEquals("Child and Infant tickets cannot be purchased without an Adult ticket.", thrown.getMessage());
    }

    @Test
    public void testPurchaseExceedingTicketLimit() {
        // Given
        TicketTypeRequest adultTicket = new TicketTypeRequest(Type.ADULT, 26);

        // When / Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> ticketService.purchaseTickets(123L, adultTicket));

        assertEquals("Maximum exceeded: Cannot purchase more than 25 tickets.", thrown.getMessage());
    }

    @Test
    public void testPurchaseWithNoTickets() {
        // When / Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> ticketService.purchaseTickets(123L));

        assertEquals("Minimum quantity: The required quantity should be at least 1", thrown.getMessage());
    }

    @Test
    public void testPurchaseWithInfantTicketOnly() {
        // Given
        TicketTypeRequest infantTicket = new TicketTypeRequest(Type.INFANT, 1);

        // When / Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> ticketService.purchaseTickets(123L, infantTicket));

        assertEquals("Child and Infant tickets cannot be purchased without an Adult ticket.", thrown.getMessage());
    }

    @Test
    public void testPurchaseTicketWithoutType(){
        // Given
        TicketTypeRequest adultTicket = new TicketTypeRequest(null, 5);//Invalid request type

        // When / Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> ticketService.purchaseTickets(123L, adultTicket));

        assertEquals("Ticket type expected", thrown.getMessage());
    }

    @Test
    public void testPurchaseWithInvalidAccountId(){
        // Given
        TicketTypeRequest adultTicket = new TicketTypeRequest(null, 5);//Invalid request type
        TicketTypeRequest adultTicket2 = new TicketTypeRequest(null, 10);//Invalid request type
        TicketTypeRequest adultTicket3 = new TicketTypeRequest(null, 5);//Invalid request type

        // When / Then
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> ticketService.purchaseTickets(0L, adultTicket, adultTicket2, adultTicket3));

        assertEquals("The account id not valid", thrown.getMessage());
    }
}