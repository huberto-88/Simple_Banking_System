
package banking;

import java.sql.Connection;
import java.sql.ResultSet;
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
        System.out.println();

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
                scanner.nextLine();
                String acc = scanner.nextLine().trim();
                System.out.println("Enter your PIN:");
                String pin = scanner.nextLine();
                try {
                    Statement statement = connection.createStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT * FROM card WHERE number == " + acc +
                            " AND pin == " + pin);
                    if (resultSet.next() ) {
                        bankAccount = new BankAccount(Long.valueOf(acc), pin, resultSet.getInt("balance"));
                        System.out.println("You have successfully logged in!");
                        return true;
                    } else {
                        System.out.println("Wrong card number or PIN");
                        return false;
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            }
            case 0: {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("Bye!");
                System.exit(0);
                return true;
            }
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
        System.out.println("2. Add income");
        System.out.println("3. Do transfer");
        System.out.println("4. Close account");
        System.out.println("5. Log out");
        System.out.println("0. Exit");
        command = scanner.nextInt();
        scanner.nextLine();

        switch (command) {
            case 1: {
                System.out.println("Balance: " + bankAccount.getBalance());
                return true;
            }
            case 2: {
                System.out.println("Enter income: ");
                long income = scanner.nextLong();
                bankAccount.setBalance(income);
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("UPDATE card SET balance = balance + " + income + " WHERE number == "
                            + bankAccount.getAccountNumber());
                    System.out.println("Income was added!");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            }

            case 3: {
                doTransfer();
                return true;
            }
            case 4: {
                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("DELETE FROM card WHERE number == " + bankAccount.getAccountNumber()
                    );
                    System.out.println("The account has been closed!");
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            break;

            case 5: {
                System.out.println("You have successfully logged out!");
                return false;
            }

            case 0: {
                System.out.println("Bye!");
                scanner.close();
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
            break;
            default:
                System.out.println("Wrong command!");
                return true;
        }
        return true;
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

    private void doTransfer() {
        System.out.println("Enter card number: ");
        long cardNumber = scanner.nextLong();
        if (cardNumberValidator(cardNumber) == false) {
            System.out.println("Probably you made a mistake in the card number. Please try again!");
            return;
        } else if (cardNumber == bankAccount.getAccountNumber()) {
            System.out.println("You can't transfer money to the same account!");
            return;
        } else if (checkIsAccountExist(cardNumber) == false) {
            System.out.println("Such a card does not exist.");
            return;
        }

        System.out.println("Enter how much money you want to transfer:");
        long income = scanner.nextLong();
        if (bankAccount.getBalance() < income) {
            System.out.println("Not enough money!");
            return;
        }

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("UPDATE card SET balance = balance + " + income + " WHERE number == " + cardNumber
            );
            statement.executeUpdate("UPDATE card SET balance = balance - " + income + " WHERE number == "
                    + bankAccount.getAccountNumber());
            System.out.println("Success!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIsAccountExist(long cardNumber) {
        ResultSet resultSet;
        try {
            Statement statement = connection.createStatement();
             resultSet = statement.executeQuery("SELECT * FROM card WHERE number == " + cardNumber);
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean cardNumberValidator(long cardNumber) {
        char[] temp = String.valueOf(cardNumber).toCharArray();
        int[] array = new int[temp.length];
        int sum = 0;

        for (int i = 0; i < temp.length; i++) {
            array[i] = Character.getNumericValue(temp[i]);
            sum += i % 2 == 0 ? (array[i] * 2 > 9 ? array[i] * 2 - 9 : array[i] * 2) : array[i];
        }
        return sum % 10 == 0;

    }

}