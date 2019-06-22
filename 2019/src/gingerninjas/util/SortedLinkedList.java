package gingerninjas.util;

import java.util.LinkedList;
import java.util.ListIterator;

public class SortedLinkedList<T extends Comparable<T> & Copyable<T>> extends LinkedList<T> implements Copyable<SortedLinkedList<T>>
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

	@Override
	public SortedLinkedList<T> copy()
	{

		SortedLinkedList<T> result = new SortedLinkedList<>();
		for(T element : this)
		{
			result.addLast(element.copy());
		}
		return result;
	}
}
