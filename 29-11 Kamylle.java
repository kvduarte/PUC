//Kamylle Vitoria Duarte de Oliveira 
//CMP2108/A03

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Sistema de Cadastro");
            JMenuBar menuBar = new JMenuBar();

            JMenu menu = new JMenu("Cadastros");
            JMenuItem clientesItem = new JMenuItem("Clientes");
            JMenuItem vendedoresItem = new JMenuItem("Vendedores");

            clientesItem.addActionListener(e -> exibirCadastro("Clientes", "Nome", "Email", "clientes", "email"));
            vendedoresItem.addActionListener(e -> exibirCadastro("Vendedores", "Nome", "Setor", "vendedores", "setor"));

            menu.add(clientesItem);
            menu.add(vendedoresItem);
            menuBar.add(menu);

            frame.setJMenuBar(menuBar);
            frame.setSize(400, 300);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            criarTabelas(); // Cria as tabelas no banco, se necessário
        });
    }

    private static void exibirCadastro(String titulo, String campo1, String campo2, String tabela, String colunaExtra) {
        JFrame frame = new JFrame("Cadastro de " + titulo);
        frame.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        JTextField campo1Field = new JTextField();
        JTextField campo2Field = new JTextField();

        formPanel.add(new JLabel(campo1 + ":"));
        formPanel.add(campo1Field);
        formPanel.add(new JLabel(campo2 + ":"));
        formPanel.add(campo2Field);

        JButton salvarButton = new JButton("Salvar");
        JButton listarButton = new JButton("Listar");

        salvarButton.addActionListener(e -> {
            String valorCampo1 = campo1Field.getText().trim();
            String valorCampo2 = campo2Field.getText().trim();

            if (valorCampo1.isEmpty() || valorCampo2.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Preencha todos os campos!", "Erro", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO " + tabela + " (nome, " + colunaExtra + ") VALUES (?, ?)")) {
                stmt.setString(1, valorCampo1);
                stmt.setString(2, valorCampo2);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(frame, titulo + " salvo com sucesso!");
                campo1Field.setText("");
                campo2Field.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Erro ao salvar " + titulo + ": " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        listarButton.addActionListener(e -> {
            try (Connection conn = Database.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM " + tabela)) {

                StringBuilder sb = new StringBuilder(titulo + ":\n");
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getInt("id"))
                      .append(", ").append(campo1).append(": ").append(rs.getString("nome"))
                      .append(", ").append(campo2).append(": ").append(rs.getString(colunaExtra))
                      .append("\n");
                }

                if (sb.length() == (titulo.length() + 2)) {
                    sb.append("Nenhum registro encontrado.");
                }
                JOptionPane.showMessageDialog(frame, sb.toString());
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Erro ao listar " + titulo + ": " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        frame.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(salvarButton);
        buttonPanel.add(listarButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setSize(400, 200);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private static void criarTabelas() {
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {

            String sqlClientes = """
                    CREATE TABLE IF NOT EXISTS clientes (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        email TEXT NOT NULL
                    )
                    """;

            String sqlVendedores = """
                    CREATE TABLE IF NOT EXISTS vendedores (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        nome TEXT NOT NULL,
                        setor TEXT NOT NULL
                    )
                    """;

            stmt.execute(sqlClientes);
            stmt.execute(sqlVendedores);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao criar tabelas: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class Database {
    private static final String URL = "jdbc:sqlite:cadastro.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}

----------------------------------
CREATE TABLE clientes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    email TEXT NOT NULL
);

CREATE TABLE vendedores (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome TEXT NOT NULL,
    setor TEXT NOT NULL
);

