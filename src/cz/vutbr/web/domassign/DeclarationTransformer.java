package cz.vutbr.web.domassign;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.Term;
import cz.vutbr.web.css.TermColor;
import cz.vutbr.web.css.TermFunction;
import cz.vutbr.web.css.TermIdent;
import cz.vutbr.web.css.TermInteger;
import cz.vutbr.web.css.TermLength;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.css.TermNumber;
import cz.vutbr.web.css.TermPair;
import cz.vutbr.web.css.TermPercent;
import cz.vutbr.web.css.TermString;
import cz.vutbr.web.css.TermURI;
import cz.vutbr.web.css.NodeData.BackgroundAttachment;
import cz.vutbr.web.css.NodeData.BackgroundColor;
import cz.vutbr.web.css.NodeData.BackgroundImage;
import cz.vutbr.web.css.NodeData.BackgroundRepeat;
import cz.vutbr.web.css.NodeData.BorderCollapse;
import cz.vutbr.web.css.NodeData.BorderColor;
import cz.vutbr.web.css.NodeData.BorderSpacing;
import cz.vutbr.web.css.NodeData.BorderStyle;
import cz.vutbr.web.css.NodeData.BorderWidth;
import cz.vutbr.web.css.NodeData.Bottom;
import cz.vutbr.web.css.NodeData.CSSProperty;
import cz.vutbr.web.css.NodeData.CaptionSide;
import cz.vutbr.web.css.NodeData.Clear;
import cz.vutbr.web.css.NodeData.Clip;
import cz.vutbr.web.css.NodeData.Color;
import cz.vutbr.web.css.NodeData.Content;
import cz.vutbr.web.css.NodeData.CounterIncrement;
import cz.vutbr.web.css.NodeData.CounterReset;
import cz.vutbr.web.css.NodeData.Cursor;
import cz.vutbr.web.css.NodeData.Direction;
import cz.vutbr.web.css.NodeData.Display;
import cz.vutbr.web.css.NodeData.EmptyCells;
import cz.vutbr.web.css.NodeData.FontFamily;
import cz.vutbr.web.css.NodeData.FontSize;
import cz.vutbr.web.css.NodeData.FontStyle;
import cz.vutbr.web.css.NodeData.FontVariant;
import cz.vutbr.web.css.NodeData.FontWeight;
import cz.vutbr.web.css.NodeData.Height;
import cz.vutbr.web.css.NodeData.Left;
import cz.vutbr.web.css.NodeData.LineHeight;
import cz.vutbr.web.css.NodeData.ListStyleImage;
import cz.vutbr.web.css.NodeData.ListStylePosition;
import cz.vutbr.web.css.NodeData.ListStyleType;
import cz.vutbr.web.css.NodeData.Margin;
import cz.vutbr.web.css.NodeData.MaxHeight;
import cz.vutbr.web.css.NodeData.MaxWidth;
import cz.vutbr.web.css.NodeData.MinHeight;
import cz.vutbr.web.css.NodeData.MinWidth;
import cz.vutbr.web.css.NodeData.Orphans;
import cz.vutbr.web.css.NodeData.OutlineColor;
import cz.vutbr.web.css.NodeData.OutlineStyle;
import cz.vutbr.web.css.NodeData.OutlineWidth;
import cz.vutbr.web.css.NodeData.Overflow;
import cz.vutbr.web.css.NodeData.Padding;
import cz.vutbr.web.css.NodeData.PageBreak;
import cz.vutbr.web.css.NodeData.PageBreakInside;
import cz.vutbr.web.css.NodeData.Position;
import cz.vutbr.web.css.NodeData.Quotes;
import cz.vutbr.web.css.NodeData.Right;
import cz.vutbr.web.css.NodeData.TableLayout;
import cz.vutbr.web.css.NodeData.TextAlign;
import cz.vutbr.web.css.NodeData.TextDecoration;
import cz.vutbr.web.css.NodeData.TextIndent;
import cz.vutbr.web.css.NodeData.TextTransform;
import cz.vutbr.web.css.NodeData.Top;
import cz.vutbr.web.css.NodeData.UnicodeBidi;
import cz.vutbr.web.css.NodeData.VerticalAlign;
import cz.vutbr.web.css.NodeData.Visibility;
import cz.vutbr.web.css.NodeData.WhiteSpace;
import cz.vutbr.web.css.NodeData.Widows;
import cz.vutbr.web.css.NodeData.Width;
import cz.vutbr.web.css.NodeData.WordSpacing;
import cz.vutbr.web.css.NodeData.ZIndex;
import cz.vutbr.web.csskit.TermImpl;
import cz.vutbr.web.csskit.TermListImpl;
import cz.vutbr.web.csskit.TermPairImpl;

/**
 * Contains methods to transform declaration into values applicable to
 * NodeDataImpl. Contains map of CSS properties as supported in CSS 2.1 and
 * their default values. Implements singleton pattern.
 * 
 * @author kapy
 * 
 */
public class DeclarationTransformer {

	private static Logger log = Logger.getLogger(DeclarationTransformer.class);

	/**
	 * Cache of parsing methods
	 */
	private Map<String, Method> methods;

	/**
	 * Singleton instance
	 */
	private static final DeclarationTransformer instance;

	static {
		instance = new DeclarationTransformer();
	}

	/**
	 * Returns instance
	 * 
	 * @return Singleton instance
	 */
	public static final DeclarationTransformer getInstance() {
		return instance;
	}

	/**
	 * Converts string divided by dash ('-') characters into camelCase such as
	 * convenient for Java method names
	 * 
	 * @param string
	 *            String to convert
	 * @return CamelCase version of string
	 */
	public static final String camelCase(String string) {

		StringBuilder sb = new StringBuilder();

		boolean upperFlag = false;

		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			if (ch == '-')
				upperFlag = true;
			else if (upperFlag && Character.isLetter(ch)) {
				sb.append(Character.toUpperCase(ch));
				upperFlag = false;
			} else if (!upperFlag && Character.isLetter(ch))
				sb.append(ch);
			else if (ch == '_') // vendor extension
				sb.append(ch);
		}
		return sb.toString();
	}

	/**
	 * Core function. Parses CSS declaration into structure applicable to
	 * DataNodeImpl
	 * 
	 * @param d
	 *            Declaration
	 * @param properties
	 *            Holder of parsed declaration's properties
	 * @param values
	 *            Holder of parsed declaration's value
	 * @return <code>true</code> in case of success, <code>false</code>
	 *         otherwise
	 */
	public boolean parseDeclaration(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		String propertyName = d.getProperty().toLowerCase();

		CSSProperty defaultValue = SupportedCSS.getInstance().getDefaultValue(
				propertyName);
		// no such declaration is supported
		if (defaultValue == null)
			return false;

		try {
			Method m = methods.get(propertyName);
			if (m != null)
				return (Boolean) m.invoke(this, d, properties, values);
		} catch (IllegalArgumentException e) {
			log.warn("Illegal argument: " + e);
		} catch (IllegalAccessException e) {
			log.warn("Illegal access: " + e);
		} catch (InvocationTargetException e) {
			log.warn("Invocation target: " + e + e.getCause());
		}

		return false;
	}

	/**
	 * Sole constructor
	 */
	private DeclarationTransformer() {
		this.methods = parsingMethods();
	}

	private Map<String, Method> parsingMethods() {

		Map<String, Method> map = new HashMap<String, Method>(SupportedCSS
				.getInstance().totalProperties(), 1.0f);

		for (String key : SupportedCSS.getInstance().propertyNames()) {
			try {
				Method m = DeclarationTransformer.class.getDeclaredMethod(
						DeclarationTransformer.camelCase("process-" + key),
						Declaration.class, Map.class, Map.class);
				map.put(key, m);
			} catch (Exception e) {
				log.warn("Unable to find method for property: " + key);
			}
		}
		if (log.isInfoEnabled()) {
			log.info("Total methods found: " + map.size());
		}
		return map;
	}

	// generic methods

	/**
	 * Converts term into property if it is contained in intersection set and is
	 * valid property for given enum class
	 */
	private <T extends Enum<T> & CSSProperty> T genericPropertyRaw(
			Class<T> enumType, Set<T> intersection, TermIdent term) {

		try {
			String name = term.getValue().replace("-", "_").toUpperCase();
			T property = Enum.valueOf(enumType, name);
			if (intersection.contains(property))
				return property;

			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Converts TermIdent into value of enum of given class and stores it into
	 * properties map under key property
	 * 
	 * @param <T>
	 *            Enum & CSSProperty limitation
	 * @param enumType
	 *            Type of enum which instance is retrieved
	 * @param term
	 *            Term with value to be converted
	 * @param properties
	 *            Properties map where to store value
	 * @param propertyName
	 *            Name under which property is stored in map
	 * @return <code>true</code> in case of success, <code>false</code>
	 *         otherwise
	 */
	private <T extends Enum<T> & CSSProperty> boolean genericProperty(
			Class<T> enumType, TermIdent term,
			Map<String, CSSProperty> properties, String propertyName) {

		// try to find enum with given value and if so
		// insert it inside
		try {
			String name = term.getValue().replace("-", "_").toUpperCase();
			properties.put(propertyName, Enum.valueOf(enumType, name));
			return true;
		} catch (IllegalArgumentException e) {
			// no such enum value
		} catch (NullPointerException e) {
			log.warn("TermIdent contained empty value!");
		}

		return false;

	}

	/**
	 * Converts TermIdent into value of enum for given class
	 * 
	 * @param <T>
	 * @param enumType
	 * @param d
	 * @param properties
	 * @return
	 */
	private <T extends Enum<T> & CSSProperty> boolean genericTermIdent(
			Class<T> enumType, Term<?> term, String propertyName,
			Map<String, CSSProperty> properties) {

		if (term instanceof TermIdent) {
			return genericProperty(enumType, (TermIdent) term, properties,
					propertyName);
		}
		return false;

	}

	private <T extends Enum<T> & CSSProperty> boolean genericTermColor(
			Term<?> term, String propertyName, T colorIdentification,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		if (term instanceof TermColor) {
			properties.put(propertyName, colorIdentification);
			values.put(propertyName, term);
			return true;
		}

		return false;

	}

	/**
	 * Check whether given declaration contains one term of given type. It is
	 * able to check even whether is above zero for numeric values
	 * 
	 * @param <T>
	 *            Class of enum to be used for result
	 * @param termType
	 *            Type of term
	 * @param d
	 *            Declaration
	 * @param typeIdentification
	 *            How this type of term is described in enum T
	 * @param sanify
	 *            Check if value is positive
	 * @param properties
	 *            Where to store property type
	 * @param values
	 *            Where to store property value
	 * @return <code>true</code> if succeeded in recognition, <code>false</code>
	 *         otherwise
	 */
	private <T extends Enum<T> & CSSProperty> boolean genericTerm(
			Class<? extends Term<?>> termType, Term<?> term,
			String propertyName, T typeIdentification, boolean sanify,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		// check type
		if (termType.isInstance(term)) {
			// sanity check
			if (sanify) {
				// check for integer
				if (term.getValue() instanceof Integer) {
					final Integer zero = new Integer(0);
					if (zero.compareTo((Integer) term.getValue()) > 0) {
						// return false is also possibility
						// but we will change to zero
						((TermInteger) term).setValue(zero);
					}
				}
				// check for float
				else if (term.getValue() instanceof Float) {
					final Float zero = new Float(0.0f);
					if (zero.compareTo((Float) term.getValue()) > 0) {
						// return false is also possibility
						// but we will change to zero
						((TermNumber) term).setValue(zero);
					}
				}
			}
			// passed both type check and (optional) sanity check,
			// store
			properties.put(propertyName, typeIdentification);
			values.put(propertyName, term);
			return true;

		}
		return false;

	}

	/**
	 * Processes declaration which is supposed to contain one identification
	 * term
	 * 
	 * @param <T>
	 *            Type of enum
	 * @param enumType
	 *            Class of enum to be stored
	 * @param d
	 *            Declaration to be parsed
	 * @param properties
	 *            Properties map where to store enum
	 * @return <code>true</code> in case of success, <code>false</code>
	 *         elsewhere
	 */
	private <T extends Enum<T> & CSSProperty> boolean genericOneIdent(
			Class<T> enumType, Declaration d,
			Map<String, CSSProperty> properties) {

		if (d.getTerms().size() != 1)
			return false;

		return genericTermIdent(enumType, d.getTerms().get(0), d.getProperty(),
				properties);
	}

	/**
	 * Processes declaration which is supposed to contain one identification
	 * term or one TermColor
	 * 
	 * @param <T>
	 *            Type of enum
	 * @param enumType
	 *            Class of enum to be stored
	 * @param colorIdentification
	 *            Instance of enum stored into properties to indicate that
	 *            additional value of type TermColor is stored in values
	 * @param d
	 *            Declaration to be parsed
	 * @param properties
	 *            Properties map where to store enum
	 * @param values
	 * @return <code>true</code> in case of success, <code>false</code>
	 *         elsewhere
	 */
	private <T extends Enum<T> & CSSProperty> boolean genericOneIdentOrColor(
			Class<T> enumType, T colorIdentification, Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		if (d.getTerms().size() != 1)
			return false;

		return genericTermIdent(enumType, d.getTerms().get(0), d.getProperty(),
				properties)
				|| genericTermColor(d.getTerms().get(0), d.getProperty(),
						colorIdentification, properties, values);
	}

	private <T extends Enum<T> & CSSProperty> boolean genericOneIdentOrInteger(
			Class<T> enumType, T integerIdentification, boolean sanify,
			Declaration d, Map<String, CSSProperty> properties,
			Map<String, Term<?>> values) {

		if (d.getTerms().size() != 1)
			return false;

		return genericTermIdent(enumType, d.getTerms().get(0), d.getProperty(),
				properties)
				|| genericTerm(TermInteger.class, d.getTerms().get(0), d
						.getProperty(), integerIdentification, sanify,
						properties, values);
	}

	private <T extends Enum<T> & CSSProperty> boolean genericOneIdentOrLength(
			Class<T> enumType, T lengthIdentification, boolean sanify,
			Declaration d, Map<String, CSSProperty> properties,
			Map<String, Term<?>> values) {

		if (d.getTerms().size() != 1)
			return false;

		return genericTermIdent(enumType, d.getTerms().get(0), d.getProperty(),
				properties)
				|| genericTerm(TermLength.class, d.getTerms().get(0), d
						.getProperty(), lengthIdentification, sanify,
						properties, values);
	}

	private <T extends Enum<T> & CSSProperty> boolean genericOneIdentOrLengthOrPercent(
			Class<T> enumType, T lengthIdentification, T percentIdentification,
			boolean sanify, Declaration d, Map<String, CSSProperty> properties,
			Map<String, Term<?>> values) {

		if (d.getTerms().size() != 1)
			return false;

		return genericTermIdent(enumType, d.getTerms().get(0), d.getProperty(),
				properties)
				|| genericTerm(TermLength.class, d.getTerms().get(0), d
						.getProperty(), lengthIdentification, sanify,
						properties, values)
				|| genericTerm(TermPercent.class, d.getTerms().get(0), d
						.getProperty(), percentIdentification, sanify,
						properties, values);
	}

	// =============================================================
	// processing methods

	@SuppressWarnings("unused")
	private boolean processColor(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrColor(Color.class, Color.color, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processBackgroundAttachement(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(BackgroundAttachment.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processBackgroundColor(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrColor(BackgroundColor.class,
				BackgroundColor.color, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processBackgroundImage(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		if (d.getTerms().size() != 1)
			return false;

		return genericOneIdent(BackgroundImage.class, d, properties)
				|| genericTerm(TermURI.class, d.getTerms().get(0), d
						.getProperty(), BackgroundImage.uri, false, properties,
						values);
	}

	@SuppressWarnings("unused")
	private boolean processBackgroundRepeat(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(BackgroundRepeat.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processBorderCollapse(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(BorderCollapse.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processBorderTopColor(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator borderSide = new BorderSideVariator("top");
		return borderSide.tryVariant(BorderSideVariator.COLOR, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderRightColor(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator borderSide = new BorderSideVariator("right");
		return borderSide.tryVariant(BorderSideVariator.COLOR, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderBottomColor(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator borderSide = new BorderSideVariator("bottom");
		return borderSide.tryVariant(BorderSideVariator.COLOR, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderLeftColor(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator borderSide = new BorderSideVariator("left");
		return borderSide.tryVariant(BorderSideVariator.COLOR, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderTopStyle(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator borderSide = new BorderSideVariator("top");
		return borderSide.tryVariant(BorderSideVariator.STYLE, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderRightStyle(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator borderSide = new BorderSideVariator("right");
		return borderSide.tryVariant(BorderSideVariator.STYLE, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderBottomStyle(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator borderSide = new BorderSideVariator("bottom");
		return borderSide.tryVariant(BorderSideVariator.STYLE, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderLeftStyle(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator borderSide = new BorderSideVariator("left");
		return borderSide.tryVariant(BorderSideVariator.STYLE, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderSpacing(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		if (d.getTerms().size() == 1) {
			Term<?> term = d.getTerms().get(0);
			String propertyName = d.getProperty();
			// is it identifier or length ?
			if (genericTermIdent(BorderSpacing.class, term, propertyName,
					properties)
					|| genericTerm(TermLength.class, term, propertyName,
							BorderSpacing.hor_ver_list, true, properties,
							values)) {
				// one term with length was inserted, double it
				if (properties.get(propertyName) == BorderSpacing.hor_ver_list) {
					TermList terms = new TermListImpl(2);
					terms.add(term);
					terms.add(term);
					values.put(propertyName, terms);
				}
				return true;
			}
		}
		// two numerical values
		else if (d.getTerms().size() == 2) {
			Term<?> term1 = d.getTerms().get(0);
			Term<?> term2 = d.getTerms().get(1);
			String propertyName = d.getProperty();
			// two lengths ?
			if (genericTerm(TermLength.class, term1, propertyName,
					BorderSpacing.hor_ver_list, true, properties, values)
					&& genericTerm(TermLength.class, term2, propertyName,
							BorderSpacing.hor_ver_list, true, properties,
							values)) {
				TermList terms = new TermListImpl(2);
				terms.add(term1);
				terms.add(term2);
				values.put(propertyName, terms);
				return true;
			}
			return false;
		}
		return false;
	}

	@SuppressWarnings("unused")
	private boolean processBorderColor(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		Repeater borderColor = new BorderColorRepeater();
		return borderColor.repeatOverFourTermDeclaration(d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderStyle(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		Repeater borderStyle = new BorderStyleRepeater();
		return borderStyle.repeatOverFourTermDeclaration(d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderTopWidth(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator borderSide = new BorderSideVariator("top");
		return borderSide.tryVariant(BorderSideVariator.WIDTH, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderRightWidth(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator borderSide = new BorderSideVariator("right");
		return borderSide.tryVariant(BorderSideVariator.WIDTH, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderBottomWidth(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator borderSide = new BorderSideVariator("bottom");
		return borderSide.tryVariant(BorderSideVariator.WIDTH, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderLeftWidth(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator borderSide = new BorderSideVariator("left");
		return borderSide.tryVariant(BorderSideVariator.WIDTH, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderWidth(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		Repeater borderWidth = new BorderWidthRepeater();
		return borderWidth.repeatOverFourTermDeclaration(d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderTop(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		Variator borderSide = new BorderSideVariator("top");
		borderSide.assignTermsFromDeclaration(d);
		return borderSide.vary(properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderRight(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		Variator borderSide = new BorderSideVariator("right");
		borderSide.assignTermsFromDeclaration(d);
		return borderSide.vary(properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderBottom(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		Variator borderSide = new BorderSideVariator("bottom");
		borderSide.assignTermsFromDeclaration(d);
		return borderSide.vary(properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processBorderLeft(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		Variator borderSide = new BorderSideVariator("left");
		borderSide.assignTermsFromDeclaration(d);
		return borderSide.vary(properties, values);
	}

	/*
	 * 
	 * private Boolean processBorder(Declaration d) { NodeData trans =
	 * beginTransaction(); //Nastavení na výchozí hodnoty borderColorTopType =
	 * null; borderColorRightType = null; borderColorBottomType = null;
	 * borderColorLeftType = null; borderColorTopValue = null;
	 * borderColorRightValue = null; borderColorBottomValue = null;
	 * borderColorLeftValue = null; borderStyleTopType = null;
	 * borderStyleRightType = null; borderStyleBottomType = null;
	 * borderStyleLeftType = null; borderWidthTopType = null;
	 * borderWidthRightType = null; borderWidthBottomType = null;
	 * borderWidthLeftType = null; borderWidthTopValue = null;
	 * borderWidthRightValue = null; borderWidthBottomValue = null;
	 * borderWidthLeftValue = null;
	 * 
	 * //Každou z částí lze nastavit pouze jednou. Není přípustné aby se v jedné
	 * deklaraci //objevila například 2x barva. K určení slouží následující
	 * proměnné boolean processedColor = false; boolean processedStyle = false;
	 * boolean processedWidth = false;
	 * 
	 * for(Term t : d.getTerms()) { //Pokud je první (a jediný) identifikátor
	 * inherit, pak se nastaví všecm hodnotám inherit //Pokud by se inherit
	 * objevilo až například jako třetí term, dojde k ignorování celé deklarace
	 * if((t instanceof TermIdent) &&
	 * ((TermIdent)t).getValue().equalsIgnoreCase("inherit")) {
	 * if(d.getTerms().size() == 1) { borderColorTopType =
	 * EnumColorTransparent.inherit; borderColorRightType =
	 * EnumColorTransparent.inherit; borderColorBottomType =
	 * EnumColorTransparent.inherit; borderColorLeftType =
	 * EnumColorTransparent.inherit; borderColorTopValue = null;
	 * borderColorRightValue = null; borderColorBottomValue = null;
	 * borderColorLeftValue = null; borderStyleTopType =
	 * EnumBorderStyle.inherit; borderStyleRightType = EnumBorderStyle.inherit;
	 * borderStyleBottomType = EnumBorderStyle.inherit; borderStyleLeftType =
	 * EnumBorderStyle.inherit; borderWidthTopType = EnumBorderWidth.inherit;
	 * borderWidthRightType = EnumBorderWidth.inherit; borderWidthBottomType =
	 * EnumBorderWidth.inherit; borderWidthLeftType = EnumBorderWidth.inherit;
	 * borderWidthTopValue = null; borderWidthRightValue = null;
	 * borderWidthBottomValue = null; borderWidthLeftValue = null; return true;
	 * } else { rollbackTransaction(trans); return false; } }
	 * 
	 * //Vytvořím pomocnou deklaraci, která obsahuje jeden jediný term (ten
	 * aktuální) //a v jednotlivých blocích se pokouším tuto deklaraci
	 * zpracovat. Declaration tmpDeclaration = new DataDeclaration("border");
	 * tmpDeclaration.getTerms().add(t);
	 * 
	 * //Vyzkouším, jestli se jedná o barvu
	 * tmpDeclaration.setProperty("border-color");
	 * if(processBorderColor(tmpDeclaration)) { //Jedná se o barvu. Zjistím,
	 * jestli barva už nebyla jednou zadána if(processedColor) { //Barva už byla
	 * jednou zadáno, deklarace je chybná, rollback a konec
	 * rollbackTransaction(trans); return false; } else { //Barva ještě nebyla
	 * zadána, pokračujeme dalším termem processedColor = true; continue; } }
	 * tmpDeclaration.setProperty("border-style");
	 * if(processBorderStyle(tmpDeclaration)) { if(processedStyle) {
	 * rollbackTransaction(trans); return false; } else { processedStyle = true;
	 * continue; } } tmpDeclaration.setProperty("border-width");
	 * if(processBorderWidth(tmpDeclaration)) { if(processedWidth) {
	 * rollbackTransaction(trans); return false; } else { processedWidth = true;
	 * continue; } }
	 * 
	 * //Pokud program dojde sem, znamená to že term není ani color, style nebo
	 * width - ignorace celé deklarace rollbackTransaction(trans); return false;
	 * } return true; }
	 */

	@SuppressWarnings("unused")
	private boolean processFontFamily(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator font = new FontVariator();
		return font.tryVariant(FontVariator.FONT_FAMILY, properties, values, d
				.getTerms().toArray(new Term<?>[0]));
	}

	@SuppressWarnings("unused")
	private boolean processFontSize(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(FontSize.class,
				FontSize.length, FontSize.percentage, true, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processFontStyle(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator font = new FontVariator();
		return font.tryVariant(FontVariator.FONT_STYLE, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processFontVariant(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator font = new FontVariator();
		return font.tryVariant(FontVariator.FONT_WEIGHT, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processFontWeight(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator font = new FontVariator();
		return font.tryVariant(FontVariator.FONT_WEIGHT, d, properties, values);
	}

	/*
	 * private Boolean processFont(Declaration d) { NodeData trans =
	 * beginTransaction(); //Hodnoty jsou děděné (inherited) - Nastavení na
	 * výchozí hodnoty fontStyleType = EnumFontStyle.normal; fontFamilyType =
	 * EnumFontFamily.font; fontFamilyValues.clear(); fontSizeType =
	 * EnumFontSize.medium; fontSizePercentValue = null; fontSizeNumberValue =
	 * null; fontVariantType = EnumFontVariant.normal; fontWeightType =
	 * EnumFontWeight.normal; lineHeightType = EnumLineHeight.normal;
	 * lineHeightNumberValue = null; lineHeightPercentValue = null;
	 * 
	 * //Každou z částí lze nastavit pouze jednou. Není přípustné aby se v jedné
	 * deklaraci //objevila například 2x barva. K určení slouží následující
	 * proměnné boolean processedStyle = false; boolean processedVariant =
	 * false; boolean processedWeight = false; boolean processedFontSize =
	 * false; boolean processedLineHeight = false; int count = 0;
	 * 
	 * //Sem se budou kládat všechny hodnoty na konci (pravděpodobně se bude
	 * jednat o hodnoty font-family) Declaration fontFamilyDeclaration = new
	 * DataDeclaration("font-family");
	 * 
	 * for(Term t : d.getTerms()) { //Pokud je první (a jediný) identifikátor
	 * inherit, pak se nastaví všecm hodnotám inherit //Pokud by se inherit
	 * objevilo až například jako třetí term, dojde k ignorování celé deklarace
	 * if((t instanceof TermIdent) &&
	 * ((TermIdent)t).getValue().equalsIgnoreCase("inherit")) {
	 * if(d.getTerms().size() == 1) { fontStyleType = EnumFontStyle.inherit;
	 * fontFamilyType = EnumFontFamily.inherit; fontFamilyValues.clear();
	 * fontSizeType = EnumFontSize.inherit; fontSizePercentValue = null;
	 * fontSizeNumberValue = null; fontVariantType = EnumFontVariant.inherit;
	 * fontWeightType = EnumFontWeight.inherit; lineHeightType =
	 * EnumLineHeight.inherit; lineHeightNumberValue = null;
	 * lineHeightPercentValue = null; return true; } else {
	 * rollbackTransaction(trans); return false; } } if((t instanceof TermIdent)
	 * && ((TermIdent)t).getValue().equalsIgnoreCase("caption")) {
	 * if(d.getTerms().size() == 1) { //neimplementováno return true; } else {
	 * rollbackTransaction(trans); return false; } } if((t instanceof TermIdent)
	 * && ((TermIdent)t).getValue().equalsIgnoreCase("icon")) {
	 * if(d.getTerms().size() == 1) { //neimplementováno return true; } else {
	 * rollbackTransaction(trans); return false; } } if((t instanceof TermIdent)
	 * && ((TermIdent)t).getValue().equalsIgnoreCase("menu")) {
	 * if(d.getTerms().size() == 1) { //neimplementováno return true; } else {
	 * rollbackTransaction(trans); return false; } } if((t instanceof TermIdent)
	 * && ((TermIdent)t).getValue().equalsIgnoreCase("message-box")) {
	 * if(d.getTerms().size() == 1) { //neimplementováno return true; } else {
	 * rollbackTransaction(trans); return false; } } if((t instanceof TermIdent)
	 * && ((TermIdent)t).getValue().equalsIgnoreCase("small-caption")) {
	 * if(d.getTerms().size() == 1) { //neimplementováno return true; } else {
	 * rollbackTransaction(trans); return false; } } if((t instanceof TermIdent)
	 * && ((TermIdent)t).getValue().equalsIgnoreCase("status-bar")) {
	 * if(d.getTerms().size() == 1) { //neimplementováno return true; } else {
	 * rollbackTransaction(trans); return false; } }
	 * 
	 * //Vytvořím pomocnou deklaraci, která obsahuje jeden jediný term (ten
	 * aktuální) //a v jednotlivých blocích se pokouším tuto deklaraci
	 * zpracovat. Declaration tmpDeclaration = new DataDeclaration("font");
	 * tmpDeclaration.getTerms().add(t);
	 * 
	 * if(!processedFontSize && count < 3) { if((t instanceof TermIdent) &&
	 * ((TermIdent)t).getValue().equalsIgnoreCase("normal")) { //U normal není
	 * jasné o co se jedná. Ale je to výchozí hodnota, lze jí ignorovat count++;
	 * continue; }
	 * 
	 * //Vyzkouším, jestli se jedná o barvu
	 * tmpDeclaration.setProperty("font-style");
	 * if(processFontStyle(tmpDeclaration)) { //Jedná se o barvu. Zjistím,
	 * jestli barva už nebyla jednou zadána if(processedStyle) { //Barva už byla
	 * jednou zadáno, deklarace je chybná, rollback a konec
	 * rollbackTransaction(trans); return false; } else { //Barva ještě nebyla
	 * zadána, pokračujeme dalším termem processedStyle = true; count++;
	 * continue; } } tmpDeclaration.setProperty("font-variant");
	 * if(processFontVariant(tmpDeclaration)) { if(processedVariant) {
	 * rollbackTransaction(trans); return false; } else { processedVariant =
	 * true; count++; continue; } } tmpDeclaration.setProperty("font-weight");
	 * if(processFontWeight(tmpDeclaration)) { if(processedWeight) {
	 * rollbackTransaction(trans); return false; } else { processedWeight =
	 * true; count++; continue; } } }
	 * 
	 * //V tomto místě musí být deklarováno bezpodmínečně font-size
	 * if(!processedFontSize) { tmpDeclaration.setProperty("font-size");
	 * if(processFontSize(tmpDeclaration)) { processedFontSize = true; continue;
	 * } else { rollbackTransaction(trans); return false; } }
	 * 
	 * if(!processedLineHeight && t.getOperator() == Term.Operator.SLASH) {
	 * tmpDeclaration.setProperty("line-height");
	 * if(processLineHeight(tmpDeclaration)) { processedLineHeight = true;
	 * continue; } else { rollbackTransaction(trans); return false; } }
	 * 
	 * //Vše co neprojde předchozím fontFamilyDeclaration.getTerms().add(t); }
	 * 
	 * //Font family musí být zadáno
	 * if(fontFamilyDeclaration.getTerms().isEmpty()) {
	 * rollbackTransaction(trans); return false; }
	 * 
	 * if(processFontFamily(fontFamilyDeclaration)) { return true; } else {
	 * rollbackTransaction(trans); return false; } }
	 */

	@SuppressWarnings("unused")
	private boolean processLineHeight(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		if (d.getTerms().size() != 1)
			return false;

		return genericOneIdent(LineHeight.class, d, properties)
				|| genericTerm(TermNumber.class, d.getTerms().get(0), d
						.getProperty(), LineHeight.number, true, properties,
						values)
				|| genericTerm(TermPercent.class, d.getTerms().get(0), d
						.getProperty(), LineHeight.percentage, true,
						properties, values)
				|| genericTerm(TermLength.class, d.getTerms().get(0), d
						.getProperty(), LineHeight.length, true, properties,
						values);
	}

	@SuppressWarnings("unused")
	private boolean processTop(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Top.class, Top.lenght,
				Top.percentage, false, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processRight(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Right.class, Right.lenght,
				Right.percentage, false, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processBottom(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Bottom.class, Bottom.lenght,
				Bottom.percentage, false, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processLeft(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Left.class, Left.lenght,
				Left.percentage, false, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processWidth(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Width.class, Width.lenght,
				Width.percentage, false, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processHeight(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Height.class, Height.lenght,
				Height.percentage, false, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processCaptionSide(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(CaptionSide.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processClear(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(Clear.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processClip(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		if (d.getTerms().size() != 1)
			return false;

		Term<?> term = d.getTerms().get(0);
		if (term instanceof TermIdent) {

			final Set<Clip> allowedClips = EnumSet.allOf(Clip.class);
			Clip clip = genericPropertyRaw(Clip.class, allowedClips,
					(TermIdent) term);
			if (clip != null) {
				properties.put("clip-top", clip);
				properties.put("clip-right", clip);
				properties.put("clip-bottom", clip);
				properties.put("clip-left", clip);
				return true;
			}
			return false;
		} else if (term instanceof TermFunction) {
			TermFunction termf = (TermFunction) term;
			// this is possibly valid rect() function
			if ("rect".equals(termf.getFunctionName()) && termf.size() == 4) {
				Repeater clip = new ClipRepeater();
				clip.assignTerms(termf.getValue().toArray(new Term<?>[0]));
				return clip.repeat(properties, values);
			}
		}
		return false;
	}

	@SuppressWarnings("unused")
	private boolean processCounterIncrement(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		if (d.getTerms().size() == 1
				&& genericOneIdent(CounterIncrement.class, d, properties)) {
			return true;
		}
		// counter with increments
		else {
			// counters are stored there
			Set<Term<?>> termList = new LinkedHashSet<Term<?>>();
			String counterName = null;
			for (Term<?> term : d.getTerms()) {
				// counter name
				if (term instanceof TermIdent) {
					counterName = ((TermIdent) term).getValue();
					TermPair<String, Integer> tp = new TermPairImpl<String, Integer>();
					tp.setKey(counterName);
					tp.setValue(new Integer(1));
					termList.add(tp);
				}
				// counter reset value follows counter name
				else if (term instanceof TermInteger && counterName != null) {
					TermPair<String, Integer> tp = new TermPairImpl<String, Integer>();
					tp.setKey(counterName);
					tp.setValue(((TermInteger) term).getValue());
					termList.add(tp);
					counterName = null;
				} else {
					return false;
				}
			}
			if (!termList.isEmpty()) {
				TermList list = new TermListImpl();
				list.addAll(termList);
				properties.put("counter-increment",
						CounterIncrement.list_values);
				values.put("counter-increment", list);
				return true;
			}
			return false;
		}
	}

	@SuppressWarnings("unused")
	private boolean processCounterReset(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		if (d.getTerms().size() == 1
				&& genericOneIdent(CounterReset.class, d, properties)) {
			return true;
		}
		// counter with resets
		else {
			// counters are stored there
			Set<Term<?>> termList = new LinkedHashSet<Term<?>>();
			String counterName = null;
			for (Term<?> term : d.getTerms()) {
				// counter name
				if (term instanceof TermIdent) {
					counterName = ((TermIdent) term).getValue();
					TermPair<String, Integer> tp = new TermPairImpl<String, Integer>();
					tp.setKey(counterName);
					tp.setValue(new Integer(1));
					termList.add(tp);
				}
				// counter reset value follows counter name
				else if (term instanceof TermInteger && counterName != null) {
					TermPair<String, Integer> tp = new TermPairImpl<String, Integer>();
					tp.setKey(counterName);
					tp.setValue(((TermInteger) term).getValue());
					termList.add(tp);
					counterName = null;
				} else {
					return false;
				}
			}
			if (!termList.isEmpty()) {
				TermList list = new TermListImpl();
				list.addAll(termList);
				properties.put("counter-reset", CounterReset.list_values);
				values.put("counter-reset", list);
				return true;
			}
			return false;
		}

	}

	@SuppressWarnings("unused")
	private boolean processCursor(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		if (d.getTerms().size() == 1
				&& genericOneIdent(Cursor.class, d, properties)) {
			return true;
		} else {

			final Set<Cursor> allowedCursors = EnumSet.complementOf(EnumSet
					.of(Cursor.INHERIT));

			TermList list = new TermListImpl();
			Cursor cur = null;
			for (Term<?> term : d.getTerms()) {
				if (term instanceof TermURI) {
					list.add(term);
				} else if (term instanceof TermIdent
						&& (cur = genericPropertyRaw(Cursor.class,
								allowedCursors, (TermIdent) term)) != null) {
					// this have to be the last cursor in sequence
					// and only one generic cursor is allowed
					if (d.getTerms().indexOf(term) != d.getTerms().size() - 1)
						return false;

					// passed as last cursor, insert into properties and values
					properties.put("cursor", cur);
					if (!list.isEmpty())
						values.put("cursor", list);
					return true;
				} else
					return false;
			}
			return false;
		}
	}

	@SuppressWarnings("unused")
	private boolean processDirection(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(Direction.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processDisplay(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(Display.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processEmptyCells(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(EmptyCells.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processFloat(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(EmptyCells.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processListStyleImage(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator listStyle = new ListStyleVariator();
		return listStyle.tryVariant(ListStyleVariator.IMAGE, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processListStylePosition(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator listStyle = new ListStyleVariator();
		return listStyle.tryVariant(ListStyleVariator.POSITION, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processListStyleType(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator listStyle = new ListStyleVariator();
		return listStyle.tryVariant(ListStyleVariator.TYPE, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processListStyle(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		Variator listStyle = new ListStyleVariator();
		listStyle.assignTermsFromDeclaration(d);
		return listStyle.vary(properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processMarginTop(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Margin.class, Margin.lenght,
				Margin.percentage, false, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processMarginRight(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Margin.class, Margin.lenght,
				Margin.percentage, false, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processMarginBottom(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Margin.class, Margin.lenght,
				Margin.percentage, false, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processMarginLeft(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Margin.class, Margin.lenght,
				Margin.percentage, false, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processMargin(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		Repeater margin = new MarginRepeater();
		return margin.repeatOverFourTermDeclaration(d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processMaxHeight(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(MaxHeight.class,
				MaxHeight.lenght, MaxHeight.percentage, true, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processMaxWidth(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(MaxWidth.class,
				MaxWidth.lenght, MaxWidth.percentage, true, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processMinHeight(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(MinHeight.class,
				MinHeight.lenght, MinHeight.percentage, true, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processMinWidth(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(MinWidth.class,
				MinWidth.lenght, MinWidth.percentage, true, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processOrphans(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrInteger(Orphans.class, Orphans.integer, true,
				d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processOutlineColor(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator outline = new OutlineVariator();
		return outline.tryVariant(OutlineVariator.COLOR, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processOutlineStyle(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator outline = new OutlineVariator();
		return outline.tryVariant(OutlineVariator.STYLE, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processOutlineWidth(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		final Variator outline = new OutlineVariator();
		return outline.tryVariant(OutlineVariator.WIDTH, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processOutline(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		Variator outline = new OutlineVariator();
		outline.assignTermsFromDeclaration(d);
		return outline.vary(properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processOverflow(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(Overflow.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processPaddingTop(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Padding.class, Padding.length,
				Padding.percentage, true, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processPaddingRight(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Padding.class, Padding.length,
				Padding.percentage, true, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processPaddingBottom(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Padding.class, Padding.length,
				Padding.percentage, true, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processPaddingLeft(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(Padding.class, Padding.length,
				Padding.percentage, true, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processPadding(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		Repeater padding = new PaddingRepeater();
		return padding.repeatOverFourTermDeclaration(d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processPageBreakAfter(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(PageBreak.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processPageBreakBefore(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(PageBreak.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processPageBreakInside(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(PageBreakInside.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processPosition(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(Position.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processQuotes(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		if (d.getTerms().size() == 1
				&& genericTermIdent(Quotes.class, d.getTerms().get(0),
						"quotes", properties)) {
			return true;
		} else {
			TermList list = new TermListImpl();
			for (Term<?> term : d.getTerms()) {
				if (term instanceof TermString)
					list.add(term);
				else
					return false;
			}

			// there are pairs of quotes
			if (!list.isEmpty() && list.size() % 2 == 0) {
				properties.put("quotes", Quotes.list_values);
				values.put("quotes", list);
				return true;
			}
			return false;
		}
	}

	@SuppressWarnings("unused")
	private boolean processTableLayout(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(TableLayout.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processTextAlign(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(TextAlign.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processTextDecoration(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		final Set<TextDecoration> availableDecorations = EnumSet.of(
				TextDecoration.BLINK, TextDecoration.LINE_THROUGH,
				TextDecoration.OVERLINE, TextDecoration.UNDERLINE);

		// it one term
		if (d.getTerms().size() == 1) {
			return genericOneIdent(TextDecoration.class, d, properties);
		}
		// there are more terms, we have to construct list
		else {
			TermList list = new TermListImpl();
			TextDecoration dec = null;
			for (Term<?> term : d.getTerms()) {
				if (term instanceof TermIdent
						&& (dec = genericPropertyRaw(TextDecoration.class,
								availableDecorations, (TermIdent) term)) != null) {
					// construct term with value of parsed decoration
					Term<TextDecoration> decTerm = new TermImpl<TextDecoration>();
					decTerm.setValue(dec);
					list.add(decTerm);
				} else
					return false;
			}
			if (!list.isEmpty()) {
				properties.put("text-decoration", TextDecoration.list_values);
				values.put("text-decoration", list);
				return true;
			}
			return false;
		}
	}

	@SuppressWarnings("unused")
	private boolean processTextIdent(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(TextIndent.class,
				TextIndent.length, TextIndent.percentage, false, d, properties,
				values);
	}

	@SuppressWarnings("unused")
	private boolean processTextTransform(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(TextTransform.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processUnicodeBidi(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(UnicodeBidi.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processVerticalAlign(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLengthOrPercent(VerticalAlign.class,
				VerticalAlign.length, VerticalAlign.percentage, false, d,
				properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processVisibility(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(Visibility.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processWhiteSpace(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdent(WhiteSpace.class, d, properties);
	}

	@SuppressWarnings("unused")
	private boolean processWidows(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrInteger(Widows.class, Widows.integer, true, d,
				properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processWordSpacing(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrLength(WordSpacing.class, WordSpacing.length,
				false, d, properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processZIndex(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {
		return genericOneIdentOrInteger(ZIndex.class, ZIndex.integer, false, d,
				properties, values);
	}

	@SuppressWarnings("unused")
	private boolean processContent(Declaration d,
			Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

		// content contains no explicit values
		if (d.getTerms().size() == 1
				&& genericOneIdent(Content.class, d, properties)) {
			return true;
		} else {

			// valid term idents
			final Set<String> validTermIdents = new HashSet<String>(Arrays
					.asList("open-quote", "close-quote", "no-open-quote",
							"no-close-quote"));

			// valid term function names
			final Set<String> validFuncNames = new HashSet<String>(Arrays
					.asList("counter", "counters", "attr"));

			TermList list = new TermListImpl();

			for (Term<?> t : d.getTerms()) {
				// one of valid terms
				if (t instanceof TermIdent
						&& validTermIdents.contains(((TermIdent) t).getValue()
								.toLowerCase()))
					list.add(t);
				else if (t instanceof TermString)
					list.add(t);
				else if (t instanceof TermURI)
					list.add(t);
				else if (t instanceof TermFunction
						&& validFuncNames.contains(((TermFunction) t)
								.getFunctionName().toLowerCase()))
					list.add(t);
				else
					return false;
			}
			// there is nothing in list after parsing
			if (list.isEmpty())
				return false;

			properties.put("content", Content.list_values);
			values.put("content", list);
			return true;
		}
	}

	/**
	 * Variator for list style
	 * 
	 * @author kapy
	 */
	private final class ListStyleVariator extends Variator {

		public static final int IMAGE = 0;
		public static final int TYPE = 1;
		public static final int POSITION = 2;

		protected String[] variantPropertyNames = { "list-style-image",
				"list-style-type", "list-style-position" };

		public ListStyleVariator() {
			super(3);
		}

		@Override
		protected boolean variant(int v, int i,
				Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

			switch (v) {
			case IMAGE:
				// list style image
				return genericTermIdent(ListStyleImage.class, terms.get(i),
						variantPropertyNames[v], properties)
						|| genericTerm(TermURI.class, terms.get(i),
								variantPropertyNames[v], ListStyleImage.uri,
								false, properties, values);
			case TYPE:
				// list style type
				return genericTermIdent(ListStyleType.class, terms.get(i),
						variantPropertyNames[v], properties);
			case POSITION:
				// list style position
				return genericTermIdent(ListStylePosition.class, terms.get(i),
						variantPropertyNames[v], properties);
			default:
				return false;
			}
		}

		@Override
		protected boolean inheritance(Map<String, CSSProperty> properties,
				Map<String, Term<?>> values) {
			properties.put(variantPropertyNames[0], ListStyleImage.INHERIT);
			properties.put(variantPropertyNames[1], ListStyleType.INHERIT);
			properties.put(variantPropertyNames[2], ListStylePosition.INHERIT);
			return true;
		}

	}

	/**
	 * Variator for border side
	 * 
	 * @author kapy
	 * 
	 */
	private final class BorderSideVariator extends Variator {

		public static final int COLOR = 0;
		public static final int STYLE = 1;
		public static final int WIDTH = 2;

		public BorderSideVariator() {
			super(3);
		}

		public BorderSideVariator(String side) {
			super(3);
			this.variantPropertyNames = new String[] {
					"border-" + side + "-color", "border-" + side + "-style",
					"border-" + side + "-width" };
		}

		@Override
		protected boolean variant(int v, int i,
				Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

			switch (v) {
			case 0:
				// process color
				return genericTermIdent(BorderColor.class, terms.get(i),
						variantPropertyNames[v], properties)
						|| genericTermColor(terms.get(i),
								variantPropertyNames[v], BorderColor.color,
								properties, values);
			case 1:
				// process style
				return genericTermIdent(BorderStyle.class, terms.get(i),
						variantPropertyNames[v], properties);
			case 2:
				// process width
				return genericTermIdent(BorderWidth.class, terms.get(i),
						variantPropertyNames[v], properties)
						|| genericTerm(TermLength.class, terms.get(i),
								variantPropertyNames[v], BorderWidth.length,
								true, properties, values);
			default:
				return false;
			}
		}

		@Override
		protected boolean inheritance(Map<String, CSSProperty> properties,
				Map<String, Term<?>> values) {
			properties.put(variantPropertyNames[0], BorderColor.INHERIT);
			properties.put(variantPropertyNames[1], BorderStyle.INHERIT);
			properties.put(variantPropertyNames[2], BorderWidth.INHERIT);
			return true;
		}
	}

	/**
	 * Outline variator
	 * 
	 * @author kapy
	 * 
	 */
	private final class OutlineVariator extends Variator {

		public static final int COLOR = 0;
		public static final int STYLE = 1;
		public static final int WIDTH = 2;

		protected String[] variantPropertyNames = { "outline-color",
				"outline-style", "outline-width" };

		public OutlineVariator() {
			super(3);
		}

		@Override
		protected boolean variant(int v, int i,
				Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

			switch (v) {
			case COLOR:
				// process color
				return genericTermIdent(OutlineColor.class, terms.get(i),
						variantPropertyNames[v], properties)
						|| genericTermColor(terms.get(i),
								variantPropertyNames[v], OutlineColor.color,
								properties, values);
			case STYLE:
				// process style
				return genericTermIdent(OutlineStyle.class, terms.get(i),
						variantPropertyNames[v], properties);
			case WIDTH:
				// process width
				return genericTermIdent(OutlineWidth.class, terms.get(i),
						variantPropertyNames[v], properties)
						|| genericTerm(TermLength.class, terms.get(i),
								variantPropertyNames[v], OutlineWidth.length,
								true, properties, values);
			default:
				return false;
			}
		}

		@Override
		protected boolean inheritance(Map<String, CSSProperty> properties,
				Map<String, Term<?>> values) {
			properties.put(variantPropertyNames[0], OutlineColor.INHERIT);
			properties.put(variantPropertyNames[1], OutlineStyle.INHERIT);
			properties.put(variantPropertyNames[2], OutlineWidth.INHERIT);
			return true;
		}
	}

	private final class FontVariator extends Variator {

		public static final int FONT_STYLE = 0;
		public static final int FONT_VARIANT = 1;
		public static final int FONT_WEIGHT = 2;
		public static final int FONT_SIZE = 3;
		public static final int LINE_HEIGHT = 4;
		public static final int FONT_FAMILY = 5;

		protected String[] variantPropertyNames = { "font-style",
				"font-variant", "font-weight", "font-size", "line-height",
				"font_family" };

		public FontVariator() {
			super(6);
		}

		@Override
		protected boolean inheritance(Map<String, CSSProperty> properties,
				Map<String, Term<?>> values) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		protected boolean variant(int v, int i,
				Map<String, CSSProperty> properties, Map<String, Term<?>> values) {

			switch (v) {
			case FONT_STYLE:
				// process font style
				return genericTermIdent(FontStyle.class, terms.get(i),
						variantPropertyNames[v], properties);
			case FONT_VARIANT:
				// process font variant
				return genericTermIdent(FontVariant.class, terms.get(i),
						variantPropertyNames[v], properties);
			case FONT_WEIGHT:
				// process font weight
				// test against numeric values
				final Integer[] fontWeight = new Integer[] { 100, 200, 300,
						400, 500, 600, 700, 800, 900 };

				Term<?> term = terms.get(i);

				if (term instanceof TermIdent) {
					return genericProperty(FontWeight.class, (TermIdent) term,
							properties, variantPropertyNames[v]);
				} else if (term instanceof TermInteger) {

					Integer value = ((TermInteger) term).getValue();
					for (Integer test : fontWeight) {
						int result = value.compareTo(test);
						// not found if value is smaller than currently compared
						if (result < 0)
							break;

						// match
						// construct according enum name
						if (result == 0) {
							try {
								properties.put(variantPropertyNames[v],
										FontWeight.valueOf("numeric_"
												+ value.intValue()));
								return true;
							} catch (IllegalArgumentException e) {
								log
										.warn("Not found numeric values for FontWeight: "
												+ "numeric_ "
												+ value.intValue());
								return false;
							}
						}
					}
				}
				return false;

			case FONT_FAMILY:
				/*
				 * for (Term t : d.getTerms()) { if (t instanceof TermIdent) {
				 * String ident = ((TermIdent) t).getValue(); fontFamilyType =
				 * EnumFontFamily.font; if (t.getOperator() ==
				 * Term.Operator.SPACE && !input.isEmpty()) { String tmp =
				 * input.get(input.size() - 1); tmp = tmp + " " + ident;
				 * input.remove(input.size() - 1); input.add(tmp); } else {
				 * input.add(ident); } } else if (t instanceof TermString) {
				 * String ident = ((TermString) t).getValue(); fontFamilyType =
				 * EnumFontFamily.font; input.add(ident); } else {
				 * rollbackTransaction(trans); return false; } }
				 * fontFamilyValues.clear(); fontFamilyValues.addAll(input);
				 * 
				 * return true; }
				 */

				return true;
			default:
				return false;
			}
		}

	}

	/**
	 * Border style repeater
	 * 
	 * @author kapy
	 * 
	 */
	private final class BorderStyleRepeater extends Repeater {

		protected String[] propertyNames = new String[] { "border-top-style",
				"border-right-style", "border-bottom-style",
				"border-left-style" };

		public BorderStyleRepeater() {
			super(4);
		}

		@Override
		protected boolean operation(int i, Map<String, CSSProperty> properties,
				Map<String, Term<?>> values) {

			return genericTermIdent(BorderStyle.class, terms[i],
					propertyNames[i], properties);
		}
	}

	/**
	 * Border color repeater
	 * 
	 * @author kapy
	 * 
	 */
	private final class BorderColorRepeater extends Repeater {

		protected String[] propertyNames = new String[] { "border-top-color",
				"border-right-color", "border-bottom-color",
				"border-left-color" };

		public BorderColorRepeater() {
			super(4);
		}

		@Override
		protected boolean operation(int i, Map<String, CSSProperty> properties,
				Map<String, Term<?>> values) {

			return genericTermIdent(BorderColor.class, terms[i],
					propertyNames[i], properties)
					|| genericTerm(TermColor.class, terms[i], propertyNames[i],
							BorderColor.color, false, properties, values);
		}
	}

	/**
	 * Border width repeater
	 * 
	 * @author kapy
	 * 
	 */
	private final class BorderWidthRepeater extends Repeater {

		protected String[] propertyNames = new String[] { "border-top-width",
				"border-right-width", "border-bottom-width",
				"border-left-width" };

		public BorderWidthRepeater() {
			super(4);
		}

		@Override
		protected boolean operation(int i, Map<String, CSSProperty> properties,
				Map<String, Term<?>> values) {

			return genericTermIdent(BorderWidth.class, terms[i],
					propertyNames[i], properties)
					|| genericTerm(TermLength.class, terms[i],
							propertyNames[i], BorderWidth.length, true,
							properties, values);
		}
	}

	/**
	 * Margin repeater
	 * 
	 * @author kapy
	 * 
	 */
	private final class MarginRepeater extends Repeater {

		protected String[] propertyNames = new String[] { "margin-top",
				"margin-right", "margin-bottom", "margin-left" };

		public MarginRepeater() {
			super(4);
		}

		@Override
		protected boolean operation(int i, Map<String, CSSProperty> properties,
				Map<String, Term<?>> values) {

			return genericTermIdent(Margin.class, terms[i], propertyNames[i],
					properties)
					|| genericTerm(TermLength.class, terms[i],
							propertyNames[i], Margin.lenght, false, properties,
							values)
					|| genericTerm(TermPercent.class, terms[i],
							propertyNames[i], Margin.percentage, false,
							properties, values);
		}
	}

	/**
	 * Padding repeater
	 * 
	 * @author kapy
	 * 
	 */
	private final class PaddingRepeater extends Repeater {

		protected String[] propertyNames = new String[] { "padding-top",
				"padding-right", "padding-bottom", "padding-left" };

		public PaddingRepeater() {
			super(4);
		}

		@Override
		protected boolean operation(int i, Map<String, CSSProperty> properties,
				Map<String, Term<?>> values) {

			return genericTermIdent(Margin.class, terms[i], propertyNames[i],
					properties)
					|| genericTerm(TermLength.class, terms[i],
							propertyNames[i], Margin.lenght, false, properties,
							values)
					|| genericTerm(TermPercent.class, terms[i],
							propertyNames[i], Margin.percentage, false,
							properties, values);
		}
	}

	/**
	 * Clip repeater
	 * 
	 * @author kapy
	 * 
	 */
	private final class ClipRepeater extends Repeater {
		protected String[] propertyNames = new String[] { "clip-top",
				"clip-right", "clip-bottom", "clip-left" };

		public ClipRepeater() {
			super(4);
		}

		@Override
		protected boolean operation(int i, Map<String, CSSProperty> properties,
				Map<String, Term<?>> values) {

			final Set<Clip> allowedClips = EnumSet.of(Clip.AUTO);

			Clip clip = null;

			if (terms[i] instanceof TermIdent
					&& (clip = genericPropertyRaw(Clip.class, allowedClips,
							(TermIdent) terms[i])) != null) {
				properties.put(propertyNames[i], clip);
				return true;
			} else
				return genericTerm(TermLength.class, terms[i],
						propertyNames[i], Clip.rect, false, properties, values);
		}
	}

}
