package cz.vutbr.web.css;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeFilter;

import cz.vutbr.web.csskit.antlr.CSSParserFactory;
import cz.vutbr.web.csskit.antlr.CSSParserFactory.SourceType;
import cz.vutbr.web.domassign.Analyzer;
import cz.vutbr.web.domassign.TidyTreeWalker.Traversal;

/**
 * This class is abstract factory for other factories used during CSS parsing.
 * Use it, for example, to retrieve current(default) TermFactory,
 * current(default) SupportedCSS implementation and so on.
 * 
 * Factories need to be registered first. This can be done using Java static
 * block initialization together with Java classloader.
 * 
 * By default, factory searches automatically for implementations:
 * <code>cz.vutbr.web.csskit.TermFactoryImpl</code>
 * <code>cz.vutbr.web.domassign.SupportedCSS21</code>
 * <code>cz.vutbr.web.csskit.RuleFactoryImpl</code>
 * <code>cz.vutbr.web.domassign.SingleMapNodeData</code>
 * 
 * Example:
 * 
 * <pre>
 * public class TermFactoryImpl implemenent TermFactory {
 * 		static {
 * 			CSSFactory.registerTermFactory(new TermFactoryImpl());
 * 		}
 * 		...
 * }
 * 
 * That, default factory is set when this class is loaded by class loader.
 * 
 * <pre>
 * Class.forName(&quot;xx.package.TermFactoryImpl&quot;)
 * </pre>
 * 
 * </pre>
 * 
 * @author kapy
 * 
 */
public final class CSSFactory {
	private static Logger log = LoggerFactory.getLogger(CSSFactory.class);

	private static final String DEFAULT_TERM_FACTORY = "cz.vutbr.web.csskit.TermFactoryImpl";
	private static final String DEFAULT_SUPPORTED_CSS = "cz.vutbr.web.domassign.SupportedCSS21";
	private static final String DEFAULT_RULE_FACTORY = "cz.vutbr.web.csskit.RuleFactoryImpl";
	private static final String DEFAULT_NODE_DATA_IMPL = "cz.vutbr.web.domassign.SingleMapNodeData";

	/**
	 * Default instance of TermFactory implementation
	 */
	private static TermFactory tf;

	/**
	 * Default instance of SupportedCSS implementation
	 */
	private static SupportedCSS css;

	/**
	 * Default instance of RuleFactory implementation
	 */
	private static RuleFactory rf;

	private static Class<? extends NodeData> ndImpl;

	/**
	 * Registers new TermFactory instance
	 * 
	 * @param newFactory
	 *            New TermFactory
	 */
	public static final void registerTermFactory(TermFactory newFactory) {
		tf = newFactory;
	}

	/**
	 * Returns TermFactory registered in step above
	 * 
	 * @return TermFactory registered
	 */
	@SuppressWarnings("unchecked")
	public static final TermFactory getTermFactory() {
		if (tf == null) {
			try {
				Class<? extends TermFactory> clazz = (Class<? extends TermFactory>) Class
						.forName(DEFAULT_TERM_FACTORY);
				Method m = clazz.getMethod("getInstance");
				registerTermFactory((TermFactory) m.invoke(null));
				log.debug("Retrived {} as default TermFactory implementation.",
						DEFAULT_TERM_FACTORY);
			} catch (Exception e) {
				log.error("Unable to get TermFactory from default", e);
				throw new RuntimeException(
						"No TermFactory implementation registered!");
			}
		}
		return tf;
	}

	public static final void registerSupportedCSS(SupportedCSS newCSS) {
		css = newCSS;
	}

	@SuppressWarnings("unchecked")
	public static final SupportedCSS getSupportedCSS() {
		if (css == null) {
			try {
				Class<? extends SupportedCSS> clazz = (Class<? extends SupportedCSS>) Class
						.forName(DEFAULT_SUPPORTED_CSS);
				Method m = clazz.getMethod("getInstance");
				registerSupportedCSS((SupportedCSS) m.invoke(null));
				log.debug(
						"Retrived {} as default SupportedCSS implementation.",
						DEFAULT_SUPPORTED_CSS);
			} catch (Exception e) {
				log.error("Unable to get SupportedCSS from default", e);
				throw new RuntimeException(
						"No SupportedCSS implementation registered!");
			}
		}
		return css;
	}

	public static final void registerRuleFactory(RuleFactory newRuleFactory) {
		rf = newRuleFactory;
	}

	@SuppressWarnings("unchecked")
	public static final RuleFactory getRuleFactory() {
		if (rf == null) {
			try {
				Class<? extends RuleFactory> clazz = (Class<? extends RuleFactory>) Class
						.forName(DEFAULT_RULE_FACTORY);
				Method m = clazz.getMethod("getInstance");
				registerRuleFactory((RuleFactory) m.invoke(null));
				log.debug("Retrived {} as default RuleFactory implementation.",
						DEFAULT_RULE_FACTORY);
			} catch (Exception e) {
				log.error("Unable to get RuleFactory from default", e);
				throw new RuntimeException(
						"No RuleFactory implementation registered!");
			}
		}

		return rf;
	}

	public static final void registerNodeDataInstance(
			Class<? extends NodeData> clazz) {
		try {
			@SuppressWarnings("unused")
			NodeData test = clazz.newInstance();
			ndImpl = clazz;
		} catch (InstantiationException e) {
			throw new RuntimeException("NodeData implemenation ("
					+ clazz.getName() + ") doesn't provide sole constructor", e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("NodeData implementation ("
					+ clazz.getName() + ") is not accesible", e);
		}

	}

	@SuppressWarnings("unchecked")
	public static final NodeData createNodeData() {
		if (ndImpl == null) {
			try {
				registerNodeDataInstance((Class<? extends NodeData>) Class
						.forName(DEFAULT_NODE_DATA_IMPL));
				log.debug("Registered {} as default NodeData instance.",
						DEFAULT_NODE_DATA_IMPL);
			} catch (Exception e) {
			}
		}

		try {
			return ndImpl.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("No NodeData implementation registered");
		}
	}

	public static final StyleSheet parse(String fileName, String encoding)
			throws CSSException, IOException {
		return CSSParserFactory.parse(fileName, SourceType.FILE);
	}

	public static final StyleSheet parse(String css) throws IOException,
			CSSException {
		return CSSParserFactory.parse(css, SourceType.EMBEDDED);
	}

	public static final Map<Element, NodeData> assignDOM(Document doc,
			String media, boolean useInheritance) {

		StyleSheet style = (StyleSheet) getRuleFactory().createStyleSheet()
				.unlock();
		
		Traversal<StyleSheet> traversal = new CSSAssignTraversal(doc,
				(Object) media, NodeFilter.SHOW_ELEMENT);

		traversal.listTraversal(style);

		Analyzer analyzer = new Analyzer(style);
		return analyzer.evaluateDOM(doc, media, useInheritance);
	}

	/**
	 * Walks (X)HTML document and collects style information
	 * 
	 * @author kapy
	 * 
	 */
	private static final class CSSAssignTraversal extends Traversal<StyleSheet> {

		public CSSAssignTraversal(Document doc, Object source, int whatToShow) {
			super(doc, source, whatToShow);
		}

		@Override
		protected void processNode(StyleSheet result, Node current,
				Object source) {

			// allowed media
			String media = (String) source;
			Element elem = (Element) current;

			// embedded style-sheet
			if (isEmbeddedStyleSheet(elem, media)) {
				try {
					result = CSSParserFactory.append(extractElementText(elem),
							SourceType.EMBEDDED, result);
					log.debug("Matched embedded CSS style");
				} catch (IOException e) {
					log.warn("Embedded THROWN:", e);
				} catch (CSSException e) {
					log.warn("Embedded THROWN:", e);
				}
			}
			// linked style-sheet
			else if (isLinkedStyleSheet(elem, media)) {
				try {
					result = CSSParserFactory.append(elem.getAttribute("href"),
							SourceType.FILE, result);
					log.debug("Matched linked CSS style");
				} catch (IOException e) {
					log.warn("Linked THROWN:", e);
				} catch (CSSException e) {
					log.warn("Linked THROWN:", e);
				}
			}
			// inline style
			else if (elem.getAttribute("style") != null &&
					elem.getAttribute("style").length()>0) {
				try {
					result = CSSParserFactory.append(
							elem.getAttribute("style"), SourceType.INLINE,
							elem, result);
					log.debug("Matched inline CSS style");
				} catch (IOException e) {
					log.warn("Inline THROWN:", e);
				} catch (CSSException e) {
					log.warn("Inline THROWN:", e);
				}
			}

		}

		private static boolean isEmbeddedStyleSheet(Element e, String media) {
			return "style".equalsIgnoreCase(e.getNodeName())
					&& isAllowedMedia(e, media);
		}

		private static boolean isLinkedStyleSheet(Element e, String media) {
			return e.getNodeName().equals("link")
					&& "stylesheet".equalsIgnoreCase(e.getAttribute("rel"))
					&& "text/css".equalsIgnoreCase(e.getAttribute("type"))
					&& isAllowedMedia(e, media);
		}

		/**
		 * Extracts element's text, if any
		 * 
		 * @param e
		 *            Element
		 * @return Element's text or {@code null}
		 */
		private static String extractElementText(Element e) {
			Node text = e.getFirstChild();
			if (text != null && text.getNodeType() == Node.TEXT_NODE)
				return ((Text) text).getData();
			return null;
		}

		/**
		 * Checks allowed media by searching for {@code media} attribute on
		 * element and its content
		 * 
		 * @param e
		 *            (STYLE) Element
		 * @param media
		 *            Media used for parsing
		 * @return {@code true} if allowed, {@code false} otherwise
		 */
		private static boolean isAllowedMedia(Element e, String media) {
			String mediaList = e.getAttribute("media");
			if (mediaList == null || mediaList.length() == 0
					|| mediaList.indexOf(media) != -1)
				return true;

			return false;
		}
	}

}
