package src.main.java;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RecipeMaker extends JFrame {
    private JTextField dishNameField;
    private JTextArea recipeArea;
    private JButton generateButton;
    private JButton saveButton;
    private JButton editButton;
    private JButton deleteButton;
    private JList<String> recipeList;
    private DefaultListModel<String> recipeListModel;
    private RecipeManager recipeManager;
    private JLabel cookbookImage;

    public RecipeMaker() {
        // Set FlatLaf look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Smart Recipe Maker");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Wood Theme Colors
        Color backgroundColor = new Color(153, 101, 21);
        Color lightWoodColor = new Color(210, 180, 140);
        Color darkWoodColor = new Color(101, 67, 33);
        Color buttonColor = new Color(139, 69, 19);

        // Apply Colors
        getContentPane().setBackground(backgroundColor);

        dishNameField = new JTextField(20);
        dishNameField.setBackground(lightWoodColor);
        dishNameField.setForeground(darkWoodColor);

        recipeArea = new JTextArea();
        recipeArea.setBackground(lightWoodColor);
        recipeArea.setForeground(darkWoodColor);
        recipeArea.setFont(new Font("Serif", Font.PLAIN, 16));

        generateButton = new JButton("Generate Recipe");
        generateButton.setBackground(buttonColor);
        generateButton.setForeground(Color.WHITE);

        saveButton = new JButton("Save Recipe");
        saveButton.setBackground(buttonColor);
        saveButton.setForeground(Color.WHITE);

        editButton = new JButton("Edit Recipe");
        editButton.setBackground(buttonColor);
        editButton.setForeground(Color.WHITE);

        deleteButton = new JButton("Delete Recipe");
        deleteButton.setBackground(buttonColor);
        deleteButton.setForeground(Color.WHITE);

        recipeListModel = new DefaultListModel<>();
        recipeList = new JList<>(recipeListModel);
        recipeList.setBackground(lightWoodColor);
        recipeList.setForeground(darkWoodColor);
        recipeList.setFont(new Font("Serif", Font.PLAIN, 18));

        recipeManager = new RecipeManager();

        cookbookImage = new JLabel(new ImageIcon("assets/cookbook.png"));  // Load the image
        cookbookImage.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setBackground(backgroundColor);
        inputPanel.add(new JLabel("Dish Name:"));
        inputPanel.add(dishNameField);
        inputPanel.add(generateButton);

        JPanel recipePanel = new JPanel();
        recipePanel.setBackground(backgroundColor);
        recipePanel.setLayout(new BorderLayout());
        recipePanel.add(new JScrollPane(recipeArea), BorderLayout.CENTER);

        JPanel actionPanel = new JPanel();
        actionPanel.setBackground(backgroundColor);
        actionPanel.add(saveButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        recipePanel.add(actionPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(recipeList), recipePanel);
        splitPane.setDividerLocation(300);
        add(inputPanel, BorderLayout.NORTH);
        add(cookbookImage, BorderLayout.WEST);
        add(splitPane, BorderLayout.CENTER);

        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateRecipe();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveRecipe();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editRecipe();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRecipe();
            }
        });

        recipeList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedRecipe = recipeList.getSelectedValue();
                    showRecipeDialog(selectedRecipe);
                }
            }
        });

        recipeList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedRecipe = recipeList.getSelectedValue();
                    if (selectedRecipe != null) {
                        recipeArea.setText(recipeManager.getRecipe(selectedRecipe));
                        dishNameField.setText(selectedRecipe);
                    }
                }
            }
        });

        setVisible(true);
    }

    private void generateRecipe() {
        String dishName = dishNameField.getText();
        if (dishName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a dish name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String recipe = recipeManager.generateRecipe(dishName);
            System.out.println("API Response: " + recipe); // Log API response
            recipeArea.setText(recipe);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating recipe: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveRecipe() {
        String dishName = dishNameField.getText();
        String recipe = recipeArea.getText();
        if (dishName.isEmpty() || recipe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please generate a recipe first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        recipeManager.saveRecipe(dishName, recipe);
        if (!recipeListModel.contains(dishName)) {
            recipeListModel.addElement(dishName);
        }
    }

    private void editRecipe() {
        String selectedRecipe = recipeList.getSelectedValue();
        if (selectedRecipe == null) {
            JOptionPane.showMessageDialog(this, "Please select a recipe to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newRecipe = recipeArea.getText();
        if (newRecipe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Recipe cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        recipeManager.saveRecipe(selectedRecipe, newRecipe);
    }

    private void deleteRecipe() {
        String selectedRecipe = recipeList.getSelectedValue();
        if (selectedRecipe == null) {
            JOptionPane.showMessageDialog(this, "Please select a recipe to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        recipeManager.deleteRecipe(selectedRecipe);
        recipeListModel.removeElement(selectedRecipe);
        recipeArea.setText("");
        dishNameField.setText("");
    }

    private void showRecipeDialog(String dishName) {
        String recipe = recipeManager.getRecipe(dishName);
        if (recipe != null) {
            JTextArea recipeTextArea = new JTextArea(recipe);
            recipeTextArea.setEditable(false);
            recipeTextArea.setFont(new Font("Serif", Font.PLAIN, 16));
            JScrollPane scrollPane = new JScrollPane(recipeTextArea);
            JDialog dialog = new JDialog(this, dishName, true);
            dialog.add(scrollPane);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Recipe not found.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RecipeMaker());
    }
}
