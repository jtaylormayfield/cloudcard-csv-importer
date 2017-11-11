package com.sharptop.cloudcard.csvimporter.util;

import java.io.File;

import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class FileUtilsTests {

	@Test
	public void testStripFileExtension_Success() {
		assertThat(FileUtils.stripFileExtension(new File("A:\\dir\\test.txt")))
			.isEqualTo("test");
	}

	@Test
	public void testStripFileExtension_Null() {
		assertThatThrownBy(() -> FileUtils.stripFileExtension(null))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testStripFileExtension_MultiplePeriodChars() {
		assertThat(FileUtils.stripFileExtension(new File("A:\\dir\\test.test.txt")))
			.isEqualTo("test.test");
	}

	@Test
	public void testStripFileExtension_AllExtension() {
		assertThat(FileUtils.stripFileExtension(new File(".gitignore")))
			.isEqualTo("");
	}

	@Test
	public void testStripFileExtension_NoExtension() {
		assertThat(FileUtils.stripFileExtension(new File("A:\\bash\\script")))
			.isEqualTo("script");
	}
}
