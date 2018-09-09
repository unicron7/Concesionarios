/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.concesionarios.util;

import ec.gob.arcom.auditoresmineros.persistencia.entidades.Auditor;

/**
 *
 * @author mejiaw
 */
public class AuditoriaCount {
    private Auditor auditor;
    private Long count;
    
    public AuditoriaCount(Auditor a, Long c) {
        this.auditor= a;
        this.count= c;
    }

    public Auditor getAuditor() {
        return auditor;
    }

    public void setAuditor(Auditor auditor) {
        this.auditor = auditor;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
