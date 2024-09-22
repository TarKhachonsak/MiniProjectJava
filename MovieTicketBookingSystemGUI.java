import java.util.ArrayList;
import javax.swing.*;

// คลาสหลักของระบบการจองตั๋วภาพยนตร์
public class MovieTicketBookingSystemGUI extends JFrame {

    private final ArrayList<Ticket> tickets;
    private final ArrayList<Movie> movies;

    public static void main(String[] args) {
        MovieTicketBookingSystemGUI frame = new MovieTicketBookingSystemGUI();
        frame.setVisible(true);
    }

    // กำหนดค่าหน้าต่าง GUI
    public MovieTicketBookingSystemGUI() {
        setTitle("Movie Ticket Booking System"); //ชื่อหน้าต่าง
        setSize(500, 400); //ขนาด
        setDefaultCloseOperation(EXIT_ON_CLOSE); //การปิดหน้าต่าง

        tickets = new ArrayList<>(); //เพื่อเก็บตั๋วที่จองแล้ว
        movies = new ArrayList<>(); //เก็บรายการภาพยนตร์ที่พร้อมให้จอง

        // ข้อมูลภาพยนตร์และเวลาแสดงตัวอย่าง
        Movie inception = new Movie("Inception", "Action", 120);
        inception.addShowTime(new ShowTime("10:00 AM", 5));
        inception.addShowTime(new ShowTime("01:00 PM", 5));
        movies.add(inception);

        Movie avenger = new Movie("Avenger", "Action", 130);
        avenger.addShowTime(new ShowTime("05:00 PM", 5));
        movies.add(avenger);

        // สร้างเมนูบาร์
        createMenuBar();
    }

    // ฟังก์ชันสร้างเมนูบาร์
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // เมนู "Booking"
        JMenu bookingMenu = new JMenu("การจอง");

        // เมนูย่อยสำหรับเริ่มการจองตั๋วใหม่
        JMenuItem newBookingMenuItem = new JMenuItem("การจองตั๋วใหม่");
        newBookingMenuItem.addActionListener(e -> startNewBooking());
        bookingMenu.add(newBookingMenuItem);

        // เมนูย่อยสำหรับแสดงประวัติการจอง
        JMenuItem viewBookingHistoryMenuItem = new JMenuItem("ดูประวัติการจอง");
        viewBookingHistoryMenuItem.addActionListener(e -> viewBookingHistory());
        bookingMenu.add(viewBookingHistoryMenuItem);

        // เมนูย่อยสำหรับยกเลิกการจอง
        JMenuItem cancelBookingMenuItem = new JMenuItem("ยกเลิกการจอง");
        cancelBookingMenuItem.addActionListener(e -> cancelBooking());
        bookingMenu.add(cancelBookingMenuItem);

        menuBar.add(bookingMenu);

        setJMenuBar(menuBar);
    }

    // ฟังก์ชันเริ่มการจองตั๋วใหม่
    private void startNewBooking() {
        // ให้ผู้ใช้เลือกภาพยนตร์จากรายชื่อ
        String[] movieTitles = movies.stream().map(Movie::getTitle).toArray(String[]::new);
        String selectedMovie = (String) JOptionPane.showInputDialog(this, "เลือกภาพยนตร์:", "จองตั๋ว", // เพื่อแสดงกล่องข้อความให้ผู้ใช้เลือกภาพยนตร์
                JOptionPane.PLAIN_MESSAGE, null, movieTitles, movieTitles[0]); //JOptionPane.PLAIN_MESSAGE กำหนดให้เป็นประเภทของไดอะล็อกที่ไม่มีไอคอน

        // ตรวจสอบว่าผู้ใช้ได้เลือกภาพยนตร์
        if (selectedMovie != null) {
            // หา Movie ที่ผู้ใช้เลือก
            Movie movie = findMovie(selectedMovie);

            // ให้ผู้ใช้เลือกเวลาฉายของภาพยนตร์นั้น
            String[] showTimes = movie.getShowTimes().stream().map(Event::getTime).toArray(String[]::new);
            String selectedShowTime = (String) JOptionPane.showInputDialog(this, "เลือกเวลาฉาย:", "จองตั๋ว",
                    JOptionPane.PLAIN_MESSAGE, null, showTimes, showTimes[0]);

            // ตรวจสอบว่าผู้ใช้ได้เลือกเวลาฉาย
            if (selectedShowTime != null) {
                ShowTime showTime = findShowTime(movie, selectedShowTime);
                if (showTime != null) {
                    // แสดงที่นั่งที่มีให้เลือก
                    ArrayList<Seat> availableSeats = new ArrayList<>();
                    for (Seat seat : showTime.getSeats()) {
                        if (!seat.isBooked()) {
                            availableSeats.add(seat);
                        }
                    }
                    if (availableSeats.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "ไม่มีที่นั่งว่างสำหรับรอบนี้");
                        return;
                    }

                    String[] seatNumbers = availableSeats.stream().map(seat -> String.valueOf(seat.getSeatNumber())).toArray(String[]::new);
                    String selectedSeatNumber = (String) JOptionPane.showInputDialog(this, "เลือกที่นั่ง:", "จองตั๋ว",
                            JOptionPane.PLAIN_MESSAGE, null, seatNumbers, seatNumbers[0]);

                    if (selectedSeatNumber != null) {
                        Seat selectedSeat = availableSeats.stream()
                                .filter(seat -> seat.getSeatNumber() == Integer.parseInt(selectedSeatNumber))
                                .findFirst()
                                .orElse(null);

                        if (selectedSeat != null) {
                            selectedSeat.book();
                            User user = new User("1", "John Doe", "john.doe@example.com"); // Dummy user
                            Ticket newTicket = new Ticket(movie, showTime, selectedSeat, user);
                            tickets.add(newTicket);
                            JOptionPane.showMessageDialog(this, "คุณจองตั๋วสำหรับภาพยนตร์ " + movie.getTitle()
                                    + " เวลาฉาย " + selectedShowTime + " ที่นั่ง " + selectedSeatNumber + " สำเร็จแล้ว!");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "คุณยังไม่ได้เลือกที่นั่ง");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "เวลาฉายที่เลือกไม่ถูกต้อง");
                }
            } else {
                JOptionPane.showMessageDialog(this, "คุณยังไม่ได้เลือกเวลาฉาย");
            }
        } else {
            JOptionPane.showMessageDialog(this, "คุณยังไม่ได้เลือกภาพยนตร์");
        }
    }

    // ฟังก์ชันสำหรับดูประวัติการจอง
    private void viewBookingHistory() {
        if (tickets.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ไม่มีประวัติการจอง");
        } else {
            StringBuilder history = new StringBuilder();
            for (Ticket ticket : tickets) {
                history.append(ticket.getMovie().getDescription())
                        .append(", ").append(ticket.getShowTime().getDescription())
                        .append(", ที่นั่ง: ").append(ticket.getSeat().getSeatNumber())
                        .append(", ผู้ใช้: ").append(ticket.getUser().getName())
                        .append("\n");
            }
            JOptionPane.showMessageDialog(this, "Booking History:\n" + history.toString());
        }
    }

    // ฟังก์ชันสำหรับยกเลิกการจอง
    private void cancelBooking() {
        if (tickets.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ไม่มีรายการการจอง");
        } else {
            String message = "เลือกตั๋วที่จะยกเลิก:\n";
            for (int i = 0; i < tickets.size(); i++) {
                Ticket ticket = tickets.get(i);
                message += (i + 1) + ". หนัง: " + ticket.getMovie().getTitle()
                        + ", เวลาฉาย: " + ticket.getShowTime().getTime()
                        + ", ที่นั่ง: " + ticket.getSeat().getSeatNumber() + "\n";
            }

            // ให้ผู้ใช้เลือกหมายเลขตั๋วที่ต้องการยกเลิก
            String input = JOptionPane.showInputDialog(this, message);
            try {
                int ticketIndex = Integer.parseInt(input) - 1;

                // ตรวจสอบว่าหมายเลขที่กรอกถูกต้องหรือไม่
                if (ticketIndex >= 0 && ticketIndex < tickets.size()) {
                    Ticket ticketToCancel = tickets.get(ticketIndex);
                    ticketToCancel.getSeat().unbook();  // ยกเลิกการจองที่นั่ง
                    tickets.remove(ticketIndex);  // ลบตั๋วออกจากรายการ
                    JOptionPane.showMessageDialog(this, "ยกเลิกตั๋วเรียบร้อยแล้ว");
                } else {
                    JOptionPane.showMessageDialog(this, "การเลือกไม่ถูกต้อง");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ข้อมูลไม่ถูกต้อง กรุณากรอกตัวเลข");
            }
        }
    }

    // ฟังก์ชันหา Movie โดยชื่อ
    private Movie findMovie(String title) {
        for (Movie movie : movies) {
            if (movie.getTitle().equals(title)) {
                return movie;
            }
        }
        return null;
    }

    // ฟังก์ชันหา ShowTime โดยเวลา
    private ShowTime findShowTime(Movie movie, String time) {
        for (ShowTime showTime : movie.getShowTimes()) {
            if (showTime.getTime().equals(time)) {
                return showTime;
            }
        }
        return null;
    }
}

// คลาสฐานที่ใช้สืบทอด
abstract class Event {
    private final String time;

    public Event(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public abstract String getDescription();
}

// คลาส ShowTime ขยายจาก Event
class ShowTime extends Event {
    private final ArrayList<Seat> seats;

    public ShowTime(String time, int totalSeats) {
        super(time);
        this.seats = new ArrayList<>();
        for (int i = 1; i <= totalSeats; i++) {
            seats.add(new Seat(i));
        }
    }

    public ArrayList<Seat> getSeats() {
        return seats;
    }

    @Override
    public String getDescription() {
        return "เวลาฉาย: " + getTime();
    }
}

// คลาส Movie ขยายจาก Event
class Movie extends Event {
    private final String title;
    private final String genre;
    private final int duration;
    private final ArrayList<ShowTime> showTimes;

    public Movie(String title, String genre, int duration) {
        super(""); // ตั้งค่าเวลาที่ว่างเปล่าสำหรับ Movie
        this.title = title;
        this.genre = genre;
        this.duration = duration;
        this.showTimes = new ArrayList<>();
    }

    public void addShowTime(ShowTime showTime) {
        this.showTimes.add(showTime);
    }

    public ArrayList<ShowTime> getShowTimes() {
        return showTimes;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String getDescription() {
        return "หนัง: " + getTitle() + ", หมวดหมู่: " + getGenre() + ", ระยะเวลา: " + getDuration() + " นาที";
    }
}

// คลาส Seat
class Seat {
    private final int seatNumber;
    private boolean isBooked;

    public Seat(int seatNumber) {
        this.seatNumber = seatNumber;
        this.isBooked = false;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void book() {
        this.isBooked = true;
    }

    public void unbook() {
        this.isBooked = false;
    }

    public int getSeatNumber() {
        return seatNumber;
    }
}

// คลาส User
class User {
    private final String userID;
    private final String name;
    private final String email;

    public User(String userID, String name, String email) {
        this.userID = userID;
        this.name = name;
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

// คลาส Ticket
class Ticket {
    private final Movie movie;
    private final ShowTime showTime;
    private final Seat seat;
    private final User user;

    public Ticket(Movie movie, ShowTime showTime, Seat seat, User user) {
        this.movie = movie;
        this.showTime = showTime;
        this.seat = seat;
        this.user = user;
    }

    public Movie getMovie() {
        return movie;
    }

    public ShowTime getShowTime() {
        return showTime;
    }

    public Seat getSeat() {
        return seat;
    }

    public User getUser() {
        return user;
    }
}
