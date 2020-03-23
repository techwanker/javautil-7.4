package org.javautil.core.tree;

import java.util.List;

/**
 * Interface to be implemented by TreeNode classes that should be built by the
 * TreeNodeBuilder class.
 */
public interface ModifiableTreeNode<T extends ModifiableTreeNode<T>> extends TreeNode<T> {

	/**
	 * Assigns the children that have a parent node number equals to this node's
	 * node number.
	 * 
	 * @param children list of nodes
	 */
	public void setChildren(List<T> children);

	/**
	 * Returns the nodes that have a parent node equal to the id of this node.
	 * 
	 * @return The children
	 */
	public List<T> getChildren();

}
