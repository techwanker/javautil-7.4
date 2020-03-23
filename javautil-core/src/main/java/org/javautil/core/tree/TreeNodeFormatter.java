package org.javautil.core.tree;

public class TreeNodeFormatter {

	public static String format(final TreeNode node) {
		final String string = "nodeNbr: " + node.getNodeNbr() + " parentNodeNbr: " + node.getParentNodeNbr()
		    + " description " + node.getDescription();
		return string;
	}
}
