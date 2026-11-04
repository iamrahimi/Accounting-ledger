package com.pluralsight;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public class LedgerGUI extends JFrame {
    private static final Color NAVY = new Color(25, 43, 67);
    private static final Color PAGE = new Color(246, 248, 251);
    private static final Color MUTED = new Color(102, 115, 132);
    private static final Color GREEN = new Color(28, 130, 91);
    private static final Color RED = new Color(191, 73, 73);
    private static final Path TRANSACTIONS_FILE = Path.of(Main.fileSrc);

    private final List<Transactions> transactions = new ArrayList<>();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Date", "Time", "Type", "Description", "Vendor", "Amount"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JComboBox<String> typeFilter = new JComboBox<>(
            new String[]{"All transactions", "Deposits", "Payments"});
    private final JTextField searchField = new JTextField(22);
    private final JLabel depositsValue = new JLabel("$0.00");
    private final JLabel paymentsValue = new JLabel("$0.00");
    private final JLabel netValue = new JLabel("$0.00");
    private final JLabel statusLabel = new JLabel("Ready");
    private LocalDate reportStart;
    private LocalDate reportEnd;

    public LedgerGUI() {
        super("Accounting Ledger");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(920, 600));
        setSize(1100, 700);
        setLocationRelativeTo(null);
        buildInterface();
        reloadTransactions();
    }

    private void buildInterface() {
        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(PAGE);
        root.setBorder(new EmptyBorder(18, 22, 14, 22));

        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 12));
        center.setOpaque(false);
        center.add(buildToolbar(), BorderLayout.NORTH);
        center.add(buildTable(), BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);
        root.add(buildFooter(), BorderLayout.SOUTH);

        setContentPane(root);
    }

    private JComponent buildHeader() {
        JPanel header = new JPanel(new BorderLayout(15, 10));
        header.setBackground(NAVY);
        header.setBorder(new EmptyBorder(20, 22, 20, 22));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Accounting Ledger");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 25));
        JLabel subtitle = new JLabel("Track deposits and payments in one place");
        subtitle.setForeground(new Color(207, 218, 231));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(5));
        titleBlock.add(subtitle);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 5));
        actions.setOpaque(false);
        JButton deposit = styledButton("＋  Add deposit", new Color(35, 150, 106), Color.WHITE);
        JButton payment = styledButton("−  Make payment", new Color(35, 150, 106), Color.WHITE);
        deposit.addActionListener(e -> showTransactionDialog(true));
        payment.addActionListener(e -> showTransactionDialog(false));
        actions.add(deposit);
        actions.add(payment);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(actions, BorderLayout.EAST);
        return header;
    }

    private JComponent buildToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 8));
        toolbar.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        typeFilter.setFocusable(false);
        typeFilter.addActionListener(e -> refreshTable());
        left.add(new JLabel("Show"));
        left.add(typeFilter);
        left.add(Box.createHorizontalStrut(8));
        left.add(new JLabel("Search"));
        searchField.setToolTipText("Search description or vendor");
        searchField.addActionListener(e -> refreshTable());
        left.add(searchField);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.setOpaque(false);
        JButton dateButton = styledButton("Date report", new Color(226, 235, 245), NAVY);
        JButton clearDateButton = styledButton("Clear dates", Color.WHITE, NAVY);
        JButton refreshButton = styledButton("↻  Refresh", Color.WHITE, NAVY);
        dateButton.addActionListener(e -> chooseDateRange());
        clearDateButton.addActionListener(e -> {
            reportStart = null;
            reportEnd = null;
            refreshTable();
        });
        refreshButton.addActionListener(e -> reloadTransactions());
        right.add(dateButton);
        right.add(clearDateButton);
        right.add(refreshButton);

        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(right, BorderLayout.EAST);
        return toolbar;
    }

    private JComponent buildTable() {
        table.setRowHeight(31);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(232, 236, 241));
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(235, 240, 246));
        table.getTableHeader().setForeground(NAVY);
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(0).setPreferredWidth(95);
        table.getColumnModel().getColumn(1).setPreferredWidth(75);
        table.getColumnModel().getColumn(2).setPreferredWidth(85);
        table.getColumnModel().getColumn(3).setPreferredWidth(230);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);
        table.getColumnModel().getColumn(5).setPreferredWidth(110);

        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                setText(value == null ? "" : value.toString());
                setHorizontalAlignment(SwingConstants.RIGHT);
                if (value != null && value.toString().startsWith("-$")) {
                    setForeground(RED);
                } else {
                    setForeground(GREEN);
                }
            }
        };
        table.getColumnModel().getColumn(5).setCellRenderer(amountRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(225, 231, 238)));
        scrollPane.getViewport().setBackground(Color.WHITE);
        return scrollPane;
    }

    private JComponent buildFooter() {
        JPanel footer = new JPanel(new BorderLayout(0, 10));
        footer.setOpaque(false);

        JPanel cards = new JPanel(new GridLayout(1, 3, 12, 0));
        cards.setOpaque(false);
        cards.add(summaryCard("TOTAL DEPOSITS", depositsValue, GREEN));
        cards.add(summaryCard("TOTAL PAYMENTS", paymentsValue, RED));
        cards.add(summaryCard("NET CHANGE", netValue, NAVY));

        statusLabel.setForeground(MUTED);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JPanel status = new JPanel(new BorderLayout());
        status.setOpaque(false);
        status.add(statusLabel, BorderLayout.WEST);

        footer.add(cards, BorderLayout.CENTER);
        footer.add(status, BorderLayout.SOUTH);
        return footer;
    }

    private JPanel summaryCard(String heading, JLabel valueLabel, Color valueColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 235, 241)),
                new EmptyBorder(12, 15, 12, 15)));

        JLabel label = new JLabel(heading);
        label.setForeground(MUTED);
        label.setFont(new Font("SansSerif", Font.BOLD, 11));
        valueLabel.setForeground(valueColor);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 21));
        card.add(label);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLabel);
        return card;
    }

    private JButton styledButton(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 227, 235)),
                new EmptyBorder(8, 12, 8, 12)));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showTransactionDialog(boolean deposit) {
        JTextField descriptionField = new JTextField(22);
        JTextField vendorField = new JTextField(22);
        JTextField amountField = new JTextField(12);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 5, 6, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        addFormRow(form, c, 0, "Description", descriptionField);
        addFormRow(form, c, 1, "Vendor", vendorField);
        addFormRow(form, c, 2, "Amount ($)", amountField);

        String title = deposit ? "Add a deposit" : "Record a payment";
        int result = JOptionPane.showConfirmDialog(this, form, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String description = descriptionField.getText().trim();
        String vendor = vendorField.getText().trim();
        String amountText = amountField.getText().trim().replace("$", "").replace(",", "");
        if (description.isEmpty() || vendor.isEmpty() || amountText.isEmpty()) {
            showError("Please complete the description, vendor, and amount.");
            return;
        }
        if (description.contains("|") || vendor.contains("|")) {
            showError("Description and vendor cannot contain the | character.");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (!Double.isFinite(amount) || amount <= 0) {
                showError("Enter an amount greater than zero.");
                return;
            }
            if (!deposit) {
                amount = -amount;
            }

            LocalDate today = LocalDate.now();
            Transactions transaction = new Transactions(
                    today.toString(), LocalTime.now().withNano(0).toString(),
                    description, vendor, amount);
            appendTransaction(transaction);
            transactions.add(transaction);
            sortTransactions();
            refreshTable();
            statusLabel.setText((deposit ? "Deposit" : "Payment") + " saved successfully.");
        } catch (NumberFormatException ex) {
            showError("Enter a valid amount, such as 25.50.");
        } catch (IOException ex) {
            showError("Could not save the transaction:\n" + ex.getMessage());
        }
    }

    private void addFormRow(JPanel form, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridy = row;
        c.gridx = 0;
        c.weightx = 0;
        form.add(new JLabel(label + ":"), c);
        c.gridx = 1;
        c.weightx = 1;
        form.add(field, c);
    }

    private void chooseDateRange() {
        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));
        JTextField startField = new JTextField(
                reportStart == null ? LocalDate.now().withDayOfMonth(1).toString() : reportStart.toString());
        JTextField endField = new JTextField(
                reportEnd == null ? LocalDate.now().toString() : reportEnd.toString());
        form.add(new JLabel("Start date (yyyy-MM-dd):"));
        form.add(startField);
        form.add(new JLabel("End date (yyyy-MM-dd):"));
        form.add(endField);

        int result = JOptionPane.showConfirmDialog(this, form, "Filter by date range",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            LocalDate start = LocalDate.parse(startField.getText().trim());
            LocalDate end = LocalDate.parse(endField.getText().trim());
            if (end.isBefore(start)) {
                showError("The end date must be the same as or later than the start date.");
                return;
            }
            reportStart = start;
            reportEnd = end;
            refreshTable();
        } catch (DateTimeParseException ex) {
            showError("Enter dates in yyyy-MM-dd format, for example 2026-11-04.");
        }
    }

    private void reloadTransactions() {
        transactions.clear();
        if (!Files.exists(TRANSACTIONS_FILE)) {
            refreshTable();
            statusLabel.setText("No transaction file found yet. Add a deposit or payment to create it.");
            return;
        }

        int skipped = 0;
        try (BufferedReader reader = Files.newBufferedReader(TRANSACTIONS_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] row = line.split("\\|", -1);
                if (row.length > 0 && row[0].trim().equalsIgnoreCase("date")) {
                    continue;
                }
                if (row.length != 5) {
                    skipped++;
                    continue;
                }
                try {
                    LocalDate.parse(row[0].trim());
                    LocalTime.parse(row[1].trim());
                    double amount = Double.parseDouble(row[4].trim());
                    transactions.add(new Transactions(row[0].trim(), row[1].trim(),
                            row[2].trim(), row[3].trim(), amount));
                } catch (DateTimeParseException | NumberFormatException ex) {
                    skipped++;
                }
            }
            sortTransactions();
            refreshTable();
            statusLabel.setText(skipped == 0
                    ? transactions.size() + " transaction(s) loaded from " + TRANSACTIONS_FILE
                    : transactions.size() + " transaction(s) loaded; " + skipped + " invalid row(s) skipped.");
        } catch (IOException ex) {
            showError("Could not read the transaction file:\n" + ex.getMessage());
        }
    }

    private void appendTransaction(Transactions transaction) throws IOException {
        Path parent = TRANSACTIONS_FILE.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(TRANSACTIONS_FILE,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(transaction.getDate() + "|" + transaction.getTime() + "|"
                    + transaction.getDescription() + "|" + transaction.getVendor() + "|"
                    + transaction.getAmount());
            writer.newLine();
        }
    }

    private void sortTransactions() {
        transactions.sort(Comparator.comparing((Transactions transaction) ->
                LocalDateTime.parse(transaction.getDate() + "T" + transaction.getTime())).reversed());
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        String selectedType = (String) typeFilter.getSelectedItem();
        String keyword = searchField.getText().trim().toLowerCase(Locale.ROOT);
        double deposits = 0;
        double payments = 0;
        double net = 0;
        int visibleRows = 0;

        for (Transactions transaction : transactions) {
            double amount = transaction.getAmount();
            net += amount;
            if (amount > 0) {
                deposits += amount;
            } else {
                payments += Math.abs(amount);
            }

            LocalDate date = LocalDate.parse(transaction.getDate());
            if ("Deposits".equals(selectedType) && amount < 0) continue;
            if ("Payments".equals(selectedType) && amount >= 0) continue;
            if (reportStart != null && date.isBefore(reportStart)) continue;
            if (reportEnd != null && date.isAfter(reportEnd)) continue;
            if (!keyword.isEmpty()
                    && !transaction.getDescription().toLowerCase(Locale.ROOT).contains(keyword)
                    && !transaction.getVendor().toLowerCase(Locale.ROOT).contains(keyword)) continue;

            String type = amount >= 0 ? "Deposit" : "Payment";
            String amountText = amount < 0
                    ? "-$" + String.format(Locale.US, "%,.2f", Math.abs(amount))
                    : "$" + String.format(Locale.US, "%,.2f", amount);
            tableModel.addRow(new Object[]{transaction.getDate(), transaction.getTime(), type,
                    transaction.getDescription(), transaction.getVendor(), amountText});
            visibleRows++;
        }

        depositsValue.setText(currency(deposits));
        paymentsValue.setText(currency(payments));
        netValue.setText((net < 0 ? "-" : "") + currency(Math.abs(net)));

        String dateText = reportStart == null ? "" : "  •  Dates: " + reportStart + " to " + reportEnd;
        statusLabel.setText(visibleRows + " transaction(s) shown" + dateText);
    }

    private String currency(double amount) {
        return "$" + String.format(Locale.US, "%,.2f", amount);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Please check", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LedgerGUI().setVisible(true));
    }
}
