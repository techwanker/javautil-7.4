package org.javautil.core.json;

import java.util.Map;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ModelGsonMarshaller {
	// https://stackoverflow.com/questions/40909205/java-gson-exclude-fields-during-serialization

	private Gson gson           = new GsonBuilder().setPrettyPrinting().create();

	// https://stackoverflow.com/questions/2638590/best-way-of-invoking-getter-by-reflection
//	import static org.reflections.ReflectionUtils.*;
//	Set<Method> getters = ReflectionUtils.getAllMethods(someClass,
//	      withModifier(Modifier.PUBLIC), withPrefix("get"), withAnnotation(annotation));

	// https://play.lighthouseapp.com/projects/57987/tickets/1242-renderjson-and-onetomany
	Gson         gsonNoChildren = new GsonBuilder().addSerializationExclusionStrategy(new ExclusionStrategy() {
																@Override
																public boolean shouldSkipField(FieldAttributes f) {

																	if (f.getAnnotation(OneToMany.class) != null) {
																		return true;
																	}
																	if (f.getAnnotation(ManyToOne.class) != null) {
																		return true;
																	}
																	return false;
																}

																@Override
																public boolean shouldSkipClass(Class<?> aClass) {
																	return false;
																}
															}).setPrettyPrinting().create();

	public String formatObject(Object obj) {
		return gsonNoChildren.toJson(obj);
	}

	public String formatMap(Map map, String name) {
		return gsonNoChildren.toJson(map);

	}

}
