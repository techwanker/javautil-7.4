package org.javautil.dataset.render.typewriter;

import java.io.IOException;
import java.io.OutputStream;

public interface TypewriterContents<R, C> {

	public TypewriterContent<R, C> getContent(String identifier);

	public void write(OutputStream outputStream) throws IOException;
}
