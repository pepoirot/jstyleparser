package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Map;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.tidy.Tidy;

import cz.vutbr.web.css.CSSFactory;
import cz.vutbr.web.css.NodeData;
import cz.vutbr.web.css.RuleMedia;
import cz.vutbr.web.css.RuleSet;
import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.css.TermFactory;
import cz.vutbr.web.css.TermList;
import cz.vutbr.web.css.CSSProperty.Color;
import cz.vutbr.web.css.CSSProperty.FontFamily;
import cz.vutbr.web.domassign.Analyzer;

public class GrammarRecovery1 {
	private static Logger log = LoggerFactory.getLogger(GrammarRecovery1.class);

	public static final TermFactory tf = CSSFactory.getTermFactory();

	public static final String TEST_CHARSET_WITHOUT_SEMICOLON1 = "@charset \"UTF-8\"";

	public static final String TEST_CHARSET_WITHOUT_SEMICOLON2 = "@charset \"UTF-8\"\n"
			+ "BODY { color: blue;}";

	public static final String TEST_CHARSET_WITHOUT_SEMICOLON3 = "@charset \"UTF-8\"\n"
			+ "BODY { color: blue;}\n" + "BODY { color: red; }";

	public static final String TEST_INVALID_ATKEYWORD = "@three-dee {\n"
			+ "	  @background-lighting {\n" + "    azimuth: 30deg;\n"
			+ "    elevation: 190deg;\n" + "	  }\n" + "  h2 { color: green }\n"
			+ "	}";

	public static final String TEST_NOT_CLOSED_STRING = 
			"@charset \"UTF-8\n" +
		    "BODY { color: blue;}\n" +
		    "p {color: white; quotes: '^' '^\n" +
		    "color: red;" +
		    "color: green}";
	
	public static final String TEST_DECL1 = "p { color:red;   color; color:green }";

	public static final String TEST_DECL2 = "p { color:green; color: }";

	public static final String TEST_DECL3 = "p { color:red;   color:; color:green }";

	public static final String TEST_DECL4 = "p { color:green; color{;color:maroon} }";

	public static final String TEST_DECL5 = "p { color:red;   color{;color:maroon}; color:green }";

	public static final String TEST_UNEXP_EOF =
		"@media screen {\n" +
	    "p:before { content: 'Hello";
	
	public static final String TEST_INVALID_SELECTOR =
		" h1, h2 & h3 {color: green;}\n " +
		" h1 {font-family: Times New Roman}";
	
	
	@BeforeClass
	public static void init() {
		log.info("\n\n\n == GrammarRecovery1 test at {} == \n\n\n", new Date());
	}

	@Test
	public void charsetCharsetWithoutSemicolon() {

		StyleSheet ss = CSSFactory.parse(TEST_CHARSET_WITHOUT_SEMICOLON1);
		assertEquals("Charset should not be set", null, ss.getCharset());

		assertEquals("No rules are defined", 0, ss.size());
	}

	@Test
	public void charsetWithoutSemicolonAndDefinitinAfter() {

		StyleSheet ss = CSSFactory.parse("data/invalid/recovery2.css", null);
		assertEquals("Charset should not be set", null, ss.getCharset());

		assertEquals("No rules are set", 0, ss.size());

	}

	@Test
	public void charsetWithoutSemicolonAndDoubleDAfter() {

		StyleSheet ss = CSSFactory.parse(TEST_CHARSET_WITHOUT_SEMICOLON3);
		assertEquals("Charset should not be set", null, ss.getCharset());

		RuleSet rule = (RuleSet) ss.get(0);

		assertEquals("Rule contains one selector BODY ", SelectorsUtil
				.createSelectors("BODY"), rule.getSelectors());

		assertEquals("Rule contains one declaration { color: red;}",
				DeclarationsUtil.appendDeclaration(null, "color", tf
						.createColor(255, 0, 0)), rule.asList());

	}

	@Test
	public void unclosedString() {
		StyleSheet ss = CSSFactory.parse(TEST_NOT_CLOSED_STRING);
		
		assertEquals("Contains one ruleset", 1, ss.size());

		RuleSet rule = (RuleSet) ss.get(0);

		assertEquals("Rule contains last declaration { color: green;}",
				DeclarationsUtil.createDeclaration("color", tf.createColor(0,
						0x80, 0)), rule.get(rule.size() - 1));
		
	}
	
	@Test
	public void invalidAtKeyword() {
		StyleSheet ss = CSSFactory.parse(TEST_INVALID_ATKEYWORD);
		assertTrue("Ruleset is empty", ss.isEmpty());

	}

	@Test
	public void noTerms() {

		StyleSheet ss = CSSFactory.parse(TEST_DECL5);

		assertEquals("Contains one ruleset", 1, ss.size());

		RuleSet rule = (RuleSet) ss.get(0);

		assertEquals("Rule contains last declaration { color: green;}",
				DeclarationsUtil.createDeclaration("color", tf.createColor(0,
						0x80, 0)), rule.get(rule.size() - 1));
	}

	@Test
	public void unexpectedEOF() {
		StyleSheet ss = CSSFactory.parse(TEST_UNEXP_EOF);
		
		assertEquals("Contains one @media", 1, ss.size());
		
		RuleMedia rm = (RuleMedia) ss.get(0);
		
		assertEquals("Media is set for screen", "screen", rm.getMedias().get(0));
	}
	
	@Test
	public void invalidSelector() throws FileNotFoundException {
		StyleSheet sheet = CSSFactory.parse(TEST_INVALID_SELECTOR);
		
		Tidy parser = new Tidy();
		parser.setCharEncoding(org.w3c.tidy.Configuration.UTF8);

		Document doc = parser.parseDOM(new FileInputStream("data/simple/h1.html"),
				null);

		Analyzer analyzer = new Analyzer(sheet);
		Map<Element, NodeData> decl = analyzer.evaluateDOM(doc, "all", true);

		ElementMap elements = new ElementMap(doc);
		
		NodeData nd = decl.get(elements.getLastElementByName("h1"));
		
		Assert.assertNull("There is no color", nd.getProperty(Color.class, "color"));
		
		Assert.assertEquals("There is font-family", 
				FontFamily.list_values, nd.getProperty(FontFamily.class, "font-family"));
		Assert.assertEquals("Font is 'Times New Roman'",
				tf.createString("Times New Roman"),
				nd.getValue(TermList.class, "font-family").get(0));
		
	}
	
}
