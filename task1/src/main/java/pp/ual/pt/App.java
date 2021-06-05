package pp.ual.pt;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


class DepositThread implements Runnable {
    private Random random;
    private double amount = 0;
    private double amountDeposited = 0;
    private BankAccount bankAccount;

    public DepositThread(Random random, BankAccount bankAccount) {
        this.random = random;
        this.bankAccount = bankAccount;
    }

    public void run() {
        while (amountDeposited < 50) {
            int time = Math.min(3, random.nextInt(3) + 1);

            amount = Math.min(10, random.nextInt(10) + 1);

            bankAccount.deposit(amount);

            amountDeposited += amount;

            try {
                Thread.sleep(time * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("ID: " + Thread.currentThread().getId() + " CurrentAmount: " + bankAccount.getBalance() + " DepositAmount: " + amount);
        }
    }
}

class BankAccount{
    private double balance;

    public BankAccount(double balance){
        this.balance = balance;
    }

    public double getBalance(){
        return balance;
    }

    public synchronized void deposit(double amount){
        balance += amount;
        notifyAll();
    }

    public synchronized void withdraw(double amount){
        while(balance < amount){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        balance -= amount;
        notifyAll();
    }
}

class WithdrawThread implements Runnable {
    private Random random;
    private BankAccount bankAccount;
    private double amount = 0;
    private int n = 0;

    public WithdrawThread(Random random, BankAccount bankAccount) {
        this.random = random;
        this.bankAccount = bankAccount;
    }

    public void run() {
        while (n < 4) {
            int time = Math.min(5, random.nextInt(5) + 2);

            amount = Math.min(20, random.nextInt(20) + 5);

            bankAccount.withdraw(amount);

            n++;

            System.out.println("ID: " + Thread.currentThread().getId() + " CurrentAmount: " + bankAccount.getBalance() + " NumberWithdraws: " + n);

            try {
                Thread.sleep(time * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }
}
public class App 
{
    public static void main( String[] args )
    {
        Random random = new Random();

        BankAccount bankAccount = new BankAccount(0);

       /* DepositThread depositThread = new DepositThread(random,bankAccount);

        WithdrawThread withdrawThread = new WithdrawThread(random, bankAccount);

        new Thread(depositThread).start();
        new Thread(withdrawThread).start();*/

        ExecutorService executer = Executors.newCachedThreadPool();
        executer.execute(new DepositThread(random, bankAccount));
        executer.execute(new WithdrawThread(random, bankAccount));
        executer.shutdown();

    }
}
