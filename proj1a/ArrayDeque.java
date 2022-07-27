public class ArrayDeque<T>{
  private T[] items;
  private int size;
  private int nextFirst;
  private int nextLast;
  private int RFACTOR;
  private int R;

  /** create an empty array deque */
  public ArrayDeque(){
    items = (T[]) new Object[8];
    size = 0;
    nextFirst = 4;
    nextLast = 5;
  }

  /** update the nextFirst*/
  public int nextF(int i){
    int f;
    if (i == 0){
      f = items.length - 1;
    }
    else{
      f = i - 1;
    }
    return f;
  }

  /** update the nextLast*/
  public int nextL(int i){
    int l;
    if (i == items.length - 1){
      l = 0;
    }
    else{
      l = i + 1;
    }
    return l;
  }

  /** return true if deque is empty*/
  public boolean isEmpty(){
    return size == 0;
  }

  /** return the number of items in the deque*/
  public int size(){
    return size;
  }

  /** add item to the front */
  public void addFirst(T item){
    if (size == items.length){
      large(size * RFACTOR);
    }
    items[nextFirst] = item;
    size = size + 1;
    nextFirst = nextF(nextFirst);
  }

  /** add item to the back */
  public void addLast(T item){
    if (size == items.length){
      large(size * RFACTOR);
    }
    items[nextLast] = item;
    size = size + 1;
    nextLast = nextL(nextLast);
  }

  /** print the items in the deque from first to last*/
  public void printDeque(){
    int p = nextL(nextFirst);
    while (p != nextLast){
      System.out.print(items[p] + " ");
      p = nextL(p);
    }
    System.out.println();
  }


  /** get item given the index i */
  public T get(int i){
    if (i > size){
      return null;
    }
    int p = nextL(nextFirst) + i;
    if (p >= items.length){
      p = p - items.length;
    }
    return items[p];
  }

  /** resize for lager */
  public void large(int capacity){
    T[] larger = (T[]) new Object[capacity];
    int size_0 = size;
    size = 0;
    nextFirst = 4;
    nextLast = 5;
    for (int i = 0; i < size_0; i ++){
      addFirst((T) get(i));
    }
    items = larger;
  }

  /** resize for smaller */
  public void small(int capacity){
    T[] smaller = (T[]) new Object(capacity);
    int size_0 = size;
    size = 0;
    nextFirst = 4;
    nextLast = 5;
    for (int i = 0; i < size_0; i ++){
      addFirst((T) get(i));
    }
    items = smaller;
  }

  /** remove and return the item at the front*/
  public T removeFirst(){
    if (isEmpty()){
      return null;
    }
    R = size / items.length;
    if (R < 0.25){
      small(items.length / 2);
    }

    int p = nextL(nextFirst);
    T record = items[p];
    items[p] = null;
    nextFirst = p;
    size = size - 1;
    return record;
  }

  /** remove and return the item at the back*/
  public T removeLast(){
    if (isEmpty()){
      return null;
    }
    R = size / items.length;
    if (R < 0.25){
      small(items.length / 2);
    }

    int p = nextF(nextLast);
    T record = items[p];
    items[p] = null;
    nextLast = p;
    size = size - 1;
    return record;
  }

  /** create the deep copy of other*/
  public ArrayDeque(ArrayDeque other){
    items = (T[]) new Object[8];
    size = 0;
    nextFirst = 4;
    nextLast = 5;

    for (int i = 0; i < other.size; i ++){
      addFirst((T) other.get(i));
    }
  }


}
