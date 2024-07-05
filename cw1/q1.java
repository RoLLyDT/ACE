//Maksim KOZLOV

import java.util.*;

class Transaction {
    private final int time;
    private final int price;
    private int amount;

    //Constructor to initialize objects
    public Transaction(int time, int amount, int price){
        this.time = time;
        this.price = price;
        this.amount = amount;
    }

    //We are not using primitive data type "int" to being able to use compareTo
    public Integer getTime() {
        return time;
    }

    public Integer getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }

    // Methods that define the rules for comparing two transactions
    // if s1 > s2 => "+" ; s1 < s2 => "-" ; s1 = s2 => "0"
    public int compareByTimeTo(Transaction o) {
        return this.getTime().compareTo(o.getTime());
    }

    public int compareByPriceTo(Transaction o){
        if (Objects.equals(this.getPrice(), o.getPrice())){
            return this.getTime().compareTo(o.getTime());
        }
        else
            return -this.getPrice().compareTo(o.getPrice()); //to get -int in case if Price 10 compared with price 15(as an example), -int.
    }

}

class PriceComparator implements Comparator<Transaction>{
    @Override
    public int compare(Transaction o1, Transaction o2) {
        return o1.compareByPriceTo(o2);
    }
}

class TimeComparator implements Comparator<Transaction>{
    @Override
    public int compare(Transaction o1, Transaction o2) {
        return o1.compareByTimeTo(o2);
    }
}

public class q1 {
    public static void main(String[] args) {
        LinkedList<Transaction> transactions = new LinkedList<>(); // List initialization from Constructor
        int profit = 0;

        // To read inserted data
        Scanner scanner = new Scanner(System.in);
        int n = Integer.parseInt(scanner.nextLine()); // As all input should be legal -> first input should be int value

        // Loop function with main processes
        for (int i = 0; i < n; i++){
            String s = scanner.nextLine();  // Scan all input after the amount of total txs
            String[] values = s.split(" ");  // Divide string into substrings at the specified regular expression
            //if(s.isEmpty()) break;

            // Store substrings into int values x and y by parsing string into int
            int x = Integer.parseInt(values[0]);
            int y = Integer.parseInt(values[1]);
            String z; //Principle (z)


            // If s.split with no limit got 3rd value it means that it's the last input, as it's mentioned, all inputs are legal.
            if (values.length < 3) {
                transactions.add(new Transaction(i, x, y)); // Main part of storing data of shares and their prices.
            }
            else{
                z = values[2];

                Comparator<Transaction> comparator;
                if (z.equals("A") || z.equals("B"))
                    comparator = new TimeComparator();
                else
                    comparator = new PriceComparator();
                transactions.sort(comparator);
                if (z.equals("B") || z.equals("D"))
                    Collections.reverse(transactions);

                while (x > 0){
                    Transaction t = transactions.getFirst(); //Transaction class instance. Экземпляр класса транзакции
                    int amount = Math.min(x, t.getAmount());
                    t.setAmount(t.getAmount() - amount);
                    x -= amount;
                    profit += amount * (y - t.getPrice());
                    if (t.getAmount() == 0)
                        transactions.remove();
                }
            }
        }
        scanner.nextLine();
        System.out.println(profit);
    }
}
