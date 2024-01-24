package HOTELRESERVATIONSYSTEMJDBC;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;



public class HotelReservationSystem {
	
	private static final String url = "jdbc:mysql://localhost:3306/Hotel_bd";
	private static final String username = "root";
	private static final String password = "root";
	
	public static void main(String[] args) {
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} 
		catch (ClassNotFoundException e) {
		
			e.printStackTrace();
		}
		
		try {
			Connection connection = DriverManager.getConnection(url,username,password);
			while(true) {
				System.out.println();
				System.out.println("HOTEL RESERVATION SYSTEM");
				Scanner scanner = new Scanner(System.in);
				System.out.println("1. Reserve a Room");
				System.out.println("2. View Reservations");
				System.out.println("3. Get Room Number");
				System.out.println("4. Update Reservation");
				System.out.println("5. Delete Reservation");
				System.out.println("0. Exit");
				int choice = scanner.nextInt();
				switch (choice) {
				case 1:
					reserveroom(connection,scanner);
					break;
					
				case 2:
					viewreservation(connection);
					break;
					
				case 3:
					getroomnumber(connection,scanner);
					break;
					
				case 4:
					updatereservation(connection,scanner);
					break;
					
				case 5:
					deletereservation(connection,scanner);
					break;
					
				case 0:
					exit();
					scanner.close();
					return;
					
					default:
						System.out.println("Invalid Choice Try again...");
				}
				
			}
		}
		catch (SQLException e) {
		
			System.out.println(e.getMessage());
		}
		catch (InterruptedException e) {
			
			throw new RuntimeException(e);
			
		}
	}
	
	
	public static void reserveroom(Connection connection, Scanner scanner) {
		try {
			System.out.println("Enter guest Name");
			String guestname = scanner.next();
			System.out.println("Enter Room number");
			int roomnum = scanner.nextInt();
			System.out.println("Enter Contact number");
			String contactnum = scanner.next();
			
			String sql = "insert into reservations (guest_name, room_number , contact_number)"
					+ "values('"+ guestname + "', "+ roomnum +", '" + contactnum +"')";
			
			try (Statement statement = connection.createStatement()) {
				int rows = statement.executeUpdate(sql);
				
				if(rows > 0) {
					System.out.println("Reservation Succesfull");
				}else {
					System.out.println("reservation Failed");
				}
				
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void viewreservation(Connection connection) throws SQLException{
		String sql = "select reservation_id ,  guest_name , room_number , contact_number , reservation_date from reservations";
		
		  try (Statement statement = connection.createStatement()){
			  
			  ResultSet resultset = statement.executeQuery(sql);
			  
			  System.out.println("Current reservations: ");
			   System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
	            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number      | Reservation Date        |");
	            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
			  
			  while(resultset.next()) {
				  int reservationid = resultset.getInt("reservation_id");
				  String guestname = resultset.getString("guest_name");
				  int roomnum = resultset.getInt("room_number");
				  String contactnumber = resultset.getString("contact_number");
				  String reservationdate = resultset.getTimestamp("reservation_date").toString();
				  
				  System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |\n",
	                        reservationid, guestname, roomnum, contactnumber, reservationdate);
				  
			  }
			   System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
			  
		  }
	}
		  

    private static void getroomnumber(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
    private static void updatereservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE reservations SET guest_name = '" + newGuestName + "', " +
                    "room_number = " + newRoomNumber + ", " +
                    "contact_number = '" + newContactNumber + "' " +
                    "WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    

    private static void deletereservation(Connection connection , Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement()) {
                int affectedRows = statement.executeUpdate(sql);

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = " + reservationId;

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                return resultSet.next(); // If there's a result, the reservation exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Handle database errors as needed
        }
    }
    
    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while(i!=0){
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }

		  
	}


