package com.javautil.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 *  Container for information about a file.
 */

public class FileNode {
	private static final String className = "com.javautil.util.FileNode";
	private static final Logger logger    = LoggerFactory.getLogger(className);

	private File                file;
	private long                lastModifiedCache;
	private ArrayList<FileNode> children  = new ArrayList<FileNode>();
	private FileNode            parent    = null;

	public FileNode(File file, FileNode parent) {
		logger.debug("file: " + file + " parent " + parent);
		this.file = file;
		refresh();
	}

	public FileNode(String fileName, FileNode parent) {
		this.file = new File(fileName);
		refresh();
	}

	/*
	 * Adds a child node after ensuring that the child is not the parent or any
	 * ancestor of the parent.
	 * 
	 * If a violation is found an IllegalStateException is thrown.
	 * 
	 * @param file the file to be added as a child
	 * 
	 * @throws java.io.IOException When there is an error
	 */
	public void addChild(File file) throws java.io.IOException {
		FileNode node = new FileNode(file, this);

		for (FileNode ancestor = this; ancestor != null; ancestor = ancestor.parent) {
			if (ancestor.file.getCanonicalFile().equals(file)) {
				throw new java.lang.IllegalStateException(
				    "Attempt to add " + file + " as child to " + this.file + " violating ancestor " + ancestor.file);
			}
		}

		children.add(node);
	}

	/*
	 * @return true if this node or any of its children, recursively,
	 */
	public boolean hasModifiedNodes() {
		return (modifiedNodes().length > 0);
	}

	/*
	 * Returns true if the lastModifiedCacheDate is different from that of the file.
	 * 
	 * This returns true even if the cached date is after the that of the file. The
	 * logic here is that somebody tried to update the timestamp with unix touch
	 * command or something similar and messed up.
	 * 
	 * Applies only to this node.
	 * 
	 * @return boolean out of date
	 */
	public boolean needsRefresh() {
		return (this.lastModifiedCache != file.lastModified());
	}

	public void refresh() {
		this.lastModifiedCache = file.lastModified();
	}

	private FileNode[] modifiedNodes() {
		HashMap<FileNode, FileNode> nodes = new HashMap<FileNode, FileNode>();

		FileNode[] fileNodes;
		// check this node
		if (this.lastModifiedCache != file.lastModified()) {
			nodes.put(this, this);
		}
		// recursively process all of the children
		Iterator it = children.iterator();
		while (it.hasNext()) {
			FileNode node = (FileNode) it.next();
			FileNode[] mods = node.modifiedNodes();
			for (int ndx = 0; ndx < mods.length; ndx++) {
				nodes.put(mods[ndx], mods[ndx]);
			}
		}
		// construct return
		fileNodes = new FileNode[nodes.size()];
		Iterator nodeIt = nodes.values().iterator();
		for (int ndx = 0; nodeIt.hasNext(); ndx++) {
			FileNode node = (FileNode) nodeIt.next();
			fileNodes[ndx] = node;
		}

		return fileNodes;
	}
}
