package thirdparty.paymentgateway;

public interface paymentService {

    void makePayment(long accountId, int totalAmountToPay);

}
