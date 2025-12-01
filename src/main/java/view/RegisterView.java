package view;

import interface_adapter.google_login.GoogleLoginController;
import interface_adapter.register.RegisterController;
import interface_adapter.register.RegisterState;
import interface_adapter.register.RegisterViewModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import view.panel_makers.LabelTextPanel;

/**
 * The View for the Register Use Case.
 */
public class RegisterView extends JPanel implements ActionListener, PropertyChangeListener {

  public static final String VIEW_NAME = "register";

  private final RegisterViewModel registerViewModel;

  private final JTextField emailInputField = new JTextField(15);
  private final JTextField nicknameInputField = new JTextField(15);
  private final JPasswordField passwordInputField = new JPasswordField(15);
  private final JPasswordField repeatPasswordInputField = new JPasswordField(15);

  private final JLabel errorLabel = new JLabel();
  private final JButton register;
  private final JButton toLogin;
  private final JButton googleLogin;
  private RegisterController registerController = null;
  private GoogleLoginController googleLoginController = null;

  public RegisterView(RegisterViewModel registerViewModel) {
    this.registerViewModel = registerViewModel;
    registerViewModel.addPropertyChangeListener(this);

    final JLabel title = new JLabel(RegisterViewModel.TITLE_LABEL);
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    title.setFont(new Font("Arial", Font.BOLD, 20));

    final LabelTextPanel emailInfo = new LabelTextPanel(
        new JLabel(RegisterViewModel.EMAIL_LABEL), emailInputField);
    final LabelTextPanel nicknameInfo = new LabelTextPanel(
        new JLabel(RegisterViewModel.NICKNAME_LABEL), nicknameInputField);
    final LabelTextPanel passwordInfo = new LabelTextPanel(
        new JLabel(RegisterViewModel.PASSWORD_LABEL), passwordInputField);
    final LabelTextPanel repeatPasswordInfo = new LabelTextPanel(
        new JLabel(RegisterViewModel.REPEAT_PASSWORD_LABEL), repeatPasswordInputField);

    // Style error label
    errorLabel.setForeground(Color.RED);
    errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    final JPanel buttons = new JPanel();

    register = new JButton(RegisterViewModel.REGISTER_BUTTON_LABEL);
    buttons.add(register);

    googleLogin = new JButton(RegisterViewModel.GOOGLE_BUTTON_LABEL);
    buttons.add(googleLogin);

    toLogin = new JButton(RegisterViewModel.TO_LOGIN_BUTTON_LABEL);
    buttons.add(toLogin);

    // === Action Listeners ===

    // Custom Registration (Email/Password)
    register.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            if (evt.getSource().equals(register)) {
              final RegisterState currentState = registerViewModel.getState();

              if (registerController != null) {
                registerController.execute(
                    currentState.getEmail(),
                    currentState.getNickname(),
                    currentState.getPassword(),
                    currentState.getRepeatPassword()
                );
              } else {
                JOptionPane.showMessageDialog(RegisterView.this,
                    "Register Controller not initialized.");
              }
            }
          }
        }
    );

    // Google OAuth Registration/Login
    googleLogin.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            if (evt.getSource().equals(googleLogin)) {
              if (googleLoginController != null) {
                googleLoginController.execute();
              } else {
                JOptionPane.showMessageDialog(RegisterView.this,
                    "Google Login Controller not initialized.");
              }
            }
          }
        }
    );

    // Navigate to Login View
    toLogin.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            if (registerController != null) {
              registerController.switchToLoginView();
            } else {
              JOptionPane.showMessageDialog(RegisterView.this,
                  "Controller not initialized.");
            }
          }
        }
    );

    // === Document Listeners ===
    addEmailListener();
    addNicknameListener();
    addPasswordListener();
    addRepeatPasswordListener();

    // === Layout ===

    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    this.add(Box.createVerticalStrut(20));
    this.add(title);
    this.add(Box.createVerticalStrut(15));
    this.add(emailInfo);
    this.add(nicknameInfo);
    this.add(passwordInfo);
    this.add(repeatPasswordInfo);
    this.add(errorLabel);
    this.add(Box.createVerticalStrut(15));
    this.add(buttons);
  }

  // === Document Listener Helpers ===

  private void addEmailListener() {
    emailInputField.getDocument().addDocumentListener(new DocumentListener() {
      private void documentListenerHelper() {
        final RegisterState currentState = registerViewModel.getState();
        currentState.setEmail(emailInputField.getText());
        registerViewModel.setState(currentState);
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        documentListenerHelper();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        documentListenerHelper();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        documentListenerHelper();
      }
    });
  }

  private void addNicknameListener() {
    nicknameInputField.getDocument().addDocumentListener(new DocumentListener() {
      private void documentListenerHelper() {
        final RegisterState currentState = registerViewModel.getState();
        currentState.setNickname(nicknameInputField.getText());
        registerViewModel.setState(currentState);
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        documentListenerHelper();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        documentListenerHelper();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        documentListenerHelper();
      }
    });
  }

  private void addPasswordListener() {
    passwordInputField.getDocument().addDocumentListener(new DocumentListener() {
      private void documentListenerHelper() {
        final RegisterState currentState = registerViewModel.getState();
        currentState.setPassword(new String(passwordInputField.getPassword()));
        registerViewModel.setState(currentState);
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        documentListenerHelper();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        documentListenerHelper();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        documentListenerHelper();
      }
    });
  }

  private void addRepeatPasswordListener() {
    repeatPasswordInputField.getDocument().addDocumentListener(new DocumentListener() {
      private void documentListenerHelper() {
        final RegisterState currentState = registerViewModel.getState();
        currentState.setRepeatPassword(new String(repeatPasswordInputField.getPassword()));
        registerViewModel.setState(currentState);
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
        documentListenerHelper();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        documentListenerHelper();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        documentListenerHelper();
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    // Not used - all buttons have their own action listeners
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    final RegisterState state = (RegisterState) evt.getNewValue();

    // Display general error if present
    if (state.getGeneralError() != null && !state.getGeneralError().isEmpty()) {
      errorLabel.setText(state.getGeneralError());
    } else {
      errorLabel.setText("");
    }

    // Optional: You could add individual error labels for each field
    // by checking state.getEmailError(), state.getPasswordError(), etc.
  }

  public String getViewName() {
    return VIEW_NAME;
  }

  public void setRegisterController(RegisterController controller) {
    this.registerController = controller;
  }

  public void setGoogleLoginController(GoogleLoginController controller) {
    this.googleLoginController = controller;
  }
}