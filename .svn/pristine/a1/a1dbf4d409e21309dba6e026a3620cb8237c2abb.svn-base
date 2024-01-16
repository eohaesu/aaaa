package com.deotis.digitalars.util.collection;

import java.util.HashMap;

import com.deotis.digitalars.util.common.EscapeUtil;

/**
 * 
 * @author jongjin
 * @description common deotis collection Map
 * @param <K>
 * @param <V>
 */

public class DMap<K, V> extends HashMap<K, V> {
	private static final long serialVersionUID = 1L;

	/**
	 * Xss prevent escape if value is string
	 */
	@SuppressWarnings("unchecked")
	public V put(K key, V value) {
		if(key != null && key instanceof String)
			key = (K) ((String) key).toLowerCase();
		if(value != null && value instanceof String)
			value = (V) EscapeUtil.escape((String) value);
		return super.put(key, value);
	};
	
	/**
	 * original
	 */
	public V putOrigin(K key, V value) {
		return super.put(key, value);
	};
	
	/**
	 * Xss convert with unescape if value is string
	 */
	@SuppressWarnings("unchecked")
	public V get(Object key) {
		return super.get(key) instanceof String ? (V) EscapeUtil.unescape(String.valueOf(super.get(key))) : super.get(key) ;
	};
	
	/**
	 * unEscaped String 값을 반환
	 * @param key
	 * @return
	 */
	public String getUnEscapeString(Object key) {
		if (key != null) {
			return EscapeUtil.unescape(String.valueOf(get(key)));
		}
		return null;
	}

	/**
	 * String 값을 반환
	 * @param key
	 * @return
	 */
	public String getString(Object key) {
		if (key != null) {
			return String.valueOf(get(key));
		}
		return null;
	}

	/**
	 * Int 값을 반환
	 * @param key
	 * @return
	 */
	public int getInt(Object key) {
		if (key != null) {
			return Integer.parseInt(getString(key));
		}
		throw new NullPointerException();
	}

	/**
	 * Double 값을 반환
	 * @param key
	 * @return
	 */
	public double getDouble(Object key) {
		if (key != null) {
			return Double.parseDouble(getString(get(key)));
		}
		throw new NullPointerException();
	}

	/**
	 * Float 값을 반환
	 * @param key
	 * @return
	 */
	public float getFloat(Object key) {
		if (key != null) {
			return Float.parseFloat(getString(get(key)));
		}
		throw new NullPointerException();
	}
	
	/**
	 * Float 값을 반환<br>만약 값이 null이라면 0을 반환한다.
	 * @param key
	 * @return
	 */
	public float getFloatWithZero(Object key) {
		if (key != null) {
			Object val = get(key);
			if(val == null)
				return 0f;
			return Float.parseFloat(String.valueOf(val));
		}
		throw new NullPointerException();
	}
	
	/**
	 * Boolean 값을 반환
	 * @param key
	 * @return
	 */
	public boolean getBoolean(Object key){
		String boolStr = getString(key);
		return Boolean.parseBoolean(boolStr);
	}
	
	/**
	 * Boolean값을 가져와 true면 1 false면 0을 반환
	 * @param key
	 * @return
	 */
	public int getBooleanToInt(Object key){
		boolean bool = getBoolean(key);
		if(bool)
			return 1;
		else
			return 0;
	}
}
