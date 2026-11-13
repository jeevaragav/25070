import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PersonalFinanceManagement extends JFrame {

    CardLayout card = new CardLayout();
    JPanel main = new JPanel(card);

    CardLayout pages = new CardLayout();
    JPanel content = new JPanel(pages);

    double totalIncome = 0;
    double totalExpense = 0;
    double totalSavings = 0;

    String[] categories = {
        "Food", "Entertainment", "Travel",
        "Education", "Shopping", "Bills", "Other"
    };

    LinkedHashMap<String, Double> expenseTotals = new LinkedHashMap<>();

    ArrayList<String> savingsNames = new ArrayList<>();
    ArrayList<Double> savingsAmounts = new ArrayList<>();

    ArrayList<String> goalNames = new ArrayList<>();
    ArrayList<Double> goalTargets = new ArrayList<>();

    ArrayList<String> expenseHistory = new ArrayList<>();

    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    JLabel incomeLabel = new JLabel("₹ 0.00");
    JLabel expenseLabel = new JLabel("₹ 0.00");
    JLabel balanceLabel = new JLabel("₹ 0.00");
    JLabel savingsLabel = new JLabel("₹ 0.00");

    JTextArea dashboardReport = new JTextArea();
    JTextArea summaryArea = new JTextArea();
    JTextArea reportArea = new JTextArea();
    JTextArea savingsArea = new JTextArea();

    JTextField incomeAmount = new JTextField(15);
    JTextField incomeSource = new JTextField(15);
    JTextField expenseAmount = new JTextField(15);

    JComboBox<String> expenseCategory =
        new JComboBox<>(categories);

    Color dark = new Color(25, 35, 55);
    Color blue = new Color(55, 110, 220);
    Color background = new Color(240, 244, 250);

    public PersonalFinanceManagement() {

        setTitle("Personal Finance Management System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        for (String c : categories) {
            expenseTotals.put(c, 0.0);
        }

        main.add(loginPage(), "login");
        main.add(dashboardPage(), "dashboard");

        add(main);
        card.show(main, "login");

        setVisible(true);
    }

    JButton createButton(String text) {

        JButton b = new JButton(text);
        b.setBackground(blue);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 14));
        b.setFocusPainted(false);

        return b;
    }

    JLabel heading(String text) {

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 24));

        return label;
    }

    // LOGIN PAGE

    JPanel loginPage() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(dark);

        JPanel box = new JPanel(new GridLayout(4, 1, 10, 15));
        box.setPreferredSize(new Dimension(350, 250));
        box.setBorder(BorderFactory.createEmptyBorder(
            25, 25, 25, 25));
        box.setBackground(Color.WHITE);

        JLabel title = new JLabel(
            "PERSONAL FINANCE", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JTextField username = new JTextField();
        JPasswordField password = new JPasswordField();

        username.setBorder(BorderFactory.createTitledBorder("Username"));
        password.setBorder(BorderFactory.createTitledBorder("Password"));

        JButton login = createButton("LOGIN");

        login.addActionListener(e -> {

            if (!username.getText().trim().isEmpty()
                && password.getPassword().length > 0) {

                card.show(main, "dashboard");
                pages.show(content, "home");
                refreshDashboard();

            } else {
                JOptionPane.showMessageDialog(this,
                    "Enter Username and Password!");
            }
        });

        box.add(title);
        box.add(username);
        box.add(password);
        box.add(login);

        panel.add(box);

        return panel;
    }

    // DASHBOARD PAGE

    JPanel dashboardPage() {

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(background);

        JPanel header = new JPanel();
        header.setBackground(dark);

        JLabel title = new JLabel(
            "PERSONAL FINANCE MANAGEMENT");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 23));

        header.add(title);

        JPanel sidebar = new JPanel(new GridLayout(7, 1, 5, 10));
        sidebar.setBackground(dark);
        sidebar.setPreferredSize(new Dimension(190, 0));

        String[] menu = {
            "Dashboard", "Add Income", "Add Expense",
            "Savings", "Expense Summary",
            "Financial Report", "Logout"
        };

        for (String item : menu) {

            JButton button = createButton(item);
            sidebar.add(button);

            button.addActionListener(e -> {

                switch (item) {

                    case "Dashboard":
                        pages.show(content, "home");
                        refreshDashboard();
                        break;

                    case "Add Income":
                        pages.show(content, "income");
                        break;

                    case "Add Expense":
                        pages.show(content, "expense");
                        break;

                    case "Savings":
                        updateSavings();
                        pages.show(content, "savings");
                        break;

                    case "Expense Summary":
                        showCategorySummary();
                        pages.show(content, "summary");
                        break;

                    case "Financial Report":
                        showFinancialReport();
                        pages.show(content, "report");
                        break;

                    case "Logout":
                        card.show(main, "login");
                        break;
                }
            });
        }

        content.add(homePage(), "home");
        content.add(incomePage(), "income");
        content.add(expensePage(), "expense");
        content.add(savingsPage(), "savings");
        content.add(summaryPage(), "summary");
        content.add(reportPage(), "report");

        panel.add(header, BorderLayout.NORTH);
        panel.add(sidebar, BorderLayout.WEST);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    // DASHBOARD HOME

    JPanel homePage() {

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(background);
        panel.setBorder(BorderFactory.createEmptyBorder(
            25, 25, 25, 25));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBackground(background);
        top.add(heading("Welcome to Dashboard"));

        JPanel cards = new JPanel(new GridLayout(2, 2, 15, 15));
        cards.setBackground(background);

        cards.add(statCard("TOTAL INCOME", incomeLabel));
        cards.add(statCard("TOTAL EXPENSE", expenseLabel));
        cards.add(statCard("AVAILABLE BALANCE", balanceLabel));
        cards.add(statCard("TOTAL SAVINGS", savingsLabel));

        dashboardReport.setEditable(false);
        dashboardReport.setFont(
            new Font("Monospaced", Font.PLAIN, 13));

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(background);

        center.add(cards, BorderLayout.NORTH);
        center.add(new JScrollPane(dashboardReport),
            BorderLayout.CENTER);

        panel.add(top, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    JPanel statCard(String title, JLabel value) {

        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(
            Color.LIGHT_GRAY));

        JLabel name = new JLabel(title, JLabel.CENTER);
        name.setFont(new Font("Arial", Font.BOLD, 14));

        value.setHorizontalAlignment(JLabel.CENTER);
        value.setFont(new Font("Arial", Font.BOLD, 23));
        value.setForeground(blue);

        panel.add(name);
        panel.add(value);

        return panel;
    }

    // ADD INCOME PAGE

    JPanel incomePage() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(background);

        JPanel form = new JPanel(new GridLayout(4, 2, 15, 15));
        form.setBorder(BorderFactory.createEmptyBorder(
            30, 30, 30, 30));
        form.setBackground(Color.WHITE);

        JButton add = createButton("ADD INCOME");

        form.add(new JLabel("Income Source:"));
        form.add(incomeSource);

        form.add(new JLabel("Amount:"));
        form.add(incomeAmount);

        form.add(new JLabel(""));
        form.add(add);

        add.addActionListener(e -> {

            String source = incomeSource.getText().trim();
            Double amount = parseAmount(incomeAmount.getText());

            if (source.isEmpty() || amount == null) {
                JOptionPane.showMessageDialog(this,
                    "Enter valid income details!");
                return;
            }

            totalIncome += amount;

            String dateTime =
                LocalDateTime.now().format(formatter);

            dashboardReport.append(
                "Income: " + source +
                " - Rs." + amount +
                " | " + dateTime + "\n");

            incomeSource.setText("");
            incomeAmount.setText("");

            refreshDashboard();

            JOptionPane.showMessageDialog(this,
                "Income Added Successfully!");
        });

        JPanel container = new JPanel(new BorderLayout(10, 20));
        container.setBackground(background);

        container.add(heading("ADD INCOME"), BorderLayout.NORTH);
        container.add(form, BorderLayout.CENTER);

        panel.add(container);

        return panel;
    }

    // ADD EXPENSE PAGE

    JPanel expensePage() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(background);

        JPanel form = new JPanel(new GridLayout(5, 2, 15, 15));
        form.setBorder(BorderFactory.createEmptyBorder(
            30, 30, 30, 30));
        form.setBackground(Color.WHITE);

        JButton add = createButton("ADD EXPENSE");

        form.add(new JLabel("Expense Category:"));
        form.add(expenseCategory);

        form.add(new JLabel("Expense Amount:"));
        form.add(expenseAmount);

        form.add(new JLabel(""));
        form.add(add);

        add.addActionListener(e -> {

            String category =
                (String) expenseCategory.getSelectedItem();

            Double amount = parseAmount(expenseAmount.getText());

            if (amount == null) {
                JOptionPane.showMessageDialog(this,
                    "Enter a valid amount!");
                return;
            }

            // AUTOMATIC DATE AND TIME

            String dateTime =
                LocalDateTime.now().format(formatter);

            totalExpense += amount;

            expenseTotals.put(
                category,
                expenseTotals.get(category) + amount
            );

            String record =
                "Category: " + category +
                " | Amount: Rs. " + amount +
                " | Date & Time: " + dateTime;

            expenseHistory.add(record);

            dashboardReport.append(record + "\n");

            expenseAmount.setText("");

            refreshDashboard();

            JOptionPane.showMessageDialog(this,
                "Expense Added Successfully!\n\n" +
                "Category: " + category +
                "\nAmount: Rs. " + amount +
                "\nDate & Time: " + dateTime);
        });

        JPanel container = new JPanel(new BorderLayout(10, 20));
        container.setBackground(background);

        container.add(heading("ADD EXPENSE"), BorderLayout.NORTH);
        container.add(form, BorderLayout.CENTER);

        panel.add(container);

        return panel;
    }

    // SAVINGS PAGE

    JPanel savingsPage() {

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(background);
        panel.setBorder(BorderFactory.createEmptyBorder(
            20, 20, 20, 20));

        JPanel form = new JPanel(new GridLayout(6, 2, 12, 12));
        form.setBackground(Color.WHITE);

        JTextField name = new JTextField();
        JTextField amount = new JTextField();
        JTextField goalName = new JTextField();
        JTextField target = new JTextField();

        JButton addSaving = createButton("ADD SAVINGS");
        JButton addGoal = createButton("ADD GOAL");

        form.add(new JLabel("Savings Name:"));
        form.add(name);

        form.add(new JLabel("Savings Amount:"));
        form.add(amount);

        form.add(new JLabel(""));
        form.add(addSaving);

        form.add(new JLabel("Goal Name:"));
        form.add(goalName);

        form.add(new JLabel("Target Amount:"));
        form.add(target);

        form.add(new JLabel(""));
        form.add(addGoal);

        savingsArea.setEditable(false);

        addSaving.addActionListener(e -> {

            String savingName = name.getText().trim();
            Double a = parseAmount(amount.getText());

            if (savingName.isEmpty() || a == null) {
                JOptionPane.showMessageDialog(this,
                    "Enter valid savings details!");
                return;
            }

            savingsNames.add(savingName);
            savingsAmounts.add(a);
            totalSavings += a;

            String dateTime =
                LocalDateTime.now().format(formatter);

            dashboardReport.append(
                "Savings: " + savingName +
                " - Rs. " + a +
                " | " + dateTime + "\n");

            name.setText("");
            amount.setText("");

            refreshDashboard();
            updateSavings();

            JOptionPane.showMessageDialog(this,
                "Savings Added Successfully!");
        });

        addGoal.addActionListener(e -> {

            String gName = goalName.getText().trim();
            Double t = parseAmount(target.getText());

            if (gName.isEmpty() || t == null) {
                JOptionPane.showMessageDialog(this,
                    "Enter valid goal details!");
                return;
            }

            goalNames.add(gName);
            goalTargets.add(t);

            goalName.setText("");
            target.setText("");

            updateSavings();

            JOptionPane.showMessageDialog(this,
                "Savings Goal Added Successfully!");
        });

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBackground(background);
        center.add(form, BorderLayout.NORTH);
        center.add(new JScrollPane(savingsArea), BorderLayout.CENTER);

        panel.add(heading("SAVINGS MANAGEMENT"), BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    void updateSavings() {

        StringBuilder result = new StringBuilder();

        result.append("TOTAL SAVINGS: Rs. ")
            .append(String.format("%.2f", totalSavings))
            .append("\n\nSAVINGS DETAILS\n");

        for (int i = 0; i < savingsNames.size(); i++) {
            result.append(savingsNames.get(i))
                .append(" : Rs. ")
                .append(savingsAmounts.get(i))
                .append("\n");
        }

        result.append("\nSAVINGS GOALS\n");

        for (int i = 0; i < goalNames.size(); i++) {
            result.append(goalNames.get(i))
                .append(" : Target Rs. ")
                .append(goalTargets.get(i))
                .append("\n");
        }

        savingsArea.setText(result.toString());
    }

    // EXPENSE SUMMARY

    JPanel summaryPage() {

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(background);
        panel.setBorder(BorderFactory.createEmptyBorder(
            25, 25, 25, 25));

        panel.add(heading("CATEGORY-WISE EXPENSE"),
            BorderLayout.NORTH);

        summaryArea.setEditable(false);
        summaryArea.setFont(
            new Font("Monospaced", Font.PLAIN, 16));

        panel.add(new JScrollPane(summaryArea),
            BorderLayout.CENTER);

        return panel;
    }

    void showCategorySummary() {

        summaryArea.setText("");

        summaryArea.append(
            "========== EXPENSE SUMMARY ==========\n\n");

        for (String c : categories) {

            summaryArea.append(String.format(
                "%-18s Rs. %.2f%n",
                c, expenseTotals.get(c)));
        }

        summaryArea.append(
            "\n-------------------------------------\n");

        summaryArea.append(
            "TOTAL EXPENSE: Rs. " + totalExpense);

        summaryArea.append("\n\nEXPENSE HISTORY\n");
        summaryArea.append(
            "-------------------------------------\n");

        for (String record : expenseHistory) {
            summaryArea.append(record + "\n\n");
        }
    }

    // FINANCIAL REPORT

    JPanel reportPage() {

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(background);
        panel.setBorder(BorderFactory.createEmptyBorder(
            25, 25, 25, 25));

        panel.add(heading("FINANCIAL REPORT"),
            BorderLayout.NORTH);

        reportArea.setEditable(false);
        reportArea.setFont(
            new Font("Monospaced", Font.PLAIN, 14));

        panel.add(new JScrollPane(reportArea),
            BorderLayout.CENTER);

        return panel;
    }

    void showFinancialReport() {

        reportArea.setText("");

        reportArea.append("PERSONAL FINANCE REPORT\n\n");

        reportArea.append(
            "Total Income   : Rs. " + totalIncome + "\n");

        reportArea.append(
            "Total Expense  : Rs. " + totalExpense + "\n");

        reportArea.append(
            "Balance        : Rs. " +
            (totalIncome - totalExpense) + "\n");

        reportArea.append(
            "Total Savings  : Rs. " + totalSavings + "\n\n");

        reportArea.append("CATEGORY-WISE EXPENSES\n\n");

        for (String c : categories) {
            reportArea.append(
                c + " : Rs. " + expenseTotals.get(c) + "\n");
        }

        reportArea.append("\nEXPENSE HISTORY WITH DATE & TIME\n");
        reportArea.append(
            "-------------------------------------\n");

        for (String record : expenseHistory) {
            reportArea.append(record + "\n\n");
        }

        reportArea.append("\nSAVINGS DETAILS\n");

        for (int i = 0; i < savingsNames.size(); i++) {
            reportArea.append(
                savingsNames.get(i) + " : Rs. " +
                savingsAmounts.get(i) + "\n");
        }

        reportArea.append("\nSAVINGS GOALS\n");

        for (int i = 0; i < goalNames.size(); i++) {
            reportArea.append(
                goalNames.get(i) + " : Target Rs. " +
                goalTargets.get(i) + "\n");
        }
    }

    // DASHBOARD UPDATE

    void refreshDashboard() {

        incomeLabel.setText(
            "Rs. " + String.format("%.2f", totalIncome));

        expenseLabel.setText(
            "Rs. " + String.format("%.2f", totalExpense));

        balanceLabel.setText(
            "Rs. " + String.format("%.2f",
            totalIncome - totalExpense));

        savingsLabel.setText(
            "Rs. " + String.format("%.2f", totalSavings));
    }

    // AMOUNT VALIDATION

    Double parseAmount(String text) {

        try {

            double amount = Double.parseDouble(text);

            if (amount <= 0 || !Double.isFinite(amount)) {
                return null;
            }

            return amount;

        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() ->
            new PersonalFinanceManagement());
    }
}