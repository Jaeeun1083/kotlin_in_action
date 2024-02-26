# 9장 제네릭스

## 9.1 제네릭 타입 파라미터
제네릭스를 사용하면 타입 파라미터를 받는 타입을 정의할 수 있다.
제네릭 타입의 인스턴스를 만들려면 타입 파라미터를 구체적인 타입 인자로 치환해야 한다.
예를 들어 `List<String>`은 `String` 타입의 원소를 갖는 리스트 타입이다.

코틀린 컴파일러는 보통 타입과 마찬가지로 타입 인자도 추론할 수 있다.
```kotlin
val authors = listOf("Dmitry", "Svetlana")
```

`listOf`에 전달된 두 값이 문자열이기 때문에 컴파일러는 여기서 생기는 리스트가 `List<String>` 타입이라고 추론한다.
반면에 빈 리스트를 만들 때는 타입 인자를 명시적으로 지정해야 한다.

```kotlin
val readers: MutableList<String> = mutableListOf()
val readers = mutableListOf<String>()
```

### 9.1.1 제네릭 함수와 프로퍼티
리스트를 다루는 함수를 작성한다면 어떤 특정 타입을 저장하는 리스트뿐 아니라 모든 리스트를 다룰 수 있는 함수를 원할 것이다.
이럴때 제네릭 함수를 작성해야한다.

```kotlin
fun <T> List<T>.slice(indices: IntRange): List<T> {
    return filterIndexed { index, _ -> index in indices }
}

val letters = ('a'..'z').toList()
println(letters.slice<Char>(0..2)) // 타입인자를 명시적으로 지정
// [a, b, c]

val authors = listOf("Dmitry", "Svetlana")
println(authors.slice(0..1)) // 타입인자를 추론
// [Dmitry, Svetlana]
```

제네릭 함수를 정의할 때와 마찬가지 방법으로 제네릭 확장 프로퍼티를 선언할 수 있다.
예를 들어 다음은 리스트의 마지막 원소 바로 앞에 있는 원소를 반환하는 확장 프로퍼티다.

```kotlin
val <T> List<T>.penultimate: T // 모든 리스트 타입에 이 제네릭 확장 프로퍼티를 사용할 수 있다.
    get() = this[size - 2]

println(listOf(1, 2, 3, 4).penultimate) // 이 호출에서 타입 파라미터 T는 Int로 추론된다.
// 3
```

> 확장 프로퍼티만 제네릭하게 만들 수 있다.
> 일반(확장이 아닌) 프로퍼티는 타입 파라미터를 가질 수 없다. 클래스 프로퍼티에 여러 타입의 값을 저장할 수 는 없으므로
> 제네릭한 일반 프로퍼티는 말이 되지 않는다. 일반 프로퍼티를 제네릭하게 정의하면 컴파일러가 다음과 같은 오류를 표시한다.
> ```kotlin
> val <T> x: T = TODO()
> // Error: Type parameter of a property must be used in its receiver type
> ```

### 9.1.2 제네릭 클래스 선언
자바와 마찬가지로 코틀린에서도 타입 파라미터를 넣은 꺾쇠 기호를 클래스 이름 뒤에 붙이면 클래스를 제네릭하게
만들 수 있다.

```kotlin
class MyList<T> {
    fun get(index: Int): T {
        TODO()
    }
    fun add(element: T) {
        TODO()
    }
}
```

심지어 클래스가 자기 자식은 타입 인자로 참조할 수도 있다. `Comparable` 인터페이스를 구현하는 클래스가
이런 패턴의 예다. 비교 가능한 모든 값은 자신을 같은 타입의 다른 값과 비교하는 방법을 제공해야만 한다.

```kotlin
interface Comparable<T> {
    fun compareTo(other: T): Int
}

class String : Comparable<String> {
    override fun compareTo(other: String): Int {
        // ...
    }
}
```

`String` 클래스는 `Comparable<String>`를 구현하면서 그 인터페이스의 타입 파라미터 T로 `String` 자신을 지정한다.

### 9.1.3 타입 파라미터 제약
**타입 파라미터 제약**은 클래스나 함수에 사용할 수 있는 타입 인자를 제한하는 기능이다.

```kotlin
fun <T : Number> List<T>.sum(): T {
    // ...
}

val list = listOf(1, 2, 3)
println(list.sum()) // 6

val list = listOf("a", "b", "c")
println(list.sum()) // 컴파일 오류
```

어떤 타입을 제네릭 타입 파라미터에 대한 `upper bound`로 지정하면 그 제네릭 타입을 인스턴스화할 때 사용하는 타입인자는 반드시
그 상한 타입이거나 그 상한 타입의 하위 타입이어야 한다.

### 9.1.4 타입 파라미터를 널이 될 수 없는 타입으로 한정
제네릭 클래스나 함수를 정의하고 그 타입을 인스턴스화할 때는 널이 될 수 있는 타입을 포함하는 어떤 타입으로
타입 인자를 지정해도 타입 파라미터를 치환할 수 있다.

아무런 상한을 정하지 않은 타입 파라미터는 결과적으로 `Any?`를 상한으로 정한 파라미터와 같다.

항상 널이 될 수 없는 타입만 타입 인자로 받게 만들려면 타입 파라미터에 제약을 가해야 한다.
널 가능성을 제외한 아무런 제약도 필요 없다면 `Any`를 상한으로 지정하면 된다.

```kotlin
fun <T : Any> printHashCode(t: T) {
    println(t.hashCode())
}

printHashCode(null) // 컴파일 오류
```

## 9.2 실행 시 제네릭스의 동작: 소거된 타입 파라미터와 실체화된 타입 파라미터
JVM의 제네릭스는 보통 타입 소거(type erasure)를 사용해 구현된다. 이는 실행 시점에 제네릭 클래스의 인스턴스에
타입 인자 정보가 들어있지 않다는 뜻이다.

### 9.2.1 실행 시점의 제네릭: 타입 검사와 캐스트
자바와 마찬가지로 코틀린 제네릭 타입 인자 정보는 런타임에 지워진다. 이는 제네릭 클래스 인스턴스가
그 인스턴스를 생성할 때 쓰인 타입 인자에 대한 정보를 유지하지 않는다는 뜻이다.

예를 들어 `List<String>` 객체를 만들고 그 안에 문자열을 여럿 넣더라도 실행 시점에는 그 객체를 오직 `List`로만 볼 수 있다.
그 `List` 객체가 어떤 타입의 원소를 저장하는지 실행 시점에는 알 수 없다.

```kotlin
val list1: List<String> = listOf("a", "b")
val list2: List<Int> = listOf(1, 2, 3)
println(list1::class == list2::class) // true
```
컴파일러는 두 리스트를 서로 다른 타입으로 인식하지만 실행 시점에 그 둘은 완전히 같은 타입의 객체다.

그럼에도 불구하고 보통은 `List<String`에는 문자열만 들어있고 `List<Int>`에는 정수만 들어있다고 가정할 수 있는데,
이는 컴파일러가 타입 인자를 알고 올바른 타입의 값만 각 리스트에 넣도록 보장해주기 때문이다.

다음으로 타입 소거로 인해 생기는 한계를 살펴보자. 타입 인자를 따로 저장하지 않기 때문에 실행 시점에 타입 인자를 검사할 수 없다.
예를 들어 어떤 리스트가 문자열로 이뤄진 리스트인지 다른 객체로 이뤄진 리스트인지를 실행 시점에 검사할 수 없다.

일반적으로 말하자면 `is` 검사에서 타입 인자로 지정한 타입을 검사할 수는 없다.

```kotlin
if (value is List<String>) { // 컴파일 오류
    println(value.joinToString())
}
// Error: Cannot check for instance of erased type: List<String>
```

실행 시점에 어떤 값이 `List`인지 여부는 확실히 알아낼 수 있지만 그 리스트가 `String`의 리스트인지,
`Person`의 리스트인지 혹은 다른 어떤 타입의 리스트인지는 알 수가 없다. 그런 정보는 지워진다.

어떤 값이 집합이나 맵이 아니라 리스트라는 사실을 어떻게 확인할 수 있을까?
바로 스타 프로젝션(`star projection`)을 사용하면 된다.

타입 파라미터가 2개 이상이라면 모든 타입 파라미터에 *를 포함시켜야 한다. 
`as`와 `as?` 캐스팅에도 여전히 제네릭 타입을 사용할 수 있다. 하지만 기저 클래스는 같지만
타입 인자가 다른 타입으로 캐스팅해도 여전히 캐스팅에 성공한다는 점을 조심해야 한다.
실행 시점에는 제네릭 타입의 인자를 알 수 없으므로 캐스팅은 항상 성공한다.
그런 타입 캐스팅을 사용하면 컴파일러가 `unchecked cast` 경고를 표시한다.

하지만 컴파일러는 단순히 경고만 하고 컴파일을 진행하므로 다음 코드처럼 원하는 제네릭 타입으로 캐스팅해 사용해도 된다.

```kotlin
fun printSum(c: Collection<*>) {
    val intList = c as? List<Int> ?: throw IllegalArgumentException("List is expected")
    println(intList.sum())
}

printSum(listOf(1, 2, 3)) // 6

printSum(listOf("a", "b", "c")) // IllegalArgumentException

printSum(setOf(1, 2, 3)) // ClassCastException (Set은 List가 아니므로)
```

코틀린 컴파일러는 컴파일 시점에 타입 정보가 주어진 경우에는 `is` 검사를 수행하게 허용할 수 있을 정도로 똑똑하다.

```kotlin
fun printSum(c: Collection<Int>) {
    if (c is List<Int>) { // 이 검사는 올바르다.
        println(c.sum())
    }
}
```

```java
public class Test {
  public static void main(String[] args) {
    test(List.of(1, 2, 3));
  }

  public static void test(Collection<Integer> obj) {
    if (obj instanceof List<Integer>) {
      System.out.println("obj is a Collection");
    }
  }
}
```

컴파일 시점에 c 컬렉션이 `Int` 값을 저장한다는 사실이 알려져 있으므로 c가 `List<Int>` 타입인지 검사할 수 있다.

일반적으로 코틀린 컴파일러는 여러분에게 안전하지 못한 검사와 수행할 수 있는 검사를 알려주기 위해 최대한 노력한다.

### 9.2.2 실체화한 타입 파라미터를 사용한 함수 선언
코틀린 제네릭 타입의 타입 인자 정보는 실행 시점에 지워진다. 따라서 제네릭 클래스의 인스턴스가 있어도 그 인스턴스를 만들 때 사용한 타입 인자를 알아낼 수 없다.
하지만 이런 제약을 피할 수 있는 경우가 하나 있다. 

인라인 함수의 타입 파라미터는 실체화되므로 실행 시점에 인라인 함수의 타입 인자를 알 수 있다.

```kotlin
inline fun <reified T> isA(value: Any) = value is T

println(isA<String>("abc")) // true

println(isA<String>(123)) // false
```

실체화한 타입 파라미터를 사용하는 예를 살펴보자. 실체화한 타입 파라미터를 활용하는 가장 간단한 예제 중 하나는
표준 라이브러리 함수인 `filterIsInstance`다.

이 함수는 인자로 받은 컬렉션의 원소 중에서 타입 인자로 지정한 클래스의 인스턴스만을 모아서 만든 리스트를 반환한다.

```kotlin
val items = listOf("one", 2, "three")
println(items.filterIsInstance<String>()) // [one, three]
```

`filterIsInstance`의 타입 인자로 `String`을 지정함으로써 문자열만 필요하다는 사실을 기술한다.

이 함수의 반환 타입은 따라서 `List<String>`이다. 여기서는 타입 인자를 실행 시점에 알 수 있고 `filterIsInstance`는 그 타입 인자를 사용해
리스트의 원소중에 타입 인자와 타입이 일치하는 원소만을 추려낼 수 있다.

```kotlin
inline fun <reified T> Iterable<*>.filterIsInstance(): List<T> { // reified 키워드는 이 타입 파라미터가 실행 시점에 지워지지 않음을 표시한다.
    val destination = ArrayList<T>()
    for (element in this) {
        if (element is T) { // 각 원소가 타입 인자로 지정한 클래스의 인스턴스인지 검사할 수 있다.
            destination.add(element)
        }
    }
    return destination
}
```

> **인라인 함수에서만 실체화한 타입 인자를 쓸 수 있는 이유**
> 왜 일반 함수에서는 `element is T`를 쓸 수 없고 인라인 함수에서만 쓸 수 있는 걸까>
> 
> 컴파일러는 인라인 함수의 본문을 구현한 바이트코드를 그 함수가 호출되는 모든 지점에 삽입한다.
> 컴파일러는 실체화한 타입 인자를 사용해 인라인 함수를 호출하는 각 부분의 젖ㅇ확한 타입 인자를 알 수 있다.
> 따라서 컴파일러는 타입 인자로 쓰인 구체적인 클래스를 참조하는 바이트코드를 생성해 삽입할 수 있다.
> 결과적으로 위 예제의 `filterIsInstance<String>` 호출은 다음과 동등한 코드를 만들어낸다.
>
> ```kotlin
> val destination = ArrayList<String>()
> for (element in this) {
>    if (element is String) {
>       destination.add(element)
>   }
> }
> return destination
> ```

### 9.2.3 실체화한 타입 파라미터로 클래스 참조 대신
`java.lang.Class` 타입 인자를 파라미터로 받는 API에 대한 코틀린 어댑터를 구축하는 경우
실체화한 타입 파라미터를 자주 사용한다. `java.lang.Class`를 사용하는 API의 예로는 JDK의 `ServiceLoader`가 있다.

`ServiceLoader`는 어떤 추상 클래스나 인터페이스를 표현하는 `java.lang.Class` 객체를 받아 그 클래스나 인스턴스를 구현한 인스턴스를 반환한다.
실체화한 타입 파라미터를 활용해 이런 API를 쉽게 호출할 수 있게 만드는 방법을 살펴보자.

```kotlin
val serviceImpl = ServiceLoader.load(Service::class.java)
```

```kotlin
inline fun <reified T> loadService() {
    return ServiceLoader.load(T::class.java)
}

val serviceImpl = loadService<Service>()
```

### 9.2.4 실체화한 타입 파라미터의 제약
실체화한 타입 파라미터는 유용한 도구지만 몇 가지 제약이 있다. 일부는 실체화의 개념으로 인해 생기는 제약이며, 나머지는 지금 코틀린이 실체화를 구현하는 방식에 의해 생기는 제약으로
향후 완화될 가능성이 있다.

다음과 같은 경우에 실체화한 타입 파라미터를 사용할 수 있다.
- 타입 검사와 캐스팅(`is`, `!is`, `as`, `as?`)
- 코틀린 리플렉션 API(`::class`)
- 코틀린 타입에 대응하는 `java.lang.Class` 인스턴스를 얻기(`::class.java`)
- 다른 함수를 호출할 때 타입 인자로 사용

하지만 다음과 같은 일은 할 수 없다.
- 타입 파라미터 클래스의 인스턴스 생성하기
- 타입 파라미터 클래스의 동반 객체 메소드 호출하기
- 실체화한 타입 파라미터를 요구하는 함수를 호출하면서 실체화하지 않은 타입 파라미터로 받은 타입을 타입 인자로 넘기기
- 클래스, 프로퍼티, 인라인 함수가 아닌 함수의 타입 파라미터를 `reified`로 지정하기

## 9.3 변성: 제네릭과 하위 타입
`variance` 개념은 `List<String>`와 `List<Any>`와 같이 기저 타입이 같고 타입 인자가 다른 여러 타입이 서로 어떤 관계가 있는지
설명하는 개념이다. 일반적으로 이런 관계가 왜 중요한지 먼저 설명한 다음에 코틀린에서 변성을 어떻게 표시하는지 살펴본다.

변성을 잘 활용하면 사용에 불편하지 않으면서 타입 안정성을 보장하는 API를 만들 수 있다.

### 9.3.1 변성이 있는 이유: 인자를 함수에 넘기기
`List<Any>` 타입의 파라미터를 받는 함수에 `List<String>`을 넘기면 안전할까?
`String` 클래스는 `Any` 를 확장하므로 `Any` 타입 값을 파라미터로 받는 함수에 `String` 값을 넘겨도 안전하다.

하지만 `Any`와 `String`이 `List` 인터페이스의 타입 인자로 들어가는 경우 그렇게 자신 있게 안정성을 말할 수 없다.

```kotlin
fun printContents(list: List<Any>) {
    println(list.joinToString())
}

printContents(listOf("abc", "bac")) // abc, bac
```

이 경우에는 문자열 리스트도 잘 동작한다. 이 함수는 각 원소를 `Any`로 취급하며 모든 문자열은 `Any` 타입이기도 하므로 완전히 안전하다.

```kotlin
fun addAnswer(list: MutableList<Any>) {
    list.add(42)
}

val strings = mutableListOf("abc", "bac")
addAnswer(strings) // 이 줄이 컴파일된다면
println(strings.maxByOrNull { it.length }) // 실행 시점에 예외가 발생할 것이다.
```

이 함수 호출은 컴파일 될 수 없다. 이 예제는 `MutableList<Any>`가 필요한 곳에 `MutableList<String>`을 넘기면 안된다는 사실을 보여준다.
코틀린 컴파일러는 실제 이런 함수 호출을 금지한다.

이제 `List<Any>` 타입의 파라미터를 받는 함수에 `List<String>`을 넘기면 안전한가라는 질문에 답할 수 있다.
어떤 함수가 리스트의 원소를 추가하거나 변경한다면 타입 불일치가 생길 수 있어서 `List<Any>` 대신 `List<String>`을 넘길 수 없다.
하지만 원소 추가나 변경이 없는 경우에는 `List<String>`을 `List<Any>` 대신 넘겨도 안전하다.

코틀린에서는 변경 가능성에 따라 적절한 인터페이스를 선택하면 안전하지 못한 함수 호출을 막을 수 있다.

### 9.3.2 클래스, 타입, 하위 타입
제네릭 클래스가 아닌 클래스에서는 클래스 이름을 바로 타입으로 쓸 수 있다.
예를 들어 `var x: String`이라고 쓰면 `String` 클래스의 인스턴스를 저장하는 변수를 정의할 수 있다.
하지만 `var x: String?`처럼 같은 클래스 이름을 널이 될 수 있는 타입에도 쓸 수 있다는 점을 기억하라.
이는 모든 코틀린 클래스가 적어도 둘 이상의 타입을 구성할 수 있다는 뜻이다. (String, String? 는 서로 다른 타입이다.)

제네릭 클래스에서는 상황이 더 복잡하다. 올바른 타입을 얻으려면 제네릭 타입의 타입 파라미터를 구체적인 타입 인자로 바꿔줘야 한다.
예를 들어 `List`는 타입이 아니다(하지만 클래스다). 하지만 타입 인자를 치환한 `List<String>`은 타입이다.
각각의 제네릭 클래스는 무수히 많은 타입을 만들어낼 수 있다.

타입 사이의 관계를 논하기 위해 하위 타입(`subtype`)이라는 개념을 잘 알아야 한다. 어떤 타입 A의 값이 필요한 모든 장소에 
어떤 타입 B의 값을 넣어도 아무 문제가 없다면 타입 B는 타입 A의 하위 타입이다.
예를 들어 `Int`는 `Number`의 하위 타입이지만 `String`의 하위 타입은 아니다.
이 정의는 모든 타입이 자신의 하위 타입이라는 뜻이기도 하다.

널이 될 수 있는 타입은 하위 타입과 하위 클래스가 같지 않은 경우는 보여주는 예다.
`String`은 `String?`의 하위 타입이지만 `String`은 `String?`의 하위 클래스가 아니다.

제네릭 타입을 인스턴스화할 때 타입 인자로 서로 다른 타입이 들어가면 인스턴스 타입 사이의 하위 타입 관계가 성릭하지 않으면
그 제네릭 타입을 무공변(`invariant`)이라고 한다.
`MutableList`를 예로 들면 A와 B가 서로 다르기만 하면 `MutableList<A>`는 항상 `MutableList<B>`의 하위 타입이 아니다.
자바에서는 모든 클래스가 무공변이다.

코틀린의 `List` 인터페이스는 읽기 전용 컬렉션을 표현한다. A가 B의 하위 타입이면 `List<A>`는 `List<B>`의 하위 타입이다.
그런 클래스나 인터페이스를 공변적(`covariant`)이라고 한다.

### 9.3.3 공변성: 하위 타입 관계를 유지
코틀린에서 제네릭 클래스가 타입 파라미터에 대해 공변적임을 표시하려면 타입 파라미터 이름 앞에 `out`을 넣어야 한다.

```kotlin
interface List<out T> {
    operator fun get(index: Int): T
}
```

클래스의 타입 파라미터를 공변적으로 만들면 함수 정의에 사용한 파라미터 타입과 타입 인자의 타입이
정확하게 일치하지 않더라도 그 클래스의 인스턴스를 함수 인자나 반환 값으로 사용할 수 있다.

```kotlin
open class Animal {
    fun feed() {
        println("feed")
    }
}

class Herd<out T : Animal> { // T는 공변적이다.
    val size: Int get() = 3
    operator fun get(i: Int): T {
        TODO()
    }
}

class Cat : Animal() {
    fun cleanLitter() {
        println("clean")
    }
}

fun feedAll(animals: Herd<Animal>) {
    for (i in 0 until animals.size) {
        animals[i].feed()
    }
}

fun takeCareOfCats(cats: Herd<Cat>) {
    for (i in 0 until cats.size) {
        cats[i].cleanLitter()
    }
    feedAll(cats) // 캐스팅이 필요 없다.
}


fun main() {
    val cats = Herd<Cat>()
    takeCareOfCats(cats)
}
```

모든 클래스를 공변적으로 만들 수는 없다. 공변적으로 만들면 안전하지 못한 클래스도 있다.
타입 파라미터를 공변적으로 지정하면 클래스 내부에서 그 파라미터를 사용하는 방법을 제한한다.
타입 안전성을 보장하기 위해 공변적 파라미터는 항상 `out` 위치에서만 사용해야 한다.
이는 클래스가 T타입의 값을 생산할 수는 있지만 T타입의 값을 소비할 수는 없다는 뜻이다.

클래스 멤버를 선언할 때 타입 파라미터를 사용할 수 있는 지점은 모두 `in`과 `out` 위치로 나눈다.
`T`라는 타입 파라미터를 선언하고 `T`를 사용하는 함수가 멤버로 있는 클래스를 생각해보자.
`T`가 함수의 반환 타입에 쓰인다면 `T`는 아웃 위치에 있다. 그 함수는 `T`타입의 값을 생산한다.
`T`가 함수의 파라미터 타입에 쓰인다면 `T`는 인 위치에 있다. 그 함수는 `T`타입의 값을 소비한다.

클래스 타입 파라미터 `T`앞에 `out`을 붙이면 클래스 안에서 `T`를 사용하는 메소드가 아웃 위치에서만
`T`를 사용하게 허용하고 인 위치에서는 `T`를 사용하지 못하게 막는다.
`out`키워드는 `T`의 사용법을 제한하며 `T`로 인해 생기는 하위 타입 관계의 타입 안정성을 보장한다.

타입 파라미터 `T`가 붙은 `out` 키워드는 다음 두 가지를 함께 의미한다.

- 공변성: `T`가 하위 타입 관계를 유지한다.
- 사용 제한: `T`를 아웃 위치에서만 사용할 수 있다.

이제 `List<T>` 인터페이스를 보자. 코틀린 `List<T>`는 읽기 전용이다. 따라서 그 안에는 `T` 타입의 원소를 반환하는
`get` 메소드는 있지만 리스트에 `T` 타입의 값을 추가하거나 리스트에 있는 기존 값을 변경하는 메소드는 없다.
따라서 `List`는 `T`에 대해 공변적이다.

```kotlin
interface List<out T> {
    operator fun get(index: Int): T
}
```

타입 파라미터를 함수의 파라미터 타입이나 반환 타입에만 쓸 수 있는 것은 아니다.
타입 파라미터를 다른 타입의 타입 인자로 사용할 수도 있다. 예를 들어 `List` 인터페이스에는
`List<T>`를 반환하는 `subList`라는 메소드가 있다.

```kotlin
interface List<out T> {
    fun subList(fromIndex: Int, toIndex: Int): List<T>
}
```
이 경우 `subList`함수에 쓰인 `T`는 아웃 위치에 쓰였다.

이런 위치 규칙은 오직 외부에서 볼 수 있는 (`public`, `protected`, `internal`) 클래스 API에서만 적용할 수 있다.
`private` 메소드의 파라미터는 인도 아니고 아웃도 아닌 위치다. 변성 규칙은 클래스 외부의 사용자가 클래스를 잘못 사용하는 일을 막기 위한 것이므로
클래스 내부 구현에는 적용되지 않는다.

### 9.3.4 반공변성: 뒤집힌 하위 타입 관계
반공변성(`contravariance`)은 공변성을 거울에 비친 상이라 할 수 있다. 반공변 클래스의 하위 타입 관계는 공변 클래스의 경우와 반대다.
예를 들어 `Comparator` 인터페이스를 살펴보자. 이 인터페이스에는 `compare`라는 메소드가 있다.
이 메소드는 주어진 두 객체를 비교한다.

```kotlin
interface Comparator<in T> {
    fun compare(e1: T, e2: T): Int // T를 인 위치에 쓰고 있다.
}

val anyComparator = Comparator<Any> { e1, e2 -> e1.hashCode() - e2.hashCode() }
val strings: List<String> = listOf("abc", "bac")
println(strings.sortedWith(anyComparator)) // [abc, bac]
```
이 인터페이스의 메소드는 `T` 타입의 값을 소비하기만 한다. 이는 `T`가 인 위치만 쓰인다는 뜻이다.

`sortedWith`함수는 `Comparator<String>`을 요구하므로 `String` 보다 더 일반적인 타입의 값을 비교하는 `Comparator<Any>`를 넘길 수 있다.

이는 `Comparator<Any>`가 `Comparator<String>`의 하위 타입이라는 뜻이다.

공변성, 반공변성, 무공변성 클래스

| 공변성                               | 반공변성                                          | 무공변성                    |
|-----------------------------------|-----------------------------------------------|-------------------------|
| `List<out T>`                     | `Comparator<in T>`                            | `MutableList<T>`        |
| 타입 인자의 하위 타입 관계가 제네릭 타입에서도 유지된다.  | 타입 인자의 하위 타입 관계가 제네릭 타입에서는 뒤집힌다.              | 하위 타입 관계가 성립하지 않는다.     |
| List<String>은 List<Any>의 하위 타입이다. | Comparator<Any>는 Comparator<String>의 하위 타입이다. |                         |
| `T`를 아웃 위치에서만 사용할 수 있다.           | `T`를 인 위치에서만 사용할 수 있다.                        | `T`를 아무 위치에서나 사용할 수 있다. |

클래스나 인터페이스가 어떤 타입 파라미터에 대해서는 공변적이면서 다른 타입 파라미터에 대해서는 반공변적일 수도 있다.
`Function` 인터페이스가 고전적인 예다. 다음 선언은 파라미터가 하나뿐인 `Function` 인터페이스인 `Function1`이다.

```kotlin
interface Function1<in P, out R> {
    operator fun invoke(p: P): R
}
```

코틀린 표기에서 `(P) -> R`은 `Function1<P, R>`을 더 알아보기 쉽게 적은 것인 뿐이다.
여기서 `P`는 인 위치에 쓰이고 `R`은 아웃 위치에 쓰인다.

### 9.3.5 사용 지점 변성: 타입이 언급되는 지점에서 변성 지정

클래스를 선언하면서 변성을 지정하면 그 클래스를 사용하는 모든 장소에 변성 지정자가 영향을 끼치므로 편리하다.
이런 방식을 선언 지정 변성(`declaration-site variance`)이라고 한다.
자바의 와일드카드 타입에 익숙하다면 자바는 변성을 다른 방식으로 다룬다는 점을 깨달았을 것이다.
자바에서는 타입 파라미터가 있는 타입을 사용할 때마다 해당 타입 파라미터를 하위 타입이나 상위 타입 중 어떤 타입으로 대치할 수 있는지
명시해야 한다.
이런 방식을 사용 지점 변성(`use-site variance`)이라고 한다.

코틀린도 사용지점 변성을 지원한다. 따라서 클래스 안에서 어떤 타입 파라미터가 공변적이거나 반공변적인지 선언할 수 없는 경우에도
특정 타입 파라미터가 나타나는 지점에서 변성을 정할 수 있다.

`MutableList`와 같은 상당수의 인터페이스는 타입 파라미터로 지정된 타입을 소비하는 동시에 생산할 수 있기 때문에 일반적으로
공변적이지도 반공변적이지도 않다.
하지만 그런 인터페이스 타입의 변수가 한 함수 안에서 생산자나 소비자 중 단 한가지 역할만을 담당하는 경우가 자주 있다.

```kotlin
fun <T> copyData(source: MutableList<T>, destination: MutableList<T>) {
    for (item in source) {
        destination.add(item)
    }
}
```

이 함수는 컬렉션의 원소를 다른 컬렉션으로 복사한다. 두 컬렉션 모두 무공변 타입이지만 원본 컬렉션에서는 읽기만 하고 대상 컬렉션에서는 쓰기만 한다.

코틀린에서는 함수 구현이 아웃위치에 있는 타입 파라미터를 사용하는 메소드만 호출한다면 그런 정보를 바탕으로 함수 정의 시
타입 파라미터에 변성 변경자를 추가할 수 있다.

```kotlin
fun <T> copyData(
    source: MutableList<out T>, // out 키워드를 타입을 사용하는 위치 앞에 붙이면 T 타입을 in 위치에 사용하는 메소드를 호출하지 않는다는 뜻이다. 
    destination: MutableList<T>) {
    for (item in source) {
        destination.add(item)
    }
}
```

타입 선언에서 타입 파라미터를 사용하는 위치라면 어디에나 변성 변경자를 붙일 수 있다. 따라서 파라미터 타입, 로컬 변수 타입,
함수 반환 타입 등에 타입 파라미터가 쓰이는 경우 `in`이나 `out` 변경자를 붙일 수 있다.

이때 타입 프로젝션(`type projection`)이 일어난다. 즉 `source`를 일반적인 `MutableList`가 아니라 `MutableList`를 프로젝션 한(제약을 가한) 타입으로 만든다.
이 경우 `copyData` 함수는 `MutableList`의 메소드 중에서 타입 파리미터 T를 아웃 위치에서 사용하는 메소드만 호출할 수 있다.

> 코틀린 사용 지점 변성 선언은 자바의 한정 와일드카드와 똑같다.
> 코틀린 `MutableList<out T>`는 자바 `MutableList<? extends T>`와 같다.
> 코틀린 `MutableList<in T>`는 자바 `MutableList<? super T>`와 같다.

### 9.3.6 스타 프로젝션: 타입 인자 대신 * 사용
타입 인자 정보가 없음을 표현하기 위해 `*`를 사용할 수 있다. 이를 스타 프로젝션(`star projection`)이라고 한다.

`MutableList<*>`는 `MutableList<Any?>`와 같지 않다. `MutableList<Any?>`는 모든 타입의 원소를 담을 수 있다는 사실을 알 수 있는 리스트다.
반면 `MutableList<*>`는 어떤 정해진 구체적인 타입의 원소만을 담는 리스트지만 그 원소의 타입을 정확히 모른다는 사실을 표현한다.

리스트의 타입이 `MutableList<*>`라는 말은 그 리스트가 `String`과 같은 구체적인 타입의 원소를 저장하기 위해 만들어진 것이라는 뜻이다.

```kotlin
fun printFirst(list: List<*>) {
    if (list.isNotEmpty()) {
        println(list.first())
    }
}

printFirst(listOf("Svetlana", "Dmitry")) // Svetlana
```

`printFirst` 함수는 `List`의 원소를 읽기만 하므로 `List`의 타입 인자에 대해 아무런 정보가 필요하지 않다.


```kotlin
interface FieldValidator<in T> {
    fun validate(input: T): Boolean
}

object DefaultStringValidator : FieldValidator<String> {
    override fun validate(input: String) = input.isNotEmpty()
}

object DefaultIntValidator : FieldValidator<Int> {
    override fun validate(input: Int) = input >= 0
}

object Validators {
    private val validators = mutableMapOf<KClass<*>, FieldValidator<*>>()

    fun <T: Any> registerValidator(kClass: KClass<T>, validator: FieldValidator<T>) {
        validators[kClass] = validator
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T: Any> get(kClass: KClass<T>): FieldValidator<T> {
        return validators[kClass] as? FieldValidator<T>
            ?: throw IllegalArgumentException("No validator for ${kClass.simpleName}")
    }
}

fun main() {
    Validators.registerValidator(String::class, DefaultStringValidator)
    Validators.registerValidator(Int::class, DefaultIntValidator)

    println(Validators[String::class].validate("Kotlin")) // true
    println(Validators[Int::class].validate(42)) // true
}
```

안전하지 못한 타입 로직을 클래스 내부에 감추고 외부에서 그 부분을 잘못 사용하지 않음을 보장할 수 있다.
Validators 객체에 있는 제네릭 메소드에서 검증기(`FieldValidator<T>`)와 클래스 (`KClass<T>`)의 타입 인자가 같기 때문에
컴파일러가 타입이 일치하지 않는 클래스와 검증기를 등록하지 못하게 막는다.