package view.panel_makers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ReviewPanel extends JPanel {
    private final JPanel titlePanel;
    private final JLabel authorLabel;
    private final JLabel creationDateLabel;
    private JTextArea contentText = new JTextArea(5, 20);
    private final JPanel footerPanel;
    private final JButton translateButton;
    private JScrollPane contentArea;
    private JPanel main;
    private JPanel margin;

    public ReviewPanel(String authorDisplayName, String creationDate, String content){
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setOpaque(true);



        authorLabel = new JLabel(authorDisplayName);
        authorLabel.setFont(authorLabel.getFont().deriveFont(Font.BOLD));

        creationDateLabel = new JLabel(creationDate);
        this.contentText.setText(content);
        this.contentText.setEditable(false);           // Makes it read-only
        this.contentText.setCursor(null);              // Removes the typing cursor
        this.contentText.setOpaque(false);             // Allows the background color of the parent panel to show through
        this.contentText.setLineWrap(true);            // Enables wrapping
        this.contentText.setWrapStyleWord(true);

        contentArea = new JScrollPane(contentText);
        contentArea.setBorder(null);


        titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setOpaque(true);
        titlePanel.add(authorLabel, BorderLayout.WEST);
        titlePanel.add(creationDateLabel, BorderLayout.EAST);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.X_AXIS));
        translateButton = new JButton("Translate");
        translateButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        footerPanel.add(Box.createHorizontalGlue());
        footerPanel.add(translateButton);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        footerPanel.setBackground(this.getBackground());

        margin = new JPanel();
        margin.setLayout(new BoxLayout(margin, BoxLayout.Y_AXIS));

        main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));


        main.setBorder(BorderFactory.createLineBorder(this.getBackground(), 15, true));
        main.add(titlePanel);
        main.add(contentArea);
        main.add(footerPanel);

        margin.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));
        margin.add(main);

        add(margin);

    }

    public void setAuthorName(String name){
        authorLabel.setText(name);
    }

    public void setContent(String content){
        contentText.setText(content);
    }

    public void setCreationDate(String date){
        creationDateLabel.setText(date);
    }

    //** Let other views hook into this card's Translate button. */
    public void addTranslateButtonListener(ActionListener listener) {
        translateButton.addActionListener(listener);
    }

    public String getContent() {
        return contentText.getText();
    }

    public String getCreationDate() {
        return creationDateLabel.getText();
    }

}
