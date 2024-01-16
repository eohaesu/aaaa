package com.deotis.digitalars.system.exception;

import com.deotis.digitalars.model.wms.WmsRequestEntity;

public class WmsException extends Exception{
	
    /**
	 * wms exception
	 */
	private static final long serialVersionUID = -7314228033004373716L;
	
	private final String sid;
	private WmsRequestEntity wmsValue;

    public String getSid() {
    	return sid;
    }
    
    public Object getWmsValue() {
    	return wmsValue;
    }
     
    public WmsException(String sid) {
		this.sid = sid;
    }

    public WmsException(String sid, String message) {
    	super(message);
        this.sid = sid;
    }
    
    public WmsException(String sid, WmsRequestEntity wmsValue, String message) {
    	super(message);
        this.sid = sid;
        this.wmsValue = wmsValue;
    }
}
