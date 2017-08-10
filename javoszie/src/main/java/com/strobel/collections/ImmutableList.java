package com.strobel.collections;

import com.strobel.annotations.NotNull;
import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ImmutableList<A> extends AbstractCollection<A> implements List<A> {
	public A head;
	public ImmutableList<A> tail;

	private static final ImmutableList<?> EMPTY_LIST = new ImmutableList(null, null) {

		public boolean isEmpty() {
			return true;
		}
	};

	ImmutableList(A head, ImmutableList<A> tail) {
		this.tail = tail;
		this.head = head;
	}

	public static <A> ImmutableList<A> empty() {
		return (ImmutableList<A>)EMPTY_LIST;
	}

	public static <A> ImmutableList<A> of(A x1) {
		return new ImmutableList(x1, empty());
	}

	@SafeVarargs
	public static <A> ImmutableList<A> of(A x1, A... rest) {
		return new ImmutableList(x1, from(rest));
	}

	public static <A> ImmutableList<A> of(A x1, A x2) {
		return new ImmutableList(x1, of(x2));
	}

	public static <A> ImmutableList<A> of(A x1, A x2, A x3) {
		return new ImmutableList(x1, of(x2, x3));
	}

	public static <A> ImmutableList<A> of(A x1, A x2, A x3, A... rest) {
		return new ImmutableList(x1, new ImmutableList(x2, new ImmutableList(x3, from(rest))));
	}

	public static <A> ImmutableList<A> from(A[] array) {
		ImmutableList<A> xs = empty();
		if (array != null) {
			for (int i = array.length - 1; i >= 0; i--) {
				xs = new ImmutableList(array[i], xs);
			}
		}
		return xs;
	}

	@Deprecated
	public static <A> ImmutableList<A> fill(int len, A init) {
		ImmutableList<A> l = empty();
		for (int i = 0; i < len; i++) {
			l = new ImmutableList(init, l);
		}
		return l;
	}

	public boolean isEmpty() {
		return this.tail == null;
	}

	public boolean nonEmpty() {
		return this.tail != null;
	}

	public int length() {
		ImmutableList<A> l = this;
		int len = 0;
		while (l.tail != null) {
			l = l.tail;
			len++;
		}
		return len;
	}

	public int size() {
		return length();
	}

	public ImmutableList<A> setTail(ImmutableList<A> tail) {
		this.tail = tail;
		return tail;
	}

	public ImmutableList<A> prepend(A x) {
		return new ImmutableList(x, this);
	}

	public ImmutableList<A> prependList(ImmutableList<A> xs) {
		if (isEmpty()) {
			return xs;
		}

		if (xs.isEmpty()) {
			return this;
		}

		if (xs.tail.isEmpty()) {
			return prepend(xs.head);
		}

		ImmutableList<A> result = this;
		ImmutableList<A> rev = xs.reverse();

		assert (rev != xs);

		while (rev.nonEmpty()) {
			ImmutableList<A> h = rev;
			rev = rev.tail;
			h.setTail(result);
			result = h;
		}
		return result;
	}

	public ImmutableList<A> reverse() {
		if ((isEmpty()) || (this.tail.isEmpty())) {
			return this;
		}

		ImmutableList<A> rev = empty();
		for (ImmutableList<A> l = this; l.nonEmpty(); l = l.tail) {
			rev = new ImmutableList(l.head, rev);
		}
		return rev;
	}

	public ImmutableList<A> append(A x) {
		return of(x).prependList(this);
	}

	public ImmutableList<A> appendList(ImmutableList<A> x) {
		return x.prependList(this);
	}

	public ImmutableList<A> appendList(ListBuffer<A> x) {
		return appendList(x.toList());
	}

	@NotNull
	public <A> A[] toArray(@NotNull A[] vec) {
		int i = 0;
		ImmutableList<A> l = this;
		while ((l.nonEmpty()) && (i < vec.length)) {
			vec[i] = l.head;
			l = l.tail;
			i++;
		}
		if (l.isEmpty()) {
			if (i < vec.length) {
				vec[i] = null;
			}
			return vec;
		}

		return toArray((Object[]) Array.newInstance(vec.getClass().getComponentType(), size()));
	}

	@NotNull
	public Object[] toArray() {
		return toArray(new Object[size()]);
	}

	public String toString(String sep) {
		if (isEmpty()) {
			return "";
		}

		StringBuilder buffer = new StringBuilder();
		buffer.append(this.head);
		for (ImmutableList<A> l = this.tail; l.nonEmpty(); l = l.tail) {
			buffer.append(sep);
			buffer.append(l.head);
		}
		return buffer.toString();
	}

	public String toString() {
		return toString(",");
	}

	public int hashCode() {
		ImmutableList<A> l = this;
		int h = 1;
		while (l.tail != null) {
			h = h * 31 + (l.head == null ? 0 : l.head.hashCode());
			l = l.tail;
		}
		return h;
	}

	public boolean equals(Object other) {
		if ((other instanceof ImmutableList)) {
			return equals(this, (ImmutableList) other);
		}
		if ((other instanceof List)) {
			ImmutableList<A> t = this;
			Iterator<?> it = ((List) other).iterator();
			while ((t.tail != null) && (it.hasNext())) {
				Object o = it.next();
				if (t.head == null ? o != null : !t.head.equals(o)) {
					return false;
				}
				t = t.tail;
			}
			return (t.isEmpty()) && (!it.hasNext());
		}
		return false;
	}

	public static boolean equals(ImmutableList<?> xs, ImmutableList<?> ys) {
		while ((xs.tail != null) && (ys.tail != null)) {
			if (xs.head == null) {
				if (ys.head != null) {
					return false;
				}

			} else if (!xs.head.equals(ys.head)) {
				return false;
			}

			xs = xs.tail;
			ys = ys.tail;
		}
		return (xs.tail == null) && (ys.tail == null);
	}

	public boolean contains(Object x) {
		ImmutableList<A> l = this;
		while (l.tail != null) {
			if (x == null) {
				if (l.head == null) {
					return true;
				}

			} else if (l.head.equals(x)) {
				return true;
			}

			l = l.tail;
		}
		return false;
	}

	public A last() {
		A last = null;
		ImmutableList<A> t = this;
		while (t.tail != null) {
			last = t.head;
			t = t.tail;
		}
		return last;
	}

	public static <T> ImmutableList<T> convert(Class<T> type, ImmutableList<?> list) {
		if (list == null) {
			return null;
		}
		for (Object o : list) {
			type.cast(o);
		}
		return list;
	}

	private static final Iterator<?> EMPTY_ITERATOR = new Iterator() {
		public boolean hasNext() {
			return false;
		}

		public Object next() {
			throw new NoSuchElementException();
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	};

	private static <A> Iterator<A> emptyIterator() {
		return EMPTY_ITERATOR;
	}

	@NotNull
	public Iterator<A> iterator() {
		if (this.tail == null) {
			return emptyIterator();
		}
		new Iterator() {
			private ImmutableList<A> _elements = ImmutableList.this;

			public boolean hasNext() {
				return this._elements.tail != null;
			}

			public A next() {
				if (this._elements.tail == null) {
					throw new NoSuchElementException();
				}
				A result = this._elements.head;
				this._elements = this._elements.tail;
				return result;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public A get(int index) {
		if (index < 0) {
			throw new IndexOutOfBoundsException(String.valueOf(index));
		}

		ImmutableList<A> l = this;

		int i = index;

		while ((i-- > 0) && (!l.isEmpty())) {
			l = l.tail;
		}

		if (l.isEmpty()) {
			throw new IndexOutOfBoundsException("Index: " + index + ", " + "Size: " + size());
		}

		return (A) l.head;
	}

	public boolean addAll(int index, @NotNull Collection<? extends A> c) {
		if (c.isEmpty()) {
			return false;
		}
		throw new UnsupportedOperationException();
	}

	public A set(int index, A element) {
		throw new UnsupportedOperationException();
	}

	public void add(int index, A element) {
		throw new UnsupportedOperationException();
	}

	public A remove(int index) {
		throw new UnsupportedOperationException();
	}

	public int indexOf(Object o) {
		int i = 0;
		for (ImmutableList<A> l = this; l.tail != null; i++) {
			if (l.head == null ? o == null : l.head.equals(o)) {
				return i;
			}
			l = l.tail;
		}

		return -1;
	}

	public int lastIndexOf(Object o) {
		int last = -1;
		int i = 0;
		for (ImmutableList<A> l = this; l.tail != null; i++) {
			if (l.head == null ? o == null : l.head.equals(o)) {
				last = i;
			}
			l = l.tail;
		}

		return last;
	}

	@NotNull
	public ListIterator<A> listIterator() {
		return Collections.unmodifiableList(new ArrayList(this)).listIterator();
	}

	@NotNull
	public ListIterator<A> listIterator(int index) {
		return Collections.unmodifiableList(new ArrayList(this)).listIterator(index);
	}

	@NotNull
	public List<A> subList(int fromIndex, int toIndex) {
		if ((fromIndex < 0) || (toIndex > size()) || (fromIndex > toIndex)) {
			throw new IllegalArgumentException();
		}

		ArrayList<A> a = new ArrayList(toIndex - fromIndex);
		int i = 0;
		for (ImmutableList<A> l = this; l.tail != null; i++) {
			if (i == toIndex) {
				break;
			}
			if (i >= fromIndex) {
				a.add(l.head);
			}
			l = l.tail;
		}

		return Collections.unmodifiableList(a);
	}
}
