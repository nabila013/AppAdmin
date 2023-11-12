
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryApp extends JFrame {
    private Connection connection;
    private List<Order> orders = new ArrayList<>();
    private JTextField customerNameField, custIdField, orderIdField, quantityField, priceField;
    private JTextArea displayArea;

    public InventoryApp() {
        connection = Koneksi.connect();

        setTitle("Inventory App");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(0x87CEEB));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Customer Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        customerNameField = new JTextField(20);
        mainPanel.add(customerNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Cust ID:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        custIdField = new JTextField(20);
        mainPanel.add(custIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Order ID:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        orderIdField = new JTextField(20);
        mainPanel.add(orderIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        quantityField = new JTextField(20);
        mainPanel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 4;
        priceField = new JTextField(20);
        mainPanel.add(priceField, gbc);

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 5;
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        JButton addButton = new JButton("Tambah");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Hapus");
        JButton displayButton = new JButton("Tampilkan");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(displayButton);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addOrder();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editOrder();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteOrder();
            }
        });

        displayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayOrders();
            }
        });

        mainPanel.add(buttonPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.gridheight = 1;
        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false);
        mainPanel.add(new JScrollPane(displayArea), gbc);

        getContentPane().add(mainPanel);
    }

    private void addOrder() {
        String customerName = customerNameField.getText();
        String custId = custIdField.getText();
        String orderId = orderIdField.getText();
        int quantity = Integer.parseInt(quantityField.getText());
        double price = Double.parseDouble(priceField.getText());

        boolean custIdExists = orders.stream().anyMatch(order -> order.getCustId().equals(custId));

        if (custIdExists) {
            displayArea.append("Cust ID sudah ada dalam daftar. Gunakan Cust ID yang berbeda.\n");
        } else {
            String sql = "INSERT INTO orders (customer_name, cust_id, order_id, quantity, price) VALUES (?, ?, ?, ?, ?)";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, customerName);
                preparedStatement.setString(2, custId);
                preparedStatement.setString(3, orderId);
                preparedStatement.setInt(4, quantity);
                preparedStatement.setDouble(5, price);

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    Order order = new Order(customerName, custId, orderId, quantity, price);
                    orders.add(order);
                    clearFields();
                    displayArea.append("Order ditambahkan: " + order + "\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void editOrder() {
        String orderIdToEdit = orderIdField.getText();

        for (Order order : orders) {
            if (order.getOrderId().equals(orderIdToEdit)) {
                order.setCustomerName(customerNameField.getText());
                order.setCustId(custIdField.getText());
                order.setQuantity(Integer.parseInt(quantityField.getText()));
                order.setPrice(Double.parseDouble(priceField.getText()));
                clearFields();

                String sql = "UPDATE orders SET customer_name=?, cust_id=?, quantity=?, price=? WHERE order_id=?";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, order.getCustomerName());
                    preparedStatement.setString(2, order.getCustId());
                    preparedStatement.setInt(3, order.getQuantity());
                    preparedStatement.setDouble(4, order.getPrice());
                    preparedStatement.setString(5, order.getOrderId());

                    int rowsUpdated = preparedStatement.executeUpdate();
                    if (rowsUpdated > 0) {
                        displayArea.append("Order diperbarui: " + order + "\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        displayArea.append("Order tidak ditemukan dengan Order ID: " + orderIdToEdit + "\n");
    }

    private void deleteOrder() {
        String orderIdToDelete = orderIdField.getText();

        for (Order order : orders) {
            if (order.getOrderId().equals(orderIdToDelete)) {
                String sql = "DELETE FROM orders WHERE order_id=?";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(sql);
                    preparedStatement.setString(1, orderIdToDelete);

                    int rowsDeleted = preparedStatement.executeUpdate();
                    if (rowsDeleted > 0) {
                        orders.remove(order);
                        clearFields();
                        displayArea.append("Order dihapus: " + order + "\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        displayArea.append("Order tidak ditemukan dengan Order ID: " + orderIdToDelete + "\n");
    }

    private void displayOrders() {
        displayArea.setText("");
        orders.clear();

        String sql = "SELECT * FROM orders";
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                String customerName = resultSet.getString("customer_name");
                String custId = resultSet.getString("cust_id");
                String orderId = resultSet.getString("order_id");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");

                Order order = new Order(customerName, custId, orderId, quantity, price);
                orders.add(order);
                displayArea.append(order + "\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        customerNameField.setText("");
        custIdField.setText("");
        orderIdField.setText("");
        quantityField.setText("");
        priceField.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new InventoryApp().setVisible(true);
            }
        });
    }

    class Order {
        private String customerName;
        private String custId;
        private String orderId;
        private int quantity;
        private double price;

        public Order(String customerName, String custId, String orderId, int quantity, double price) {
            this.customerName = customerName;
            this.custId = custId;
            this.orderId = orderId;
            this.quantity = quantity;
            this.price = price;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getCustId() {
            return custId;
        }

        public void setCustId(String custId) {
            this.custId = custId;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        @Override
        public String toString() {
            return "Customer Name: " + customerName +
                    ", Cust ID: " + custId +
                    ", Order ID: " + orderId +
                    ", Quantity: " + quantity +
                    ", Price: " + price;
        }
    }
}
