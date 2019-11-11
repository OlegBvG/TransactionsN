import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class BankTest {

    private ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();
    private Bank bank;
    private int numberAccounts = 100;
//    private int NTHREADS = 1500;

//    ExecutorService exec = Executors.newFixedThreadPool(NTHREADS);
//    ExecutorService exec = Executors.newCachedThreadPool();
//    ExecutorService exec = Executors.newWorkStealingPool();

    @Before
    public void setUp() {
        String numberAcc = "00";

        for (int i = 1; i < numberAccounts; i++){
            numberAcc = String.valueOf(i).trim();
            accounts.put(numberAcc, new Account(numberAcc, 1000000, false));
        }

        bank = new Bank(accounts);
    }

    @After
    public void tearDown() {
    }


    @Test
    public  void testAccBank() throws InterruptedException {
        AtomicLong inputBalanceBank = bank.getBalanceBank();
        System.out.println("Входящий Баланс банка: " + inputBalanceBank);

        ConcurrentLinkedQueue<PaymentDetails> queue = new ConcurrentLinkedQueue<PaymentDetails>();

         AtomicReference<String> fromAccountNum = new AtomicReference<>();
         AtomicReference<String> toAccountNum = new AtomicReference<>();
         AtomicLong amount = new AtomicLong();

            for (int i = 1; i <= 1500; i++) {
                fromAccountNum.set(String.valueOf((int) ((Math.random() * (numberAccounts - 1)) + 1)));
                toAccountNum.set(String.valueOf((int) ((Math.random() * (numberAccounts - 1)) + 1)));

            while (fromAccountNum.equals(toAccountNum.get())) {
                toAccountNum.set(String.valueOf((int) ((Math.random() * (numberAccounts - 1)) + 1)));
            }

            amount.set((long) (Math.random() * 100000));

            queue.add(new PaymentDetails(fromAccountNum.get(), toAccountNum.get(), amount.get()));

            }

        while (queue.size()>0){
            PaymentDetails paymentDetailsQueue = queue.poll();
            Runnable r = () -> {
                try {
                    bank.transfer(paymentDetailsQueue);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            };

//            exec.execute(r);

            Thread t = new Thread(r);
            t.start();
        }

        System.out.println("currentThread - " + Thread.currentThread().getName());
        Thread.currentThread().join(15000);

        accounts.forEach((k, v) ->
                System.out.println("account = " + k + " / " + v.getAccNumber() + "; сумма = " + v.getMoney()  + "; блокировка = " + v.isLocked() ) );

        AtomicLong outputBalanceBank = bank.getBalanceBank();
        System.out.println("Исходящий Баланс банка: " + outputBalanceBank);

        Assert.assertEquals(inputBalanceBank.get(), outputBalanceBank.get());
    }
}
