/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.concesionarios.beans;

import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import ec.gob.arcom.auditoresmineros.concesionarios.util.LDAPConexion;
import ec.gob.arcom.auditoresmineros.concesionarios.util.SSHA;
import ec.gob.arcom.auditoresmineros.persistencia.daos.UsuarioSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Auditor;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Usuario;
import ec.gob.arcom.auditoresmineros.properties.PropertiesReader;
import ec.gob.arcom.auditoresmineros.util.Crypt;
import ec.gob.arcom.auditoresmineros.util.FacesUtilComun;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Will
 */
@Named(value = "loginMB")
@SessionScoped
public class LoginMB implements Serializable {
    @EJB
    private UsuarioSBLocal usuarioSB;
    
    
    /**
     * Creates a new instance of LoginBean
     */
    public LoginMB() {
    }
    
    private String userName;
    private String userPassword;
    private boolean logged = false;
    private boolean admin= false;
    private Usuario usuario;
    private String sgmLink;
    
    
    public String getSgmLink() {
        sgmLink= loadSGMLink();
        return sgmLink;
    }

    public void setSgmLink(String sgmLink) {
        this.sgmLink = sgmLink;
    } 

    public UsuarioSBLocal getUsuarioSB() {
        return usuarioSB;
    }

    public void setUsuarioSB(UsuarioSBLocal usuarioSB) {
        this.usuarioSB = usuarioSB;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public boolean isLogged() {
        return logged;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    
    /**
     * Acciones
     * @return 
     */
    public String logoutAction() {
        HttpSession session = FacesUtilComun.getSession();
        session.invalidate();
        return "login";
    }
    
    public String loginAction() {
        boolean result= this.obtenerUsuario(userName, userPassword);
        
        if (result) {
            // get Http Session and store username
            HttpSession session = FacesUtilComun.getSession();
            session.setAttribute("logged", logged);
            session.setAttribute("nombre", usuario.getNombre().toUpperCase());
            session.setAttribute("apellido", usuario.getApellido().toUpperCase());
            session.setAttribute("cr", usuario.getNumero_documento()); //cr= cedula ruc
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Bienvenid@",
                    usuario.getNombre().toUpperCase() + 
                            " " + usuario.getApellido().toUpperCase()));
            return "cuentausuario";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Error!", "Usuario o clave incorrectos!"));
            return "login";
        }
    }
    
    private boolean obtenerUsuario(String ruc, String userPassword) {
        Usuario usr= this.usuarioSB.findByLogin(ruc);
        
        if (usr!=null) {
            if(validarCredenciales(userPassword, usr.getClave())) {
                this.logged= true;
                this.usuario= usr;
                return true;
            }
        }
        return false;
    }
    
    private boolean validarCredenciales(String pwdUser, String pwdUserDB) {
        return pwdUserDB.equals(Crypt.cryptMD5(pwdUser));
    }
    
    public String loadSGMLink() {
        return PropertiesReader.leer("sgmlink");
    }
}
