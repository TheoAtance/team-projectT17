package view;

import interface_adapter.google_login.GoogleLoginController;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;
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
 * The View for when the user is logging into the program.
 */
public class LoginView extends JPanel implements ActionListener, PropertyChangeListener {

  public static final String VIEW_NAME = "login";
  private final LoginViewModel loginViewModel;

  private final JTextField emailInputField = new JTextField(15);
  private final JLabel errorLabel = new JLabel();
  private final JPasswordField passwordInputField = new JPasswordField(15);

  private final JButton logIn;
  private final JButton toRegister;
  private final JButton googleLogin;

  private LoginController loginController = null;
  private GoogleLoginController googleLoginController = null;

  public LoginView(LoginViewModel loginViewModel) {
    this.loginViewModel = loginViewModel;
    this.loginViewModel.addPropertyChangeListener(this);

    final JLabel title = new JLabel(LoginViewModel.TITLE_LABEL);
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    title.setFont(new Font("Arial", Font.BOLD, 20));

    final LabelTextPanel emailInfo = new LabelTextPanel(
        new JLabel(LoginViewModel.EMAIL_LABEL), emailInputField);
    final LabelTextPanel passwordInfo = new LabelTextPanel(
        new JLabel(LoginViewModel.PASSWORD_LABEL), passwordInputField);

    // Style error label
    errorLabel.setForeground(Color.RED);
    errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    final JPanel buttons = new JPanel();
    logIn = new JButton(LoginViewModel.LOGIN_BUTTON_LABEL);
    buttons.add(logIn);

    googleLogin = new JButton(LoginViewModel.GOOGLE_BUTTON_LABEL);
    buttons.add(googleLogin);

    toRegister = new JButton(LoginViewModel.TO_REGISTER_BUTTON_LABEL);
    buttons.add(toRegister);

    // === Action Listeners ===

    // Custom Login (Email/Password)
    logIn.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            if (evt.getSource().equals(logIn)) {
              final LoginState currentState = loginViewModel.getState();

              if (loginController != null) {
                loginController.execute(
                    currentState.getEmail(),
                    currentState.getPassword()
                );
              } else {
                JOptionPane.showMessageDialog(LoginView.this,
                    "Login Controller not initialized.");
              }
            }
          }
        }
    );

    // Google OAuth Login
    googleLogin.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            if (evt.getSource().equals(googleLogin)) {
              if (googleLoginController != null) {
                googleLoginController.execute();
              } else {
                JOptionPane.showMessageDialog(LoginView.this,
                    "Google Login Controller not initialized.");
              }
            }
          }
        }
    );

    // Navigate to Register View
    toRegister.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
            if (loginController != null) {
              loginController.switchToRegisterView();
            } else {
              JOptionPane.showMessageDialog(LoginView.this,
                  "Controller not initialized.");
            }
          }
        }
    );

    // === Document Listeners ===

    emailInputField.getDocument().addDocumentListener(new DocumentListener() {
      private void documentListenerHelper() {
        final LoginState currentState = loginViewModel.getState();
        currentState.setEmail(emailInputField.getText());
        loginViewModel.setState(currentState);
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

    passwordInputField.getDocument().addDocumentListener(new DocumentListener() {
      private void documentListenerHelper() {
        final LoginState currentState = loginViewModel.getState();
        currentState.setPassword(new String(passwordInputField.getPassword()));
        loginViewModel.setState(currentState);
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

    // === Layout ===

    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    this.add(Box.createVerticalStrut(20));
    this.add(title);
    this.add(Box.createVerticalStrut(15));
    this.add(emailInfo);
    this.add(passwordInfo);
    this.add(errorLabel);
    this.add(Box.createVerticalStrut(15));
    this.add(buttons);


  }

  @Override
  public void actionPerformed(ActionEvent evt) {
    // Not used - all buttons have their own action listeners
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    final LoginState state = (LoginState) evt.getNewValue();
    setFields(state);

    // Display error if present
    if (state.getLoginError() != null && !state.getLoginError().isEmpty()) {
      errorLabel.setText(state.getLoginError());
    } else {
      errorLabel.setText("");
    }
  }

  private void setFields(LoginState state) {
    emailInputField.setText(state.getEmail());
    // Note: We don't set password field for security reasons
  }

  public String getViewName() {
    return VIEW_NAME;
  }

  public void setLoginController(LoginController loginController) {
    this.loginController = loginController;
  }

  public void setGoogleLoginController(GoogleLoginController googleLoginController) {
    this.googleLoginController = googleLoginController;
  }
}