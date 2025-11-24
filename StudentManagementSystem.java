import java.io.*;
import java.util.*;

/**
 * Student Management System
 * - Demonstrates exception handling (custom exceptions, try-catch-finally)
 * - Uses multithreading to simulate loading/saving operations
 * - Uses wrapper classes (Integer, Double) and autoboxing
 * - Thread-safe collection for student records
 *
 * To compile: javac StudentManagementSystem.java
 * To run:     java StudentManagementSystem
 */

// -------------------- Custom Exceptions --------------------
class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String message) {
        super(message);
    }
}

class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}

// -------------------- Data Model --------------------
class Student implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;           // wrapper Integer
    private String name;
    private Double marks;         // wrapper Double

    public Student(Integer id, String name, Double marks) {
        this.id = id;             // autoboxing if primitive provided
        this.name = name;
        this.marks = marks;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public Double getMarks() { return marks; }

    public void setName(String name) { this.name = name; }
    public void setMarks(Double marks) { this.marks = marks; }

    @Override
    public String toString() {
        return String.format("Student{id=%d, name='%s', marks=%.2f}", id, name, marks);
    }
}

// -------------------- Manager --------------------
class StudentManager {
    // thread-safe list
    private final List<Student> students = Collections.synchronizedList(new ArrayList<>());

    // add student with validation
    public void addStudent(String idStr, String name, String marksStr) throws InvalidInputException {
        try {
            // Using wrapper class parsing
            Integer id = Integer.valueOf(idStr.trim());
            Double marks = Double.valueOf(marksStr.trim());

            validateStudentData(id, name, marks);

            Student s = new Student(id, name.trim(), marks);
            synchronized (students) {
                students.add(s);
            }
        } catch (NumberFormatException nfe) {
            throw new InvalidInputException("ID must be integer and marks must be a number.");
        }
    }

    private void validateStudentData(Integer id, String name, Double marks) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Name cannot be empty");
        }
        if (id == null || id < 1) {
            throw new InvalidInputException("ID must be positive");
        }
        if (marks == null || marks < 0.0 || marks > 100.0) {
            throw new InvalidInputException("Marks must be between 0 and 100");
        }
        // ensure unique ID
        synchronized (students) {
            for (Student s : students) {
                if (s.getId().equals(id)) {
                    throw new InvalidInputException("Duplicate ID: " + id);
                }
            }
        }
    }

    public Student findStudentById(Integer id) throws StudentNotFoundException {
        synchronized (students) {
            for (Student s : students) {
                if (s.getId().equals(id)) return s;
            }
        }
        throw new StudentNotFoundException("Student with ID " + id + " not found.");
    }

    public void removeStudent(Integer id) throws StudentNotFoundException {
        synchronized (students) {
            Iterator<Student> it = students.iterator();
            while (it.hasNext()) {
                if (it.next().getId().equals(id)) {
                    it.remove();
                    return;
                }
            }
        }
        throw new StudentNotFoundException("Student with ID " + id + " not found to remove.");
    }

    public List<Student> listAll() {
        synchronized (students) {
            return new ArrayList<>(students);
        }
    }

    // simulate save to file on a background thread with progress updates
    public Thread saveToFileAsync(String filename) {
        Thread t = new Thread(() -> {
            System.out.println("[Save] Starting save operation in background...");
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
                simulateProgress("Saving");
                // write a snapshot
                synchronized (students) {
                    oos.writeObject(new ArrayList<>(students));
                }
                System.out.println("[Save] Completed. Data written to: " + filename);
            } catch (IOException e) {
                System.err.println("[Save] IO Error during save: " + e.getMessage());
            }
        });
        t.setDaemon(true);
        t.start();
        return t;
    }

    // simulate load from file (blocking) but with progress
    @SuppressWarnings("unchecked")
    public void loadFromFile(String filename) {
        System.out.println("[Load] Loading data from file...");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            simulateProgress("Loading");
            Object obj = ois.readObject();
            if (obj instanceof List) {
                synchronized (students) {
                    students.clear();
                    students.addAll((List<Student>) obj);
                }
            }
            System.out.println("[Load] Completed. Loaded " + students.size() + " students.");
        } catch (FileNotFoundException fnf) {
            System.err.println("[Load] File not found, starting with empty dataset.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Load] Error during load: " + e.getMessage());
        }
    }

    // small helper to simulate a loading bar; sleeps but prints progress
    private void simulateProgress(String action) {
        try {
            for (int i = 0; i <= 20; i++) {
                int pct = i * 5;
                System.out.print("\r[" + action + "] " + pct + "%");
                Thread.sleep(120);
            }
            System.out.println();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("\n[" + action + "] Interrupted.");
        }
    }
}

// -------------------- UI / Main --------------------
public class StudentManagementSystem {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentManager manager = new StudentManager();
    private static final String DATA_FILE = "students.dat";

    public static void main(String[] args) {
        // load existing data (if any)
        manager.loadFromFile(DATA_FILE);

        boolean running = true;
        while (running) {
            showMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    handleAddStudent();
                    break;
                case "2":
                    handleListStudents();
                    break;
                case "3":
                    handleFindStudent();
                    break;
                case "4":
                    handleRemoveStudent();
                    break;
                case "5":
                    // save asynchronously
                    Thread saveThread = manager.saveToFileAsync(DATA_FILE);
                    // demonstrate responsiveness: main thread remains usable
                    System.out.println("Save started in background (you may continue).\n");
                    break;
                case "6":
                    // blocking load
                    manager.loadFromFile(DATA_FILE);
                    break;
                case "0":
                    System.out.println("Exiting... saving data synchronously before exit.");
                    // save and wait
                    Thread t = manager.saveToFileAsync(DATA_FILE);
                    try { t.join(5000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }

        System.out.println("Goodbye.");
    }

    private static void showMenu() {
        System.out.println("\n=== Student Management System ===");
        System.out.println("1. Add Student");
        System.out.println("2. List Students");
        System.out.println("3. Find Student by ID");
        System.out.println("4. Remove Student by ID");
        System.out.println("5. Save (background)");
        System.out.println("6. Load from file");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void handleAddStudent() {
        System.out.println("--- Add Student ---");
        System.out.print("Enter ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter marks (0-100): ");
        String marks = scanner.nextLine();

        try {
            manager.addStudent(id, name, marks);
            System.out.println("Student added successfully.");
        } catch (InvalidInputException ex) {
            System.err.println("Failed to add student: " + ex.getMessage());
        } finally {
            System.out.println("Returning to main menu.");
        }
    }

    private static void handleListStudents() {
        System.out.println("--- Student List ---");
        List<Student> list = manager.listAll();
        if (list.isEmpty()) {
            System.out.println("No students currently.");
            return;
        }
        for (Student s : list) {
            System.out.println(s);
        }
    }

    private static void handleFindStudent() {
        System.out.print("Enter ID to find: ");
        String idStr = scanner.nextLine();
        try {
            Integer id = Integer.valueOf(idStr.trim());
            Student s = manager.findStudentById(id);
            System.out.println("Found: " + s);
        } catch (NumberFormatException nfe) {
            System.err.println("Invalid ID format.");
        } catch (StudentNotFoundException snf) {
            System.err.println(snf.getMessage());
        } finally {
            System.out.println("Returning to main menu.");
        }
    }

    private static void handleRemoveStudent() {
        System.out.print("Enter ID to remove: ");
        String idStr = scanner.nextLine();
        try {
            Integer id = Integer.valueOf(idStr.trim());
            manager.removeStudent(id);
            System.out.println("Student removed.");
        } catch (NumberFormatException nfe) {
            System.err.println("Invalid ID format.");
        } catch (StudentNotFoundException snf) {
            System.err.println(snf.getMessage());
        } finally {
            System.out.println("Returning to main menu.");
        }
    }
}
