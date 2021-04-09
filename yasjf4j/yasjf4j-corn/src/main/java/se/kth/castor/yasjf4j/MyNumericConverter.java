package se.kth.castor.yasjf4j;

import net.sf.corn.converter.ConversionException;
import net.sf.corn.converter.ObjectReferenceMap;
import net.sf.corn.converter.json.AbstractJsonConverter;
import net.sf.corn.converter.json.JsTypeObject;
import net.sf.corn.converter.json.JsTypeSimple;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;

public class MyNumericConverter extends AbstractJsonConverter {

	public MyNumericConverter() {
		getConvertibles().add(short.class);
		getConvertibles().add(Short.class);
		getConvertibles().add(long.class);
		getConvertibles().add(Long.class);
		getConvertibles().add(int.class);
		getConvertibles().add(Integer.class);
		getConvertibles().add(float.class);
		getConvertibles().add(Float.class);
		getConvertibles().add(double.class);
		getConvertibles().add(Double.class);
		getConvertibles().add(byte.class);
		getConvertibles().add(Byte.class);
		getConvertibles().add(BigInteger.class);
		getConvertibles().add(BigDecimal.class);
	}

	public JsTypeObject inToTarget(Object o, ObjectReferenceMap referenceMap)
			throws ConversionException {
		return new JsTypeSimple(o);
	}

	public Object inToJava(Class<?> clazz, JsTypeObject obj)
			throws ConversionException {
		if (!obj.isSimple()) {
			throw new ConversionException(this.getClass().getName()
					+ " can't be used as converter for " + clazz.getName());
		}
		Object retVal = null;
		try {
			Constructor<?> stringConstructor = null;
			if(clazz.isPrimitive()){
				Class<?> oclazz = findObjectType4Primitive(clazz);
				stringConstructor = oclazz
						.getDeclaredConstructor(String.class);
			}else{
				stringConstructor = clazz
						.getDeclaredConstructor(String.class);
			}

			retVal = stringConstructor.newInstance(obj.toStringValue());
		} catch (Exception e) {
			throw new ConversionException(
					"An exception occured while transforming json to "
							+ clazz.getName(), e);
		}
		// Reverse must be proven
//		if (!obj.toStringValue().equalsIgnoreCase(retVal.toString())) {
//			throw new ConversionException(this.getClass().getName()
//					+ " can't be used as converter for " + clazz.getName());
//		}
		return retVal;
	}

	public static Class<?> findObjectType4Primitive(Class<?> clazz){
		if(clazz.equals(short.class)){
			return Short.class;
		}else if(clazz.equals(int.class)){
			return Integer.class;
		}else if(clazz.equals(byte.class)){
			return Byte.class;
		}else if(clazz.equals(long.class)){
			return Long.class;
		}else if(clazz.equals(float.class)){
			return Float.class;
		}else if(clazz.equals(double.class)){
			return Double.class;
		}
		return clazz;
	}
	@Override
	public boolean isSubjectForCircularReferenceCheck() {
		return false;
	}

}
