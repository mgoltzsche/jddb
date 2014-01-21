package de.algorythm.jddb.controller.codegen;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

import de.algorythm.jddb.controller.codegen.DataModelGenerator;

public class InMemoryCompiler {

	public void compile() throws IOException {
		JavaSource source = new JavaSource("de.algorythm.jddb.generated.DynamicCompilation", DataModelGenerator.sourceCode);
		List<JavaSource> sources = Arrays.asList(new JavaSource[] {source});
		
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		JavaFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(null, null, null));
		
		try {
			CompilationTask compilationTask = compiler.getTask(null, fileManager, diagnostics, null, null, sources);
			boolean status = compilationTask.call();
			
			if (!status)
	    		/*Iterate through each compilation problem and print it*/
				for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics())
					System.err.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic);
			
			ClassLoader classLoader = fileManager.getClassLoader(null);
			Class<?> clazz;
			
			try {
				clazz = classLoader.loadClass("de.algorythm.jddb.generated.DynamicCompilation");
			} catch(ClassNotFoundException e) {
				throw new RuntimeException("Cannot find generated class", e);
			}

			try {
				TestInterface instance = (TestInterface) clazz.newInstance();
				instance.hello("Developer");
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Cannot instantiate generated class", e);
			}
		} finally {
			fileManager.close();
		}
	}
}
