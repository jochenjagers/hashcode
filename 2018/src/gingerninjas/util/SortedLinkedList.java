package gingerninjas.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.ListIterator;

import gingerninjas.jochen.pizza.Slice;

public class SortedLinkedList<T extends Comparable<T>> extends LinkedList<T>
{
	private static final long serialVersionUID = 3934645506565894404L;
	public boolean add(T newElement)
	{
		ListIterator<T> it = this.listIterator();
		while(it.hasNext())
		{
			T data = it.next();
			int result = newElement.compareTo(data);
			if(result < 0)
			{
				// Insert here
				if(it.hasPrevious())
				{
					it.previous();
				}
				it.add(newElement);
				return true;
			}
		}
		super.add(newElement);
		return true;
	};


	public static void main(String[] args)
	{
		SortedLinkedList<Slice> test = new SortedLinkedList<>();

		Slice s1 = new Slice(0, 1, 2, 3, null, null);
		test.add(s1);
		test.add(new Slice(4, 5, 6, 7, null, null));
		test.add(new Slice(8, 9, 10, 11, null, null));

		//SortedLinkedList<Slice> test2 =  test.copy();
		s1.x = 15;
		for(Slice i : test)
		{
			System.out.println(i);
		}
	}
}
