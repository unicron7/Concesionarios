/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.concesionarios.beans;

import ec.gob.arcom.auditoresmineros.catalogos.Catalogo;
import ec.gob.arcom.auditoresmineros.concesionarios.controllers.SolicitudController;
import ec.gob.arcom.auditoresmineros.controllers.AuditoriaController;
import ec.gob.arcom.auditoresmineros.controllers.CatalogoController;
import ec.gob.arcom.auditoresmineros.controllers.LocalidadController;
import ec.gob.arcom.auditoresmineros.persistencia.daos.AuditorSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.AuditoriaSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.CatalogoSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.ConcesionMineraSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.LocalidadSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.SolicitudSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.SystemConfigurationSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Auditor;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Auditoria;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.ConcesionMinera;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Solicitud;
import ec.gob.arcom.auditoresmineros.properties.PropertiesReader;
import ec.gob.arcom.auditoresmineros.util.FacesUtilComun;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author Will
 */
@Named(value = "solicitudMB")
@SessionScoped
public class SolicitudMB implements Serializable {
    @EJB
    private AuditoriaSBLocal auditoriaDao;
    @EJB
    private ConcesionMineraSBLocal concesionMineraDao;
    @EJB
    private AuditorSBLocal auditorDao;
    @EJB
    private SystemConfigurationSBLocal sysConfigDao;
    @EJB
    private SolicitudSBLocal solicitudDao;
    @EJB
    private CatalogoSBLocal catalogoDao;
    @EJB
    private LocalidadSBLocal localidadDao;
    
    /*
    * Propiedades
    */
    private List<Auditoria> auditorias;
    private List<ConcesionMinera> concesionesMineras;
    private String cr; //cr= cedula-ruc
    private int activeTab= 0;
    private Auditor asignedAuditor;
    private Auditoria selectedAuditoria;
    private String mensaje;
    private Integer concesionActiva;
    private Catalogo tipoAuditoriaRealizar;
    private Catalogo selectedTipoAuditorValue;
    //
    private List<Solicitud> solicitudes;
    private Solicitud selectedSolicitud;
    private Auditor selectedAuditorSolicitud;
    //
    private int auditorSeleccionado=0;
    //
    private List<Auditor> auditoresCalificados;
    
    /**
     * Creates a new instance of SolicitudMB
     */
    public SolicitudMB() {
        this.cr= FacesUtilComun.getSession().getAttribute("cr").toString();
        selectedSolicitud= new Solicitud();
        selectedAuditoria= new Auditoria();
    }
    
    @PostConstruct
    public void inicializar() {
        obtenerAuditorias();
        obtenerSolicitudes();
        obtenerConcesiones();
    }
    
    /*
     * Getters and Setters
     */

    public List<Auditor> getAuditoresCalificados() {
        return auditoresCalificados;
    }

    public void setAuditoresCalificados(List<Auditor> auditoresCalificados) {
        this.auditoresCalificados = auditoresCalificados;
    }
    
    public int getAuditorSeleccionado() {
        return auditorSeleccionado;
    }
    
    public void setAuditorSeleccionado(int auditorSeleccionado) {    
        this.auditorSeleccionado = auditorSeleccionado;
    }

    public List<Auditoria> getAuditorias() {
        return auditorias;
    }

    public void setAuditorias(List<Auditoria> auditorias) {
        this.auditorias = auditorias;
    }

    public Catalogo getSelectedTipoAuditorValue() {
        return selectedTipoAuditorValue;
    }

    public void setSelectedTipoAuditorValue(Catalogo selectedTipoAuditorValue) {
        this.selectedTipoAuditorValue = selectedTipoAuditorValue;
    }
    
    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public AuditorSBLocal getAuditorSB() {
        return auditorDao;
    }

    public void setAuditorSB(AuditorSBLocal auditorDao) {
        this.auditorDao = auditorDao;
    }

    public Auditoria getSelectedAuditoria() {
        return selectedAuditoria;
    }

    public void setSelectedAuditoria(Auditoria selectedAuditoria) {
        this.selectedAuditoria = selectedAuditoria;
    }
    
    public List<ConcesionMinera> getConcesionesMineras() {
        return concesionesMineras;
    }

    public void setConcesionesMineras(List<ConcesionMinera> concesionesMineras) {
        this.concesionesMineras = concesionesMineras;
    }

    public Auditor getAsignedAuditor() {
        return asignedAuditor;
    }

    public void setAsignedAuditor(Auditor asignedAuditor) {
        this.asignedAuditor = asignedAuditor;
    }

    public int getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(int activeTab) {
        this.activeTab = activeTab;
    }

    public List<Solicitud> getSolicitudes() {
        return solicitudes;
    }

    public void setSolicitudes(List<Solicitud> solicitudes) {
        this.solicitudes = solicitudes;
    }

    public Solicitud getSelectedSolicitud() {
        return selectedSolicitud;
    }

    public void setSelectedSolicitud(Solicitud selectedSolicitud) {
        this.selectedSolicitud = selectedSolicitud;
    }

    public Auditor getSelectedAuditorSolicitud() {
        return selectedAuditorSolicitud;
    }

    public void setSelectedAuditorSolicitud(Auditor selectedAuditorSolicitud) {
        this.selectedAuditorSolicitud = selectedAuditorSolicitud;
    }
    
    /*
    * Acciones
    */
    //Cargar las concesiones que ya tienen asignado un auditor - Objeto Auditoria
    private void obtenerAuditorias() {
        auditorias= AuditoriaController.listarAuditoriaPorDocumento(auditoriaDao, cr, CatalogoController.getCatalogoByNemonico("ESTVIG", catalogoDao));
    }
    
    //Cargar todas las concesiones que tiene el titular
    private void obtenerConcesiones() {
        Long tipoconmin= new Long(PropertiesReader.leer("tipoconmin"));
        Long tipolibapr= new Long(PropertiesReader.leer("tipolibapr"));
        Long tipopeqmin= new Long(PropertiesReader.leer("tipopeqmin"));
        
        List<ConcesionMinera> cs= concesionMineraDao.listConcesionMinera(cr, tipoconmin, tipolibapr, tipopeqmin); //cargar las concesiones correspondientes al número de documento de identidad
        List<Solicitud> ss= SolicitudController.obtenerSolicitudes(solicitudDao, cr); //Cargar las solicitudes generadas que corresponden a este usuario
        List<Auditoria> auds= AuditoriaController.listarAuditoriaPorDocumento(auditoriaDao, cr, CatalogoController.getCatalogoByNemonico("ESTVIG", catalogoDao)); //cargar las auditorias que ya tienen asignado un auditor y que estan vigentes
        
        List<ConcesionMinera> listaConcesiones= new ArrayList<ConcesionMinera>();
        
        //Buscar las concesiones que ya tienen generada una solicitud
        for(ConcesionMinera c : cs) {
            for(Solicitud s : ss) {
                if(c.getCodigo_concesion().equals(s.getConcesionMinera().getCodigo_concesion())) {
                    listaConcesiones.add(c);
                }
            }
        }
        
        //Buscar las concesiones que ya tienen asignado un auditor
        for(ConcesionMinera c : cs) {
            for(Auditoria a : auds) {
                if(c.getCodigo_concesion().equals(a.getConcesionMinera().getCodigo_concesion())) {
                    listaConcesiones.add(c);
                }
            }
        }

        //Quitar de la lista de concesiones del usuario aquellas que ya tienen generada una solicitud o asignado un auditor
        for(ConcesionMinera listaConcesion : listaConcesiones) {
            cs.remove(listaConcesion);
        }
        
        concesionesMineras= cs;
    }
    
    public void selectTipoAuditor() {
        if(this.selectedTipoAuditorValue.getNemonico().compareTo(Auditor.PRODUCCION)==0) {
            System.out.println("Se asignara un auditor de informes de produccion");
            this.tipoAuditoriaRealizar= catalogoDao.findByNemonico(Auditor.PRODUCCION);
            this.mensaje= "Se designará un Auditor para informe de producción. ¿Esta de acuerdo?";
            RequestContext.getCurrentInstance().execute("PF('infpdlg').show();");
            
        } else if(this.selectedTipoAuditorValue.getNemonico().compareTo(Auditor.CATEGORIA_A)==0) {
            System.out.println("Se asignara un auditor para cambio de fase");
            this.tipoAuditoriaRealizar= catalogoDao.findByNemonico(Auditor.CATEGORIA_A);
            this.mensaje= "Se designará un Auditor para cambio de fase. ¿Esta de acuerdo?";
            RequestContext.getCurrentInstance().execute("PF('catadlg').show();");
            
        } else if(this.selectedTipoAuditorValue.getNemonico().compareTo(Auditor.CATEGORIA_B)==0) {
            System.out.println("Se asignara un auditor para informes de exploracion");
            this.tipoAuditoriaRealizar= catalogoDao.findByNemonico(Auditor.CATEGORIA_B);
            this.mensaje= "Se designará un Auditor para informe de exploración. ¿Esta de acuerdo?";
            RequestContext.getCurrentInstance().execute("PF('catbdlg').show();");
        }
    }
    
    public void viewAsignedAuditor(Integer row) {
        asignedAuditor= auditorias.get(row).getAuditor();
    }
    
    //Nueva implementación - Solicitado que se presenten 3 Auditores de forma aleatoria y permitir al Titular Minero elegir con cual de los 3 se queda.
    //Se vuelve a cambiar ahora se solicita que se presenten todos los auditores calificados y el Titular Minero escoja uno de ellos
    public void newSolicitudAction(Integer row) {
        if(SolicitudController.existenCalificados(auditorDao, catalogoDao)) {
            verificarTipoAuditoria(row);
        } else {
            FacesUtilComun.showErrorMessage("Error", "Aun no existen auditores calificados");
        }
    }
    
    //Si existen auditores calificados que se puedan asignar se ejecuta el siguiente metodo
    private void verificarTipoAuditoria(Integer row) {
        if(this.concesionesMineras.get(row).getTipoMineria().getCodigo_tipo_mineria()
                .compareTo(new Long(PropertiesReader.leer("tipolibapr")))==0) {//Si la concesion es LIBRE APROVECHAMIENTO
            this.concesionActiva= row;
            this.tipoAuditoriaRealizar= catalogoDao.findByNemonico(Auditor.PRODUCCION);
            this.mensaje= "Se designará un Auditor para informe de producción. ¿Esta de acuerdo?";
            RequestContext.getCurrentInstance().execute("PF('infpdlg').show();");
        } else {//Si la concesion es PEQUEÑA MINERIA o CONCESION MINERA
            this.concesionActiva= row;
            RequestContext.getCurrentInstance().execute("PF('choosertipoauditordlg').show();");
        }
    }
    
    public void generarSolicitud() {
        this.auditoresCalificados= SolicitudController.obtenerAuditoresInfProd(auditorDao, catalogoDao);
        RequestContext.getCurrentInstance().update("viewauditorlist");
        RequestContext.getCurrentInstance().execute("PF('viewauditorlistdlg').show();");
    }
    
    public void generarSolicitudCatB() {
        this.auditoresCalificados= SolicitudController.obtenerAuditoresCatB(auditorDao, catalogoDao);
        RequestContext.getCurrentInstance().update("viewauditorlist");
        RequestContext.getCurrentInstance().execute("PF('viewauditorlistdlg').show();");
    }
    
    public void obtenerSolicitudes() {
        solicitudes= SolicitudController.obtenerSolicitudes(solicitudDao, cr);
    }
    
    public String presentaSolicitud(Solicitud s) {
        return "Solicitud para código: " + s.getConcesionMinera().getCodigo_arcom() + " - " + s.getConcesionMinera().getNombre_concesion().toUpperCase();
    }
    
    public void showSolicitudAction(Integer row) {
        selectedSolicitud= solicitudes.get(row);
    }
    
    public void onRowSelect(SelectEvent event) {
        selectedSolicitud= (Solicitud) event.getObject();
        RequestContext.getCurrentInstance().update("viewsolicitud");
        RequestContext.getCurrentInstance().execute("PF('viewsolicituddlg').show();");
    }
    
    public void showOpcion1Action() {
        selectedAuditorSolicitud= selectedSolicitud.getOpcion1();
    }
    
    public void showOpcion2Action() {
        selectedAuditorSolicitud= selectedSolicitud.getOpcion2();
    }
    
    public void showOpcion3Action() {
        selectedAuditorSolicitud= selectedSolicitud.getOpcion3();
    }
    
    //Se usa para cuando se escogia entre 3 auditores
    public void establecerAuditorSeleccionar(int value) {
        if(value==1) {
            selectedAuditorSolicitud= selectedSolicitud.getOpcion1();
        } else if(value==2) {
            selectedAuditorSolicitud= selectedSolicitud.getOpcion2();
        } else if(value==3) {
            selectedAuditorSolicitud= selectedSolicitud.getOpcion3();
        }
    }
    
    public void saveAuditorSelectedAction() {
        Auditoria auditoria= new Auditoria(); //objeto que guarda la relación entre la concesion y el auditor y del tipo de auditoria
        
        auditoria.setConcesionMinera(concesionesMineras.get(concesionActiva));
        auditoria.setAuditor(selectedAuditorSolicitud);
        auditoria.setDocumentoTitular(cr);
        auditoria.setTipoAuditoria(tipoAuditoriaRealizar);
        auditoria.setFechaDeAsignacion(Calendar.getInstance().getTime());
        auditoria.setHoraDeAsignacion(Calendar.getInstance().getTime());
        auditoria.setEstadoAuditoria(CatalogoController.getCatalogoByNemonico("ESTVIG", catalogoDao));
        System.out.println("Guardando auditor: " + selectedAuditorSolicitud.getRazonSocial());
        SolicitudController.guardarAuditoria(auditoriaDao, auditoria);
        obtenerAuditorias();
        obtenerConcesiones();
        this.activeTab= 1;
        selectedAuditoria= auditoria;
        FacesUtilComun.showInfoMessage("", "Auditor asignado correctamente");
    }
    
    public void showAuditorInfo(int value) {
        System.out.println(value);
        selectedAuditorSolicitud= auditoresCalificados.get(value);
        RequestContext.getCurrentInstance().update("viewauditoropc");
        RequestContext.getCurrentInstance().execute("PF('viewauditoropcdlg').show();");
    }
    /*
    public String obtenerNombreProvincia(String codigoInec) {
        return LocalidadController.getProvinciaByCodigoInec(codigoInec, localidadDao).getNombre();
    }*/
    
}
