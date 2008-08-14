package cz.vutbr.web.css;

/**
 * Holds value of numeric type. This could be integer or float
 * according to <T>.
 * @author kapy
 *
 * @param <T> Type of value stored in term
 */
public interface TermNumeric<T> extends Term<T> {
	
	/**
	 * These are available units in CSS
	 * @author kapy
	 *
	 */
	public enum Unit {
    	px("px"),
    	em("em"),
    	ex("ex"),
    	cm("cm"),
    	mm("mm"),
    	pt("pt"),
    	pc("pc"),
    	deg("deg"),
    	rad("rad"),
    	grad("grad"),
    	ms("ms"),
    	s("s"),
    	hz("hz"),
    	khz("khz");
    
    	private String value;
    	
    	private Unit(String value) { 
    		this.value = value;
    	}
    	public String value() { return value; }
    }
	
	/**
	 * Returns unit of type or <code>null</code> if not defined
	 * for numeric types that does not allow units
	 * @return Unit or <code>null</code>
	 */
	public Unit getUnit();
	
	/**
	 * Sets unit
	 * @param unit Unit value
	 * @return Modified object to allow chaining
	 */
    public TermNumeric<T> setUnit(Unit unit);

}
