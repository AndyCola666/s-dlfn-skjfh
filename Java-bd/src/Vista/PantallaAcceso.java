
package Vista;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PantallaAcceso extends JFrame implements ActionListener {
    private Vista ventanaPrincipal;
    private JTextField txtUsuario;
    private JPasswordField txtContraseña;

    public PantallaAcceso(Vista ventanaPrincipal) {
        this.ventanaPrincipal = ventanaPrincipal;
        setTitle("Pantalla de Acceso");
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        JLabel lblUsuario = new JLabel("Usuario:");
        JLabel lblContraseña = new JLabel("Contraseña:");
        txtUsuario = new JTextField(15);
        txtContraseña = new JPasswordField(15);
        JButton btnIniciarSesion = new JButton("Iniciar sesión");

        btnIniciarSesion.addActionListener(this);

        panel.add(lblUsuario);
        panel.add(txtUsuario);
        panel.add(lblContraseña);
        panel.add(txtContraseña);
        panel.add(btnIniciarSesion);

        add(panel);
        setLocationRelativeTo(null); // Centrar la ventana en pantalla
        setVisible(true);
    }

    private PantallaAcceso() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

@Override
public void actionPerformed(ActionEvent e) {
    String usuario = txtUsuario.getText();
    String contraseña = new String(txtContraseña.getPassword());

    // Aquí realizarías la validación del usuario y contraseña
    // Puedes comparar con una base de datos o utilizar una lógica específica

    if (usuario.equals("usuario") && contraseña.equals("contraseña")) {
        JOptionPane.showMessageDialog(this, "Acceso concedido");
        this.dispose(); // Cierra la pantalla de acceso
        ventanaPrincipal.setVisible(true); // Muestra la ventana principal
    } else {
        JOptionPane.showMessageDialog(this, "Acceso denegado. Usuario o contraseña incorrectos.");
    }
}

}