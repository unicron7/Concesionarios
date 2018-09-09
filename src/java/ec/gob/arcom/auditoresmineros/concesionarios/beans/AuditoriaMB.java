/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.concesionarios.beans;

import ec.gob.arcom.auditoresmineros.controllers.AuditoriaController;
import ec.gob.arcom.auditoresmineros.controllers.CatalogoController;
import ec.gob.arcom.auditoresmineros.controllers.FileDownloadController;
import ec.gob.arcom.auditoresmineros.persistencia.daos.AuditoriaSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.CatalogoSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Auditoria;
import ec.gob.arcom.auditoresmineros.util.FacesUtilComun;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Will
 */
@Named(value = "auditoriaMB")
@SessionScoped
public class AuditoriaMB implements Serializable {
    @EJB
    private AuditoriaSBLocal auditoriaDao;
    @EJB
    private CatalogoSBLocal catalogoDao;
    
    private List<Auditoria> finalizadas;
    
    /**
     * Creates a new instance of ConcesionAuditorMB
     */
    public AuditoriaMB() {
        finalizadas= new ArrayList();
    }
    
    @PostConstruct
    public void inicializar() {
        obtenerFinalizadas();
    }

    public List<Auditoria> getFinalizadas() {
        return finalizadas;
    }

    public void setFinalizadas(List<Auditoria> finalizadas) {
        this.finalizadas = finalizadas;
    }
    
    private void obtenerFinalizadas() {
        finalizadas= AuditoriaController.listarAuditoriaPorDocumento(auditoriaDao, (String) FacesUtilComun.getSession().getAttribute("cr"), CatalogoController.getCatalogoByNemonico("ESTFIN", catalogoDao));
    }
    
    public StreamedContent obtenerInfAuditoria(Integer row) {
        return FileDownloadController.obtenerStreamedInfAuditoria(finalizadas.get(row).getId());
    }
}
