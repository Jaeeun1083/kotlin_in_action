# 9. 제네릭스


## 1. 제네릭 타입 파라미터

제네릭 타입의 인스턴스를 생성하기 위해서는 타입 파라미터를 구체적인 타입 인자(type argument)로 치환해야 한다. `Map<String, Person>`과 같이 구체적인 타입을 타입 인자로 넘기면 타입을 인스턴스화할 수 있다.

```kotlin
val authors = listOf("Dmitry", "Svetlana")
```

코틀린 컴파일러는 위 코드와 같이 `listOf`에 전달된 두 값이 문자열이기 때문에 컴파일러는 알아서 `List<String>` 타입임을 추론할 수 있다. 만약, 빈 리스트를 생성해야 한다면, 추론할 수 없기 때문에 직접 타입 인자를 명시해야 한다.

```kotlin
// 아래 두 코드는 동일한 선언이다.
val authors = mutableListOf<String>()
val authors: MutableList<String> = mutableListOf()
```

> 자바에서는 제네릭이 비교적 늦게 도입되어 타입 인자가 없는 로(raw) 타입을 허용한다. 하지만 코틀린에서는 이러한 로 타입을 지원하지 않아, 제네릭의 타입 인자를 항상 정의해야 한다.

### 1-1. 제네릭 함수와 프로퍼티

제네릭 함수를 호출할 때에는 반드시 구체적 타입으로 타입 인자를 넘겨야 한다. 컬렉션의 `slice` 함수는 구체적 범위 안에 든 원소만 포함하는 새 리스트를 반환한다. 이를 예시로 확인해보자.

```kotlin
// <T> : 타입 파라미터 선언
// List<T>.slice : 수신 객체 타입
// :List<T> : 반환 타입
public fun <T> List<T>.slice(indices: IntRange): List<T> {  
    if (indices.isEmpty()) return listOf()  
    return this.subList(indices.start, indices.endInclusive + 1).toList()  
}
```

위와 같은 함수를 구체적인 리스트에 대해 호출할 때, 타입 인자를 명시적으로 지정할 수 있지만, 대부분 컴파일러가 타입 인자를 추론할 수 있으므로 그럴 필요가 없다.

```kotlin
fun main() {  
    val letters = ('a'..'z').toList()  
    println(letters.slice<Char>(0..2))  
}
```

위처럼 `Char`을 작성하게 되면 컴파일러가 알아서 추론하기 때문에 생략이 가능하다며, IDE에서 `Remove explicit type arguments`를 띄워준다. 해당 예제는 일반적인 제네릭 함수라면, 이번에는 람다 함수에 제네릭을 포함하고 있는 `filter` 함수를 통해 확인해보자.

```kotlin
public inline fun <T> Iterable<T>.filter(predicate: (T) -> Boolean): List<T> {  
    return filterTo(ArrayList<T>(), predicate)  
}
```

```kotlin
val authors = listOf("Dmitry", "Svetlana")
authors.filter { it.startsWith("D") }
```

`predicate`의 타입을 보면 `(T) -> Boolean`으로 되어있는 것을 볼 수 있다. 이는, `filter` 블록에서 사용할 `it`의 타입을 `T`로 사용할 수 있도록 하는 것이다.

제네릭 확장 프로퍼티도 앞서 본 제네릭 함수와 동일하게 선언할 수 있다. 아래 함수는 리스트의 마지막 원소 바로 앞에 있는 원소를 반환하는 확장 프로퍼티이다.

```kotlin
val <T> List<T>.penultimate: T  
    get() = this[size - 2]  
  
fun main() {  
    val letters = ('a'..'z').toList()  
    println(letters.penultimate)
}
```

확장이 아닌 일반 프로퍼티는 타입 파라미터를 가질 수 없다. 클래스 프로퍼티에 여러 타입의 값을 저장할 수는 없으므로, 제네릭한 일반 프로퍼티는 말이 되지 않는다.

### 1-2. 제네릭 클래스 선언

자바와 마찬가지로 코틀린에서도 타입 파라미터를 넣은 꺾쇠 기호를 클래스 이름 뒤에 붙이면 제네릭하게 만들 수 있다.

```kotlin
interface List<T> { 
	// 인터페이스 안에서 T를 일반 타입처럼 사용할 수 있다.
	operator fun get(index: Int): T
}
```

제네릭 클래스를 확장하는 클래스를 정의하려면, 기반 타입의 제네릭 파라미터에 대해 타입 인자를 지정해야 한다. 이 때, 구체적인 타입을 넘겨도 되고, 하위 클래스도 제네릭 클래스일 경우 타입 파라미터로 받은 타입을 그대로 넘길수도 있다.

```kotlin
class StringList: List<String> {  
    override fun get(index: Int): String {  
        TODO("Not yet implemented")  
    }  
}
```

위 코드에서 `StringList` 클래스는 String 타입의 원소만을 포함하며, 상위 클래스에 정의된 함수를 오버라이드할 때, 타입 인자를 T가 아닌 구체적 타입인 String으로 치환해야 한다.

```kotlin
class ArrayList<T>: List<T> {  
    override fun get(index: Int): T {  
        TODO("Not yet implemented")  
    }  
}
```

위의 `ArrayList` 클래스는 타입 파라미터를 T로 정의하였고, 이를 기반 클래스의 타입 인자로도 사용한다.

### 1-3. 타입 파라미터 제약

타입 파라미터 제약(type parameter constraint)은 클래스나 함수에 사용할 수 있는 타입 인자를 제한하는 기능이다. 예를 들어, 리스트에 속한 모든 원소의 합을 구하는 `sum` 함수를 확인해보자.

```kotlin
// : Number -> 상한
fun <T : Number> List<T>.sum(): T
```

원소들에 대한 `sum`을 하기 위해서는 리스트에 있는 값이 숫자여야 연산할 수 있다. 때문에 위와 같이 `T : Number`를 사용해 타입 파라미터에 대한 상한(upper bound)를 지정해야 한다. 타입 파라미터가 `Number` 타입이거나, `Number`의 하위 타입이어야 해당 함수를 호출할 수 있도록 한다.

> 자바에서는 `<T extends Number> T sum(List<T> list)`와 같이 표현한다. 또한, 자바에서는 생산자와 소비자에 따라 extends와 super를 사용하는 PECS(Producer-Extends Consumer-Super) 규약이 존재한다.

이번에는 두 파라미터 사이에서 더 큰 값을 찾는 `max` 함수를 만들어보자.

```kotlin
fun <T : Comparable<T>> max(first: T, second: T): T {  
    return if(first > second) first else second  
}
```

`first > second`라는 식은 코틀린 연산자 관례에 따라 `first.comareTo(second) > 0`과 동일한 코드로 컴파일 된다.

```kotlin
public class String : Comparable<String>, CharSequence { ... }
```

```kotlin
println(max("kotlin", "java"))
```

위 코드와 같이 max를 호출할 때, 타입으로 사용하는 인자가 `Comparable`을 구현한 클래스여야 해당 함수를 사용할 수 있다. 만약 `Comparable`을 구현하지 않았을 경우에는 다음과 같은 컴파일 에러가 발생한다.

```kotlin
private class TestClass(val num: Int)  
fun main() {   
    max(TestClass(10), TestClass(20))  
}
```

```
Type mismatch.
Required: Comparable<TestClass>
Found: TestClass
```

드물게 타입 파라미터에 대해 둘 이상의 제약을 가해할 경우 `where` 키워드를 사용하면 된다. 아래 예제는 맨 끝에 마침표가 있는지 검사하는 예제 코드이다.

```kotlin
fun <T> ensureTrailingPeriod(seq: T)  
        where T : CharSequence, T : Appendable {  
            if(!seq.endsWith('.')) seq.split('.')  
}
```

```kotlin
val sb = StringBuilder("Hello World")  
ensureTrailingPeriod(sb)
// 출력 : Hello World.
println(sb)
```

`where`을 통해 타입 인자 T가 `CharSequence`, `Appendable`를 반드시 구현해야 한다는 사실을 표현한다.

### 1-4. 타입 파라미터를 널이 될 수 없는 타입으로 한정

제네릭 클래스나 함수를 정의하고 그 타입을 인스턴스화할 때, 보통 널이 될 수 있는 타입으로 지정되어 `Any?`를 상한으로 정한 것과 같은 효과를 낸다.

```kotlin
class Processor<T> {  
    fun process(value: T) {  
        value?.hashCode()  
    }  
}
```

위 코드에서 value의 파라미터 타입에 `?`가 붙어있지는 않지만, 실제로는 T에 해당하는 타입 인자로 널이 될 수 있는 타입을 넘길 수 있다. 아래 코드를 작성해보면 아무런 컴파일 경고도 내지 않는 것을 볼 수 있다.

```kotlin
fun main() {  
    val nullableStringProcessor = Processor<String?>()  
    nullableStringProcessor.process(null)  
}
```

이렇게 항상 nullable한 상태로 타입을 받을 수 없기 때문에 상한을 널이 될 수 없는 타입으로 지정해주는 제약을 가해야 한다. 널을 허용하는 가장 상한 클래스는 `Any?` 이므로, 이 대신 `Any`를 상한으로 지정하면, 널을 허용하지 않는 타입 파라미터가 된다.

```kotlin
private class Processor<T : Any> {  
    fun process(value: T) {  
        value.hashCode()  
    }  
}
```

이렇게 수정할 경우 컴파일러가 `nullableStringProcessor`를 생성하는 부분에서 다음과 같은 에러를 내뿜는다.

```
Kotlin: Type argument is not within its bounds: should be subtype of 'Any'
```

타입 파라미터를 널이 될 수 없는 타입으로 제약하기만 하면 타입 인자로 널이 될 수 있는 타입이 들어오는 일을 막을 수 있다. 다른 널이 될 수 없는 타입을 사용해 상한을 정해도 상관 없다.

### 2. 실행 시 제네릭스의 동작 : 타입 파라미터의 소거와 실체화

JVM의 제네릭스는 보통 타입 소거(type erasure)를 사용해 구현된다. 즉, 실행 시점에 제네릭 클래스의 인스턴스에 타입 인자 정보가 들어있지 않다는 뜻이다. 제네릭이 런타임에 소거되는 순서는 다음과 같다.

1. 제네릭 타입 `T`가 `Any`로 치환
    - 혹은 상한으로 지정한 타입
2. 제네릭 타입에 대한 변수를 상한 타입으로 캐스팅

### 2-1. 실행 시점의 제네릭 : 타입 검사와 캐스트

코틀린 역시 자바와 마찬가지로 타입 인자 정보는 런타임에 소거된다. 이 말은 제네릭 클래스 인스턴스를 생성할 때, 쓰인 타입 인자에 대한 정보를 유지하지 않는다는 것이다.

#### 2-1-1. 타입 검사

```kotlin
// 작성한 코드
val list: List<String> = listOf("Hello", "World")

// 런타임
val list: List = listOf("Hello", "World")
```

결국엔 `String`에 대한 정보가 지워지고, 안에 있는 원소만 남게 된다. 해당 원소를 꺼내 어떤 타입인지 검사할 수는 있지만, 기존에 만들었던 `List`의 파라미터 타입이 `Any` 였다면, `listOf` 안에 다양한 타입의 요소가 있어 타입을 검사하기 어려울 것이다.

아래 코드를 통해 실행 시점에 대해 더 확인해보자.

```kotlin
val list1: List<String> = listOf("a", "b")  
val list2: List<Int> = listOf(1, 2, 3)
```

컴파일러는 두 리스트를 서로 다른 타입으로 인식하지만, 실행 시점에 위 두 객체는 완전히 같은 타입(List)의 객체이다. 우리는 `list1`에는 문자열, `list2`에는 숫자가 들어있다고 확정할 수 있는 이유는 컴파일러가 타입 인자를 알고, 올바른 타입의 값만 넣도록 보장해주기 때문이다.

> 자바의 경우 로(raw) 타입을 사용해 리스트에 접근한 뒤, 타입 캐스트를 활용하면 컴파일러를 속일 수 있다. [Effective Java 3/E Item26. 로타입은 사용하지 말라]를 보면 로 타입의 단점을 알 수 있다.

타입이 소거되면, 타입 인자를 따로 저장하지 않기 때문에 실행 시점에 타입 인자를 검사할 수 없다. A라는 리스트가 문자열로 이뤄진 리스트인지, 다른 객체로 이뤄진 리스트인지를 실행 시점에 `is`를 이용해 검사할 수 없는 것이다.

```kotlin
fun <T> checkType(value: T) {
	// 컴파일 에러 : Cannot check for instance of erased type: List<String>
    if (value is List<String>) {  
        println("True")  
    }  
}
```

실행 시점에 어떤 값이 List 타입인 것은 확실히 알 수 있지만, 그 리스트가 어떤 타입의 리스트인지는 알 수 없는 것이다. 다만, 이런 타입 정보를 지워서 얻는 장점은 있다. 저장해야 하는 타입 정보의 크기가 줄어들어서 전반적인 메모리 사용량이 줄어들게 된다.

앞서 말한 것과 같이 위 코드의 `value`가 `List` 타입인 것은 어떻게 확실할 수 있을까? 자바의 경우 로(raw) 타입을 지원하기 때문에 다음과 같이 검사할 수 있다.

```java
public class ListTypeCheckTest {  
    public static <T> void checkType(T value) {  
        if (value instanceof List) {  
            System.out.println("True");  
        }  
    }  
    public static void main(String[] args) {  
        List<String> strList = List.of("Hello", "World");  
        checkType(strList);  
    }  
}
```

하지만 코틀린은 로타입을 지원하지 않기 때문에 다음과 같이 작성할 경우 컴파일 에러가 발생한다.

```kotlin
fun <T> checkType(value: T) {
	// List<*>로 수정!
    if (value is List) {  
        println("True")  
    }  
}
```

때문에 코틀린에서는 스타 프로젝션(star projection)을 사용해 `List<*>`로 수정하면 된다. 이렇게 작성할 경우, 타입 원소는 몰라도 해당 타입이 `List`라는 것은 검사할 수 있다. 스타 프로젝션은 자바의 `List<?>`와 비슷하다고 생각하면 된다.

#### 2-1-2. 캐스트

제네릭 타입을 검사할 때, `as` 혹은 `as?`를 이용한 캐스팅도 가능하다. 하지만 기저 클래스는 같지만 타입 인자가 다른 타입으로 캐스팅해도 여전히 캐스팅에 성공한다는 점을 주의해야 한다.

```kotlin
fun typeCastTest(value: Collection<*>) {  
    val list = value as List<Int>  
        ?: throw IllegalArgumentException("List is expected")  
    for (i in list) {  
        println(i)  
    }  
}  

fun main() {  
    val list1: List<String> = listOf("a", "b")  
    typeCastTest(list1)
}
```

실행 시점에는 제네릭 타입의 타입 인자를 알 수 없어, 캐스팅은 항상 성공한다. 하지만 위 코드처럼 스타 프로젝션을 타입 파라미터로 지정한 뒤, 캐스팅을 진행할 경우, 정상적인 캐스팅을 할 수 없을 수도 있어 다음과 같은 비검사 경고가 발생한다.

```
Unchecked cast: Collection<*> to List<Int>
```

실제로 String에 대한 리스트를 해당 함수에 보내줄 경우 Int 타입으로 캐스팅을 하는 과정에서 `ClassCastException`이 발생한다. 만약 숫자 리스트만 사용해야할 경우에는 스타 프로젝션이 아닌 정확한 타입을 지정하고, `as`를 통한 캐스팅이 아닌 `is`를 통한 타입 검사를 진행 후, 로직을 작성하는 것이 옳다.

```kotlin
fun typeCastTest(value: Collection<Int>) {  
    if (value is List<Int>) {  
        for (i in value) {  
            println(i)  
        }  
    }  
}
```

코틀린 컴파일러는 안전하지 못한 검사와 수행할 수 있는 검사를 알려주기 위해 최대한 노력한다. 예를 들어 안전하지 못한 is 검사 자체는 금지하고, 위험한 as 캐스팅은 경고를 출력한다.

### 2-2. 실체화한 타입 파라미터를 사용한 함수 선언

앞서 얘기한 것과 같이 제네릭 타입 인자 정보는 실행 시점에 소거된다. 제네릭 함수도 이와 마찬가지이다.

```kotlin
// 컴파일 에러 : Cannot check for instance of erased type: T
fun <T> isA(value: Any) = value is T
```

위와 같이 지워진 유형의 인스턴스를 확인할 수 없다는 에러가 발생한다. 하지만 이러한 제약을 피할 수 있는 방법이 하나 있는데, 바로 인라인 함수를 사용하는 것이다. 인라인 함수의 타입 파라미터는 실체화되므로 실행 시점에 인라인 함수의 타입 인자를 알 수 있다.

```kotlin
inline fun <reified T> isA(value: Any) = value is T
```

이렇게 타입 파라미터를 `reified`로 지정할경우 value의 타입이 T의 인스턴스인지를 실행 시점에 검사할 수 있다.

> inline 키워드를 붙이면 컴파일러는 해당 함수를 호출한 식을 함수 본문으로 바꾼다. 람다를 사용할 경우 람다 코드도 함께 인라인이 되기 때문에 성능이 더 좋아질 수 있다.

실제로 컬렉션의 `filterIsInstance` 함수는 원소 중에서 타입 인자로 지정한 클래스의 인스턴스만을 모아 만든 리스트를 반환한다.

```kotlin
fun main() {  
    val items = listOf("one", 2, "three")  
    println(items.filterIsInstance<String>())  
}
```

```kotlin
public static final void main() {  
   List items = CollectionsKt.listOf(new Object[]{"one", 2, "three"});  
   Iterable $this$filterIsInstance$iv = (Iterable)items;  
   int $i$f$filterIsInstance = false;  
   Collection destination$iv$iv = (Collection)(new ArrayList());  
   int $i$f$filterIsInstanceTo = false;  
   Iterator var6 = $this$filterIsInstance$iv.iterator();  
  
   while(var6.hasNext()) {  
      Object element$iv$iv = var6.next();  
      if (element$iv$iv instanceof String) {  
         destination$iv$iv.add(element$iv$iv);  
      }  
   }  
  
   List var8 = (List)destination$iv$iv;  
   System.out.println(var8);  
}
```

실제로 자바 코드로 디컴파일해보면 위와 같이 String으로 형변환을 하고 있는 모습을 볼 수 있다. 그렇다면 왜 인라인 함수에서만 실체화한 타입 인자를 쓸 수 있는 것일까?

컴파일러는 인라인 함수의 본문을 구현한 바이트코드를 함수가 호출되는 모든 지점에 삽입한다. 때문에 실체화한 타입 인자를 사용해 인라인 함수를 호출하는 각 부분의 정확한 타입 인자를 알 수 있다. 따라서 컴파일러는 타입 인자로 쓰인 구체적인 클래스를 참조하는 바이트 코드를 생성해 삽입할 수 있게 되는 것이다.

코틀린 코드를 변환한 바이트 코드를 살펴보면 다음과 같다.

```java
// items.filterIsInstance<String>() 호출
LINENUMBER 9 L1
   ...
   L4
    ICONST_0
    ISTORE 5
	// _Collections.kt / filterIsInstanceTo 함수 본문 실행
   L5
    LINENUMBER 20 L5
    ALOAD 3
    INVOKEINTERFACE java/lang/Iterable.iterator ()Ljava/util/Iterator; (itf)
    ASTORE 6
    // Iterator를 통해 각 원소를 순회
   L6
    ALOAD 6
    INVOKEINTERFACE java/util/Iterator.hasNext ()Z (itf)
    IFEQ L7
    ALOAD 6
    INVOKEINTERFACE java/util/Iterator.next ()Ljava/lang/Object; (itf)
    ASTORE 7
    // String 타입에 대한 검사 진행
   L8
    ALOAD 7
    INSTANCEOF java/lang/String
    IFEQ L6
    ALOAD 4
    ALOAD 7
    INVOKEINTERFACE java/util/Collection.add (Ljava/lang/Object;)Z (itf)
    POP
   L9
    GOTO L6
```

이렇게 타입 파라미터가 아닌 구체적인 타입을 사용하기 때문에 만들어진 바이트코드는 실행 시점에 벌어지는 타입 소거에 대한 영향을 받지 않는다. 단, 자바에서는 `reified` 타입 파라미터를 사용한 인라인 함수를 호출할 수 없다.

성능을 좋게 하려면 인라인 함수의 크기를 계속 관찰해야 한다. 함수가 커질 경우 실체화한 타입에 의존하지 않는 부분을 별도의 일반함수로 뽑아내는 편이 낫다.

### 2-3. 실체화한 타입 파라미터로 클래스 참조 대신

코틀린에서 클래스에 대한 참조를 얻기 위해서는 다음과 같이 작성한다.

```kotlin
Service::class.java
```

해당 코드를 작성하면 `Service`라는 자바 클래스를 받아와 리플렉션을 이용해 활용할 수 있다. 대표적으로 표준 자바 API인 `ServiceLoader`는 어떤 추상 클래스나 인터페이스를 표현하는 `java.lang.Class`를 받아서 그에 대한 구현체 인스턴스를 반환해준다.

```kotlin
interface Service { ... }
  
fun main() {  
    val service: ServiceLoader<Service> = ServiceLoader.load(Service::class.java)  
}
```

이렇게 작성하는 것도 나쁘진 않지만, 클라이언트 코드에서 `::class.java`를 쓰는 것은 다른 사람이 봤을 때 크게 이해하기 어려울 수 있고, 작성하기도 불편하다. 이를 구체화한 타입 파라미터를 사용해 수정하면 다음과 같이 수정할 수 있다.

```kotlin
inline fun <reified T> loadService(): ServiceLoader<T> = ServiceLoader.load(T::class.java)  
  
fun main() {  
    val service = loadService<Service>()  
}
```

이렇게 클래스를 타입 인자로 지정하면 `::class.java`라고 쓰는 경우보다 훨씬 더 이해하기도 쉽고, 읽기도 쉬워진다.

## 3. 변성 : 제네릭과 하위 타입

변성(variance)은 `List<String>`와 `List<Any>`와 같이 기저 타입이 같고 타입 인자가 다른 여러 타입이 서로 어떤 관계가 있는지 설명하는 개념이다.

### 3-1. 변성이 있는 이유 : 인자를 함수에 넘기기

`List<Any>`를 인자로 받는 함수에 `List<String>`을 넘기면 과연 안전할까?

```kotlin
fun anyList(list: List<Any>) {  
	println(list)  
}  
  
fun main() {  
	val list = listOf<String>("Hello")  
	anyList(list)  
}
```

우선 위 코드는 정상적으로 컴파일된다. 간단하게 타입만 보면 `String` 클래스는 `Any`를 확장하므로 안전하다. 하지만 이 타입들이 `List` 인터페이스의 타입 인자로 들어갈 경우 안전하다고는 할 수 없다. 예를 들어 리스트를 변경하는 함수가 있다고 가정해보자.

```kotlin
fun addAnswer(list: MutableList<Any>) {  
	list.add(42)  
}  
  
fun main() {  
	val list = mutableListOf("Hello")  
	// 컴파일 에러 : Type mismatch
	addAnswer(list)  
	println(list.maxBy { it.length })  
}
```

만약 위 코드가 컴파일 된다면 `list.maxBy{ .. }`에서 에러가 발생할 것이다. 때문에 코틀린 컴파일러는 이런 함수 호출을 금지하며, 변경이 가능한 `MutableList<Any>`에 `MutableList<String>`을 넘겨줄 수 없는 것이다.

앞서 본 변경이 가능하지 않은 `List<Any>`에 `List<String>`을 넘겨주는 것은 기존 리스트에 변화를 주지 않기 때문에 안전하다는 것을 알 수 있다.

### 3-2. 클래스, 타입, 하위 타입

제네릭 클래스가 아닌 클래스에서는 클래스 이름을 바로 타입으로 쓸 수 있으며, 동시에 `nullable`한 타입으로도 정의할 수 있게된다.

```kotlin
class Post(val title: String, val content: String)

fun main() {
	val post: Post = Post("암어", "쥑쥑")
	val nullablePost: Post? = Post("발빠진", "쥐")
}
```

하지만 제네릭 클래스 경우 조금 복잡하다. 올바른 타입을 얻기 위해서는 타입 파라미터를 구체적인 타입 인자로 바꿔줘야 한다. 위처럼 `Post`는 타입으로 지정할 수 있지만, `List`의 경우 클래스는 맞지만, 해당 클래스 자체로 타입을 지정할 수는 없다. 타입으로 지정하기 위해서는 `List<String>`과 같이 타입 인자를 넣어줘야 한다.

이런 타입 관계를 조금 더 이해하기 위해서는 하위 타입(subtype)과 상위 타입(supertype)에 대해 알아야 한다.

```kotlin
fun test(i: Int) {
	// Int는 Number의 하위 타입
	val n: Number = i // 컴파일 가능

	// Int는 String의 상위 타입이 아님
	fun f(s: String) {...}
	f(i) // 컴파일 불가능
}
```

위 코드를 보면, `i`는 `Number`의 하위 타입이기 때문에 컴파일이 가능하다. 하지만 `i`가 함수 `f`의 인자인 `s`의 상위 타입이나 하위 타입이 아니기 때문에 해당 부분에서는 컴파일 에러가 발생한다.

`nullable`한 타입의 경우 `?`가 붙은 클래스가 더 상위 타입으로 존재한다. 두 타입은 모두 같은 클래스에 해당하지만, `nullable`한 타입에 널이 될 수 없는 타입의 변수를 저장할 수는 없다.

### 3-3. 공변성 : 하위 타입 관계를 유지

A라는 클래스가 B 클래스를 상속 받았을 경우를 예로 확인해보자.

`Producer<A>`와 `Producer<B>`가 있을 때, `Producer<A>`는 항상 `Producer<B>`의 하위 타입이 아니다. 이렇게 타입 인자가 서로 다를 경우 그 제네릭 타입을 무공변(invariant)라고 한다.

특정 제네릭 클래스에서 A 클래스를 B의 하위 타입으로서 활용하고 싶다면, 타입 파라미터에 `out`을 붙여 공변적임을 표시해야 한다. 이렇게 하면 하위 타입의 관계를 유지할 수 있다.

```kotlin
class Producer<out T> { ... }
```

아래 코드의 `Herd` 클래스는 특정 동물 `T`에 대한 떼를 표현하며, `feedAll`은 특정 동물 무리 모두에게 밥을 먹이는 함수이다.

```kotlin
open class Animal {  
	fun feed() = println("Yummy")  
}
```

```kotlin
class Herd<T : Animal> {  
	val size: Int get() = ...
	operator fun get(i: Int): T = ...
}
```

```kotlin
fun feedAll(animals: Herd<Animal>) {  
	for (i in 0 until animals.size) {  
		animals[i].feed()  
	}  
}
```

이제 사용자 코드에서 동물의 하위 타입인 고양이 클래스를 생성해주고, 털을 빗어주는 함수를 넣어보자.

```kotlin
class Cat : Animal() {  
	fun cleanLitter() = ...
}
```

```kotlin
fun takeCareOfCats(cats: Herd<Cat>) {  
	for (i in 0 until cats.size) {  
		cats[i].cleanLitter()
		// Type mismatch: inferred type is Herd<Cat> but Herd<Animal> was expected
		feedAll(cats)  
	}  
}
```

위 코드를 보면 고양이들을 `feedAll` 함수의 인자로 넣을 수 없어 밥을 먹일 수 없다. 에러 로그를 살펴보면 `Animal` 타입을 기대했지만, 실제로 들어온 추론된 유형은 `Cat`이라고 한다.

앞서 설명한 것과 같이 아무리 `Cat` 클래스가 `Animal` 클래스의 하위 타입이더라도, `Herd` 클래스의 `T` 타입 파라미터에 아무 변성을 지정해주지 않았기 때문에 고양이 무리는 동물 무리의 하위 클래스라 할 수 없다.

> 무공변 : 아무 변성을 지정해주지 않았다.

하위 타입의 관계를 유지하려면 다음과 같이 `Herd` 클래스의 타입 인자 앞에 `out`을 넣어 해당 타입 인자가 하위 타입과의 관계를 유지할 수 있도록 공변적임을 표시해주어야 한다.

```kotlin
class Herd<out T : Animal> {  
	...
}
```

타입 파라미터를 공변적으로 지정하면, 타입 안전성을 보장하기 위해 공변적 파라미터는 항상 `out` 위치에만 있어야 한다. 즉, `T` 타입의 값을 생산할 수는 있지만, `T` 타입의 값을 소비할 수 없다는 것이다.

> `T`가 함수의 반환 타입에 쓰인다면 `T` 타입의 값을 **생산(produce)** 하는 것이므로, `T`는 `out` 위치에 있는 것이다. 반대로, `T`가 함수의 파라미터 타입에 쓰일 경우 `T`는 `in` 위치에 있게 되고, `T` 타입의 값을 **소비(consume)** 한다.

즉, `out` 키워드를 사용하는 것은 해당 `T`를 생산할 수 있다는 제약을 거는 것과 동일한 것이다. 이 말은 `in`의 위치에 `T`를 사용할 수 없다는 것과 동일하다. 이런 제한을 통해 `T`로 인해 생기는 하위 타입 관계의 타입 안전성을 보장할 수 있다.

### 3-4. 반공변성 : 뒤집힌 하위 타입 관계

반공변성(contravariance)은 공변성을 거울에 비친 상이라 할 수 있다. `Comparator`를 예시로 확인해보자.

```kotlin
interface Comparator<in T> {
	fun compare(e1: T, e2: T): Int {...}
}
```

`compare`은 T의 값만 소비하기 때문에 `in` 위치에만 쓰인다. 이제 이를 활용한 `sortedWith`를 확인해보자.

```kotlin
public fun <T> Iterable<T>.sortedWith(comparator: Comparator<in T>): List<T> {  
	if (this is Collection) {  
		if (size <= 1) return this.toList()  
		@Suppress("UNCHECKED_CAST")  
		return (toTypedArray<Any?>() as Array<T>).apply { sortWith(comparator) }.asList()  
	}  
	return toMutableList().apply { sortWith(comparator) }  
}
```

```kotlin
fun main() {  
	val anyComparator = Comparator<Any> {  
		e1, e2 -> e1.hashCode() - e2.hashCode()  
	}  
	  
	val strings = listOf("S1", "S2")  
	strings.sortedWith(anyComparator)  
}
```

앞서 봤듯이 `Comparator`는 `in`이라는 키워드를 통해 변성을 지정해줬으므로, 무공변이 아니다. `Any` 타입은 모든 타입의 조상이기 때문에 `String` 타입의 리스트를 정렬하기 위해 `anyComparator`를 넘겨줄 수 있다. 앞서 나온 `out`은 하위 타입인 공변 의미했다면, `in`은 상위 타입의 관계인 반공변을 의미하는 것을 알 수 있다.

타입 B가 타입 A의 하위 타입인 경우 `Consumer<A>`가 `Consumer<B>`의 하위 타입인 관계가 성립된다. 그러면 제네릭 클래스 `Consumer<T>`는 타입 인자 `T`에 대해 반공변이다. 즉, `Consumer<Animal>`은 `Consumer<Cat>`의 하위 타입이라고 볼 수 있는 것이다.

`in` 키워드를 사용하면, 해당 키워드가 붙은 타입이 해당 클래스의 메소드 안으로 전달(passed in)되어 해당 메소드에 의해 소비(consume)된다는 뜻이다.

| 공변성 | 반공변성 | 무공변성 |
| :--: | :--: | :--: |
| `Producer<out T>` | `Consumer<in T>` | `MutableList<T>` |
| 타입 인자의 하위 타입 관계 유지 | 타입 인자의 하위 타입 관계가 뒤집힘 | 하위 타입 관계 성립하지 않음 |
| `Producer<Cat>`은<br>`Producer<Animal>`의 하위 타입 | `Producer<Animal>`은<br>`Producer<Cat>`의 하위 타입 |  |
| T를 아웃 위치에서만 사용 가능 | T를 인 위치에서만 사용 가능 | T를 아무 위치에서 사용 가능 |

선언한 파라미터가 하나뿐인 `Function1` 인터페이스를 봐보자.

```kotlin
public interface Function1<in P1, out R> : Function<R> {  
	public operator fun invoke(p1: P1): R  
}
```

P1은 소비자 위치인 `in`에 있고, `R`은 생산자 위치인 `out`에 있는 것을 볼 수 있다. 이는, 함수 `Function1`의 하위 타입 관계는 첫 번째 타입 인자(P1)의 하위 타입 관계와는 반대지만, 두 번째 타입 인자(R)의 하위 타입 관계와는 같음을 의미한다.

```kotlin
fun enumerateCats(f: (Cat) -> Number) {}  
fun Animal.getIndex(): Int = 10  
  
fun main() {  
	enumerateCats(Animal::getIndex)  
}
```

위 코드를 보면 `Animal`은 `Cat`의 상위 타입이며, `Int`는 `Number`의 하위 타입이므로 올바른 코드이다.

### 3-5. 사용 지점 변성 : 타입이 언급되는 지점에서 변성 지정

클래스를 선언하면서 변성을 지정하면 해당 클래스를 사용하는 모든 장소에 변성 지정자가 영향을 끼치므로 편리하다. 이를 선언 지점 변성(declaration site variance)이라 부른다.

반대로 사용 지점 변성(use-site variance)는 타입 파라미터가 있는 타입을 사용할 때마다 해당 타입 파라미터를 하위 타입이나 상위 타입 중 어떤 타입으로 대치할 수 있는지 명시하는 것을 의미한다.

```kotlin
fun <T> copyData(  
    source: MutableList<T>,  
    destination: MutableList<T>,  
) {  
    for (item in source) {  
        destination.add(item)  
    }  
}
```

위 코드는 무공변 파라미터 타입을 사용하는 데이터 복사 함수이다. `MutableList`와 같은상당수의 인터페이스는 타입 파라미터로 지정된 타입을 소비하는 동시에 생산할 수 있기 때문에 공변적이지도 반공변적이지도 않다.

하지만 위 코드를 보면 두 컬렉션 모두 무공변 타입이지만, 원본 컬렉션에서는 읽기만 하고, 대상 컬렉션에는 쓰기만 한다. 이런 경우 두 컬렉션의 원소 타입이 정확하게 일치할 필요가 없다.

```kotlin
fun <T : R, R> copyData2(  
    source: MutableList<T>,  
    destination: MutableList<R>,  
) {  
    for (item in source) {  
        destination.add(item)  
    }  
}
```

```kotlin
fun main() {  
    val ints = mutableListOf(1, 2, 3)  
    val anyItems = mutableListOf<Any>()  
    copyData2(ints, anyItems)  
    println(anyItems)  
}
```

위와 같이 두 번째 제네릭 타입 파라미터를 도입해 여러 다른 리스트 타입에 대해 작동하게 만들 수 있다. 이 코드를 위에서 본 공변성을 적용하면 더 간결하게 줄일 수 있다.

```kotlin
fun <T> copyData3(  
    source: MutableList<out T>,  
    destination: MutableList<T>,  
) {  
    for (item in source) {  
        destination.add(item)  
    }  
}
```

우선 `Int` 타입은 `Any`의 하위 타입이고, `source`는 `T`에 대한 값을 생산하는 위치에 있으므로 `out`을 사용하였다. 이 때, 타입 프로젝션(type projection)이 일어나는데, `source`를 일반적인 `MutableList`가 아니라 `MutableList`를 프로젝션을 한(제약을 가한) 타입으로 만든다.

```kotlin
val list: MutableList<out Number> = mutableListOf(1, 2, 3)  
// Kotlin: The integer literal does not conform to the expected type Nothing
list.add(42)
```

### 3-6. 스타 프로젝션 : 타입 인자 대신 \* 사용

제네릭 타입 인자 정보가 없음을 표현할 때, 스타 프로젝션(star projection)을 사용한다. 예를 들어, 원소 타입이 알려지지 않은 리스트는 `List<*>`로 표현할 수 있다.

#### \* != Any?

`MutableList<*>`과 `MutableList<Any?>`는 서로 다른 타입이다. `Any?`의 경우 어떤 타입이라도 담을 수 있지만, `*`의 경우 어떤 정해진 구체적인 타입의 원소만 담는 리스트이다. 단, 그 원소의 타입을 정확히 모른다는 사실을 표현한 것 뿐이다. 즉, `MutableList<*>`이라는 타입은 해당 리스트가 `String`과 같이 구체적인 타입 원소를 저장하기 위해 만들어진 것이다.

> `*`이 어떤 원소 타입인지는 알 수 없지만, 결국엔 `Any?`의 하위 타입이다.

```kotlin
fun main() {  
    val list: MutableList<Any?> = mutableListOf('a', 1, "QWE")  
    val chars = mutableListOf('a', 'b', 'c')  
    val unknownElements: MutableList<*> = if (Random().nextBoolean()) list else chars  
  
    println(unknownElements)  

	// The integer literal does not conform to the expected type Nothing
    unknownElements.add(42)  
}
```

위 코드를 보면 `unknownElements`는 `MutableList` 임에도 불구하고, 추가적인 값을 넣을 수 없다. 즉, `MutableList<out Any?>`처럼 동작을 하고 있는 것이다. 리스트의 타입 파라미터가 `Any?`일 경우 모든 타입의 값을 저장할 수 있고, 그 원소를 꺼내올 수도 있다. 하지만 `*`의 경우 구체적인 타입을 알 수 없기 때문에 안전하게 원소를 꺼내오는 행위만 제공하는 것이다.

> `Type<*>`은 자바의 `Type<?>`에 대응하며, `Consumer<in T>`와 같은 반공변 타입파라미터에 대한 스타 프로젝션은 `<in Nothing>` 과 동등하다.

타입 파라미터를 시그니처에서 전혀 언급하지 않거나 데이터를 읽기는 하지만, 그 타입에는 관심이 없는 경우에도 해당 구문을 사용할 수 있다.

```kotlin
fun printFirst(list: List<*>) {  
    if (list.isNotEmpty()) {  
        println(list.first())  
    }  
}
```

위 코드에서는 타입 파라미터를 전혀 사용하지 않고, 리스트가 비어있는지 여부만 확인 후 결과를 출력했다. 여기서 `first()`를 통해 찾은 객체는 `Any?` 타입이지만, 출력만 하기 때문에 크게 문제 없다.

> 스타 프로젝션을 사용하는 경우에는 값을 만들어내는 메소드만 호출할 수 있고, 그 값의 타입은 `Any?`로 나오기 때문에 타입에 신경쓰지 않아야할 때만 사용하는 것이 좋다.

스타 프로젝션을 사용하는 방법과 이를 사용 시 빠지기 쉬운 함정에 대해 알아보자. 아래 코드는 사용자 입력을 검증하기 위해 `FieldValidator` 인터페이스를 만들고, `T`를 `in` 위치에만 쓰일 수 있도록 하여, 검증만 하도록 만들었다.

```kotlin
private interface FieldValidator<in T> {  
    fun validate(input: T): Boolean  
}
```

```kotlin 
private object DefaultStringValidator : FieldValidator<String> {  
    override fun validate(input: String) = input.isNotEmpty()  
}  
  
private object DefaultIntValidator : FieldValidator<Int> {  
    override fun validate(input: Int) = input >= 0  
}
```

String과 Int 검증기를 Map에 담은 뒤, 필요에 따라 적절한 검증기를 꺼내 사용하도록 만들어보자.

```kotlin
fun validatorTest() {  
    val validators = mutableMapOf<KClass<*>, FieldValidator<*>>()  
    validators[String::class] = DefaultStringValidator  
    validators[Int::class] = DefaultIntValidator  
}
```

위와 같이 정의하고, 검증기를 사용할 경우 String 타입의 필드를 `FieldValidator<*>` 타입의 검증기로 검증할 수 없다. 컴파일러는 `FieldValidator<*>`가 어떤 타입을 검증하는 검증기인지 모르기 때문에 String을 검증하기 위해 해당 검증기를 사용하면 안전하지 않다고 판단한다.

```kotlin 
// Out-projected type 'FieldValidator<*>' prohibits
validators[String::class]!!.validate("Hello")
```

해당 오류는 `FieldValidator<*>`에서 스타 프로젝션을 사용했기 때문에 알 수 없는 타입의 검증기에 구체적인 타입의 값을 넘길 경우 안전하지 못하다는 뜻이다. 이런 문제는 검증기 자체를 원하는 타입으로 캐스팅하면 고칠 수 있다.

```kotlin
// Unchecked cast: FieldValidator<*>? to FieldValidator<String>
val stringValidator = validators[String::class] as FieldValidator<String>  
stringValidator.validate("Hello")
```

하지만 이런 타입 캐스팅은 안전하지 못해 권장하지 않으며, 컴파일러 또한 이에 대한 경고를 내뿜는다. 또한, 검증기를 잘못 가져올 경우에도 컴파일 에러가 발생하지 않는다.

```kotlin
val stringFaultValidator = validators[Int::class] as FieldValidator<String>  
println(stringFaultValidator.validate(""))
```

이를 해결하기 위해서는 검증기를 등록하거나 가져오는 작업을 수행할 때, 타입을 제대로 검사할 수 있도록 캡슐화해야 한다. 해당 방식도 안전하지 않은 캐스팅 오류를 컴파일 시 발생시키지만, Validators 객체가 맵에 대한 접근을 통제하기 때문에 잘못된 값이 들어가지 못하게 막을 수 있다.

```kotlin
object Validators {  
    private val validators = mutableMapOf<KClass<*>, FieldValidator<*>>()  
      
    fun <T : Any> registerValidator(  
        kClass: KClass<T>, fieldValidator: FieldValidator<T>  
    ) {  
        validators[kClass] = fieldValidator  
    }  
      
    @Suppress("UNCHECKED_CAST")  
    operator fun <T : Any> get(kClass: KClass<T>): FieldValidator<T> =  
        validators[kClass] as? FieldValidator<T>  
            ?: throw IllegalArgumentException(  
                "No validator for ${kClass.simpleName}"  
            )  
}
```

이 방식을 통해, 특정 클래스와 검증기가 타입이 맞아 떨어지는 경우에만 해당 클래스와 검증기 정보를 맵에 추가해주며, 안전하지 않은 캐스팅에 대한 코드는 외부로부터 감춰 이를 사용하는 코드에서는 잘못 사용하지 않았음을 보장할 수 있다.

```kotlin
// The integer literal does not conform to the expected type String
println(Validators[String::class].validate(42))
```

앞서 작성한 방식은 컴파일러가 에러를 잡아주지 않았지만, 이를 개선한 방식에서는 캡슐화를 통해 컴파일러가 타입이 일치하지 않는 클래스와 검증기를 등록하지 못하도록 막아줄 수 있다.