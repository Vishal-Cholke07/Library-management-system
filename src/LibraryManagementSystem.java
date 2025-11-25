import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class LibraryManagementSystem extends JFrame {
    private Connection conn;
    private JTabbedPane tabbedPane;
    
    // Book Management Components
    private JTextField bookIdField, bookTitleField, bookAuthorField, bookIsbnField;
    private JSpinner bookCopiesSpinner;
    private JTable bookTable;
    private DefaultTableModel bookTableModel;
    
    // User Management Components
    private JTextField userIdField, userNameField, userEmailField, userPhoneField;
    private JTable userTable;
    private DefaultTableModel userTableModel;
    
    // Issue/Return Components
    private JComboBox<String> issueUserCombo, issueBookCombo;
    private JTable issuedBooksTable;
    private DefaultTableModel issuedBooksTableModel;
    
    public LibraryManagementSystem() {
        setTitle("Library Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initDatabase();
        initUI();
        loadData();
    }
    
    private void initDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:library.db");
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed!");
        }
    }
    
    private void createTables() throws SQLException {
        Statement stmt = conn.createStatement();
        
        // Books table
        stmt.execute("CREATE TABLE IF NOT EXISTS books (" +
                    "book_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "title TEXT NOT NULL," +
                    "author TEXT NOT NULL," +
                    "isbn TEXT UNIQUE," +
                    "total_copies INTEGER DEFAULT 1," +
                    "available_copies INTEGER DEFAULT 1)");
        
        // Users table
        stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "email TEXT UNIQUE," +
                    "phone TEXT)");
        
        // Issued books table
        stmt.execute("CREATE TABLE IF NOT EXISTS issued_books (" +
                    "issue_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "book_id INTEGER," +
                    "user_id INTEGER," +
                    "issue_date DATE," +
                    "due_date DATE," +
                    "return_date DATE," +
                    "late_fee REAL DEFAULT 0," +
                    "FOREIGN KEY(book_id) REFERENCES books(book_id)," +
                    "FOREIGN KEY(user_id) REFERENCES users(user_id))");
        
        stmt.close();
    }
    
    private void initUI() {
        tabbedPane = new JTabbedPane();
        
        // Add tabs
        tabbedPane.addTab("Books", createBookPanel());
        tabbedPane.addTab("Users", createUserPanel());
        tabbedPane.addTab("Issue/Return", createIssueReturnPanel());
        
        add(tabbedPane);
    }
    
    private JPanel createBookPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input form
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Book Details"));
        
        formPanel.add(new JLabel("Book ID:"));
        bookIdField = new JTextField();
        bookIdField.setEditable(false);
        formPanel.add(bookIdField);
        
        formPanel.add(new JLabel("Title:"));
        bookTitleField = new JTextField();
        formPanel.add(bookTitleField);
        
        formPanel.add(new JLabel("Author:"));
        bookAuthorField = new JTextField();
        formPanel.add(bookAuthorField);
        
        formPanel.add(new JLabel("ISBN:"));
        bookIsbnField = new JTextField();
        formPanel.add(bookIsbnField);
        
        formPanel.add(new JLabel("Total Copies:"));
        bookCopiesSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        formPanel.add(bookCopiesSpinner);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Book");
        JButton updateBtn = new JButton("Update Book");
        JButton deleteBtn = new JButton("Delete Book");
        JButton clearBtn = new JButton("Clear");
        
        addBtn.addActionListener(e -> addBook());
        updateBtn.addActionListener(e -> updateBook());
        deleteBtn.addActionListener(e -> deleteBook());
        clearBtn.addActionListener(e -> clearBookFields());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        formPanel.add(buttonPanel);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Title", "Author", "ISBN", "Total Copies", "Available"};
        bookTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookTable = new JTable(bookTableModel);
        bookTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = bookTable.getSelectedRow();
                loadBookToForm(row);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(bookTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Input form
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("User Details"));
        
        formPanel.add(new JLabel("User ID:"));
        userIdField = new JTextField();
        userIdField.setEditable(false);
        formPanel.add(userIdField);
        
        formPanel.add(new JLabel("Name:"));
        userNameField = new JTextField();
        formPanel.add(userNameField);
        
        formPanel.add(new JLabel("Email:"));
        userEmailField = new JTextField();
        formPanel.add(userEmailField);
        
        formPanel.add(new JLabel("Phone:"));
        userPhoneField = new JTextField();
        formPanel.add(userPhoneField);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add User");
        JButton updateBtn = new JButton("Update User");
        JButton deleteBtn = new JButton("Delete User");
        JButton clearBtn = new JButton("Clear");
        
        addBtn.addActionListener(e -> addUser());
        updateBtn.addActionListener(e -> updateUser());
        deleteBtn.addActionListener(e -> deleteUser());
        clearBtn.addActionListener(e -> clearUserFields());
        
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);
        formPanel.add(buttonPanel);
        
        panel.add(formPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Name", "Email", "Phone"};
        userTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(userTableModel);
        userTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = userTable.getSelectedRow();
                loadUserToForm(row);
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createIssueReturnPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Issue form
        JPanel issuePanel = new JPanel(new GridLayout(3, 2, 10, 10));
        issuePanel.setBorder(BorderFactory.createTitledBorder("Issue Book"));
        
        issuePanel.add(new JLabel("Select User:"));
        issueUserCombo = new JComboBox<>();
        issuePanel.add(issueUserCombo);
        
        issuePanel.add(new JLabel("Select Book:"));
        issueBookCombo = new JComboBox<>();
        issuePanel.add(issueBookCombo);
        
        JButton issueBtn = new JButton("Issue Book");
        JButton returnBtn = new JButton("Return Selected Book");
        issueBtn.addActionListener(e -> issueBook());
        returnBtn.addActionListener(e -> returnBook());
        
        issuePanel.add(issueBtn);
        issuePanel.add(returnBtn);
        
        panel.add(issuePanel, BorderLayout.NORTH);
        
        // Issued books table
        String[] columns = {"Issue ID", "Book", "User", "Issue Date", "Due Date", "Return Date", "Late Fee"};
        issuedBooksTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        issuedBooksTable = new JTable(issuedBooksTableModel);
        
        JScrollPane scrollPane = new JScrollPane(issuedBooksTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addBook() {
        try {
            String sql = "INSERT INTO books (title, author, isbn, total_copies, available_copies) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bookTitleField.getText());
            pstmt.setString(2, bookAuthorField.getText());
            pstmt.setString(3, bookIsbnField.getText());
            int copies = (int) bookCopiesSpinner.getValue();
            pstmt.setInt(4, copies);
            pstmt.setInt(5, copies);
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book added successfully!");
            loadBooks();
            clearBookFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void updateBook() {
        try {
            String sql = "UPDATE books SET title=?, author=?, isbn=?, total_copies=? WHERE book_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bookTitleField.getText());
            pstmt.setString(2, bookAuthorField.getText());
            pstmt.setString(3, bookIsbnField.getText());
            pstmt.setInt(4, (int) bookCopiesSpinner.getValue());
            pstmt.setInt(5, Integer.parseInt(bookIdField.getText()));
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Book updated successfully!");
            loadBooks();
            clearBookFields();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void deleteBook() {
        try {
            int bookId = Integer.parseInt(bookIdField.getText());
            String sql = "DELETE FROM books WHERE book_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bookId);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Book deleted successfully!");
            loadBooks();
            clearBookFields();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void addUser() {
        try {
            String sql = "INSERT INTO users (name, email, phone) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userNameField.getText());
            pstmt.setString(2, userEmailField.getText());
            pstmt.setString(3, userPhoneField.getText());
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User added successfully!");
            loadUsers();
            clearUserFields();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void updateUser() {
        try {
            String sql = "UPDATE users SET name=?, email=?, phone=? WHERE user_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userNameField.getText());
            pstmt.setString(2, userEmailField.getText());
            pstmt.setString(3, userPhoneField.getText());
            pstmt.setInt(4, Integer.parseInt(userIdField.getText()));
            
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "User updated successfully!");
            loadUsers();
            clearUserFields();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void deleteUser() {
        try {
            int userId = Integer.parseInt(userIdField.getText());
            String sql = "DELETE FROM users WHERE user_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "User deleted successfully!");
            loadUsers();
            clearUserFields();
        } catch (SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void issueBook() {
        try {
            String userStr = (String) issueUserCombo.getSelectedItem();
            String bookStr = (String) issueBookCombo.getSelectedItem();
            
            if (userStr == null || bookStr == null) {
                JOptionPane.showMessageDialog(this, "Please select user and book!");
                return;
            }
            
            int userId = Integer.parseInt(userStr.split(":")[0]);
            int bookId = Integer.parseInt(bookStr.split(":")[0]);
            
            // Check availability
            String checkSql = "SELECT available_copies FROM books WHERE book_id=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, bookId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next() && rs.getInt("available_copies") > 0) {
                LocalDate issueDate = LocalDate.now();
                LocalDate dueDate = issueDate.plusDays(14);
                
                String sql = "INSERT INTO issued_books (book_id, user_id, issue_date, due_date) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, bookId);
                pstmt.setInt(2, userId);
                pstmt.setString(3, issueDate.toString());
                pstmt.setString(4, dueDate.toString());
                pstmt.executeUpdate();
                
                // Update available copies
                String updateSql = "UPDATE books SET available_copies = available_copies - 1 WHERE book_id=?";
                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, bookId);
                updateStmt.executeUpdate();
                
                JOptionPane.showMessageDialog(this, "Book issued successfully! Due date: " + dueDate);
                loadIssuedBooks();
                loadBooks();
            } else {
                JOptionPane.showMessageDialog(this, "Book not available!");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void returnBook() {
        int row = issuedBooksTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to return!");
            return;
        }
        
        try {
            int issueId = (int) issuedBooksTableModel.getValueAt(row, 0);
            String dueDateStr = (String) issuedBooksTableModel.getValueAt(row, 4);
            LocalDate dueDate = LocalDate.parse(dueDateStr);
            LocalDate returnDate = LocalDate.now();
            
            // Calculate late fee (Rs. 5 per day)
            long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
            double lateFee = daysLate > 0 ? daysLate * 5.0 : 0;
            
            String sql = "UPDATE issued_books SET return_date=?, late_fee=? WHERE issue_id=?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, returnDate.toString());
            pstmt.setDouble(2, lateFee);
            pstmt.setInt(3, issueId);
            pstmt.executeUpdate();
            
            // Get book_id
            String getBookSql = "SELECT book_id FROM issued_books WHERE issue_id=?";
            PreparedStatement getStmt = conn.prepareStatement(getBookSql);
            getStmt.setInt(1, issueId);
            ResultSet rs = getStmt.executeQuery();
            int bookId = rs.getInt("book_id");
            
            // Update available copies
            String updateSql = "UPDATE books SET available_copies = available_copies + 1 WHERE book_id=?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setInt(1, bookId);
            updateStmt.executeUpdate();
            
            String msg = "Book returned successfully!";
            if (lateFee > 0) {
                msg += "\nLate fee: Rs. " + lateFee;
            }
            JOptionPane.showMessageDialog(this, msg);
            
            loadIssuedBooks();
            loadBooks();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void loadData() {
        loadBooks();
        loadUsers();
        loadIssuedBooks();
        loadComboBoxes();
    }
    
    private void loadBooks() {
        bookTableModel.setRowCount(0);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM books");
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("book_id"),
                    rs.getString("title"),
                    rs.getString("author"),
                    rs.getString("isbn"),
                    rs.getInt("total_copies"),
                    rs.getInt("available_copies")
                };
                bookTableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadUsers() {
        userTableModel.setRowCount(0);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("user_id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone")
                };
                userTableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadIssuedBooks() {
        issuedBooksTableModel.setRowCount(0);
        try {
            String sql = "SELECT ib.issue_id, b.title, u.name, ib.issue_date, ib.due_date, " +
                        "ib.return_date, ib.late_fee FROM issued_books ib " +
                        "JOIN books b ON ib.book_id = b.book_id " +
                        "JOIN users u ON ib.user_id = u.user_id";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Object[] row = {
                    rs.getInt("issue_id"),
                    rs.getString("title"),
                    rs.getString("name"),
                    rs.getString("issue_date"),
                    rs.getString("due_date"),
                    rs.getString("return_date"),
                    rs.getDouble("late_fee")
                };
                issuedBooksTableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadComboBoxes() {
        issueUserCombo.removeAllItems();
        issueBookCombo.removeAllItems();
        
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT user_id, name FROM users");
            while (rs.next()) {
                issueUserCombo.addItem(rs.getInt("user_id") + ": " + rs.getString("name"));
            }
            
            rs = stmt.executeQuery("SELECT book_id, title FROM books WHERE available_copies > 0");
            while (rs.next()) {
                issueBookCombo.addItem(rs.getInt("book_id") + ": " + rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadBookToForm(int row) {
        bookIdField.setText(bookTableModel.getValueAt(row, 0).toString());
        bookTitleField.setText(bookTableModel.getValueAt(row, 1).toString());
        bookAuthorField.setText(bookTableModel.getValueAt(row, 2).toString());
        bookIsbnField.setText(bookTableModel.getValueAt(row, 3).toString());
        bookCopiesSpinner.setValue(bookTableModel.getValueAt(row, 4));
    }
    
    private void loadUserToForm(int row) {
        userIdField.setText(userTableModel.getValueAt(row, 0).toString());
        userNameField.setText(userTableModel.getValueAt(row, 1).toString());
        userEmailField.setText(userTableModel.getValueAt(row, 2).toString());
        userPhoneField.setText(userTableModel.getValueAt(row, 3).toString());
    }
    
    private void clearBookFields() {
        bookIdField.setText("");
        bookTitleField.setText("");
        bookAuthorField.setText("");
        bookIsbnField.setText("");
        bookCopiesSpinner.setValue(1);
    }
    
    private void clearUserFields() {
        userIdField.setText("");
        userNameField.setText("");
        userEmailField.setText("");
        userPhoneField.setText("");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LibraryManagementSystem().setVisible(true);
        });
    }
}