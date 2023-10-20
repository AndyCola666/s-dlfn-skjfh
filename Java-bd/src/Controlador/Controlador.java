package Controlador;

import Modelo.Persona;
import Modelo.PersonaDAO;
import Vista.Vista;
import java.sql.Connection;
import Modelo.Conexion;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

public class Controlador implements ActionListener {
    private PersonaDAO dao = new PersonaDAO();
    private Persona p = new Persona();
    private Vista vista;
    private DefaultTableModel modelo = new DefaultTableModel();
    private boolean isEditing = false;
    private List<String> codigosDisponibles = new ArrayList<>();
    private List<String> codigosUtilizados = new ArrayList<>();
    private Connection connection;
    public Controlador(Vista v) {
        this.vista = v;
        this.vista.btnListar.addActionListener(this);
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnEditar.addActionListener(this);
        this.vista.btnOK.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        Conexion conexion = new Conexion();
        this.connection = conexion.getConnection();


        this.vista.Tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                System.out.println("Mouse pressed");
                int fila = vista.Tabla.rowAtPoint(e.getPoint());

                if (fila == -1) {
                    vista.Tabla.clearSelection();
                }
            }
        });
        this.vista.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                limpiarCampos();
            }
        });
        this.vista.txtTel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c)) {
                    e.consume();
                }
            }
        });
        codigosDisponibles.add("ihHJ92");
        codigosDisponibles.add("o8837d");
        codigosDisponibles.add("c0Io89");
        codigosDisponibles.add("101938");
        codigosDisponibles.add("123456");
        codigosDisponibles.add("dju189");
        codigosDisponibles.add("Just63");
        listar(vista.Tabla);
    }
        public void registrarUsuario(String nombre, String apellido, String correo, String telefono, String codigoVerificacion) {
        try {
            Conexion conexion = new Conexion();
            // Obtener la conexión a la base de datos
            Connection connection = conexion.getConnection();

            // Llamar al Stored Procedure "RegistrarUsuario"
            CallableStatement callableStatement = connection.prepareCall("{call RegistrarUsuario(?, ?, ?, ?, ?)}");
            callableStatement.setString(1, nombre);
            callableStatement.setString(2, apellido);
            callableStatement.setString(3, correo);
            callableStatement.setString(4, telefono);
            callableStatement.setString(5, codigoVerificacion);

            // Ejecutar el Stored Procedure
            callableStatement.execute();

            // Cerrar la conexión y el statement
            callableStatement.close();
            connection.close();
        } catch (SQLException e) {
            // Manejar el error
            e.printStackTrace();
        }
    }

@Override
public void actionPerformed(ActionEvent e) {
    if (e.getSource() == vista.btnListar) {
        listar(vista.Tabla);
        isEditing = false;
        vista.btnGuardar.setEnabled(true);
    }
    if (e.getSource() == vista.btnGuardar) {
        String Nom = vista.txtNombre.getText();
        String Ape = vista.txtApellido.getText();
        String Corr = vista.txtCorreo.getText();
        String Tel = vista.txtTel.getText();
        
        
 if (!validarCorreo(Corr)) {
            JOptionPane.showMessageDialog(vista, "El correo electrónico debe de ser válido");
            return; // Detener la ejecución, no se cumple la validación
        }
 
        if (Nom.isEmpty() || Ape.isEmpty() || Corr.isEmpty() || Tel.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Debe completar todos los campos");
        } else if (Tel.length() != 10) {
            JOptionPane.showMessageDialog(vista, "El número de teléfono debe tener 10 dígitos");
        } else {
            boolean correoExistente = dao.existeCorreo(Corr);
            boolean telefonoExistente = dao.existeTelefono(Tel);

            if (correoExistente) {
                JOptionPane.showMessageDialog(vista, "Ya existe un usuario con el mismo correo electrónico");
            } else if (telefonoExistente) {
                JOptionPane.showMessageDialog(vista, "Ya existe un usuario con el mismo teléfono");
            } else {
                String codigoIngresado = JOptionPane.showInputDialog(vista, "Introduzca el código de activación:");
                if (codigoIngresado != null && !codigoIngresado.isEmpty()) {
                    boolean codigoValido = verificarCodigo(codigoIngresado);
                    if (codigoValido) {
                        // Agregar el usuario solo si el código es válido
                        p.setNom(Nom);
                        p.setApe(Ape);
                        p.setCorr(Corr);
                        p.setTel(Tel);
                        int r = dao.agregar(p);
                        if (r == 1) {
                            JOptionPane.showMessageDialog(vista, "Usuario agregado con éxito");
                            codigosUtilizados.add(codigoIngresado); // Agregar el código a la lista de utilizados
                            limpiarCampos();
                            limpiarTabla();
                            listar(vista.Tabla);
                        } else {
                            JOptionPane.showMessageDialog(vista, "Error al agregar usuario");
                        }
                    } else {
                        JOptionPane.showMessageDialog(vista, "Código incorrecto o ya utilizado, no se puede agregar el usuario.");
                    }
                } else {
                    JOptionPane.showMessageDialog(vista, "Debe ingresar un código de activación.");
                }
            }
        }
    }
        if (e.getSource() == vista.btnEditar) {
            int fila = vista.Tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(vista, "Debe seleccionar una fila");
            } else {
                isEditing = true;
                vista.btnGuardar.setEnabled(false);

                int ID = Integer.parseInt(vista.Tabla.getValueAt(fila, 0).toString());
                String Nom = (String) vista.Tabla.getValueAt(fila, 1);
                String Ape = (String) vista.Tabla.getValueAt(fila, 2);
                String Corr = (String) vista.Tabla.getValueAt(fila, 3);
                String Tel = (String) vista.Tabla.getValueAt(fila, 4);
                vista.txtID.setText("" + ID);
                vista.txtNombre.setText(Nom);
                vista.txtApellido.setText(Ape);
                vista.txtCorreo.setText(Corr);
                vista.txtTel.setText(Tel);
            }
        }

                
        if (e.getSource() == vista.btnOK) {
            int fila = vista.Tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(vista, "Debe seleccionar un usuario primero");
            } else {
                actualizar();
            }
        }
        if (e.getSource() == vista.btnEliminar) {
            int fila = vista.Tabla.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(vista, "Debe seleccionar un usuario");
            } else {
                confirmarEliminacion();
            }
        }
    }

private boolean validarCorreo(String correo) {
    // Utilizar una expresión regular para validar el formato del correo
    // El formato "[Texto]@[Texto]." se puede expresar como: .+@.+\.
    String regex = ".+@.+\\..+";
    return correo.matches(regex);
}

private boolean verificarCodigo(String codigoIngresado) {
    // Construir la consulta SQL para verificar el código en la tabla "codver"
    String sql = "SELECT COUNT(*) FROM codver WHERE codigo = ?";

    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
        preparedStatement.setString(1, codigoIngresado);
        // Ejecutar la consulta SQL
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            int count = resultSet.getInt(1);
            if (count > 0) {
                // El código existe en la tabla "codver"
                // Verificar si el código ya ha sido utilizado
                if (codigosUtilizados.contains(codigoIngresado)) {
                    System.out.println("El código ya ha sido utilizado anteriormente.");
                    return false;
                } else {
                    System.out.println("Código válido y disponible.");
                    return true;
                }
            } else {
                // El código no existe en la tabla "codver"
                System.out.println("Código no válido o inexistente.");
                return false;
            }
        } else {
            System.out.println("Error al verificar el código.");
            return false;
        }
    } catch (SQLException ex) {
        System.out.println("Error de SQL: " + ex.getMessage());
        return false;
    }
}

    private void confirmarEliminacion() {
        int respuesta = JOptionPane.showConfirmDialog(vista, "¿Está seguro de eliminar el usuario?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (respuesta == JOptionPane.YES_OPTION) {
            delete();
        }
    }

    public void delete() {
        int fila = vista.Tabla.getSelectedRow();
        int ID = Integer.parseInt(vista.Tabla.getValueAt(fila, 0).toString());
        dao.delete(ID);
        JOptionPane.showMessageDialog(vista, "Usuario eliminado con éxito");
        limpiarCampos();
        limpiarTabla();
        listar(vista.Tabla);
    }

    public void limpiarTabla() {
        DefaultTableModel model = (DefaultTableModel) vista.Tabla.getModel();
        model.getDataVector().clear();
    }

public void actualizar() {
    int ID = Integer.parseInt(vista.txtID.getText());
    String Nom = vista.txtNombre.getText();
    String Ape = vista.txtApellido.getText();
    String Corr = vista.txtCorreo.getText();
    String Tel = vista.txtTel.getText();

    if (Nom.isEmpty() || Ape.isEmpty() || Corr.isEmpty() || Tel.isEmpty()) {
        JOptionPane.showMessageDialog(vista, "Debe completar todos los campos");
    } else if (Tel.length() != 10) {
        JOptionPane.showMessageDialog(vista, "El número de teléfono debe tener 10 dígitos");
    } else if (!validarCorreo(Corr)) {
        JOptionPane.showMessageDialog(vista, "El correo electrónico debe de ser válido");
    } else {
        // Verificar si el correo ya existe en la base de datos
        boolean correoExistente = dao.existeCorreo(Corr);
        // Verificar si el teléfono ya existe en la base de datos
        boolean telefonoExistente = dao.existeTelefono(Tel);

        // Obtener el correo y teléfono existentes en la fila seleccionada
        int fila = vista.Tabla.getSelectedRow();
        String correoExistenteFila = (String) vista.Tabla.getValueAt(fila, 3);
        String telefonoExistenteFila = (String) vista.Tabla.getValueAt(fila, 4);

        // Validar si el correo ya existe y es diferente al correo existente en la fila seleccionada
        if (correoExistente && !Corr.equals(correoExistenteFila)) {
            JOptionPane.showMessageDialog(vista, "Ya existe un usuario con el mismo correo electrónico");
        }
        // Validar si el teléfono ya existe y es diferente al teléfono existente en la fila seleccionada
        else if (telefonoExistente && !Tel.equals(telefonoExistenteFila)) {
            JOptionPane.showMessageDialog(vista, "Ya existe un usuario con el mismo teléfono");
        } else {
            p.setID(ID);
            p.setNom(Nom);
            p.setApe(Ape);
            p.setCorr(Corr);
            p.setTel(Tel);
            int r = dao.actualizar(p);
            if (r == 1) {
                JOptionPane.showMessageDialog(vista, "Usuario actualizado con éxito");
            } else {
                JOptionPane.showMessageDialog(vista, "Error al actualizar usuario");
            }
            limpiarCampos();
            limpiarTabla();
            listar(vista.Tabla);
            isEditing = false;
            vista.btnGuardar.setEnabled(true);
        }
    }
}

private void agregar() {
    String Nom = vista.txtNombre.getText();
    String Ape = vista.txtApellido.getText();
    String Corr = vista.txtCorreo.getText();
    String Tel = vista.txtTel.getText();

    if (Nom.isEmpty() || Ape.isEmpty() || Corr.isEmpty() || Tel.isEmpty()) {
        JOptionPane.showMessageDialog(vista, "Debe completar todos los campos");
    } else if (Tel.length() != 10) {
        JOptionPane.showMessageDialog(vista, "El número de teléfono debe tener 10 dígitos");
    } else if (!validarCorreo(Corr)) {
        JOptionPane.showMessageDialog(vista, "El correo electrónico debe de ser válido");
    } else {
        boolean correoExistente = dao.existeCorreo(Corr);
        boolean telefonoExistente = dao.existeTelefono(Tel);

        if (correoExistente) {
            JOptionPane.showMessageDialog(vista, "Ya existe un usuario con el mismo correo electrónico");
        } else if (telefonoExistente) {
            JOptionPane.showMessageDialog(vista, "Ya existe un usuario con el mismo teléfono");
        } else {
            String codigoIngresado = JOptionPane.showInputDialog(vista, "Introduzca el código de activación:");
            if (codigoIngresado != null && !codigoIngresado.isEmpty()) {
                boolean codigoValido = verificarCodigo(codigoIngresado);
                if (!codigoValido) {
                    JOptionPane.showMessageDialog(vista, "Código incorrecto o ya utilizado, no se puede agregar el usuario.");
                } else {
                    // Agregar el usuario solo si el código es válido
                    p.setNom(Nom);
                    p.setApe(Ape);
                    p.setCorr(Corr);
                    p.setTel(Tel);
                    int r = dao.agregar(p);
                    if (r == 1) {
                        JOptionPane.showMessageDialog(vista, "Usuario agregado con éxito");
                        codigosUtilizados.add(codigoIngresado); // Agregar el código a la lista de utilizados
                        limpiarCampos();
                        limpiarTabla();
                        listar(vista.Tabla);
                    } else {
                        JOptionPane.showMessageDialog(vista, "Error al agregar usuario");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(vista, "Debe ingresar un código de activación.");
            }
        }
    }
}


    public void listar(JTable Tabla) {
        modelo = (DefaultTableModel) Tabla.getModel();
        modelo.getDataVector().clear();
        List<Persona> lista = dao.listar();
        Object[] object = new Object[5];
        for (int i = 0; i < lista.size(); i++) {
            object[0] = lista.get(i).getID();
            object[1] = lista.get(i).getNom();
            object[2] = lista.get(i).getApe();
            object[3] = lista.get(i).getCorr();
            object[4] = lista.get(i).getTel();
            modelo.addRow(object);
        }
        vista.Tabla.setModel(modelo);
    }
    
    
    
    public static Connection getConnection() {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/registro";
            String user = "root";
            String pass = "admin666";
            connection = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            System.out.println("Error al cargar el driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error de SQL al obtener la conexión: " + e.getMessage());
        }
        return connection;
    }


    public void limpiarCampos() {
        vista.txtNombre.setText("");
        vista.txtApellido.setText("");
        vista.txtCorreo.setText("");
        vista.txtTel.setText("");
        vista.txtID.setText("");
        isEditing = false;
        vista.btnGuardar.setEnabled(true);
    }
}

