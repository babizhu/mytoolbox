import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class Test {

  void test(List<? super Fruit> list) {
    Apple apple = new Apple();
    list.add(apple);
  }

  void test1(List<? extends Orange> list) {
    for (Fruit fruit : list) {
      System.out.println(fruit);
      fruit.doSomething();
    }

    ArrayBlockingQueue<Integer> a = new ArrayBlockingQueue<>(10);
    a.offer(2);

  }

  public static void main(String[] args) {

  }
}

class Fruit {

  public void doSomething() {
    System.out.println("fruit");
  }
}


class Apple extends Fruit {

  @Override
  public void doSomething() {
    System.out.println("apple");
  }
}

class Orange extends Fruit {

}
