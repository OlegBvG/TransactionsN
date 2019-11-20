public class PaymentDetails {
    String fromAccountNum;
    String toAccountNum;
    private Long amount;

    public PaymentDetails(String fromAccountNum, String toAccountNum, Long amount) {
        this.fromAccountNum = fromAccountNum;
        this.toAccountNum = toAccountNum;
        this.amount = amount;
    }
    public String getFromAccountNum() {
        return fromAccountNum;
    }

    public String getToAccountNum() {
        return toAccountNum;
    }

    public Long getAmount() {
        return amount;
    }

}
