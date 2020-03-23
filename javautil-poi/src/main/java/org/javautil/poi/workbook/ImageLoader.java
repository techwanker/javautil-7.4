package org.javautil.poi.workbook;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.Workbook;
import org.javautil.io.IOUtils;
import org.javautil.io.ResourceHelper;

/**
 * 
 * @author jjs@dbexperts.com
 * 
 */
public class ImageLoader {

	public void loadImage(final HSSFWorkbook workbook, final HSSFSheet sheet, final String resourceName,
			final Class<?> resourceClass, final int col1, final int row1, final int col2, final int row2)
			throws IOException {

		final InputStream resource = ResourceHelper.getResourceAsInputStream(resourceClass, resourceName);

		final byte[] picData = IOUtils.readBytesFromStream(resource, 1024 * 4, true); // 128k
		// buffer
		final int indx = workbook.addPicture(picData, Workbook.PICTURE_TYPE_PNG);
		final HSSFPatriarch patriarch = sheet.createDrawingPatriarch();
		final HSSFClientAnchor anchor = new HSSFClientAnchor(0, // the x
																// coordinate
				// within the first
				// cell.
				0, // the y coordinate within the first cell.
				0, // the x coordinate within the second cell.
				0, // the y coordinate within the second cell.
				(short) col1, // the column (0 based) of the first cell.
				row1, // the row (0 based) of the first cell.
				(short) col2, // the column (0 based) of the second cell.
				row2 // the row (0 based) of the second cell.

		);
		anchor.setAnchorType(AnchorType.DONT_MOVE_DO_RESIZE);
		// anchor.setAnchorType(2);
		patriarch.createPicture(anchor, indx);
	}
}
