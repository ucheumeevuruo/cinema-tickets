package thirdparty.paymentgateway;

public class paymentServiceImpl implements paymentService {

    @Override
    public void makePayment(long accountId, int totalAmountToPay) {
        // Real implementation omitted, assume working code will take the payment using a card pre linked to the account.
    }

}
