package org.javautil.core.tree;

/*
 * Represents a DTO that is a node in a tree. This node has a single parent, any
 * number of children, and a human-readable description.
 * 
 * 
 * TODO jjs But there are no getChildren and setChildren methods.
 * 
 */
public interface TreeNode<T extends TreeNode<T>> {

	/*
	 * The parent node number of this node.
	 * 
	 * @return parentNodeNbr
	 */
	public Integer getParentNodeNbr();

	/*
	 * The unique node number of this node in the tree.
	 * 
	 * @return nodeNbr
	 */
	public Integer getNodeNbr();

	/*
	 * A human-readable description for this node.
	 * 
	 * @return description
	 */
	public String getDescription();

	/*
	 * A short toString
	 */
	public String format();
}
