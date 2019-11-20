import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class BankTest {

    private ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();
    private Bank bank;

//    public final CountDownLatch START = new CountDownLatch(10);
    private final int ACCOUNT_NUMBERS = 40;
    private final int THREADS = 8;
    private final int TRANSFERS_COUNT = 1_00;

//  --  ExecutorService exec = Executors.newCachedThreadPool();
//    ExecutorService exec = Executors.newFixedThreadPool(THREADS);
//    ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREADS);
    ThreadPoolExecutor exec = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    @Before
    public void setUp() {
        String numberAcc;


        for (int i = 1; i <= ACCOUNT_NUMBERS; i++) {
            numberAcc = String.valueOf(i).trim();
            accounts.put(numberAcc, new Account(numberAcc, 1000000, false));
        }

        bank = new Bank(accounts);
    }

    @After
    public void tearDown() {
    }


    @Test
    public void testAccBank() throws InterruptedException {
        AtomicLong inputBalanceBank = bank.getBalanceBank();
        System.out.println("Входящий Баланс банка: " + inputBalanceBank);

        String fromAccountNum;
        String toAccountNum;
        long amount;

        PaymentDetails paymentDetails;

        for (int i = 1; i <= TRANSFERS_COUNT; i++) {
            fromAccountNum = (String.valueOf((int) ((Math.random() * (ACCOUNT_NUMBERS - 1)) + 1)));
            toAccountNum = (String.valueOf((int) ((Math.random() * (ACCOUNT_NUMBERS - 1)) + 1)));

            while (fromAccountNum.equals(toAccountNum)) {
                toAccountNum = (String.valueOf((int) ((Math.random() * (ACCOUNT_NUMBERS - 1)) + 1)));
            }

            do {
                amount = (long) (Math.random() * 55000);

            } while (amount == 0);


/*           if (amount > 50000 && bank.isFraud(fromAccountNum, toAccountNum, amount)) {
                paymentDetails = new PaymentDetails(fromAccountNum, toAccountNum, null);
            } else {
                paymentDetails = new PaymentDetails(fromAccountNum, toAccountNum, amount);
            }
            PaymentDetails finalPaymentDetails = paymentDetails;
            Runnable r = () -> {
                bank.transfer(finalPaymentDetails);
            };
*/
            //------------------

            if (amount > 50000 && bank.isFraud(fromAccountNum, toAccountNum, amount)) {
                amount = 0;
            }

            String finalFromAccountNum = fromAccountNum;
            String finalToAccountNum = toAccountNum;
            long finalAmount = amount;

            Runnable r = () -> {
                bank.transfer(finalFromAccountNum, finalToAccountNum, finalAmount);
            };

            //------------------

            exec.execute(r);
        }


            System.out.println("Active THREAD => " + Thread.activeCount());
        while (exec.getCompletedTaskCount()<TRANSFERS_COUNT)  Thread.sleep(100);
            exec.shutdown();

        while(!exec.isShutdown()) {
            System.out.println("Active THREAD in => " + Thread.activeCount());
            System.out.println("Active THREAD in => " + Thread.getAllStackTraces());
            Thread.sleep(100);
        }

            accounts.forEach((k, v) ->
                    System.out.println("account = " + k + " / " + v.getAccNumber() + "; сумма = " + v.getMoney() + "; блокировка = " + v.isLocked()));

            AtomicLong outputBalanceBank = bank.getBalanceBank();
            System.out.println("Исходящий Баланс банка: " + outputBalanceBank);

            Assert.assertEquals(inputBalanceBank.get(), outputBalanceBank.get());
        }
    }

