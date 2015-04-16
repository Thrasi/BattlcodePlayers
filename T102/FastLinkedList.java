//package T102;
//
//public class FastLinkedList {
//
//	public static class Node {
//		private Node next;
//		public int x;
//		public int y;
//		
//		public Node(Node next, int x, int y) {
//			this.next = next;
//			this.x = x;
//			this.y = y;
//		}
//	}
//	
//	private static int count = 0;
//	public static int i = 0;
//	
//	private static Node head = null;
//	private static Node tail = null;
//	
//	public static void add(int x, int y) {
//		if (head == null) {
//			head = new Node(null, x, y);
//			tail = head;
//			count++;
//			//i++;
//			return;
//		}
//		tail.next = new Node(null, x, y);
//		tail = tail.next;
//		count++;
//		//i++;
//	}
//	
//	public static Node remove() {
//		if (head == null) {
//			return null;
//		}
//		Node v = head;
//		head = head.next;
//		if (head == null) {
//			tail = null;
//		}
//		count--;
//		return v;
//	}
//	
//	public static boolean isEmpty() {
//		return count == 0;
//	}
//	
//}
