/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.concesionarios.controllers;

import ec.gob.arcom.auditoresmineros.catalogos.Catalogo;
import ec.gob.arcom.auditoresmineros.concesionarios.beans.SolicitudMB;
import ec.gob.arcom.auditoresmineros.concesionarios.util.AuditoriaCount;
import ec.gob.arcom.auditoresmineros.controllers.AuditoriaController;
import ec.gob.arcom.auditoresmineros.persistencia.daos.AuditorSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.AuditoriaSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.CatalogoSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.daos.SolicitudSBLocal;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Auditor;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Auditoria;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.ConcesionMinera;
import ec.gob.arcom.auditoresmineros.persistencia.entidades.Solicitud;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author Will
 */
public class SolicitudController {
    
    public static boolean existenCalificados(AuditorSBLocal auditorSB, CatalogoSBLocal catalogoSB) {
        List<Auditor> auditores= auditorSB.listAuditores(catalogoSB.findByNemonico(Auditor.INSCRITO_CALIFICADO));
        return auditores.size()>0;
    }
    
    public Solicitud obtenerAuditoresInfProd(AuditorSBLocal auditorSB, AuditoriaSBLocal auditoriaDao, Catalogo estado, CatalogoSBLocal catalogoSB) {
        List<Auditor> auditores= auditorSB.listAuditores(catalogoSB.findByNemonico(Auditor.INSCRITO_CALIFICADO), 
                catalogoSB.findByNemonico(Auditor.PRODUCCION));
        System.out.println("Numero de auditores infp calificados: " + auditores.size());
        
        balancearCarga(auditores, auditoriaDao, estado);
        
        Solicitud s= new Solicitud();
        int min=0;
        int max=0;
        int numAleatorio=0;
        if(auditores.size()>=3) {
            max= auditores.size();
            numAleatorio=(int)Math.floor(Math.random()*(min-max)+max);
            Auditor a= auditores.get(numAleatorio);
            s.setOpcion1(a);
            auditores.remove(a);

            max= max-1;
            numAleatorio=(int)Math.floor(Math.random()*(min-max)+max);
            a= auditores.get(numAleatorio);
            s.setOpcion2(a);
            auditores.remove(a);

            max= max-1;
            numAleatorio= (int)Math.floor(Math.random()*(min-max)+max);
            a= auditores.get(numAleatorio);
            s.setOpcion3(a);
        } else {
            return null;
        }
        return s;
    }
    
    public Solicitud obtenerAuditoresCatB(AuditorSBLocal auditorSB, AuditoriaSBLocal auditoriaDao, Catalogo estado, CatalogoSBLocal catalogoSB) {
        List<Auditor> auditores= auditorSB.listAuditores(catalogoSB.findByNemonico(Auditor.INSCRITO_CALIFICADO),
                catalogoSB.findByNemonico(Auditor.CATEGORIA_B));
        System.out.println("Numero de auditores catb calificados: " + auditores.size());
        
        balancearCarga(auditores, auditoriaDao, estado);
        
        Solicitud s= new Solicitud();
        int min=0;
        int max=0;
        int numAleatorio=0;
        if(auditores.size()>=3) {
            max= auditores.size();
            numAleatorio=(int)Math.floor(Math.random()*(min-max)+max);
            Auditor a= auditores.get(numAleatorio);
            s.setOpcion1(a);
            auditores.remove(a);

            max= max-1;
            numAleatorio=(int)Math.floor(Math.random()*(min-max)+max);
            a= auditores.get(numAleatorio);
            s.setOpcion2(a);
            auditores.remove(a);

            max= max-1;
            numAleatorio= (int)Math.floor(Math.random()*(min-max)+max);
            a= auditores.get(numAleatorio);
            s.setOpcion3(a);
        } else {
            return null;
        }
        return s;
    }
    
    //Nueva implementación - Solicitado que se presenten 3 Auditores de forma aleatoria y permitir al Titular Minero elegir con cual de los 3 se queda.
    public boolean generarSolicitud(SolicitudSBLocal solicitudDao, AuditorSBLocal auditorDao, ConcesionMinera concesion, Long tipoAuditoria, String documento,
            AuditoriaSBLocal auditoriaDao, Catalogo estado, CatalogoSBLocal catalogoDao) {
        Solicitud solicitud= null;
        System.out.println("TIPO DE AUDITORIA: " + tipoAuditoria);
        if(tipoAuditoria.compareTo(catalogoDao.findByNemonico(Auditor.PRODUCCION).getId())==0) {
            System.out.println("Generando informe de producción");
            solicitud= obtenerAuditoresInfProd(auditorDao, auditoriaDao, estado, catalogoDao);
        } else if(tipoAuditoria.compareTo(catalogoDao.findByNemonico(Auditor.CATEGORIA_A).getId())==0) {
            System.out.println("Generando categoria A - cambio de fase");
        } else if(tipoAuditoria.compareTo(catalogoDao.findByNemonico(Auditor.CATEGORIA_B).getId())==0) {
            System.out.println("Generando categoria B - informes de exploracion");
            solicitud= obtenerAuditoresCatB(auditorDao, auditoriaDao, estado, catalogoDao);
        }
        
        if(solicitud != null) {
            Calendar c= Calendar.getInstance();
            solicitud.setFechaSolicitud(c.getTime());
            solicitud.setHoraSolicitud(c.getTime());
            solicitud.setConcesionMinera(concesion);
            solicitud.setTipoAuditoria(tipoAuditoria);
            solicitud.setDocumentoTitular(documento);
            //Guardar la solicitud
            guardarSolicitud(solicitudDao, solicitud);
            return true;
        }
        return false;
    }
    
    private void guardarSolicitud(SolicitudSBLocal solicitudDao, Solicitud solicitud) {
        solicitud.setActivo(true);
        solicitudDao.create(solicitud);
    }
    
    private static void eliminarSolicitud(SolicitudSBLocal solicitudDao, Solicitud solicitud) {
        solicitud.setActivo(false);
        solicitudDao.delete(solicitud);
    }
    
    public static List<Solicitud> obtenerSolicitudes(SolicitudSBLocal solicitudDao) {
        return solicitudDao.list();
    }
    
    public static List<Solicitud> obtenerSolicitudes(SolicitudSBLocal solicitudDao, String documento) {
        return solicitudDao.list(documento);
    }
    
    public static void guardarAuditoria(AuditoriaSBLocal auditoriaDao, SolicitudSBLocal solicitudDao, Auditoria auditoria, Solicitud solicitud) {
        auditoria.setActivo(true);
        auditoriaDao.save(auditoria);
        solicitud.setSeleccion(auditoria.getAuditor());
        SolicitudController.eliminarSolicitud(solicitudDao, solicitud);
    }
    
    public static void guardarAuditoria(AuditoriaSBLocal auditoriaDao, Auditoria auditoria) {
        auditoria.setActivo(true);
        auditoriaDao.save(auditoria);
    }
    
    public static List<Auditor> obtenerAuditoresInfProd(AuditorSBLocal auditorDao, CatalogoSBLocal catalogoDao) {
        return auditorDao.listAuditores(catalogoDao.findByNemonico(Auditor.INSCRITO_CALIFICADO), catalogoDao.findByNemonico(Auditor.PRODUCCION));
    }
    public static List<Auditor> obtenerAuditoresCatB(AuditorSBLocal auditorDao, CatalogoSBLocal catalogoDao) {
        return auditorDao.listAuditores(catalogoDao.findByNemonico(Auditor.INSCRITO_CALIFICADO), catalogoDao.findByNemonico(Auditor.CATEGORIA_B));
    }
    
    private void balancearCarga(List<Auditor> auditores, AuditoriaSBLocal auditoriaDao, Catalogo estado) {
        List<AuditoriaCount> contadores= new ArrayList();
        for(Auditor auditor : auditores) {
            Long cuenta= AuditoriaController.contarAuditoriaPorAuditor(auditoriaDao, auditor.getId(), estado);
            contadores.add(new AuditoriaCount(auditor, cuenta));
        }
        presentarContadores(contadores);
        contarDiferentes(contadores);
    }
    
    private void presentarContadores(List<AuditoriaCount> contadores) {
        for(AuditoriaCount ac : contadores) {
            System.out.println("Auditor: " + ac.getAuditor().getRazonSocial() + " - " + ac.getCount());
        }
    }
    
    private void contarDiferentes(List<AuditoriaCount> contadores) {
        List<Long> diferentes= new ArrayList();
        for(AuditoriaCount ac : contadores) {
            boolean existe= false;
            for(Long d : diferentes) {
                if(ac.getCount().compareTo(d)==0) {
                    existe=true;
                }
            }
            if(!existe)
                diferentes.add(ac.getCount());
        }
        presentarDiferentes(diferentes);
    }
    
    private void presentarDiferentes(List<Long> diferentes) {
        System.out.println("#######################################");
        for(Long l : diferentes) {
            System.out.println("Contador: " + l);
        }
        System.out.println("Min: " + obtenerMin(diferentes));
        System.out.println("Max: " + obtenerMax(diferentes));
        
    }
    
    private Long obtenerMin(List<Long> diferentes) {
        Long num;
        Long min= (long)0;
        
        System.out.println("\nCalculando minimo");
        
        for (int i = 0; i < diferentes.size(); i++) {
            num = diferentes.get(i);

            if (num < min) {
                min = num;
            }
        }
        return min;
    }
    
    private Long obtenerMax(List<Long> diferentes) {
        Long num;
        Long max= (long)0;
        
        System.out.println("\nCalculando maximo");
        
        for (int i = 0; i < diferentes.size(); i++) {
            num = diferentes.get(i);

            if (num > max) {
                max = num;
            }
        }
        return max;
    }
}
