# 8. 고차 함수; 파라미터와 반환 값으로 람다 사용


고차 함수(high order function)를 사용하면 코드 중복을 없애고, 더 나은 추상화를 구축할 수 있다.

## 1. 고차 함수 정의

고차 함수는 다른 함수를 인자로 받거나, 함수를 반환하는 함수이다.

### 1-1. 함수 타입

람다를 인자로 받는 함수의 기본적인 형태는 다음과 같다.

```kotlin
// 파라미터 2개를 받아서 Int 값을 반환하는 함수
val sum = { x: Int, y: Int -> x + y }

// 아무 인자도 받지 않고, 아무 값도 반환하지 않는 함수
val action: () -> Unit = { println(42) }

// 반환 타입을 널이 될 수 있는 타입으로 지정
var canReturnNull: (Int, Int) -> Int? = { x, y -> null }

// 함수 타입 전체가 널이 될 수 있는 타입
var funOrNull: ((Int, Int) -> Int)? = null
```

보통의 함수를 정의할 때, 반환 타입에 대한 Unit을 생략할 수 있지만, 함수의 타입을 선언할 때는 반환 타입을 반드시 명시해야 한다. 또한, 일반 함수와 마찬가지로 반환 타입을 널이 될 수 있는 타입으로도 지정할 수 있다.

### 1-2. 인자로 받은 함수 호출

아래 코드의 twoAndThree는 함수를 매개변수로 받는 고차 함수이다.

```kotlin
fun twoAndThree(operation: (Int, Int) -> Int) {  
    val result = operation(2, 3)  
    println("The result is $result")  
}  
  
fun main() {  
    twoAndThree {a, b -> a + b}  
}
```

인자로 받은 함수를 호출하는 구문인 `operation(2, 3)`은 일반 함수를 호출하는 구문과 동일하며, twoAndThree의 가장 마지막 매개변수가 함수이므로, 해당 함수를 호출하는 부분에서 소괄호가 아닌 중괄호를 통해 값을 넘겨줄 수 있다.

대표적으로 많이 사용하는 filter 기능을 구현해보면 다음과 같다.

```kotlin
fun String.filter(predicate: (Char) -> Boolean): String {  
    val sb = StringBuilder()  
    for (index in indices) {  
        val element = get(index)  
        if (predicate(element)) sb.append(element)  
    }  
    return sb.toString()  
}

fun main() {
	// 출력 : abc
	println("ab1c".filter { it in 'a' .. 'z' })
}
```

위 코드에서 it은 문자열의 각 문자를 의미하고, 해당 문자가 a ~ z까지 포함되어 있는지에 대한 술어(predicate)를 넘겨주었다. 해당 술어가 true인 경우에만 StringBuilder에 추가해준뒤, 반환한다.

### 1-3. 자바에서 코틀린 함수 타입 사용

컴파일된 코드 안에서 함수 타입의 변수는 FunctionN 인터페이스를 구현하는 객체를 저장한다. `Function0<R>`은 인자가 없는 함수, `Function1<P1, R>`은 인자가 하나인 함수 등의 인터페이스를 제공한다. 때문에 함수 타입을 사용하는 코틀린 함수를 자바에서도 쉽게 호출할 수 있다.

자바8 이전에는 람다가 존재하지 않았기에, FunctionN 인터페이스의 invoke 메소드를 구현하는 무명 클래스를 넘겨야했다.

```kotlin
fun processTheAnswer(f: (Int) -> Int) {  
    println(f(42))  
}
```

```java
public class JavaFunctionTest {  
    public static void main(String[] args) {
        processTheAnswer(new Function1<Integer, Integer>() {  
            @Override  
            public Integer invoke(Integer number) {  
                System.out.println(number);  
                return number + 1;  
            }  
        });  
    }  
}
```

자바8 이후로는 간단하게 람다를 넘기면 자동으로 함수 타입의 값으로 변환된다.

```java
public class JavaFunctionTest {  
    public static void main(String[] args) {  
        processTheAnswer(number -> number + 1);  
    }  
}
```

만약 코틀린 코드의 반환 타입이 Unit인 경우 자바에서는 void가 아닌 코틀린과 동일한 Unit에 대한 타입을 넘겨줘야 한다.

```java
List<String> strings = new ArrayList<>();  
strings.add("42");  
  
CollectionsKt.forEach(strings, s -> {  
    System.out.println(s);  
    return Unit.INSTANCE;  
});
```

### 1-4. 타입 파라미터 : 디폴트 값을 지정한 함수 / 널이 될 수 있는 함수

이전에 구현한 joinToString은 특정 요소를 제어할 수 없다. 이를 위해 함수 파라미터에 함수를 받도록 하면, 매번 람다를 넘겨야하는 불편함이 생긴다. 이는, 해당 파라미터에 디폴트 값을 넣어서 해결할 수 있다.

```kotlin
private fun <T> Collection<T>.joinToString(  
    separator: String = ", ",  
    prefix: String = "",  
    postfix: String = "",  
    transform: (T) -> String = { it.toString() }  
): String {  
    val result = StringBuilder(prefix)  
    for ((index, element) in this.withIndex()) {  
        if (index > 0) result.append(separator)  
        result.append(transform(element))  
    }  
    result.append(postfix)  
    return result.toString()  
}
```

위처럼 함수 파라미터를 선언한 뒤, 람다를 티폴트 값인 toString으로 지정해주고, 만약 호출부에서 함수를 넘겨줬을 경우, 해당 함수를 호출한다.

```kotlin
val letters = listOf("Alpha", "Beta")  

// 출력 : Alpha, Beta
println(letters.joinToString())  
// 출력 : alpha, beta
println(letters.joinToString { it.lowercase() })  
// 출력 : ALPHA! BETA!
println(letters.joinToString(separator = "! ", postfix = "! ") { it.uppercase() })
```

만약 널이 될 수 있는 함수 타입을 사용할 경우, null 여부를 명시적으로 검사한 뒤, 사용해야 한다.

```kotlin
fun foo(callback: (() -> Unit)?) {  
    if(callback != null) callback()  
}
```

위 방식도 좋지만, 앞서 말한 것과 같이 함수 파라미터는 컴파일 시 FunctionN 인터페이스로 변환되고, 그 안에 invoke 함수가 있기 때문에 다음과 같은 방법으로도 사용이 가능하다.

```kotlin
fun foo(callback: (() -> Unit)?) {  
	callback?.invoke()
}
```

이런 안전한 호출을 사용해 joinToString을 수정해보자.

```kotlin
private fun <T> Collection<T>.joinToString(  
    separator: String = ", ",  
    prefix: String = "",  
    postfix: String = "",  
    transform: ((T) -> String)? = null  
): String {  
    val result = StringBuilder(prefix)  
    for ((index, element) in this.withIndex()) {  
        if (index > 0) result.append(separator)
        // null이 아닐 경우 invoke / null일 경우 toString()
        val str = transform?.invoke(element) ?: element.toString()  
        result.append(str)  
    }  
    result.append(postfix)  
    return result.toString()  
}
```

### 1-5 함수를 함수에서 반환

```kotlin
private enum class Delivery { STANDARD, EXPEDITED }  
  
private class Order(val itemCount: Int)  
  
private fun getShippingCostCalculator(delivery: Delivery) : (Order) -> Double {  
    if (delivery == Delivery.EXPEDITED) {  
        return { order -> 6 + 2.1 * order.itemCount }  
    }  
    return { order -> 1.2 * order.itemCount }  
}  
  
fun main() {  
    val calc = getShippingCostCalculator(Delivery.EXPEDITED)  
    // 출력 : Shipping costs 12.3
    println("Shipping costs ${calc(Order(3))}")  
}
```

```java
public static final void main() {  
   Function1 calc = getShippingCostCalculator(Delivery.EXPEDITED);  
   String var1 = "Shipping costs " + 
				((Number)calc.invoke(new Order(3))).doubleValue();  
   System.out.println(var1);  
}
```

위 코드에서 `getShippingCostCalculator` 함수는 또 다른 함수를 반환한다. 때문에 이를 호출하는 부분인 `calc`의 타입은 `(Order) -> Double`이 되어 변수처럼 보이지만, 함수로 사용이 가능하다. 해당 코드를 자바 코드로 디컴파일하면 calc의 타입은 Function1로 변경된 것을 볼 수 있다.

### 1-6. 람다를 활용한 중복 제거

함수 타입과 람다 식은 재활용하기 좋은 코드를 만들 때 쓸 수 있는 훌륭한 도구이다. 웹 사이트 방문 기록을 분석하는 예시를 통해 살펴보자.

```kotlin
private data class SiteVisit(  
    val path: String,  
    val duration: Double,  
    val os: OS  
)  
  
private enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }  
  
private val log = listOf(  
    SiteVisit("/", 34.0, OS.WINDOWS),  
    SiteVisit("/", 22.0, OS.MAC),  
    SiteVisit("/login", 12.0, OS.WINDOWS),  
    SiteVisit("/signup", 8.0, OS.IOS),  
    SiteVisit("/", 16.3, OS.ANDROID)  
)  
```

```kotlin
fun main() {  
    val averageWindowsDuration = log  
        .filter { it.os == OS.WINDOWS }  
        .map(SiteVisit::duration)  
        .average()  
    // 출력 : 23.0  
    println(averageWindowsDuration)  
}
```

윈도우 환경의 방문 정보를 분석하려면 위 코드처럼 작성하면 된다. 하지만 윈도우가 아닌 맥 혹은 안드로이드에 대한 정보를 구하기 위해서는 동일한 코드로 작성한 뒤, `OS.WINDOWS`에 대한 부분만 변경하면 된다. 이런 중복 코드를 줄이기 위해서  OS를 파라미터로 뽑아 함수로 변경하면 된다.

```kotlin
private fun List<SiteVisit>.averageDurationFor(os: OS) = filter { it.os == os }  
    .map(SiteVisit::duration)  
    .average()  
  
fun main() {  
    val averageWindowsDuration = log.averageDurationFor(OS.WINDOWS)  
    val averageMacDuration = log.averageDurationFor(OS.MAC)  
    // 출력 : 23.0  
    println(averageWindowsDuration)  
    // 출력 : 22.0  
    println(averageMacDuration)  
}
```

더 나아가 특정 OS와 특정 페이지를 지정할 수도 있다.

```kotlin
private fun List<SiteVisit>.averageDurationFor(predicate: (SiteVisit) -> Boolean) = filter(predicate)  
    .map(SiteVisit::duration)  
    .average()  
  
fun main() {  
    val averageWindowsDuration = log.averageDurationFor {  
        it.os == OS.IOS && it.path == "/signup"  
    }  
    val averageMacDuration = log.averageDurationFor {  
        it.os == OS.MAC && it.path == "/"  
    }  
    // 출력 : 8.0  
    println(averageWindowsDuration)  
    // 출력 : 22.0  
    println(averageMacDuration)  
}
```

> 객체 지향의 디자인 패턴을 함수 타입과 람다 식을 사용해 단순화할 수 있다.
> 만약 전략 패턴을 사용할 때, 람다 식이 없다면 인터페이스와 구현 클래스를 통해 전략을 정의해야 한다. 하지만 람다를 사용할 경우 여러 전략을 인터페이스가 아닌 함수로서 전달할 수 있게 된다.

## 2. 인라인 함수 : 람다의 부가 비용 없애기

코틀린이 람다를 무명 클래스로 컴파일하지만 람다 식을 사용할 때마다 새로운 클래스가 만들어지지는 않는다. 만약, 람다가 변수를 포획하면 람다가 생성되는 시점마다 새로운 무명 클래스 객체가 만들어진다. 이럴 경우에는 실행 시점에 무명 클래스 생성에 따른 부가 비용이 들게 된다. 람다를 사용하는 구현은 사실상 덜 효율적인 것이다.

inline 변경자를 사용하면 코틀린 컴파일러는 함수 본문에 해당하는 바이트코드로 바꿔, 자바 일반 명령문만큼 효율적인 코드를 생성하게 할 수 있다.

### 2-1. 인라이닝이 작동하는 방식

어떤 함수를 inline으로 선언하면, 그 함수의 본문이 inline된다. 즉, 함수를 호출하는 코드를 함수를 호출하는 바이트코드 대신에 함수 본문을 번역한 바이트 코드로 컴파일하는 것이다.

아래 예제 코드는 다중 스레드 환경에서 어떤 공유 자원에 대한 동시 접근을 막기 위해 Lock 함수를 사용해 객체를 잠그고, 주어진 코드 블록을 실행한 다음 Lock을 해제하는 코드이다.

```kotlin
inline fun <T> synchronized(lock: Lock, action: () -> T): T {  
    lock.lock()  
    try {  
        return action()  
    } finally {  
        lock.unlock()  
    }  
}  
  
fun foo(l: Lock) {
    println("Before sync")  
    synchronized(l) {  
        println("Action")  
    }  
    println("After sync")  
}
```

`synchronized` 함수를 inline으로 선언해, 이를 호출하는 코드는 모두 자바의 `synchronized`와 같아진다. 위 코드를 자바 코드로 디컴파일 할 경우 다음과 같은 코드가 나온다.

```java
public static final void foo(@NotNull Lock l) {  
   Intrinsics.checkNotNullParameter(l, "l");  
   String var1 = "Before sync";  
   System.out.println(var1);  
   int $i$f$synchronized = false;  
   l.lock();  
  
   try {  
      int var2 = false;  
      String var3 = "Action";  
      System.out.println(var3);  
      Unit var7 = Unit.INSTANCE;  
   } finally {  
      l.unlock();  
   }  
  
   var1 = "After sync";  
   System.out.println(var1);  
}
```

`synchronized` 함수를 호출을 하는 것이 아닌 함수 안에 있는 내용이 그대로 바이트코드로 변환된 것을 알 수 있다. 또한, 해당 함수에 전달된 람다는 호출하는 코드(synchronized) 정의의 일부분으로 간주되기 때문에 코틀린 컴파일러는 무명 클래스로 감싸지 않고, 해당 본문을 함께 인라이닝 시킨다.

인라인 함수를 호출하면서 람다를 넘기는 대신, 함수 타입의 변수도 함께 넘길 수 있다.

```kotlin
class LockOwner(val lock: Lock) {  
    fun runUnderLock(body: () -> Unit) {
	    // 람다 대신, 함수 타입의 매개 변수를 인자로 넘긴다.
        synchronized(lock, body)  
    }  
}
```

하지만 이럴 경우 인라인 함수를 호출하는 위치에서는 변수에 저장된 람다의 코드를 알 수 없기 때문에 본문 자체는 인라이닝 되지 않고, `synchronized` 함수의 본문만 인라이닝 된다.

```java
public final class LockOwner {  
   @NotNull  
   private final Lock lock;  

   public final void runUnderLock(@NotNull Function0 body) {  
      Intrinsics.checkNotNullParameter(body, "body");  
      Lock lock$iv = this.lock;  
      int $i$f$synchronized = false;  
      lock$iv.lock();  
  
      try {  
         Object var4 = body.invoke();  
      } finally {  
         lock$iv.unlock();  
      }  
  
   }
}
```

### 2-2. 인라인 함수의 한계

위 `foo` 함수와 같이, 함수에 직접 람다 식의 본문을 전달할 경우 인라이닝할 수 있다. 하지만, 람다가 본문에 직접 펼쳐지기 때문에 람다를 본문에 사용하는 방식이 한정될 수 밖에 없다.

만약 `runUnderLock`과 같이 파라미터로 호출할 경우 파라미터로 받은 람다를 어딘가에 저장하고, 해당 변수를 사용할 때, 람다를 표현하는 객체는 어딘가에 존재해야하므로 인라이닝할 수 없다.

아래 코드는 모든 시퀀스 원소에 특정 람다를 적용한 뒤, 새 시퀀스를 반환하는 코드이다.

```kotlin
fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R> {  
    return TransformingSequence(this, transform)  
}
```

위 map 함수는 transform 파라미터로 전달받은 함수 값을 호출하지 않고, 반환하는 클래스 생성자에게 값을 넘긴다. `TransformingSequence`는 전달 받은 람다를 프로퍼티에 저장한다. 이런 기능을 지원하려면, 인라이닝을 하지 않는 일반적인 함수로 만들어야 한다. 즉, transform을 함수 인터페이스를 구현하는 무명 클래스 인스턴스로 만들어야만 하는 것이다.

만약 둘 이상의 람다를 인자로 받는 상태에서 일부만 인라이닝하고 싶을 경우 noinline 변경자를 사용하면 된다.

```kotlin
inline fun foo(inlined: () -> Unit, noinline notInline: () -> Unit) {  
      
}
```

### 2-3. 컬렉션 연산 인라이닝

Person의 리스트를 걸러내는 코드를 확인해보자.

```kotlin
fun main() {  
    val people = listOf(Person("Alice", 29), Person("Bob", 31))  
  
    // 람다를 사용해 거르기  
    println(people.filter { it.age < 30 })  
  
    // 컬렉션을 직접 거르기  
    val result = mutableListOf<Person>()  
    for (person in people) {  
        if(person.age < 30) result += person  
    }  
    println(result)  
}
```

filter 함수의 내부를 살펴보면 다음과 같이 되어있다.

```kotlin
public inline fun <T> Iterable<T>.filter(predicate: (T) -> Boolean): List<T> {  
    return filterTo(ArrayList<T>(), predicate)  
}

public inline fun <T, C : MutableCollection<in T>> Iterable<T>.filterTo(destination: C, predicate: (T) -> Boolean): C {  
    for (element in this) if (predicate(element)) destination.add(element)  
    return destination  
}
```

위 코드와 같이 컬렉션을 직접 거르는 방식과 거의 비슷한 형태이며, 자바 코드로 디컴파일할 경우 위 인라인 함수가 그대로 메인 함수에 들어오기 때문에 거의 비슷하다는 것을 알 수 있다.

```java
public static final void main() {  
   List people = CollectionsKt.listOf(new Person[]{new Person("Alice", 29), new Person("Bob", 31)});  
   Iterable $this$filter$iv = (Iterable)people;  
   int $i$f$filter = false;  
   Collection destination$iv$iv = (Collection)(new ArrayList());  
   int $i$f$filterTo = false;  
   Iterator var6 = $this$filter$iv.iterator();  
   // 람다를 사용한 코드
   while(var6.hasNext()) {  
      Object element$iv$iv = var6.next();  
      Person it = (Person)element$iv$iv;  
      int var9 = false;  
      if (it.getAge() < 30) {  
         destination$iv$iv.add(element$iv$iv);  
      }  
   }  
   // 직접 작성한 코드
   List result = (List)destination$iv$iv;  
   System.out.println(result);  
   result = (List)(new ArrayList());  
   Iterator var3 = people.iterator();  
  
   while(var3.hasNext()) {  
      Person person = (Person)var3.next();  
      if (person.getAge() < 30) {  
         destination$iv$iv = (Collection)result;  
         destination$iv$iv.add(person);  
      }  
   }  
  
   System.out.println(result);  
}
```

이처럼 코틀린 컬렉션 라이브러리에서 제공하는 인라인 함수는 성능에 크게 신경쓰지 않아도 된다. 그럼 filter와 map을 메소드 연쇄로 사용할 경우는 어떨까?

```kotlin
println(people.filter { it.age < 30 }.map(Person::name))
```

```java
while(var6.hasNext()) {  
   item$iv$iv = var6.next();  
   Person it = (Person)item$iv$iv;  
   var9 = false;  
   if (it.getAge() < 30) {  
      destination$iv$iv.add(item$iv$iv);  
   }  
}  
  
$this$map$iv = (Iterable)((List)destination$iv$iv);  
$i$f$map = false;  
destination$iv$iv = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10)));  
$i$f$mapTo = false;  
var6 = $this$map$iv.iterator();  
  
while(var6.hasNext()) {  
   item$iv$iv = var6.next();  
   var9 = false;  
   String var11 = ((Person)item$iv$iv).getName();  
   destination$iv$iv.add(var11);  
}
```

map 함수 또한 인라인 함수기 때문에 위와 같은 코드로 디컴파일 되는 것을 확인할 수 있다. 위 코드를 보면 map을 하기 위해 중간 리스트를 만드는 것을 볼 수 있다.

만약 처리할 원소가 많을 경우에는 그 만큼의 부가 비용이 들게 되어 부담이 될 수 있다. 이런 상황에서 지연 계산을 사용하는 asSequence를 사용하면, 중간 리스트로 인한 부가 비용을 줄일 수 있다. 하지만 시퀀스는 람다를 저장해야 하므로 람다를 인라인하지 않는다.

> 크기가 큰 컬렉션은 시퀀스를 통해 성능을 개선할 수 있지만, 작은 크기를 가진 컬렉션을 시퀀스로 사용할 경우 오히려 성능을 악화시킬 수 있다.

### 2-4. 함수를 인라인으로 선언해야 하는 경우

모든 함수를 인라인으로 만든다고 성능이 좋아지는 것은 아니다. 사용하더라도, 람다를 인자로 받는 함수만 성능이 좋아질 가능성이 높다.

일반 함수의 경우 JVM에서 기계어 코드로 번역하는 JIT을 통해 가장 이익이 되는 방향으로 호출을 인라이닝한다. 람다를 인자로 받는 함수를 인라이닝할 경우 아래와 같은 부가 비용을 없앨 수 있다.

- 함수 호출 비용
- 람다를 표현하는 클래스 및 람다 인스턴스에 해당하는 객체를 만들지 않을 수 있음

inline 변경자를 함수에 붙일 때에는 최대한 작은 크기로 만드는 것이 좋다. 만약 해당 함수의 크기가 커질 경우 바이트코드 또한 전체적으로 커질 수 있기 때문이다.

### 2-5. 자원 관리를 위해 인라인된 람다 사용

람다로 중복을 없앨 수 있는 패턴 중 한 가지는 자원을 획득하고, 작업을 마친 후 자원을 해제하는 자원 관리이다. 해당 패턴에서는 보통 try/finally를 사용한다.

```kotlin
fun main() {  
    val l: Lock = TODO()  
  
    l.withLock {  
        // 락에 의해 보호되는 자원 사용  
    }  
}
```

```kotlin
@kotlin.internal.InlineOnly  
public inline fun <T> Lock.withLock(action: () -> T): T {  
    contract { callsInPlace(action, InvocationKind.EXACTLY_ONCE) }  
    lock()  
    try {  
        return action()  
    } finally {  
        unlock()  
    }  
}
```

withLock의 코드를 살펴보면 lock()을 통해 다른 스레드가 동시에 접근하지 못하도록 막고, try를 실행한 뒤, unlock()을 통해 잠금을 해제한다. 위 코드 또한 Inline으로 정의되어 있어 자바 코드로 디컴파일할 경우 다음과 같이 변환된다.

```java
public static final void main() {  
   Lock l = (Lock)(new ReentrantLock());  
   l.lock();  
  
   try {  
      boolean var2 = false;  
      Unit var5 = Unit.INSTANCE;  
   } finally {  
      l.unlock();  
   }  
  
}
```

파일과 같은 자원을 관리할 때에는 자바 7부터 제공하는 try-with-resources를 사용한다.

```java
static String readFirstLineFromFile(String path) throws IOException {  
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {  
        return br.readLine();  
    }  
}
```

try-with-resources는 Closeable 인터페이스를 구현한 구현체에 대해서만 사용할 수 있는 기능이다. 사용할 자원은 try에 넣어준 뒤, 해당 자원에 대한 사용을 마치면 close() 함수를 호출해 자원을 해제한다. 코틀린에도 이러한 기능을 제공하는 use 함수가 있다.

```kotlin
fun readFirstLineFromFile(path: String): String {  
    BufferedReader(FileReader(path)).use { br ->  
        return br.readLine()  
    }  
}
```

use 함수 또한, inline으로 정의되어 있으며, 자원을 사용하거나, 람다 안에서 예외가 발생한 경우에도 자원을 확실히 닫아주는 것을 볼 수 있다.

```kotlin
@InlineOnly  
public inline fun <T : Closeable?, R> T.use(block: (T) -> R): R {  
    contract {  
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)  
    }  
    var exception: Throwable? = null  
    try {  
        return block(this)  
    } catch (e: Throwable) {  
        exception = e  
        throw e  
    } finally {  
        when {  
            apiVersionIsAtLeast(1, 1, 0) -> this.closeFinally(exception)  
            this == null -> {}  
            exception == null -> close()  
            else ->  
                try {  
                    close()  
                } catch (closeException: Throwable) {  
                    // cause.addSuppressed(closeException) // ignored here  
                }  
        }  
    }  
}
```

### 2-6. (추가) reified 키워드

제네릭 타입은 런타임 시 타입이 소거가 된다. 때문에 런타임에 특정 제네릭의 타입을 알고 싶을 경우에는 다음과 같이 클래스 타입을 함수 인자에 넘겨 값을 필드에 저장하게 된다.

```kotlin
private class MyList<T : Any>(val type: KClass<T>)
```

이렇게 타입을 저장하면 런타임에도 해당 클래스 타입을 이용할 수 있게 된다. 만약 이런 식으로 클래스 타입을 넘겨주지 않고, `T::class`로 호출할 경우 다음과 같은 컴파일 에러가 발생한다.

```kotlin
private class MyList<T : Any> {  
	fun getType() {  
		// Cannot use 'T' as reified type parameter. Use a class instead.
		// 'T'를 reified type 매개변수로 사용할 수 없습니다. 대신 클래스를 사용하십시오.
		return T::class  
	}  
}
```

컴파일 에러 메시지에서 보이는 것처럼 `T` 자체를 `reified`로서 활용할 수 없다고 한다. 여기서 말하는 `reified`가 무엇인지 확인해보자.

> 배열은 런타임에도 자신이 담기로 한 원소의 타입을 인지하고 확인한다. `Long[]`에 `String`을 넣으려고 하면 `ArrayStoreException` 에러가 발생한다.

> 제네릭은 타입 정보가 런타임에 소거(erasure)된다. 원소 타입을 컴파일 타임에만 검사하며, 런타임에는 알수조차 없게 되는 것이다. 즉, 여기서 말하는 실체화란 타입의 정보가 런타임에도 계속 남아있느냐를 의미하는 것이다.

위 두 내용은 Effective Java 3/E의 아이템28(배열보다는 리스트를 사용하라.)의 일부 내용이다. 정리해보면, 제네릭은 컴파일 타임에만 타입 정보를 들고 있으며, 런타임 시 타입 정보가 소거된다. 즉, 위에서 `T::class`를 사용할 수 없는 이유 또한 런타임 시 타입 정보가 사라지기 때문이다.

만약 클래스일 경우 처음 예시와 같이 타입 정보를 함께 저장해줄 수 있지만, 함수를 사용할 경우 타입 정보까지 받기엔 부담스러울 수 있다.

```kotlin
inline fun <T: Any> printGenerics(value: T, type: KClass<T>) {  
	println("${type} = ${value}")  
}  
  
fun main() {  
	printGenerics(2200000000, Long::class)  
	printGenerics(2100000000, Int::class)
}
```

이런 상황에서 `reified` 키워드를 사용할 경우 T의 타입 정보를 보관할 수 있다.

```kotlin
inline fun <reified T> printGenerics(value: T) {  
	println("${T::class} = ${value}")  
}
```

클래스의 경우 해당 타입 정보를 클래스 내부 프로퍼티에 저장하면 되기 때문에 대부분의 `reified` 키워드는 `inline` 함수와 함께 사용한다.

## 3. 고차 함수 안에서 흐름 제어

### 3-1. 람다 안의 return문 : 람다를 둘러싼 함수로부터 반환

리스트에서 특정 조건에 만족하는 객체가 있는지 확인하고, 출력한 뒤, 루프를 멈추는 코드가 있다고 가정하자.

```kotlin
private fun lookForAlice(people: List<Person>) {  
    for (person in people) {  
        if (person.name == "Alice") {  
            println("Found")  
            return  
        }  
    }  
}  
  
fun main() {  
    val people = listOf(Person("Alice", 29), Person("Bob", 31))  
    lookForAlice(people)  
}
```

위 코드를 만약 forEach로 변경하면 어떻게 될까?

```kotlin
private fun lookForAlice(people: List<Person>) {  
    people.forEach { person ->  
        if (person.name == "Alice") {  
            println("Found")  
            return  
        }  
    }
}
```

위 코드 또한 앞서 봤던 for문과 동일하다. 하지만 forEach는 함수이다. 해당 구현을 살펴보면 다음과 같다.

```kotlin
@kotlin.internal.HidesMembers  
public inline fun <T> Iterable<T>.forEach(action: (T) -> Unit): Unit {  
	for (element in this) action(element)  
}
```

위 코드처럼 action이라는 함수에 우리가 작성한 return이 들어가있는 것이다. 이렇게 람다 안에서 return을 사용할 경우 람다로부터만 반환되는 것이 아닌 해당 람다를 호출하는 forEach에서 반환을 하게 되는 것이다. 이러한 코드가 가능한 이유는 바로 forEach가 인라인 함수기 때문이다. 반대로 인라이닝되지 않은 함수에서는 return을 사용할 수 없다. 이렇게 자신을 둘러싸고 있는 블록보다 더 바깥에 있는 다른 블록을 반환하게 만드는 return문을 넌 로컬(non-local) return이라고 부른다.

### 3-2. 람다로부터 반환 : 레이블을 사용한 return

레이블을 사용하면 람다 식에도 로컬(local) return을 사용할 수 있다. 여기서 사용되는 return은 for 루프의 break와 비슷하다.

```kotlin
private fun lookForAliceLabel(people: List<Person>) {  
	people.forEach label@{ person ->  
		if(person.name == "Alice") return@label  
	}  

	// 위와 동일한 코드
	people.forEach { person ->  
		if(person.name == "Alice") return@forEach
	}  
}
```

로컬과 넌로컬을 구분하기 위해서는 꼭 레이블을 사용해야 한다. 레이블을 붙이기 귀찮을 경우 함수 이름으로 반환해도 문제없다. 하지만 `label@`과 같이 레이블을 지정해줄 경우 기존에 사용하는 `@forEach`로 반환할 수 없게 된다.

### 3-3. 무명 함수 : 기본적으로 로컬 return

```kotlin
private fun lookForAliceAnonymousFunction(people: List<Person>) {  
	people.forEach(fun(person) {  
		if(person.name == "Alice") return  
		println("${person.name} is not Alice")  
	})  
}
```

위 함수를 보면 forEach 안에 이름이 없는 함수를 정의했다. 무명 함수는 일반 함수와 달리 함수 이름과 파라미터 타입을 생략할 수 있다. 또한 무명 함수는 식을 본문으로 하는 함수 형태로도 사용할 수 있다.

```kotlin
private fun lookForUnderAge30(people: List<Person>) {  
	// 일반적인 형태의 함수
	people.filter(fun (person): Boolean {  
		return person.age < 30  
	})  
	// 식이 본문인 함수
	people.filter(fun(person) = person.age < 30)
}
```

람다 식 자체는 fun을 정의하지 않았으므로, 다른 함수를 반환시키지 않는다. 하지만 위 예제에서 사용된 return은 당연히 해당 return이 들어있는 함수에 대한 반환이므로, 로컬 return이다.

<center>

<img src="https://private-user-images.githubusercontent.com/82663161/302009471-5d36fbf3-f4e3-4c37-a72f-5b70a29b698f.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3MDY5Mjk4OTMsIm5iZiI6MTcwNjkyOTU5MywicGF0aCI6Ii84MjY2MzE2MS8zMDIwMDk0NzEtNWQzNmZiZjMtZjRlMy00YzM3LWE3MmYtNWI3MGEyOWI2OThmLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNDAyMDMlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjQwMjAzVDAzMDYzM1omWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWE1N2Y5NDlkNzVhODQwZjAxZTY2YzkwNzM2NDE1ODNlMjhlZmY5ODZmNjYyNmU5ODk5MmIwZjcyZDMzNjM3YTEmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0JmFjdG9yX2lkPTAma2V5X2lkPTAmcmVwb19pZD0wIn0.t-7s_uvHPs3LV1im8fH5KQ3GQxD2GHaawKlUey_6dm0"/>

</center>

무명 함수는 일반 함수와 비슷해 보이지만, 실제로는 람다 식에 대한 문법적 편의일 뿐이다.
