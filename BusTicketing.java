import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class BusTicketing {

    static Scanner scanner = new Scanner(System.in);
    static Random random = new Random();

    static ArrayList<Integer> seats = new ArrayList<>();
    static HashMap<Integer, String> bookings = new HashMap<>();
    static HashMap<Integer, Integer> seatStart = new HashMap<>();
    static HashMap<Integer, Integer> seatEnd = new HashMap<>();

    static String username;
    static String password;

    static String[] route = {
        "Bataan",
        "Pampanga",
        "Bulacan",
        "Manila"
    };

    static int[] menuOrder = { 0, 3, 1, 2 };

    static int[][] distanceKm = {
        {   0,  65, 100, 142 },
        {  65,   0,  35,  77 },
        { 100,  35,   0,  42 },
        { 142,  77,  42,   0 }
    };

    static final int BASE_FARE = 50;
    static final double RATE_PER_KM = 2.10;

    public static void main(String[] args) {

        for (int i = 1; i <= 20; i++) {
            seats.add(i);
        }

        System.out.println("=================================");
        System.out.println("       BUS TICKETING SYSTEM");
        System.out.println("=================================");

        createAccount();
        login();
        bookBus();

        scanner.close();
    }

   static void validateUsername(String input) {
    if (input.length() < 6 || input.length() > 12) {
        throw new IllegalArgumentException(
            "Username must be 6 to 12 characters."
        );
    }

    if (!input.matches("[A-Za-z0-9\\p{Punct}]+")) {
        throw new IllegalArgumentException(
            "Username may only contain letters, numbers, or special characters (no spaces)."
        );
    }
}

static void validatePassword(String input) {
    if (input.length() < 8 || input.length() > 12) {
        throw new IllegalArgumentException(
            "Password must be 8 to 12 characters."
        );
    }

    if (!input.matches("[A-Za-z0-9\\p{Punct}]+")) {
        throw new IllegalArgumentException(
            "Password may only contain letters, numbers, or special characters (no spaces)."
        );
    }
}

    static void createAccount() {

        System.out.println("\nCreate Account");

        while (true) {

            try {
                System.out.print("Enter username (6-12 characters): ");
                String inputUsername = scanner.nextLine();

                validateUsername(inputUsername);

                username = inputUsername;
                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        while (true) {

            try {
                System.out.print("Enter password (8-12 characters): ");
                String inputPassword = scanner.nextLine();

                validatePassword(inputPassword);

                password = inputPassword;
                break;

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        System.out.println("Account created successfully!");
    }

    static void login() {

        System.out.println("\nLogin");

        while (true) {

            try {
                System.out.print("Enter username: ");
                String inputUsername = scanner.nextLine();

                validateUsername(inputUsername);

                System.out.print("Enter password: ");
                String inputPassword = scanner.nextLine();

                validatePassword(inputPassword);

                if (inputUsername.equals(username)
                        && inputPassword.equals(password)) {

                    System.out.println("Login successful!");
                    break;

                } else {
                    System.out.println("Incorrect username or password.");
                }

            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    static int getFare(int start, int end) {
        double raw = BASE_FARE + distanceKm[start][end] * RATE_PER_KM;
        return (int) (Math.round(raw / 5.0) * 5);
    }

    static void showFareTable() {

        System.out.println("\nFare Table (PHP)");
        System.out.println("---------------------------------");

        for (int i = 0; i < route.length; i++) {
            for (int j = i + 1; j < route.length; j++) {
                System.out.printf("%-10s <-> %-10s : PHP %d%n",
                        route[i], route[j], getFare(i, j));
            }
        }
    }

    static void bookBus() {

        System.out.println("\n=================================");
        System.out.println("           BUS BOOKING");
        System.out.println("=================================");

        System.out.println("\nAvailable Locations:");

        for (int i = 0; i < menuOrder.length; i++) {
            System.out.println((i + 1) + ". " + route[menuOrder[i]]);
        }

        showFareTable();

        System.out.print("\nEnter starting location (1-4): ");
        int startChoice = scanner.nextInt();

        System.out.print("Enter destination (1-4): ");
        int endChoice = scanner.nextInt();

        if (startChoice < 1 || startChoice > 4
                || endChoice < 1 || endChoice > 4
                || startChoice == endChoice) {

            System.out.println("Invalid route.");
            return;
        }

        int start = menuOrder[startChoice - 1];
        int end = menuOrder[endChoice - 1];

        int fare = getFare(start, end);

        int legStart = Math.min(start, end);
        int legEnd = Math.max(start, end);

        System.out.println("\nTrip:");
        System.out.println(route[start] + " -> " + route[end]);
        System.out.println("Fare: PHP " + fare);

        boolean stopOverPassenger =
                Math.abs(start - end) == 1;

        int seatNumber;

        if (stopOverPassenger) {

            seatNumber = findRandomSeat(legStart, legEnd);

            if (seatNumber == -1) {
                System.out.println(
                    "\nNo available seats for this stopover route."
                );
                return;
            }

            System.out.println(
                "\nA random available seat was assigned: "
                + seatNumber
            );

        } else {

            System.out.println("\nSeat Preference");
            System.out.println("1. Window");
            System.out.println("2. Near Driver");
            System.out.println("3. Back");

            System.out.print("Choose your preference: ");
            int preference = scanner.nextInt();

            seatNumber = findSeat(preference, legStart, legEnd);

            if (seatNumber == -1) {
                System.out.println("\nNo available seats.");
                return;
            }

            System.out.println(
                "\nYour recommended seat is: "
                + seatNumber
            );
        }

        System.out.println("\nPayment Option");
        System.out.println("Amount due: PHP " + fare);
        System.out.println("1. Pay Now");
        System.out.println("2. Reserve");

        System.out.print("Choose option: ");
        int paymentChoice = scanner.nextInt();
        scanner.nextLine();

        if (paymentChoice == 1) {

            bookings.put(
                seatNumber,
                username + " - PAID PHP " + fare + " - "
                + route[start] + " to " + route[end]
            );

            seatStart.put(seatNumber, legStart);
            seatEnd.put(seatNumber, legEnd);

            System.out.println("\nPayment of PHP " + fare + " successful!");
            System.out.println(
                "Seat " + seatNumber + " is now booked."
            );

        } else if (paymentChoice == 2) {

            String deadline;

            while (true) {

                System.out.print(
                    "Enter reservation deadline (YYYY-MM-DD HH:MM): "
                );

                deadline = scanner.nextLine();

                if (deadline.startsWith("2026-")) {
                    break;
                } else {
                    System.out.println(
                        "Invalid input. The reservation deadline must be within 2026."
                    );
                }
            }

            bookings.put(
                seatNumber,
                username + " - RESERVED (PHP " + fare + " unpaid) until "
                + deadline + " - " + route[start] + " to " + route[end]
            );

            seatStart.put(seatNumber, legStart);
            seatEnd.put(seatNumber, legEnd);

            System.out.println("\nSeat reserved successfully!");
            System.out.println("Reservation deadline: " + deadline);
            System.out.println("Please pay PHP " + fare + " before the deadline.");
        }

        System.out.println("\n=================================");
        System.out.println("          BOOKING DETAILS");
        System.out.println("=================================");

        System.out.println("Passenger: " + username);
        System.out.println("From: " + route[start]);
        System.out.println("To: " + route[end]);
        System.out.println("Seat Number: " + seatNumber);
        System.out.println("Fare: PHP " + fare);
        System.out.println("Booking: " + bookings.get(seatNumber));
    }

    static int findSeat(int preference, int start, int end) {

        int preferredStart = 1;
        int preferredEnd = 20;

        if (preference == 1) {
            preferredStart = 6;
            preferredEnd = 15;
        } else if (preference == 2) {
            preferredStart = 1;
            preferredEnd = 5;
        } else if (preference == 3) {
            preferredStart = 16;
            preferredEnd = 20;
        }

        for (int i = preferredStart; i <= preferredEnd; i++) {
            if (isSeatAvailable(i, start, end)) {
                return i;
            }
        }

        for (int i = 1; i <= 20; i++) {
            if (isSeatAvailable(i, start, end)) {
                return i;
            }
        }

        return -1;
    }

    static int findRandomSeat(int start, int end) {

        ArrayList<Integer> availableSeats = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            if (isSeatAvailable(i, start, end)) {
                availableSeats.add(i);
            }
        }

        if (availableSeats.isEmpty()) {
            return -1;
        }

        return availableSeats.get(random.nextInt(availableSeats.size()));
    }

    static boolean isSeatAvailable(int seat, int start, int end) {

        if (!bookings.containsKey(seat)) {
            return true;
        }

        int existingStart = seatStart.get(seat);
        int existingEnd = seatEnd.get(seat);

        return end <= existingStart || start >= existingEnd;
    }
}