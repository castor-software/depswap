package se.kth.assertteam.depanalyzer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassAPIVisitor extends ClassVisitor implements Opcodes {

	public API api;

	public ClassAPIVisitor(ClassVisitor cv, API api) {
		super(ASM5, cv);
		this.api = api;
	}

	boolean partOfApi = false;

	String packageName;
	String className;

	@Override
	public void visit(
			final int version,
			final int access,
			final String name,
			final String signature,
			final String superName,
			final String[] interfaces) {
		if(!name.equals("module-info")) {
			super.visit(version,access,name,signature,superName,interfaces);
			int separatorIndex = name.lastIndexOf('/');
			packageName = name.substring(0, separatorIndex);
			className = name.substring(separatorIndex + 1);
			if (Modifier.isPublic(access)) {
				partOfApi = true;
				boolean isInterface = Modifier.isInterface(access);
				boolean isAnnotation = ArrayUtils.contains(interfaces, "java/lang/annotation/Annotation");
				boolean isAbstract = Modifier.isAbstract(access);
				api.insertType(packageName, className, true, isInterface, isAnnotation, isAbstract);
			}
		}
	}

	@Override
	public AnnotationVisitor visitTypeAnnotation(int typeRef,
	                                             TypePath typePath, String desc, boolean visible) {
		System.out.println("Annotation: " + desc);
		return cv.visitTypeAnnotation(typeRef,typePath,desc,visible);
	}

	@Override
	public MethodVisitor visitMethod(final int access, final String name,
	                                 final String desc, final String signature, final String[] exceptions) {
		if(partOfApi && (Modifier.isPublic(access) || Modifier.isProtected(access))) {
			//System.out.println("[M] " + getSignature(name, desc));
			api.insertElement(packageName, className, getSignature(name, desc),
					Modifier.isStatic(access),Modifier.isPublic(access), false);
		}
		return cv.visitMethod(access, name, desc, signature, exceptions);
	}

	@Override
	public FieldVisitor visitField(int access, String name, String desc,
	                               String signature, Object value) {
		if(partOfApi && (Modifier.isPublic(access) || Modifier.isProtected(access))) {
			api.insertElement(packageName, className, getSignature(name, desc),
					Modifier.isStatic(access),Modifier.isPublic(access), true);
		}
		return cv.visitField(access, name, desc, signature, value);
	}

	public static String getSignature(String name, String desc) {
		return name + desc;
	}

	public static void main(String[] args) throws IOException, SQLException {

		if(args.length != 2) {
			System.err.println("Usage: java -cp myJar.jar se.kth.assertteam.depanalyzer.ClassAPIVisitor /path/to/jar output-file.csv");
		}
		String pathToJar = args[0];

		API tapi = new API();

		JarFile jarFile = new JarFile(new File(pathToJar));
		Enumeration<JarEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String entryName = entry.getName();
			if (entryName.endsWith(".class") && !entryName.endsWith("module-info.class")) {
				InputStream classFileInputStream = jarFile.getInputStream(entry);
				try {
					ClassReader cr = new ClassReader(classFileInputStream);
					ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
					ClassAPIVisitor cv = new ClassAPIVisitor(cw,tapi);
					cr.accept(cv, 0);
				} finally {
					classFileInputStream.close();
				}
			}
		}
		//toyApi.pushToDB(dbWrapper.getConnection());

		FileUtils.write(new File(args[1]), tapi.toCsv(), Charset.defaultCharset(), false);

		System.out.println("Done");
	}
}
