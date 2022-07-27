public class LinkedListDeque<T>{
  private class LLDNode{
    LLDNode prev;
    T item;
    LLDNode next;

    public LLDNode(LLDNode front, T n, LLDNode back){
      prev = front;
      item = n;
      next = back;
    }
  }

  private LLDNode sentinel;
  private int size;

  /** create an empty linked list deque*/
  public LinkedListDeque(){
    sentinel = new LLDNode(null, null, null);
    sentinel.prev = sentinel;
    sentinel.next = sentinel;
    size = 0;
  }

  /** add an item of type T to the front of the deque*/
  @Override
  public void addFirst(T item){
    LLDNode added = new LLDNode(sentinel, item, sentinel.next);
    sentinel.next.prev = added;
    sentinel.next = added;
    size = size + 1;
  }

  /** add an item of type T to the back of the deque*/
  @Override
  public void addLast(T item){
    LLDNode added = new LLDNode(sentinel.prev, item, sentinel);
    sentinel.prev.next = added;
    sentinel.prev = added;
    size = size + 1;
  }

  /** return true if deque is empty*/
  @Override
  public boolean isEmpty(){
    return size == 0;
  }

  /** return the number of items in the deque*/
  public int size(){
    return size;
  }

  /** print the items in the deque from first to last*/
  public void printDeque(){
    LLDNode p = sentinel.next;
    while (p != null){
      System.out.print(p.item + " ");
      p = p.next;
    }
    System.out.println();
  }

  /** remove and return the item at the front of the deque */
  public T removeFirst(){
    if (isEmpty()){
      return null;
    }
    T record = sentinel.next.item;
    LLDNode removed = new LLDNode(sentinel, sentinel.next.next.item, sentinel.next.next.next);
    sentinel.next = removed;
    size -= 1;
    return record;
  }
  /** remove and return the item at the back of the deque */
  public T removeLast(){
    if (isEmpty()){
      return null;
    }
    T record = sentinel.prev.item;
    LLDNode removed = new LLDNode(sentinel.prev.prev.prev, sentinel.prev.prev.item, sentinel);
    sentinel.prev = removed;
    size -= 1;
    return record;
  }

  /** get the item at the given index */
  public T get(int index){
    if (index > size - 1){
      return null;
    }
    LLDNode p = sentinel.next;
    int i = 0;
    while (i != index){
      p = p.next
    }
    return p.item;
  }

  /** create the deep copy of other */
  public LinkedListDeque(LinkedListDeque other){
    sentinel = new LLDNode(null, null, null);
    sentinel.prev = sentinel;
    sentinel.next = sentinel;
    size = 0;

    for (int i = 0; i < other.size(); i++){
      addLast((T) other.get(i));
    }
  }

  /** same as get but with recursion */
  public T getRecursive(int index){
    if (index > size - 1){
      return null;
    }
    return getR_helper(sentinel.next, index);
  }

  public T getR_helper(LLDNode h, int i){
    if (i == 0){
      return h.item;
    }
    return getR_helper(h.next, i - 1);
  }
}
