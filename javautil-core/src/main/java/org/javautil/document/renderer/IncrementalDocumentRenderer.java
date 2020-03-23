package org.javautil.document.renderer;

import java.io.IOException;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.javautil.document.DocumentRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IncrementalDocumentRenderer {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public boolean canRender(final DatasetRendererRequest rendererRequest) {
		return rendererRequest != null && IncrementalRendererRequest.class.isAssignableFrom(rendererRequest.getClass());
	}

	public void render(final DocumentRendererRequest renderRequest) throws RendererException, IOException {
		// TODO why is this casting here?
		final IncrementalRendererRequest request = (IncrementalRendererRequest) renderRequest;
		if (request.getDocumentRegion() == null) {
			throw new IllegalStateException("request.documentRegion is null");
		}
		renderRegion(request);
		while (request.next()) {
			renderRegion(request);
		}
		request.write();
	}

	protected void renderRegion(final DocumentRegionRendererRequest request) {
		final DocumentRenderStatus status = request.getStatus();
		final DocumentRegion region = request.getDocumentRegion();
		status.setCurrentRegion(region);
		if (logger.isDebugEnabled()) {
			logger.debug("performing layout for region " + region.getName());
		}

		if (region == null) {
			throw new IllegalStateException("request.region is null");
		}
		region.getLayoutConstraints().performLayout(status);
		final RenderTemplate template = region.getRenderTemplate();
		if (template == null) {
			throw new IllegalStateException("request.template is null");
		}
		template.render(request);
	}
}
