package org.javautil.document.renderer;

import java.io.IOException;

public interface Renderer {

	public void process() throws RendererException, IOException;

	public boolean canRender(DatasetRendererRequest dexterRequest);

	public void setRendererRequest(DatasetRendererRequest dexterRequest);

}
