
package Modelo;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class PersonaDAO {
    Conexion conectar=new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    public List<Persona> listar;
    

    public List listar (){
        List<Persona>datos=new ArrayList<>();
        String sql="SELECT * FROM persona";
        
        try{
            con=conectar.getConnection();
            ps=con.prepareStatement(sql);
            rs=ps.executeQuery();
            while (rs.next()){
                Persona p=new Persona();
                p.setID(rs.getInt(1));
                p.setNom(rs.getString(2));
                p.setApe(rs.getString(3));
                p.setCorr(rs.getString(4));
                p.setTel(rs.getString(5));
                datos.add(p);
            }
        }catch (Exception e){
            
        }
        return datos;
    }
    
    public int agregar(Persona p){
        String sql="insert into persona(Nombres, Apellidos, Correo, Telefono) values(?,?,?,?)";
        try {
            con=conectar.getConnection();
            ps=con.prepareStatement(sql);
            ps.setString(1, p.getNom());
            ps.setString(2, p.getApe());
            ps.setString(3, p.getCorr());
            ps.setString(4, p.getTel());
            ps.executeUpdate();
        }catch (Exception e){   
        }
         return 1;
    }
    public int actualizar(Persona p){
        int r=0;
        String sql="Update persona set Nombres=?, Apellidos=?, Correo=?, Telefono=? where ID=?";
        try{
            con=conectar.getConnection();
            ps=con.prepareStatement(sql);
            ps.setString(1,p.getNom());
            ps.setString(2,p.getApe());
            ps.setString(3,p.getCorr());
            ps.setString(4,p.getTel());
            ps.setInt(5, p.getID());
            r=ps.executeUpdate();
            if (r==1){
                return 1;
            }else{
                return 0;
            }
        } catch(Exception e) {
        }
        return r;
}
    public void delete(int ID) {
    String sql = "DELETE FROM persona WHERE ID = ?";
    try {
        con = conectar.getConnection();
        ps = con.prepareStatement(sql);
        ps.setInt(1, ID); // Asignamos el valor del ID al parámetro de la consulta
        ps.executeUpdate();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
        public boolean existeCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM persona WHERE Correo = ?";
        try {
            con = conectar.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos (ps, con, rs) adecuadamente
        }
        return false;
    }

    public boolean existeTelefono(String telefono) {
        String sql = "SELECT COUNT(*) FROM persona WHERE Telefono = ?";
        try {
            con = conectar.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, telefono);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Cerrar recursos (ps, con, rs) adecuadamente
        }
        return false;
    }

}
