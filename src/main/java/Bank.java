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


    synchronized void transfer(PaymentDetails paymentDetails) {

        maxCountTransfer = Math.max(maxCountTransfer, countTransfer);
        ++countTransfer;

        logger.info(Bank.INPUT_HISTORY_MARKER,"Thread: " + Thread.currentThread().getName() + " == " + "TRANSFER ===  "
                + paymentDetails.getFromAccountNum() + " => " + paymentDetails.getToAccountNum()
                + " => " + paymentDetails.getAmount() + " countTransfer " + countTransfer + " maxCountTransfer " + maxCountTransfer );

        if (paymentDetails.getAmount() == null) {


                accounts.get(paymentDetails.getFromAccountNum()).setLocked(true);
                accounts.get(paymentDetails.getToAccountNum()).setLocked(true);

                logger.info(Bank.INPUT_HISTORY_MARKER, "\t\tLocked next accounts: "
                        + paymentDetails.getFromAccountNum() + " and " + paymentDetails.getToAccountNum());

        } else {

            if (!accounts.get(paymentDetails.getFromAccountNum()).isLocked() && !accounts.get(paymentDetails.getToAccountNum()).isLocked()) {

                        accounts.get(paymentDetails.getFromAccountNum()).debetMoney(paymentDetails.getAmount());
                        accounts.get(paymentDetails.getToAccountNum()).addMoney(paymentDetails.getAmount());

                    logger.info(Bank.INPUT_HISTORY_MARKER, "\t\tTRANSFER: from "
                            + paymentDetails.getFromAccountNum() + " to " + paymentDetails.getToAccountNum()
                            + "; amount = " + paymentDetails.getAmount() );

                }
            }
        }

     void  transfer(String fromAccountNum, String toAccountNum, long amount) {

        maxCountTransfer = Math.max(maxCountTransfer, countTransfer);
        ++countTransfer;

        logger.info(Bank.INPUT_HISTORY_MARKER,"Thread: " + Thread.currentThread().getName() + " == " + "TRANSFER ===  "
                + fromAccountNum + " => " + toAccountNum
                + " => " + amount + " countTransfer " + countTransfer + " maxCountTransfer " + maxCountTransfer );

        System.out.println("Thread: " + Thread.currentThread().getName() + " == " + "TRANSFER ===  "
                + fromAccountNum + " => " + toAccountNum
                + " => сумма " + amount + " countTransfer " + countTransfer + " maxCountTransfer " + maxCountTransfer );

        if (amount == 0) {

            accounts.get(fromAccountNum).setLocked(true);
            accounts.get(toAccountNum).setLocked(true);

            logger.info(Bank.INPUT_HISTORY_MARKER, "\t\tLocked next accounts: "
                    + fromAccountNum + " and " + toAccountNum );

        } else {

            if (!accounts.get(fromAccountNum).isLocked() && !accounts.get(toAccountNum).isLocked()) {

                if (fromAccountNum.compareTo(toAccountNum) > 0) {

                    synchronized (accounts.get(fromAccountNum)) {
                        accounts.get(fromAccountNum).debetMoney(amount);
                        synchronized (accounts.get(toAccountNum)) {
                            accounts.get(toAccountNum).addMoney(amount);
                        }
                    }
                } else {
                    synchronized (accounts.get(toAccountNum)) {
                        accounts.get(toAccountNum).addMoney(amount);
                        synchronized (accounts.get(fromAccountNum)) {
                            accounts.get(fromAccountNum).debetMoney(amount);

                        }
                    }


//                synchronized (this) {
//                    accounts.get(fromAccountNum).debetMoney(amount);
//                    accounts.get(toAccountNum).addMoney(amount);
//                }


                        logger.info(Bank.INPUT_HISTORY_MARKER, "\t\tTRANSFER: from "
                                + fromAccountNum + " to " + toAccountNum
                                + "; amount = " + amount);

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

}
