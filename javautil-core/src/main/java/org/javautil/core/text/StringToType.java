package org.javautil.core.text;

import org.javautil.dataset.DataType;

public interface StringToType {

	Object coerceString(String value, DataType type);

}