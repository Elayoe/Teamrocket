/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.itu.smds.e2013.security;

import java.io.Serializable;

/**
 *
 * @author Rao
 */
public class RoleBasedToken implements Serializable {
    
    // result = true: request successed, serverToken provided
    // result = false: request failed, see error message for more details 
    public boolean result;
    
    public String errorMessage;
    
    public String token;
    
}
