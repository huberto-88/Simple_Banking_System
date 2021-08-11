package banking;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

public class BankAccount {
    private long accountNumber;
    private String PIN;
    private long balance;
    private Random random = new Random();
    static private int id;

    /*Construtor invoke method to generate correct acc number and pin*/
    public BankAccount() {
        id++;
        this.accountNumber = generateAccountNumber();
        this.PIN = generatePIN();
        balance = 0L;

        System.out.println("Your card has been created\n" +
                "Your card number:\n" + getAccountNumber());
        System.out.println("Your card PIN:\n" + getPIN());
        System.out.println();
    }

    public int getId() {
        return id;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public String getPIN() {
        return PIN;
    }

    public long getBalance() {
        return balance;
    }

    /**
     * this private method generate account number according to given specification
     * */
    private long generateAccountNumber() {
        String prefix = "400000";
        int[] arrCode = new int[10];

        for (int i = 0; i < 9; i++) {
            arrCode[i] = random.nextInt(10);
        }

        int sum = 8;
        for (int i = 0; i < 9; i += 2) {
            int temp = arrCode[i] * 2;
            temp = temp > 9 ? temp - 9 : temp;
            sum += temp + arrCode[i + 1];
        }
        arrCode[9] = sum % 10 == 0 ? 0 : 10 - (sum % 10);
        String suffix = Arrays.stream(arrCode).mapToObj(String::valueOf).collect(Collectors.joining());

        return Long.valueOf(prefix + suffix);
    }

    private String generatePIN() {
        int[] pin = new int[4];
        for (int i = 0; i < 4; i++) {
            pin[i] = random.nextInt(10);
        }
        return Arrays.stream(pin).mapToObj(String::valueOf).collect(Collectors.joining());
    }
}
