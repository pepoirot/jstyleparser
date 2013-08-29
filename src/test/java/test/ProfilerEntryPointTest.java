package test;

import cz.vutbr.web.css.StyleSheet;
import cz.vutbr.web.csskit.antlr.CSSParserFactory;
import cz.vutbr.web.csskit.antlr.CSSParserFactory.SourceType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static test.DataUtil.getUrl;

public class ProfilerEntryPointTest {

    @Test
	public void ruleCount() throws Exception {

		StyleSheet sheet = CSSParserFactory.parse(getUrl("/data/abclinuxu/styles.css"), null,
				SourceType.URL, null);

		assertEquals("Total rules: ", 108, sheet.size());
	}

}
