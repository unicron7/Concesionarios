/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.concesionarios.util;

import com.novell.ldap.LDAPConnection;
import com.novell.ldap.LDAPEntry;
import com.novell.ldap.LDAPException;
import com.novell.ldap.LDAPSearchResults;
import java.io.UnsupportedEncodingException;

/**
 *
 * @author mejiaw
 */
public class LDAPConexion {
    
    public static LDAPConnection conectar() {
        try {
            // Conexion con ldap, aqui se usa el administrador, deberia usarse
            // una cuenta con menos privilegios, pero con acceso.
            LDAPConnection ldapCon = new LDAPConnection();
            
            // Servidor y puerto ldap
            //lc.connect("servidor_ldap", 389);
            ldapCon.connect("10.10.6.68", 389);
            
            ldapCon.bind(LDAPConnection.LDAP_V3, "cn=Administrador,c=ec", "Latinus01".getBytes("UTF8"));
            System.out.println("Conectado...");
            return ldapCon;
        } catch (LDAPException ex) {
            System.out.println("Ocurrio un error: " + ex.toString());
        } catch (UnsupportedEncodingException e) {
            System.out.println("Ocurrio un error: " + e.toString());
        }
        return null;
    }
    
    public static LDAPEntry buscarUsuario(LDAPConnection ldapCon, String usr) throws LDAPException {
        // Se consultan los atributos de un usuario determinado,
        // operador2 en este caso.
        String searchBase="c=ec";
        int searchScope = LDAPConnection.SCOPE_SUB;
        String searchFilter = "(cn=" + usr + ")";
        
        LDAPSearchResults searchResults = ldapCon.search(searchBase,
                searchScope, searchFilter, null, // return all attributes
                false);
        LDAPEntry entrada = searchResults.next();
        return entrada;
    }
}
