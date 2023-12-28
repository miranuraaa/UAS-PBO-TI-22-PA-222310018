package com.ibik.pbo.project;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONObject;


public class SearchEngine extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField textField;
    private JTable table;

    private static final String API_KEY = "pFdY29uHtjCPXtuXwzhclUdXCu5fza4d";
    private static final String BASE_URL = "https://api.shodan.io/shodan/host/search";

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    SearchEngine frame = new SearchEngine();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public SearchEngine() {
        setTitle("Search Engine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 877, 692);
        contentPane = new JPanel();
        contentPane.setLayout(null);
        setContentPane(contentPane);

        JButton btnSearch = new JButton("Search");
        btnSearch.setBounds(755, 22, 85, 38);
        contentPane.add(btnSearch);

        JLabel lblSearchHere = new JLabel("Search Here");
        lblSearchHere.setBounds(47, 37, 122, 13);
        contentPane.add(lblSearchHere);

        textField = new JTextField();
        textField.setBounds(29, 25, 705, 38);
        contentPane.add(textField);
        textField.setColumns(10);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(29, 97, 811, 548);
        contentPane.add(scrollPane_1);

        table = new JTable();
        table.setBackground(new Color(255, 255, 255));
        scrollPane_1.setViewportView(table);
        table.setModel(new DefaultTableModel(new Object[][] {}, new String[] { "Product", "Org" }));

        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performShodanAPICall();
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
    }

    private void handleWindowClosing() {
        int option = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", "Confirmation",
                JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            dispose();
        } else {
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
    }

    private void performShodanAPICall() {
        String query1 = textField.getText().trim();  // Trim any leading/trailing spaces

        if (query1.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Input Product Name", "Warning", JOptionPane.WARNING_MESSAGE);
            return; // Exit the method if the query is empty
        }

        try {
            String query = textField.getText();
            int page = 1;

            URL apiUrl = new URL(buildApiUrl(query, page));

            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

            connection.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                JSONArray matches = jsonResponse.getJSONArray("matches");

                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);

                boolean dataFound = false; // Fix: Initialize dataFound as true

                for (int i = 0; i < matches.length(); i++) {
                    JSONObject match = matches.getJSONObject(i);
                    String product = match.optString("product", "");
                    String org = match.optString("org", "");

                    if (product.toLowerCase().contains("smart tv") || product.toLowerCase().contains("camera")) {
                        model.addRow(new Object[]{product, org});
                        dataFound = true; // Fix: Set dataFound to true when data is found
                    }
                }

                if (!dataFound) {
                    JOptionPane.showMessageDialog(this, "Product not found", "Warning", JOptionPane.WARNING_MESSAGE);
                }

            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String buildApiUrl(String query, int page) {
        return String.format("%s?key=%s&query=%s&page=%d", BASE_URL, API_KEY, query, page);
    }

}
