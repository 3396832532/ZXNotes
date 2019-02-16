# Stream

* 一、引入流
* 二、使用流
* 三、用流收集数据

## 一、引入流



```java
public class Code_01_Java7AndJava8Compare {

    public static void main(String[] args) {
        // 返回 热量<400 的菜肴 的 名称, 返回结果按照从低到高排序， Java7的写法
        System.out.println(java7());
        System.out.println(java8());
    }

    static List<String> java7(){
        List<Dish> lowCaloricDishes = new ArrayList<>();
        for (Dish d : Dish.menu) {
            if (d.getCalories() < 400) {
                lowCaloricDishes.add(d);
            }
        }
        Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
            public int compare(Dish d1, Dish d2) {
                return Integer.compare(d1.getCalories(), d2.getCalories());
            }
        });
        List<String> lowCaloricDishesName = new ArrayList<>();
        for (Dish d : lowCaloricDishes) {
            lowCaloricDishesName.add(d.getName());
        }
        return lowCaloricDishesName;
    }

    static List<String> java8(){
        List<String> lowCaloricDishesName =
                Dish.menu.stream()
                        .filter(d -> d.getCalories() < 400)
                        .sorted(Comparator.comparing(Dish::getCalories))
                        .map(Dish::getName)
                        .collect(Collectors.toList());
        return lowCaloricDishesName;
    }
}
```



![](images/stream1.png)





* 概念:是数据渠道，用于操作数据源(集合，数组等)所生成的元素序列；</font> 

* "集合讲的是数据，流讲的是计算 ! "；
* 注意: ①`Stream`自己不会存储元素；② `Stream`不会改变原对象，相反，他们会返回一个持有结果的新`Stream`；③Stream操作是延迟执行的，这意味着他们会等到需要结果的时候才执行；





请注意，和迭代器类似，流只能遍历一次: 

* 遍历完之后，我们就说这个流已经被消费掉了；
* 你可以从原始数据源那里再获得一个新的流来重新遍历一遍，就像迭代器一样（这里假设它是集
  合之类的可重复的源，如果是I/O通道就没戏了）；



也就是说Stream的三个基本步骤: 

* 创建Stream : 需要一个数据源(如：集合，数组)，获取一个流；
* 中间操作: 一个中间操作链，对数据源的数据进行处理；
* 终止操作(终端操作): 一个终止操作，执行中间操作链，并产生结果；
* 中间操作就是产生的结果仍然是一个流；
* 而终止操作会从流的流水线生成结果。其结果是<font color= red>任何不是流的值，比如List、 Integer，甚至 void。