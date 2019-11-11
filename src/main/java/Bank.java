import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Bank

{
    private static Logger logger = LoggerFactory.getLogger(Bank.class);
    private static final Marker INPUT_HISTORY_MARKER = MarkerFactory.getMarker("INPUT_HISTORY");
    private ConcurrentHashMap<String, Account> accounts;



    Bank(ConcurrentHashMap<String, Account> accounts) {
        this.accounts = accounts;
    }

    private volatile int countTransfer = 0;
    private volatile int maxCountTransfer = 0;
    private volatile boolean doLock;

    private final Random random = new Random();

    public synchronized boolean isFraud(String fromAccountNum, String toAccountNum, long amount)
        throws InterruptedException
    {
        Thread.sleep(1000);
        return random.nextBoolean();
    }

    /**
     * TODO: реализовать метод. Метод переводит деньги между счетами.
     * Если сумма транзакции > 50000, то после совершения транзакции,
     * она отправляется на проверку Службе Безопасности – вызывается
     * метод isFraud. Если возвращается true, то делается блокировка
     * счетов (как – на ваше усмотрение)
     */

      void transfer(PaymentDetails paymentDetails) throws InterruptedException {

        maxCountTransfer = Math.max(maxCountTransfer, countTransfer);

        logger.info(Bank.INPUT_HISTORY_MARKER,"Thread: " + Thread.currentThread().getName() + " == " + "transfer ===  "
                + paymentDetails.getFromAccountNum() + " => " + paymentDetails.getToAccountNum() + " => " + paymentDetails.getAmount() + " countTransfer " + countTransfer + " maxCountTransfer " + maxCountTransfer );
        System.out.println("Thread: " + Thread.currentThread().getName() + " == " + "transfer ===  "
                + paymentDetails.getFromAccountNum() + " => " + paymentDetails.getToAccountNum() + " => " + paymentDetails.getAmount() + " countTransfer " + countTransfer++ + " maxCountTransfer " + maxCountTransfer );

        if (accounts.containsKey(paymentDetails.getFromAccountNum()) && accounts.containsKey(paymentDetails.getToAccountNum())) {

            if (!accounts.get(paymentDetails.getFromAccountNum()).isLocked() && !accounts.get(paymentDetails.getToAccountNum()).isLocked()) {
                doLock = false;

                if (paymentDetails.getAmount() > 50000){
                    doLock = isFraud(paymentDetails.getFromAccountNum(), paymentDetails.getToAccountNum(), paymentDetails.getAmount());
                }

                synchronized (this) {

                    if (doLock) {
                        accounts.get(paymentDetails.getFromAccountNum()).setLocked(true);
                        accounts.get(paymentDetails.getToAccountNum()).setLocked(true);


                        logger.info(Bank.INPUT_HISTORY_MARKER, "Locked next accounts: "
                                + paymentDetails.getFromAccountNum() + " and " + paymentDetails.getToAccountNum() + "; countTransfer " + (countTransfer - 1));

                    } else {
                        accounts.get(paymentDetails.getFromAccountNum()).debetMoney(paymentDetails.getAmount());
                        accounts.get(paymentDetails.getToAccountNum()).addMoney(paymentDetails.getAmount());
                        logger.info(Bank.INPUT_HISTORY_MARKER, "Transfer: from "
                                + paymentDetails.getFromAccountNum() + " to " + paymentDetails.getToAccountNum() + "; amount = " + paymentDetails.getAmount() + "; countTransfer " + (countTransfer - 1));

                    }
                }
            }
        }
    }

    /**
     * TODO: реализовать метод. Возвращает остаток на счёте.
     */
    public long getBalance(String accountNum)
    {
        return accounts.get(accountNum).getMoney();
    }

    public AtomicLong getBalanceBank(){
        AtomicLong totalBalance = new AtomicLong();
        accounts.forEach((k, v) -> totalBalance.addAndGet(v.getMoney()));
        return totalBalance;
    }

    public ConcurrentHashMap<String, Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ConcurrentHashMap<String, Account> accounts) {
        this.accounts = accounts;
    }

    public Random getRandom() {
        return random;
    }
}
