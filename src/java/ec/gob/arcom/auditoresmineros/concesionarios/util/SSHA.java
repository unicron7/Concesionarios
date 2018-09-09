/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.gob.arcom.auditoresmineros.concesionarios.util;


import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Implementacion SSHA
 * @author Edison Romero
 */
public class SSHA {
    private static BASE64Encoder enc = new BASE64Encoder();
    private static BASE64Decoder dec = new BASE64Decoder();
    private MessageDigest sha = null;
    private static SSHA inst = new SSHA("SHA");
    private int size = SSHAConstantes.TAMANO;

    public static SSHA getInstance() {
        return inst;
    }

    /**
     * @param shaEnc
     */
    public void setAlgorithm(String shaEnc) {
        inst = new SSHA(shaEnc);
    }

    /**
     * public constructor
     */
    public SSHA(String alg) {

        if (alg.endsWith("256")) {
            size = SSHAConstantes.TIPO32;
        }
        if (alg.endsWith("512")) {
            size = SSHAConstantes.TIPO64;
        }

        try {
            sha = MessageDigest.getInstance(alg);
        } catch (java.security.NoSuchAlgorithmException ex) {
            Logger.getLogger(SSHA.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Create Digest for each entity values passed in
     *
     * @param salt String to set the base for the encryption
     * @param entity string to be encrypted
     * @return string representing the salted hash output of the encryption
     * operation
     */
    public String createDigest(String salt, String entity) {
        return createDigest(salt.getBytes(), entity);
    }

    /**
     * Create Digest for each entity values passed in
     *
     * @param salt byte array to set the base for the encryption
     * @param entity string to be encrypted
     * @return string representing the salted hash output of the encryption
     * operation
     */
    public String createDigest(byte[] salt, String entity) {
        String label = "{SSHA}";

        // Update digest object with byte array of the source clear text
        // string and the salt
        sha.reset();
        sha.update(entity.getBytes());
        sha.update(salt);

        // Complete hash computation, this results in binary data
        byte[] pwhash = sha.digest();

        return label + enc.encode(concatenate(pwhash, salt));
    }

    /**
     * Create Digest for each entity values passed in. A random salt is used.
     *
     * @param entity string to be encrypted
     * @return string representing the salted hash output of the encryption
     * operation
     */
    public String createDigest(String entity) {
        return inst.createDigest(randSalt(), entity);
    }

    /**
     * Check Digest against entity
     *
     * @param digest is digest to be checked against
     * @param entity entity (string) to be checked
     * @return TRUE if there is a match, FALSE otherwise
     */
    public boolean checkDigest(String digest, String entity) {
        return inst.checkDigest0(digest, entity);
    }

    /**
     * Check Digest against entity
     *
     * @param digest is digest to be checked against
     * @param entity entity (string) to be checked
     * @return TRUE if there is a match, FALSE otherwise
     */
    private boolean checkDigest0(String digest, String entity) {
        boolean valid = true;

        // ignore the {SSHA} hash ID
        digest = digest.substring(SSHAConstantes.TAMANOSSHA);

        // extract the SHA hashed data into hs[0]
        // extract salt into hs[1]
        byte[][] hs = null;
        try {
            hs = split(dec.decodeBuffer(digest), getSize());
        } catch (IOException ex) {
            Logger.getLogger(SSHA.class.getName()).log(Level.SEVERE, null, ex);
        }
        byte[] hash = hs[0];
        byte[] salt = hs[1];

        // Update digest object with byte array of clear text string and salt
        sha.reset();
        sha.update(entity.getBytes());
        sha.update(salt);

        // Complete hash computation, this is now binary data
        byte[] pwhash = sha.digest();
        
        if (!MessageDigest.isEqual(hash, pwhash)) {
            valid = false;
        }

        if (MessageDigest.isEqual(hash, pwhash)) {
            valid = true;
        }
        return valid;
    }

    /**
     * Combine two byte arrays
     *
     * @param l first byte array
     * @param r second byte array
     * @return byte[] combined byte array
     */
    private static byte[] concatenate(byte[] l, byte[] r) {
        byte[] b = new byte[l.length + r.length];
        System.arraycopy(l, 0, b, 0, l.length);
        System.arraycopy(r, 0, b, l.length, r.length);
        return b;
    }

    /**
     * split a byte array in two
     *
     * @param src byte array to be split
     * @param n element at which to split the byte array
     * @return byte[][] two byte arrays that have been split
     */
    private static byte[][] split(byte[] src, int n) {
        byte[] l, r;
        if (src == null || src.length <= n) {
            l = src;
            r = new byte[0];
        } else {
            l = new byte[n];
            r = new byte[src.length - n];
            System.arraycopy(src, 0, l, 0, n);
            System.arraycopy(src, n, r, 0, r.length);
        }
        byte[][] lr = {l, r};
        return lr;
    }

    public byte[] randSalt() {
        int saltLen = SSHAConstantes.SALTLENG;
        byte[] b = new byte[saltLen];
        for (int i = 0; i < saltLen; i++) {
            byte bt = (byte) (((Math.random()) * 256) - 128);
            b[i] = bt;
        }
        return b;
    }

    /**
     * @return the size
     */
    public int getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(int size) {
        this.size = size;
    }
    
}
