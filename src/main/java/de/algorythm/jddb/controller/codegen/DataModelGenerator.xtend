package de.algorythm.jddb.controller.codegen

class DataModelGenerator {
	
	static public val sourceCode = '''
		package de.algorythm.jddb.generated;
		
		import de.algorythm.jddb.controller.codegen.TestInterface;
		
		public class DynamicCompilation implements TestInterface {
			
			public DynamicCompilation() {}
			
			public void hello(final String name) {
				System.out.println("Hello " + name);
			}
			
			public String toString() {
				return "asfsf";
			}
		}
	'''
}