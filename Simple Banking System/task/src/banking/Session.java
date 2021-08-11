
package banking;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
/**
 * This class manages of all procedure
 * Creates new object BankAccount and puts it into HashMap
 * with account number as key
 * */
public class Session {
    private BankAccount bankAccount;
    private Map<Long, BankAccount> mapOfBankAccounts = new HashMap<>();
    private Scanner scanner = new Scanner(System.in);
    private int command;
    private Connection connection;

    public Session(Connection connection) {
        this.connection = connection;
    }

    /*
     * method for not logged users
     * if user log in successfully method return true
     * else return false
     * it is helping in main method to choose proper state of session
     * */
    public boolean startSession() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account");
        System.out.println("0. Exit");
        command = scanner.nextInt();
   //     System.out.println();

        switch (command) {
            case 1: {
                bankAccount = new BankAccount();
                mapOfBankAccounts.put(bankAccount.getAccountNumber(), bankAccount);
                    try (Statement statement = connection.createStatement()) {
                        statement.executeUpdate(
                                "INSERT INTO card VALUES " +
                                        String.format("('%d', '%s', '%s', '%d')",
                                                bankAccount.getId(), bankAccount.getAccountNumber(),
                                                bankAccount.getPIN(), bankAccount.getBalance())
                        );
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                return false;
            }
            case 2: {
                System.out.println("Enter your card number:");
                long acc = scanner.nextLong();
                scanner.nextLine();
                System.out.println("Enter your PIN:");
                String pin = scanner.nextLine();
                try {
                    bankAccount = logToAccount(acc, pin);
                    System.out.println("You have successfully logged in!");
                    return true;
                } catch (Exception e) {
                    System.out.println("Wrong card number or PIN");
                    return false;
                }
            }
            case 0: {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Bye!");
                System.exit(0);
            }
            break;
            default:
                System.out.println("Wrong command!");
        }
        return false;
    }

    /*
     * method for logged users
     * this method return true as a result of commands which not expected to log out
     * else return false
     * it is helping in main method to choose proper state of session
     * */
    public boolean userLogged() {
        System.out.println("1. Balance");
        System.out.println("2. Log out");
        System.out.println("0. Exit");
        command = scanner.nextInt();

        switch (command) {
            case 1: {
                System.out.println("Balance: " + bankAccount.getBalance());
                return true;
            }
            case 2: {
                System.out.println("You have successfully logged out!");
                return false;
            }
            case 0: {
                System.out.println("Bye!");
                scanner.close();
                System.exit(0);
                return false;
            }
            default:
                System.out.println("Wrong command!");
                return true;
        }
    }

    /*
     * authorisation to account
     * if account number is founded in hashmap of bank accounts
     * and pin is correct method return account number
     * else return exception to indicate not successful log in
     * */
    private BankAccount logToAccount(long accountNumber, String PIN) throws Exception {
        if (mapOfBankAccounts.containsKey(accountNumber)) {
            if (mapOfBankAccounts.get(accountNumber).getPIN().equals(PIN)) {
                return mapOfBankAccounts.get(accountNumber);
            }
        }
        throw new Exception();
    }

}