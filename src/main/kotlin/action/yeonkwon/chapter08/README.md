# 8장 고차 함수: 파라미터와 반환 값으로 람다 사용

8장에서는 람다를 인자로 받거나 반환하는 함수인 고차함수(higher-order function)에 대해 알아본다.
고차 함수로 코드를 더 간결하게 다듬고 코드 중복을 없애고 더 나은 추상화를 구축하는 방법을 살펴본다.

또한 람다를 사용함에 따라 발생할 수 있는 성능상 부가 비용을 없애고 람다 안에서 더 유연하게 흐름을 제어할 수 있는
코틀린 특성인 인라인 함수에 대해 설명한다.

## 8.1 고차 함수 정의
고차 함수는 다른 함수를 인자로 받거나 함수를 반환하는 함수다.
예를 들어 표준 라이브러리 함수인 `filter`는 술어 함수를 인자로 받으므로 고차 함수다.

### 8.1.1 함수 타입
람다를 인자로 받은 함수를 정의하려면 먼저 람다 인자의 타입을 어떻게 선언할 수 있는지 알아야 한다.
인자 타입을 정의하기 전에 더 단순한 경우로 람다를 로컬 변수에 대입하는 경우를 살펴보자.

코틀린의 타입 추론으로 인해 변수 타입을 지정하지 않아도 람다를 변수에 대입할 수 있음을 이미 알 것이다.

```kotlin
val sum = { x: Int, y: Int -> x + y }
val action = { println(42) }
```

이 경우 컴파이러는 변수 `sum`의 타입을 `(Int, Int) -> Int`로 추론하고, 변수 `action`의 타입을 `() -> Unit`으로 추론한다.

함수 타입에서 파라미터 이름을 지정할 수도 있다.

```kotlin
fun performRequest(
    url: String,
    callback: (code: Int, content: String) -> Unit
) {
    /* ... */
}

val url = "http://kotl.in"
performRequest(url) { code, content -> /* ... */ }
performRequest(url) { code, page -> /* ... */ }
```

파라미터 이름은 타입 검사 시 무시된다. 이 함수 타입의 람다를 정의할 때 파라미터 이름이
꼭 함수 타입 선언의 파라미터 이름과 일치하지 않아도 된다. 하지만 함수 타입에 인자 이름을 추가하면
코드 가독성이 좋아지고, IDE는 그 이름ㅇ르 코드 완성에 사용할 수 있다.

### 8.1.2 인자로 받은 함수 호출

```kotlin
fun twoAndThree(operation: (Int, Int) -> Int) {
    val result = operation(2, 3)
    println("The result is $result")
}

twoAndThree { a, b -> a + b } // The result is 5
twoAndThree { a, b -> a * b } // The result is 6
```

인자로 받은 함수를 호출하는 구문은 일반 함수를 호출하는 구문과 같다.

```kotlin
fun String.filter(predicate: (Char) -> Boolean): String {
    val sb = StringBuilder()
    for (index in 0 until length) {
        val element = get(index)
        if (predicate(element)) sb.append(element)
    }
    return sb.toString()
}

println("ab1c".filter { it in 'a'..'z' }) // abc
```

filter 함수 구현은 단순하다. `filter`는 문자열의 각 문자를 술어에 넘겨서 반환 값이 `true`면
결과를 담는 `StringBuilder` 뒤에 그 문자를 추가한다.

### 8.1.3 자바에서 코틀린 함수 타입 사용

컴파일된 코드 안에서 함수 타입은 일반 인터페이스로 바뀐다. 즉 함수 타입의 변수는 FunctionN 인터페이스를 구현하는 객체를 저장한다.
코틀린 표준 라이브러리는 함수 인자의 개수에 따라 Function0<R>(인자가 없는 함수), Function1<P1, R>(인자가 하나인 함수), Function2<P1, P2, R>(인자가 둘인 함수)
등의 인터페이스를 제공한다.

각 인터페이스에는 `invoke` 메소드가 정의가 하나 들어있다.


```kotlin
/* 코틀린 선언 */
fun processTheAnswer(f: (Int) -> Int) {
    println(f(42))
}

/* 자바에서 사용 */
processTheAnswer(number -> number + 1);
// 43
```

디컴파일 된 자바 코드
```java
public static final void processTheAnswer(@NotNull Function1 f) {
      Intrinsics.checkNotNullParameter(f, "f");
      int var1 = ((Number)f.invoke(42)).intValue();
      System.out.println(var1);
   }
```

### 8.1.4 디폴트 값을 지정한 함수 타입 파라미터나 널이 될 수 있는 함수 파라미터

```kotlin
fun <T> Collection<T>.joinToString(
    separator: String = ", ",
    prefix: String = "",
    postfix: String = "",
    transform: (T) -> String = { it.toString() } // 디폴트 값 지정
): String {
    val result = StringBuilder(prefix)
    for ((index, element) in this.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(transform(element)) // 함수 타입 파라미터 호출
    }
    result.append(postfix)
    return result.toString()
}

val letters = listOf("Alpha", "Beta")
```

이 함수는 제네릭 함수다. 따라서 컬렉션의 원소 타입을 표현하는 `T`를 타입 파라미터로 받는다.
`transform` 람다는 그 `T`타입의 값을 인자로 받는다.

함수 타입에 대한 디폴트 값을 선언할 때 특별한 구문이 필요하지는 않다. 다른 디폴트 파라미터
값과 마찬가지로 함수 타입에 대한 디폴트 값 선언도 = 뒤에 람다를 넣으면 된다.

다른 접근 방법으로 널이 될 수 있는 함수 타입을 사용할 수도 있다. 널이 될 수 있는 함수 타입으로
함수를 받으면 그 함수를 직접 호출할 수 없다는 점에 유의하라.

코틀린은 NPE가 발생할 가능성이 있으므로 그런 코드의 컴파일을 거부할 것이다. `null` 여부를
명시적으로 검사하는 것도 한 가지 해결 방법이다.

```kotlin
fun foo(callback: (() -> Unit)?) { // 널이 될 수 있는 함수 타입
    if (callback != null) {
        callback()
    }
}
```

### 8.1.5 함수를 함수에서 반환

함수가 함수를 반환할 필요가 있는 경우보다는 함수가 함수를 인자로 받아야 할 필요가
있는 경우가 훨씬 더 많다. 하지만 함수를 반환하는 함수도 여전히 유용하다.

```kotlin
enum class Delivery { STANDARD, EXPEDITED }

class Order(val itemCount: Int)

fun getShippingCostCalculator(
    delivery: Delivery
): (Order) -> Double {
    if (delivery == Delivery.EXPEDITED) {
        return { order -> 6 + 2.1 * order.itemCount }
    }
    return { order -> 1.2 * order.itemCount }
}

val calculator = getShippingCostCalculator(Delivery.EXPEDITED)
println("Shipping costs ${calculator(Order(3))}") // Shipping costs 12.3
```

다른 함수를 반환하는 함수를 정의하려면 함수의 반환 타입으로 함수 타입을 지정해야한다.

함수를 반환하는 함수가 유용한 경우를 하나 더 살펴보자.

GUI 연락처 관리 앱을 만드는 데 UI의 상태에 따라 어떤 연락처 정보를 표시할 지 결정해야 할 필요가
있다고 가정하자.
사용자가 UI의 입력 창에 입력한 문자열과 매치되는 연락처만 화면에 표시하되 설정에 따라 전화번호
정보가 없는 연락처를 제외시킬 수도 있고 포함시킬 수도 있어야 한다.

연락처 목록 표시 로직과 연락처 필터링 UI를 분리하기 위해 연락처 목록을 필터링 하는 술어 함수를 만드는 함수를
정의할 수 있다.
이 술어 함수는 이름과 성의 접두사를 검사하고 필요하면 전화번호가 연락처에 있는지도 검사한다.

```kotlin
data class Person(
    val firstName: String,
    val lastName: String,
    val phoneNumber: String?
)

class ContactListFilters {
    var prefix: String = ""
    var onlyWithPhoneNumber: Boolean = false

    fun getPredicate(): (Person) -> Boolean { // 함수를 반환
        val startsWithPrefix = { p: Person ->
            p.firstName.startsWith(prefix) || p.lastName.startsWith(prefix)
        }
        if (!onlyWithPhoneNumber) {
            return startsWithPrefix // 람다를 반환
        }
        return { startsWithPrefix(it) && it.phoneNumber != null } // 람다를 반환
    }
}

val contacts = listOf(
    Person("Dmitry", "Jemerov", "123-4567"),
    Person("Svetlana", "Isakova", null)
)

val contactListFilters = ContactListFilters()
with(contactListFilters) {
    prefix = "Dm"
    onlyWithPhoneNumber = true
}
println(contacts.filter(contactListFilters.getPredicate())) // [Person(firstName=Dmitry, lastName=Jemerov, phoneNumber=123-4567)]
```

`getPredicate` 함수는 `Person`을 받아 `Boolean`을 반환하는 함수를 반환한다.


### 8.1.6 람다를 활용한 중복 제거

웹사이트 방문 기록을 분석하는 예를 살펴보자. `SiteVisit`에는 방문한 사이트의 경로, 사이트에서 머문 시간,
사용자의 운영체제(OS)가 들어있다. 여러 OS를 enum을 사용해 표현한다.

```kotlin
data class SiteVisit(
    val path: String,
    val duration: Double,
    val os: OS
)

enum class OS { WINDOWS, LINUX, MAC, IOS, ANDROID }

val log = listOf(
    SiteVisit("/", 34.0, OS.WINDOWS),
    SiteVisit("/", 22.0, OS.MAC),
    SiteVisit("/login", 12.0, OS.WINDOWS),
    SiteVisit("/signup", 8.0, OS.IOS),
    SiteVisit("/", 16.3, OS.ANDROID)
)
```

윈도우 사용자의 평균 방문 시간을 출력하고 싶다. `average` 함수를 사용하면 쉽게 그런 작업을 수행할 수 있다.

```kotlin
val averageWindowsDuration = log
    .filter { it.os == OS.WINDOWS }
    .map(SiteVisit::duration)
    .average()
println(averageWindowsDuration) // 23.0
```

이제 맥 사용자에 대해 같은 통계를 구하고 싶다. 중복을 피하기 위해 OS를 파라미터로 뽑아낼 수 있다.

```kotlin
fun List<SiteVisit>.averageDurationFor(os: OS) =
    filter { it.os == os }.map(SiteVisit::duration).average()

println(log.averageDurationFor(OS.WINDOWS)) // 23.0
println(log.averageDurationFor(OS.MAC)) // 22.0
```

모바일 디바이스 사용자의 평균 방문 시간을 구하고 싶다면 다음과 같이 할 수 있다.

```kotlin
val averageMobileDuration = log
    .filter { it.os in setOf(OS.IOS, OS.ANDROID) }
    .map(SiteVisit::duration)
    .average()

println(averageMobileDuration) // 12.15
```

플랫폼을 표현하는 간단한 파라미터로는 이런 상황을 처리할 수 있다. 게다가 
"IOS 사용자의 /signup 페이지 평균 방문 시간은?"과 같인 더 복잡한 질의를 사용해
방문 기록을 분석하고 싶을 때도 있다.

```kotlin
fun List<SiteVisit>.averageDurationFor(predicate: (SiteVisit) -> Boolean) =
    filter(predicate).map(SiteVisit::duration).average()

println(log.averageDurationFor { it.os in setOf(OS.IOS, OS.ANDROID) }) // 12.15
println(log.averageDurationFor { it.os == OS.IOS && it.path == "/signup" }) // 8.0
```

일부 잘 알려진 객체지향 디자인 패턴을 함수 타입과 람다 식을 사용해 단순화할 수 있다.
전략 패턴을 생각해보자. 람다 식이 없다면 인터페이스를 선언하고 구현 클래스를 통해 전략을 정의해야 한다.
하지만 람다 식을 사용하면 전략을 함수로 표현할 수 있다.

```kotlin
interface MoveableStrategy {
    fun move()
}

class RailLoadStrategy : MoveableStrategy {
    override fun move() {
        println("I'm moving on rail")
    }
}

class LoadStrategy : MoveableStrategy {
    override fun move() {
        println("I'm moving on load")
    }
}

class Car {
    private var moveableStrategy: MoveableStrategy? = null

    fun setMoveableStrategy(moveableStrategy: MoveableStrategy) {
        this.moveableStrategy = moveableStrategy
    }

    fun move() {
        moveableStrategy?.move()
    }
}

fun main() {
    val car = Car()
    car.setMoveableStrategy(RailLoadStrategy())
    car.move()
    car.setMoveableStrategy(LoadStrategy())
    car.move()
}
```

```kotlin
class Car {
    private var moveableStrategy: (() -> Unit)? = null

    fun setMoveableStrategy(moveableStrategy: () -> Unit) {
        this.moveableStrategy = moveableStrategy
    }

    fun move() {
        moveableStrategy?.invoke()
    }
}

fun main() {
    val car = Car()
    car.setMoveableStrategy { println("I'm moving on rail") }
    car.move()
    car.setMoveableStrategy { println("I'm moving on load") }
    car.move()
}
```

## 8.2 인라인 함수: 람다의 부가 비용 없애기
람다를 활용한 코드의 성능은 어떨까? 혹시 겉보기엔 일반 자바 문장과 똑같지만
실행해보면 훨씬 느리게 작동해서 사람들을 불쾌하게 하는 함수를 작성하고 있는건 아닐까?

람다가 변수를 포획하면 람다가 생성되는 시점마다 새로운 무명 클랙스 객체가 생김.

람다를 사용하는 구현은 똑같은 작업을 수행하는 일반 함수를 사용한 구현보다 덜 효율적이다.

그렇다면 반복되는 코드를 별도의 라이브러리 함수로 빼내되 컴파일러가 자바의 일반 명령문만큼
효율적인 코드를 생성하게 만들 수는 없을까? 사실 코틀린 컴파일러에서는 그런 일이 가능하다.

`inline` 변경자를 어떤 함수에 붙이면 컴파일러는 그 함수를 호출하는 모든 문장을 함수 본문에
해당하는 바이트코드로 바꿔치기 해준다.

### 8.2.1 인라이닝이 작동하는 방식

어떤 함수를 inline으로 선언하면 그 함수의 본문이 인라인된다. 다른 말로 하면 함수를
호출하는 코드를 함수를 호출하는 바이트코드 대신에 함수 본문을 번역한 바이트 코드로
컴파일한다는 뜻이다.

```kotlin
inline fun <T> synchronized(lock: Lock, action: () -> T): T {
    lock.lock()
    try {
        return action()
    } finally {
        lock.unlock()
    }
}
val l = Lock()
synchronized(l) {
    // ...
}
```

이 함수를 호출하는 코드는 자바의 `synchronized` 문과 똑같아 보인다.
차이는 자바에서는 임의의 객체에 대해 `synchronized` 블록을 사용할 수 있지만 이 함수는
`Lock` 클래스의 인스턴스를 요구한다는 점뿐이다.

`syncronized` 함수를 인라인으로 선언했으므로 `synchronized` 함수를 호출하는 코드는 모두
자바의 `synchronized` 문과 같아진다.

```kotlin
fun foo(l: Lock) {
    println("Before sync")
    synchronized(l) {
        println("Action")
    }
    println("After sync")
}
```

```java
public final class InlineKt {
   public static final Object synchronized(@NotNull Lock lock, @NotNull Function0 action) {
      int $i$f$synchronized = 0;
      Intrinsics.checkNotNullParameter(lock, "lock");
      Intrinsics.checkNotNullParameter(action, "action");
      lock.lock();

      Object var3;
      try {
         var3 = action.invoke();
      } finally {
         InlineMarker.finallyStart(1);
         lock.unlock();
         InlineMarker.finallyEnd(1);
      }

      return var3;
   }

   public static final void foo(@NotNull Lock l) {
      Intrinsics.checkNotNullParameter(l, "l");
      String var1 = "Before sync";
      System.out.println(var1);
      int $i$f$synchronized = false;
      l.lock();

      try { // synchronized
         int var2 = false;
         String var3 = "Action";
         System.out.println(var3);
         Unit var7 = Unit.INSTANCE;
      } finally {
         l.unlock();
      } // synchronized

      var1 = "After sync";
      System.out.println(var1);
   }
}
```


### 8.2.2 인라인 함수의 한계
인라이닝 하는 방식으로 인해 람다를 사용하는 모든 함수를 인라이닝할 수는 없다.
일반적으로 인라인 함수의 본문에서 람다 식을 바로 호출하거나 람다 식을 인자로 전달받아
바로 호출하는 경우에는 그 람다를 인라이닝할 수 있다.

그런 경우가 아니라면 컴파일러는 `Illegal usage of inline-parameter`라는 메시지와 함께 인라이닝을 금지시킨다.

예를 들어 시퀀스에 대해 동작하는 메소드 중에는 람다를 받아서 모든 시퀀스 원소에 그 람다를 적용한
새 시퀀스를 반환하는 함수가 많다. 그런 함수는 인자로 받은 람다를 시퀀스 객체 생성자의 인자로 넘기곤 한다.

다음은 `Sequence.map`을 정의하는 방법을 보여준다.

```kotlin
fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R> {
    return TransformingSequence(this, transform)
}
```

이 map 함수는 `transform` 파라미터로 전달받은 함수 값을 호출하지 않는 대신, `TransformingSequence`라는 클래스의 생성자에게 그 함수 값을 넘긴다.
`TransformingSequence`는 전달 받은 람다를 프로퍼티로 저장한다. 이런 기능을 지원하려면 `map`에 전달되는 `transform` 인자를 일반적인(인라이닝하지 않는) 함수 표현으로
만들 수밖에 없다. 즉 여기서는 `transform`을 함수 인터페이스를 구현하는 무명 클래스 인스턴스로 만들어야만 한다.

둘 이상의 람다를 인자로 받는 함수에서 일부 람다만 인라이닝하고 싶을 때도 있다.
예를 들어 어떤 람다에 너무 많은 코드가 들어가거나 어떤 람다에 인라이닝을 하면 안 되는
람다를 파라미터로 받는다면 `noinline` 변경자를 사용할 수 있다.


```kotlin
inline fun foo(inlined: () -> Unit, noinline notInlined: () -> Unit) {
    // ...
}
```

코틀린에서는 어떤 모듈이나 서드파티 라이브러리 안에서 인라인 함수를 정의하고 그 모듈이나
라이브러리 밖에서 해당 인라인 함수를 사용할 수 있다. 또 자바에서도 코틀린에서 정의한 인라인 함수를
호출할 수 있다.

### 8.2.3 컬렉션 연산 인라이닝
컬렉션에 대해 작용하는 코틀린 표준 라이브러리의 성능을 살펴보자. 코틀린 표준 라이브러리의
컬렉션 함수는 대부분 람다를 인자로 받는다. 표준 라이브러리 함수를 사용하지 않고 직접 이런 연산을 구현한다면 더 효율적이지 않을까?
예를 들어 `Person`의 리스트를 걸러내는 두 가지 방법을 비교해보자.

```kotlin
data class Person(val name: String, val age: Int)

val people = listOf(Person("Alice", 29), Person("Bob", 31))

println(people.filter { it.age < 30 })
```

```kotlin
fun findTheOldest(people: List<Person>) {
    var maxAge = 0
    var theOldest: Person? = null
    for (person in people) {
        if (person.age > maxAge) {
            maxAge = person.age
            theOldest = person
        }
    }
    println(theOldest)
}
```

코틀린 표준 라이브러리의 `filter` 함수는 인라인 함수로 정의되어 있다. 따라서 `filter` 함수의 바이트코드는
그 함수에 전달된 람다 본문의 바이트코드와 함께 `filter` 함수를 호출한 위치에 들어간다.
그 결과 앞 예제에서 `filter` 를 써서 생긴 바이트코드와 뒤 예제에서 생긴 바이트코드는 거의 같다.

`filter`와 `map`을 연쇄해서 사용하면 어떻게 될까?
```kotlin
println(people.filter { it.age < 30 }.map(Person::name))
```

이 예제는 람다 식과 멤버 참조를 사용한다. 이 코드는 리스트를 걸러낸 결과를 저장하는
중간 리스트를 만든다. `filter`함수에서 만들어진 코드는 원소를 그 중간 리스트에 추가하고,
`map` 함수에서 만들어진 코드는 그 중간 리스트를 읽어서 사용한다.

처리할 원소가 많아지면 중간 리스트를 사용하는 부가 비용도 걱정할 만큼 커진다.
`asSequence` 함수를 사용하면 중간 리스트를 사용하지 않고도 컬렉션 연산을 수행할 수 있다.
이때 각 중간 시퀀스는 람다를 필드에 저장하는 객체로 표현되며, 최종 연산은 중간 시퀀스에 있는
여러 람다를 연쇄 호출한다. 따라서 앞 절에서 설명한 대로 시퀀스는 람다를 인라인하지 않는다.
따라서 지연 계산을 통해 성능을 향상시키려는 이유로 모든 컬렉션 연산에 `asSequence`를 붙여서는 안 된다.
시퀀스 연산에서는 람다가 인라이닝되지 않기 때문에 크기가 작은 컬렉션은 오히려 일반 컬렉션 연산이 더 성능이 나을
수도 있다.
시퀀스 연산을 통해 성능을 향상시킬 수 있는 경우는 컬렉션 크기가 큰 경우 뿐이다.


### 8.2.4 함수를 인라인으로 선언해야 하는 경우

`inline` 키워드의 이점을 배우고 나면 코드를 더 빠르게 만들기 위해 코드 여기저기에서 `inline`을 사용하고 싶어질 수 있다.
하지만 사실 이는 좋은 생각이 아니다. `inline` 키워드를 사용해도 람다를 인자로 받는 함수만 성능이 좋아질 가능성이 높다.
다른 경우에는 주의 깊게 성능을 측정하고 조사해봐야 한다.

일반 함수 호출의 경우 JVM은 이미 강력하게 인라이닝을 지원한다. JVM은 코드 실행을 분석해서 가장 이익이 되는 방향으로
호출을 인라이닝한다. 이런 과정은 바이트코드를 실제 기계어 코드로 번역하는 과정(JIT)에서 일어난다.
이런 JVM의 최적화를 활용한다면 바이트코드에서는 각 함수 구현이 정확히 한 번만 있으면 되고, 그 함수를 호출하는 부분에서
따로 함수 코드를 중복할 필요가 없다.

반면 코틀린 인라인 함수는 바이트 코드에서 각 함수 호출 지점을 함수 본문으로 대치하기 때문에 코드 중복이 생긴다.
게다가 함수를 직접 호출하면 스택 트레이스가 더 깔끔해진다.

반면 람다를 인자로 받는 함수를 인라이닝하면 이익이 더 많다.
- 함수 호출 비용을 줄일 수 있을 뿐 아니라 람다를 표현하는 클래스와 람다 인스턴스에 해당하는 객체를 만들 필요도 없어진다.
- 현재의 JVM은 함수 호출과 람다를 인라이닝해 줄 정도로 똑똑하지는 못하다.
- 인라이닝을 사용하면 일반 람다에서는 사용할 수 없는 몇 가지 기능을 사용할 수 있다. (넌노컬 반환)

하지만 `inline` 변경자를 함수에 붙일 때는 코드 크기에 주의를 기울여야 한다. 인라이닝 하는 함수가 큰 경우 함수의
본문에 해당하는 바이트코드를 모든 호출 지점에 복사해 넣고 나면 바이트코드가 전체적으로 아주 커질 수 있다.
코틀린 표준 라이브러리가 제공하는 `inline` 함수를 보면 모두 크기가 아주 작다는 사실을 알 수 있을 것이다.

### 8.2.5 자원 관리를 위해 인라인된 람다 사용
람다로 중복을 없앨 수 있는 일반적인 패턴 중 한 가지는 어떤 작업을 하기 전에 자원을 획득하고 작업을 마친 후
자원을 해제하는 자원 관리다. 여기서 자원은 파일, 락, 데이터베이스 트랜잭션 등 여러 다른 대상을 가리킬 수 있다.

자원 관리 패턴을 만들 때 보통 사용하는 방법은 `try-finally` 문을 사용하되 `try` 블록을 시작하기 직전에 자원을 획득하고
`finally` 블록에서 자원을 해제하는 것이다.

코틀린 라이브러리에는 좀 더 코틀린 다운 API를 통해 같은 기능을 제공하는 `withLock`이라는 함수도 있다.
`withLock`은 `Lock` 인터페이스의 확장 함수다.

```kotlin
val l = Lock()
l.withLock { // 락을 잠근 다음에 주어진 동작을 수행한다.
    // 락에 의해 보호되는 자원을 사용한다.
}
```

다음은 코틀린 라이브러리에 있는 `withLock` 함수의 정의다.

```kotlin
inline fun <T> Lock.withLock(action: () -> T) { // 락을 획득한 후 작업하는 과정을 변도의 함수로 분리한다.
    lock()
    try {
        action()
    } finally {
        unlock()
    }
}
```

이런 패턴을 사용할 수 있는 다른 유형의 자원으로 파일이 있따. 자바 7부터는 이를 위한 특별한 구문인
`try-with-resources`가 추가되었다. 

```java
static String readFirstLineFromFile(String path) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(path))) {
        return br.readLine();
    }
}
```

코틀린에서는 `use`라는 이름의 확장 함수를 사용해 같은 기능 구현을 제공한다.

```kotlin
fun readFirstLineFromFile(path: String): String {
    BufferedReader(FileReader(path)).use { br -> // 블록을 실행한 후에 자원을 닫는다.
        return br.readLine()
    }
}
```

## 8.3 고차 함수 안에서 흐름 제어

### 8.3.1 람다 안의 return문: 람다를 둘러싼 함수로부터 반환

자신을 둘러싸고 있는 블록보다 더 바깥에 있는 다른 블록을 반환하게 만드는 `return`문을 `non-local return`이라 부른다.

```kotlin
fun lookForAlice(people: List<Person>) {
    people.forEach {
        if (it.name == "Alice") {
            println("Found!")
            return // lookForAlice 함수를 반환한다.
        }
    }
    println("Alice is not found")
}
```

이렇게 `return`이 바깥쪽 함수를 반환시킬 수 있는 때는 람다를 인자로 받는 함수가 인라인 함수인 경우뿐이다.
여기서 `forEach` 함수는 인라인 함수이므로 람다 본문고 함께 인라이닝된다.

### 8.3.2 람다로부터 반환: 레이블을 사용한 return

람다 식에서도 `local return`을 사용할 수 있다. 람다 안에서 로컬 `return`은 `for` 루프의
`break`문과 비슷하게 동작한다. 

```kotlin
fun lookForAlice(people: List<Person>) {
    people.forEach label@{ // 람다에 레이블을 붙인다.
        if (it.name == "Alice") return@label // 레이블을 사용한 람다 반환
    }
    println("Alice might be somewhere") // 항상 이 줄이 출력된다.
}
```

람다에 레이블을 붙여서 사용하는 대신 람다를 인자로 받는 인라인 함수의 이름을 `return` 뒤에 레이블로 사용해도 된다.

```kotlin
fun lookForAlice(people: List<Person>) {
    people.forEach {
        if (it.name == "Alice") return@forEach // 레이블을 사용한 람다 반환
    }
    println("Alice might be somewhere") // 항상 이 줄이 출력된다.
}
```

람다 식에는 레이블이 2개 이상 붙을 수 없다.

`this` 식의 레이블에도 마찬가지 규칙이 적용된다. 수식 객체 지정 람다의 본문에서는 `this` 식을 사용해
묵시적인 컨텍스트 객체(람다를 만들 때 지정한 수신 객체)를 가리킬 수 있다.

```kotlin
println.StringBuilder().apply sb@{
    listOf(1, 2, 3).apply {
        this@sb.append(this.toString())
    }
}
// [1, 2, 3]
```

하지만 넌로컬 반환문은 장황하고, 람다 안의 여러 위치에 `return`식이 들어가야 하는 경우 사용하기 불편하다.
코틀린 코드 블록을 여기저기 전달하기 위한 다른 해법을 제공하며, 그 해법을 사용하면 넌로컬 반환문을 여럿 사용해야 하는
코드 블록을 쉽게 작성할 수 있다. 바로 무명 함수가 그 해법이다.

### 8.3.3 무명 함수: 기본적으로 로컬 return

본문 여러 곳에서 `return`을 사용해야 하는 경우 무명 함수를 사용할 수 있다. 무명 함수 안에서 `return`을 사용하면
그 함수를 둘러싼 함수로부터 반환된다.

```kotlin
fun lookForAlice(people: List<Person>) {
    people.forEach(fun(person) { // 무명 함수를 사용해 람다 대신 반환
        if (person.name == "Alice") return // 무명 함수를 반환한다.
        println("${person.name} is not Alice")
    })
}
```