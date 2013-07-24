package de.algorythm.jdoe.controller.codegen;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;

public class JavaSource extends SimpleJavaFileObject {

	private final String name;
	private final CharSequence charContent;

	public JavaSource(final String name, final CharSequence sourceCode) {
		super(URI.create("string:///" + name.replace('.', '/')
				+ Kind.SOURCE.extension), Kind.SOURCE);

		this.name = name;
		this.charContent = sourceCode;
	}

	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return charContent;
	}

	public String getName() {
		return name;
	}
}
