package com.deloitte.bdh.common.desensitization;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.core.pattern.PatternFormatter;
import org.apache.logging.log4j.core.pattern.PatternParser;

/**
 * 自定义log4j2 layout， 扩展自PatternLayout（拷贝自log4j2， 留待以后扩展使用）
 *
 * @author dahpeng
 * @date 2019/04/24
 */
@Plugin(name = "CustomPatternLayout", category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE, printObject = true)
public final class CustomPatternLayout extends AbstractStringLayout {

	private static final long serialVersionUID = 1L;

	// 默认格式化
	public static final String DEFAULT_CONVERSION_PATTERN = "%m%n";

	// ttc 默认格式化
	public static final String TTCC_CONVERSION_PATTERN = "%r [%t] %p %c %x - %m%n";

	// 简单格式化
	public static final String SIMPLE_CONVERSION_PATTERN = "%d [%t] %p %c - %m%n";

	public static final String CONVERTER = "Converter";

	// 支持多个formater
	private final List<PatternFormatter> formatters;

	private final String conversionPattern;

	private final Configuration config;

	private final CustomRegexReplaces replace;

	private final boolean alwaysWriteExceptions;

	private final boolean noConsoleNoAnsi;

	/**
	 * 构造自动以patternLayout
	 *
	 * @param config
	 * @param replace
	 * @param pattern
	 * @param charset
	 * @param alwaysWriteExceptions
	 * @param noConsoleNoAnsi
	 * @param header
	 * @param footer
	 */
	private CustomPatternLayout(final Configuration config, final CustomRegexReplaces replace, final String pattern,
			final Charset charset, final boolean alwaysWriteExceptions, final boolean noConsoleNoAnsi,
			final String header, final String footer) {
		super(charset, toBytes(header, charset), toBytes(footer, charset));
		this.replace = replace;
		this.conversionPattern = pattern;
		this.config = config;
		this.alwaysWriteExceptions = alwaysWriteExceptions;
		this.noConsoleNoAnsi = noConsoleNoAnsi;
		final PatternParser parser = createPatternParser(config);
		this.formatters = parser.parse(pattern == null ? DEFAULT_CONVERSION_PATTERN : pattern,
				this.alwaysWriteExceptions, this.noConsoleNoAnsi);
	}

	private static byte[] toBytes(final String str, final Charset charset) {
		if (str != null) {
			return str.getBytes(charset != null ? charset : Charset.defaultCharset());
		}
		return null;
	}

	private byte[] strSubstitutorReplace(final byte... b) {
		if (b != null && config != null) {
			return getBytes(config.getStrSubstitutor().replace(new String(b, getCharset())));
		}
		return b;
	}

	@Override
	public byte[] getHeader() {
		return strSubstitutorReplace(super.getHeader());
	}

	@Override
	public byte[] getFooter() {
		return strSubstitutorReplace(super.getFooter());
	}

	public String getConversionPattern() {
		return conversionPattern;
	}


	@Override
	public Map<String, String> getContentFormat() {
		final Map<String, String> result = new HashMap<String, String>();
		result.put("structured", "false");
		result.put("formatType", "conversion");
		result.put("format", conversionPattern);
		return result;
	}


	@Override
	public String toSerializable(final LogEvent event) {
		final StringBuilder buf = new StringBuilder();
		for (final PatternFormatter formatter : formatters) {
			formatter.format(event, buf);
		}
		String str = buf.toString();
		if (replace != null) {
			str = replace.format(str);
		}
		return str;
	}

	/**
	 * pattern parser
	 * @param config
	 * @return
	 */
	public static PatternParser createPatternParser(final Configuration config) {
		if (config == null) {
			return new PatternParser(config, CONVERTER, LogEventPatternConverter.class);
		}
		PatternParser parser = config.getComponent(CONVERTER);
		if (parser == null) {
			parser = new PatternParser(config, CONVERTER, LogEventPatternConverter.class);
			config.addComponent(CONVERTER, parser);
			parser = (PatternParser) config.getComponent(CONVERTER);
		}
		return parser;
	}

	@Override
	public String toString() {
		return conversionPattern;
	}

	/**
	 * log4j2 拷贝代码
	 * Create a pattern layout.
	 *
	 * @param pattern
	 *            The pattern. If not specified, defaults to
	 *            DEFAULT_CONVERSION_PATTERN.
	 * @param config
	 *            The Configuration. Some Converters require access to the
	 *            Interpolator.
	 * @param replace
	 *            A Regex replacement String.
	 * @param charset
	 *            The character set.
	 * @param alwaysWriteExceptions
	 *            If {@code "true"} (default) exceptions are always written even
	 *            if the pattern contains no exception tokens.
	 * @param noConsoleNoAnsi
	 *            If {@code "true"} (default is false) and
	 *            {@link System#console()} is null, do not output ANSI escape
	 *            codes
	 * @param header
	 *            The footer to place at the top of the document, once.
	 * @param footer
	 *            The footer to place at the bottom of the document, once.
	 * @return The PatternLayout.
	 */
	@PluginFactory
	public static CustomPatternLayout createLayout(
			@PluginAttribute(value = "pattern", defaultString = DEFAULT_CONVERSION_PATTERN) final String pattern,
			@PluginConfiguration final Configuration config,
			@PluginElement("Replaces") final CustomRegexReplaces replace,
			@PluginAttribute(value = "charset", defaultString = "UTF-8") final Charset charset,
			@PluginAttribute(value = "alwaysWriteExceptions", defaultBoolean = true) final boolean alwaysWriteExceptions,
			@PluginAttribute(value = "noConsoleNoAnsi", defaultBoolean = false) final boolean noConsoleNoAnsi,
			@PluginAttribute("header") final String header, @PluginAttribute("footer") final String footer) {
		return newBuilder().withPattern(pattern).withConfiguration(config).withRegexReplacement(replace)
				.withCharset(charset).withAlwaysWriteExceptions(alwaysWriteExceptions)
				.withNoConsoleNoAnsi(noConsoleNoAnsi).withHeader(header).withFooter(footer).build();
	}

	/**
	 * Creates a PatternLayout using the default options. These options include
	 * using UTF-8, the default conversion pattern, exceptions being written,
	 * and with ANSI escape codes.
	 *
	 * @return the PatternLayout.
	 * @see #DEFAULT_CONVERSION_PATTERN Default conversion pattern
	 */
	public static CustomPatternLayout createDefaultLayout() {
		return newBuilder().build();
	}

	/**
	 * Creates a builder for a custom PatternLayout.
	 *
	 * @return a PatternLayout builder.
	 */
	@PluginBuilderFactory
	public static Builder newBuilder() {
		return new Builder();
	}

	/**
	 * Custom PatternLayout builder. Use the {@link PatternLayout#newBuilder()
	 * builder factory method} to create this.
	 */
	public static class Builder implements org.apache.logging.log4j.core.util.Builder<CustomPatternLayout> {

		// FIXME: it seems rather redundant to repeat default values (same goes
		// for field names)
		// perhaps introduce a @PluginBuilderAttribute that has no values of its
		// own and uses reflection?

		@PluginBuilderAttribute
		private String pattern = CustomPatternLayout.DEFAULT_CONVERSION_PATTERN;

		@PluginConfiguration
		private Configuration configuration = null;

		@PluginElement("Replaces")
		private CustomRegexReplaces regexReplacement = null;

		// LOG4J2-783 use platform default by default
		@PluginBuilderAttribute
		private Charset charset = Charset.defaultCharset();

		@PluginBuilderAttribute
		private boolean alwaysWriteExceptions = true;

		@PluginBuilderAttribute
		private boolean noConsoleNoAnsi = false;

		@PluginBuilderAttribute
		private String header = null;

		@PluginBuilderAttribute
		private String footer = null;

		private Builder() {
		}

		// TODO: move javadocs from PluginFactory to here

		public Builder withPattern(final String pattern) {
			this.pattern = pattern;
			return this;
		}

		public Builder withConfiguration(final Configuration configuration) {
			this.configuration = configuration;
			return this;
		}

		public Builder withRegexReplacement(final CustomRegexReplaces regexReplacement) {
			this.regexReplacement = regexReplacement;
			return this;
		}

		public Builder withCharset(final Charset charset) {
			this.charset = charset;
			return this;
		}

		public Builder withAlwaysWriteExceptions(final boolean alwaysWriteExceptions) {
			this.alwaysWriteExceptions = alwaysWriteExceptions;
			return this;
		}

		public Builder withNoConsoleNoAnsi(final boolean noConsoleNoAnsi) {
			this.noConsoleNoAnsi = noConsoleNoAnsi;
			return this;
		}

		public Builder withHeader(final String header) {
			this.header = header;
			return this;
		}

		public Builder withFooter(final String footer) {
			this.footer = footer;
			return this;
		}

		@Override
		public CustomPatternLayout build() {
			// fall back to DefaultConfiguration
			if (configuration == null) {
				configuration = new DefaultConfiguration();
			}
			return new CustomPatternLayout(configuration, regexReplacement, pattern, charset, alwaysWriteExceptions,
					noConsoleNoAnsi, header, footer);
		}
	}
}
