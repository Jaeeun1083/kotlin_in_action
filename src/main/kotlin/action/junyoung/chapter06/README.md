## 1. 널 가능성

널 가능성(nullability)은 NPE(Null Pointer Exception)을 피할 수 있게 돕기 위한 코틀린 타입 시스템의 특성이다. 널이 될 수 있는지 여부를 타입 시스템에 추가함으로써 컴파일러가 여러가지 오류를 컴파일 타임에 미리 감지해서 런 타임 시점에 발생할 수 있는 예외의 가능성을 줄여준다.

### 1-1. 널이 될 수 있는 타입

코틀린에서는 널이 될 수 있는 타입을 자바와 다르게 명시적으로 지원한다.

```java
int strLen(String s) {
	return s.length()
}
```

위 코드에 null을 넘기면 길이를 가져오기 위해 `s.`을 하는 순간 NPE가 발생할 것이다. 이와 같이 자바에서는 함수의 매개변수가 null인지 검사할 필요가 있다. 그럼 코틀린 코드는 어떻게 다른지 확인해보자.

```kotlin
fun strLen(s: String) = s.length
```

위 코드는 strLen의 매개변수로 null을 넣을 수 없는 상태이다. 혹시라도 null을 넣는 시도를 한다면 다음과 같은 컴파일 에러를 마주치게 된다.

```bash
Null can not be a value of a non-null type String
```

이 이유는 코틀린에서 null을 받을 수 있는 타입이 따로 분류되기 때문이다. 만약 null을 허용하고 싶다면 `strLen(s: String?)`과 같이 물음표(?)를 붙여줘야 한다. 어떤 타입이든 타입 뒤에 물음표를 붙이면 그 타입의 변수나 프로퍼티에 null을 참조 저장할 수 있게 된다.

또한, null이 될 수 있는 값을 null이 될 수 없는 타입의 변수에 값을 대입할 경우 컴파일 에러가 발생한다.

```kotlin
var aInt: Int = 10  
var bInt: Int? = 20  

// Type mismatch: inferred type is Int? but Int was expected
aInt = bInt
```

이렇게 제약이 많은 null이 될 수 있는 타입의 값으로 할 수 있는 일은 바로 null과 비교해 null이 아님이 확실한 영역에서는 일반 타입처럼 사용할 수 있게 된다.

```kotlin
fun strLenSage(s: String?): Int = if(s != null) s.length else 0
```

위와 같이 if문을 통해서 null이 아닌 것을 확인했다면, 내부 영역에서는 일반 타입으로 스마트 캐스팅이 된다.

### 1-2. 타입의 의미

> 타입이란, 분류(classification)이며, 타입은 어떤 값들이 가능한지와 그 타입에 대해 수행할 수 잇는 연산의 종류를 결정한다.

double과 String 타입을 비교해보면, 자바의 String 타입 변수에는 문자열이나 null이라는 두 종류의 값이 들어갈 수 있다. 문자열과 null은 엄연히 서로 다른 타입이며, `null instanceof String`을 하면 false를 반환한다.

또한, null을 막기 위해 Optional을 사용하거나 어노테이션을 사용하더라도 결국엔 null 여부를 추가로 검사하기 전까지 그 변수에 대해 어떤 연산을 수행할 수 있을지 알 수 없다. 즉, 자바에서는 타입 시스템이 널을 제대로 다루지 못한다는 뜻이다.

코틀린의 nullable 타입은 이런 문제에 대해 종합적인 해법을 제공한다.

### 1-3. 안전한 호출 연산자 : ?.

`?.` 연산자는 null 검사와 메소드 호출을 한 번의 연산으로 수행한다. 호출하려는 값이 null일 경우 `.`
뒷 함수 호출이 무시되고, null이 아닐 경우 뒷 함수들이 호출된다.

```kotlin
var s: String? = "hellow, how are you, i'm under the water"
// 기존 방식
if(s != null) s.toUpperCase() else null
// ?.연산 방식
s?.uppercase()
```

```kotlin
fun printAllCaps(s: String?) {  
	val allCaps: String? = s?.uppercase()  
    println(allCaps)  
}  
  
fun main() {  
    printAllCaps("abc")  
    printAllCaps(null)  
}
```

```kotlin
class Employee(val name: String, val manager: Employee?)  
  
fun managerName(employee: Employee): String? = employee.manager?.name  
  
fun main() {  
    val ceo = Employee("Da Boss", null)  
    val developer = Employee("Bob Smith", ceo)  
  
    println(managerName(developer))  
    println(managerName(ceo))  
}
```

위와 같이 메소드 호출, 프로퍼티를 읽거나 쓸 때도 안전한 호출을 사용할 수 있다. 또한 안전한 호출을 연쇄해서도 사용이 가능하다.

```kotlin
class Address(  
    val streetAddress: String,  
    val zipCode: Int,  
    val city: String,  
    val country: String  
)  
  
class Company(  
    val name: String,  
    val address: Address?  
)  
  
class Person(  
    val name: String,  
    val company: Company?  
)  
  
fun Person.countryName(): String {  
    val country = this.company?.address?.country  
    return if (country != null) country else "Unknown"  
}  
  
fun main() {  
    val person = Person("Dmitry", null)
    // 출력 : Unknown
    println(person.countryName())  
}
```

### 1-4. 엘비스 연산자 : ?:

코틀린은 null 대신 사용할 디폴트 값을 지정할 때, if-else가 아닌 엘비스(elvis) 연산자를 사용한다.

> 엘비스 연산자를 시계 방향으로 90도 돌리면 엘비스 프레슬리의 헤어 스타일과 눈이 보이며, 다른 이름으로는 널 복합(null coalescing) 연산자라는 이름으로도 사용된다.

이 엘비스 연산자를 앞서 정의한 `Person.countryName()` 함수에 적용해보자.

```kotlin
fun Person.countryName(): String {  
    return company?.address?.country ?: "Unknown"
}
```

엘비스 연산자의 우항에는 return, throw 등의 연산을 넣을 수 있다. 이런 패턴은 함수의 전제 조건(precondition)을 검사하는 경우 특히 유용하다.

```kotlin
fun printShippingLabel(person: Person) {
	// 주소가 없으면 Exception을 던진다.
    val address = person.company?.address  
        ?: throw IllegalArgumentException("No address")
	// 주소가 있으면 아래 내용이 출력된다.
    with(address) {  
        println(streetAddress)  
        println("$zipCode $city, $country")  
    }  
}
```

### 1-5. 안전한 캐스트 : as?

as 연산자는 자바의 타입 캐스팅과 동일한 역할을 한다. 특정 타입으로 변환할 수 없으면 ClassCastException이 발생하기 때문에 is 연산자를 통해 미리 검사를 한다.

하지만 이런 방식보다는 as?를 사용해 어떤 값을 지정한 타입으로 캐스트할 수 있다. 만약 캐스트할 수 없을 경우 ClassCastException이 발생하는 것이 아닌 null을 반환한다. 이를 엘비스 연산자와 함께 사용할 경우 효과를 배로 만들 수 있다.

```kotlin
class Animal(val firstName: String, val lastName: String) {  
    override fun equals(o: Any?): Boolean {  
        val otherAnimal = o as? Animal ?: return false  
        return otherAnimal.firstName == firstName &&  
                otherAnimal.lastName == lastName  
    }
}
```

코드에 보여지는 것과 같이 otherAnimal을 구하는 과정에서 as?를 사용해 null이 나올 경우 false를 반환하도록 했다. 이후 타입 캐스팅에 성공할 경우에는 otherAnimal을 Animal 타입으로 스마트 캐스트해 사용할 수 있다.

### 1-6. 널 아님 단언 : !!

이중 느낌표(!!)를 사용하면 어떤 값이든 널이 될 수 없는 타입으로 강제 변환할 수 있다.

> 실제 null이 든 값에 사용할 경우 NullPointerException이 발생한다.

```kotlin
fun ignoreNulls(s: String?) {  
    val sNotNull: String = s!!  
    println(sNotNull.length)  
}  
  
fun main() { 
	// 런타임 에러 발생 : Exception in thread "main" java.lang.NullPointerException
    ignoreNulls(null)  
}
```

근본적으로 !!를 사용하면 컴파일러에게 '이 값은 null이 아님을 잘 알고 있으니 문제가 생겨서 예외가 발생해도 감수하겠다.'라고 전달하는 것이다.

> !! 기호는 무례하고, 못생겼다. 그 이유는 코틀린 설계자들은 컴파일러가 검증할 수 없는 단언을 사용하기 보다는 더 나은 방법을 찾아보라는 의도로 못생긴 기호를 택한 것이다.

크게 사용하지 않을 것 같은 이 단언문은 가끔 더 나은 해법으로 사용될 경우도 있다.

> 어떤 함수가 값이 널인지 검사한 다음에 다른 함수를 호출한다고 해도 컴파일러는 호출된 함수 안에서 안전하게 그 값을 사용할 수 있음을 인식할 수 없다. 하지만 이런 경우 호출된 함수가 언제나 다른 함수에서 널이 아닌 값을 전달받는다는 사실이 분명하다면 굳이 널 검사를 다시 수행하지 않기 위해 널 아님 단언문을 사용할 수 있다.

아래 코드는 리스트 컨트롤에서 선택된 줄을 클립보드에 복사하는 코드이다. 또한, isEnabled가 true일 경우에만 actionPerformed를 호출해준다고 가정하자.

```kotlin
class CopyRowAction(val list: JList<String>) : AbstractAction() {  
    override fun isEnabled(): Boolean = list.selectedValue != null  
    override fun actionPerformed(e: ActionEvent) {  
        val value = list.selectedValue!!  
        // doSomething()  
    }  
}
```

컴파일러가 봤을 때는, actionPerformed 함수 안에서 사용되는 selectedValue가 null일 수 있다고 판단한다. 하지만 actionPerformed가 호출되는 전제 조건은 isEnabled이기 때문에 우리는 selectedValue가 널이 아님을 알고 있다. 이런 상황에서 널 아님 단언을 사용하면 중복된 널 검사를 하지 않아도 되기 때문에 사용하기 편하다.

### 1-7. let 함수

let 함수는 원하는 식을 평가해서 결과가 널인지 검사하고, 그 결과를 변수에 넣는 작업을 간단한 식을 사용해 처리할 수 있다. 이를 사용하는 가장 흔한 용례는 널이 될 수 있는 값을 널이 아닌 값만 인자로 받는 함수에 넘기는 경우다.

```kotlin
fun sendEmailTo(email: String) { /*...*/ }  
fun main() {  
    val email: String? = "전청조"
    // 컴파일 에러 : Type mismatch: inferred type is String? but String was expected 
    sendEmailTo(email)  
}
```

함수의 파라미터가 널을 허용하지 않기 때문에 메인 함수의 email 변수가 인자로 들어갈 수 없다. 이를 해결하기 위해선 널 검사를 통해 널이 아닐 경우에만 함수를 호출하도록 하는 방법 뿐이다.

```kotlin
fun sendEmailTo(email: String) { /*...*/ }  
fun main() {  
    val email: String? = "전청조"  
    if(email != null) sendEmailTo(email)  
}
```

하지만 이 방법 외에도 let 함수를 통해 인자를 전달할 수 있다.

```kotlin
fun main() {  
    val email: String? = "전청조"
    email?.let { email -> sendEmailTo(email) }  
}
```

let 함수는 이메일 주소 값이 널이 아닌 경우에만 호출되기 때문에 람다 안에서는 널이 될 수 없는 타입으로 email을 사용할 수 있다.

### 1-8. 나중에 초기화할 프로퍼티

객체 인스턴스를 일단 생성한 다음에 나중에 초기화하는 프레임워크가 많다. 하지만 코틀린에서 클래스 안의 널이 될 수 없는 프로퍼티를 생성자 안에서 초기화하지 않고, 특별한 메소드 안에서 초기화할 수는 없다. JUnit의 예시를 통해 확인해보자.

```kotlin
class MyService {
	fun performAction(): String = "FOO"
}

class MyServiceTest {
	private var myService: MyService? = null
	
	@Before fun setUp() {
		myService = MyService()
	}

	@Test fun actionTest() {
		Assert.assertEquals("FOO", myService!!.performAction())
	}
}
```

myService를 처음에 nullable한 타입으로 선언했기 때문에 사용하는 모든 곳에서 널 아님 단언을 선언해줘야 한다. 이를 해결하기 위해 lateinit 변경자를 붙이면 프로퍼티를 나중에 초기화할 수 있다.

```kotlin
class MyService {
	fun performAction(): String = "FOO"
}

class MyServiceTest {
	private lateinit var myService: MyService
	
	@Before fun setUp() {
		myService = MyService()
	}

	@Test fun actionTest() {
		Assert.assertEquals("FOO", myService.performAction())
	}
}
```

나중에 초기화되는 프로퍼티는 항상 var로 선언되어야 한다. val을 사용하기 위해서는 생성자 안에서 반드시 초기화해야 한다.

### 1-9. 널이 될 수 없는 타입 확장

널인 상태의 변수를 참조해 메소드를 호출할 경우 NPE가 발생한다. 하지만 널이 될 수 없는 타입 확장을 사용하면, 그 인스턴스가 널인지 여부를 일일이 검사하지 않아도 된다.

```kotlin
fun verifyUserInput(input: String?) {
	if(input.isNullOrBlank()) println("Please fill in the required fields")
}
```

이러한 호출이 가능한 이유는 객체 인스턴스를 통해 디스패치(dispatch)되기 때문이다.

> 디스패치(dispatch)에는 두 가지 종류가 있다.
> 1. 동적 디스패치 : 객체의 동적 타입에 따라 적절한 메소드를 호출해주는 방식
> 2. 직접 디스패치 : 컴파일러가 컴파일 시점에 어떤 메소드가 호출될지 결정해 코드를 생성하는 방식

`isNullOrBlank`의 구현 코드를 보면 다음과 같다.

```kotlin
@kotlin.internal.InlineOnly  
public inline fun CharSequence?.isNullOrBlank(): Boolean {  
    contract {
        returns(false) implies (this@isNullOrBlank != null)  
    }
    return this == null || this.isBlank()  
}
```

contract를 통해 컴파일러에게 "**수신 객체가 null이 아니면, false를 리턴한다.**"라는 사실을 알려주고, 수신 객체가 null이 아님을 이해시켜준다. 때문에 클라이언트 코드에서 수신 객체를 참조해도 컴파일러는 null이 아님을 알고 있기 때문에 NPE가 발생하지 않게 해준다.

### 1-10. 타입 파라미터의 널 가능성

제네릭을 사용한 타입 파라미터 T를 사용할 경우, 물음표가 없더라도 T가 널이 될 수 있는 타입으로 들어가진다.

```kotlin
fun <T> printHashCode(t: T) {
	println(t?.hashCode())
}
```

위처럼 t?를 사용할 경우 T의 타입은 `Any?`가 된다. T에 대한 타입 자체에 물음표가 붙어있지 않아도 t는 null을 받을 수 있는 것이다. 만약 타입 파라미터가 널이 아님을 확실히 하려면 널이 될 수 없는 타입 상한(upper bound)을 지정해야 한다.

```kotlin
fun <T: Any> printHashCode(t: T) {  
    println(t.hashCode())  
}
```

이렇게 T에 대한 타입을 Any로 확정해 null을 받을 수 없도록 만들면 된다.

### 1-11. 널 가능성과 자바

자바 타입 시스템은 널 가능성을 지원하지 않는다. 아래 코드를 확인해보자.

```kotlin
fun printString(s1: String?, s2: String) {  
    println(s1 ?: "null")  
    println(s2)  
}
```

위 코드를 자바 코드로 디컴파일 해보면 다음과 같은 결과가 나온다.

```java
public static final void printString(@Nullable String s1, @NotNull String s2) {  
   Intrinsics.checkNotNullParameter(s2, "s2");  
   String var10000 = s1;  
   if (s1 == null) {  
      var10000 = "null";  
   }  
  
   String var2 = var10000;  
   System.out.println(var2);  
   System.out.println(s2);  
}
```

자바 코드로 디컴파일을 해보면, 널을 허용하는 파라미터 s1은 `@Nullable` 어노테이션이 붙고, 널을 허용하지 않는 s2는 `@NotNull` 어노테이션이 붙는다. 실제로 위 자바 코드를 그대로 만들어

널 가능성을 허용하던 코틀린 함수 파라미터의 s1은 자바로 디컴파일이 될 때, `@Nullable` 어노테이션이 붙는다.

```java
public class NullableTest {  
    public static final void printString(@Nullable String s1, @NotNull String s2) {  
        Intrinsics.checkNotNullParameter(s2, "s2");  
        String v1 = s1;  
        if (s1 == null) {  
            v1 = "null";  
        }  
        String v2 = v1;  
        System.out.println(v2);  
        System.out.println(s2);  
    }  
    public static void main(String[] args) { 
	    // 컴파일 경고 : Passing 'null' argument to parameter annotated as @NotNull
        printString(null, null);  
    }  
}
```

`@NotNull` 어노테이션을 사용한다고 하더라도, 인자로 명시적으로 null을 보내줄 경우 컴파일 타임에 에러를 잡아주진 않고, 단순 경고만 발생한다.

#### 1-11-1. 플랫폼 타입

플랫폼 타입은 코틀린이 널 관련 정보를 알 수 없는 타입을 말한다. 코드로 보면 다음과 같다.

```kotlin
자바       코틀린
Type = Type? || Type
```

코틀린은 보통 널이 될 수 없는 값에 널 안전성 검사하는 연산을 수행하면 경고를 표시하지만, 플랫폼 타입의 값에 대해 널 안전성 검사를 중복 수행해도 아무 경고를 표시하지 않는다.

```kotlin
fun printString(s: String) {
	// 경고 : Elvis operator (?:) always returns the left operand of non-nullable type String
	println(s ?: "null")
}
```

자바 타입은 코틀린에서 플랫폼 타입(널이 될 수 있는 타입 || 널이 될 수 없는 타입)으로 모두 사용할 수 있다. 아래 코드를 통해 확인해보자.

```java
public class JavaPerson {  
    private final String name;  
  
    public JavaPerson(String name) {  this.name = name;  }  
  
    public String getName() {  return name;  }  
}
```

```kotlin
fun yellAt(person: JavaPerson) {  
	// 런타임 에러 : Exception in thread "main" java.lang.NullPointerException: getName(...) must not be null
    println(person.name.uppercase() + "!!!")  
}  
  
fun main() {  
    yellAt(JavaPerson(null))  
}
```

이렇게 널 검사 없이 자바 클래스에 접근할 경우 런타임 에러를 내뿜게 된다. 때문에 다음과 같이 널 검사를 통해 자바 클래스에 접근해야 한다.

```kotlin
fun yellAt(person: JavaPerson) {
	println((person.name ?: "Anyone").uppercase() + "!!!")
}
```

코틀린이 플랫폼 타입을 도입한 이유는 단순하다. 모든 타입을 널이 될 수 있는 타입으로 다루면, 결코 널이 될 수 없는 값에 대해서도 불필요한 널 검사를 진행해야 하기 때문이다. 제네릭을 사용할 경우 `ArrayList<String>?`과 같이 배열의 원소에 접근할 때마다 널 검사를 해야한다. 널 안전성으로 얻는 이익보다 검사에 드는 비용이 더 커지기 때문에 실용적인 접근 방법을 택했다.

#### 1-11-2. 상속

코틀린에서 자바 메소드를 오버라이드할 때, 그 메소드의 파라미터와 반환 타입을 널이 될 수 있는 타입으로 선언할지 널이 될 수 없는 타입으로 선언할지 결정해야 한다.

```java
public interface StringProcessor {  
	void process(String value);  
}
```

```kotlin  
class StringPrinter : StringProcessor {  
	override fun process(value: String) {  
		println(value)  
	}  
}  
  
class NullableStringPrinter : StringProcessor {  
	override fun process(value: String?) {  
		if (value != null) { println(value) }  
	}
}  
```

코틀린 컴파일러는 String과 String? 두 선언을 모두 받아들인다. 만약 자바 코드에서 StringPrinter에 있는 process를 통해 null 인자로 보내줄 경우 예외가 발생하게 되고, 이런 예외는 피할 수 없다.

```java
public class StringProcessorTest {  
	public static void main(String[] args) {  
		StringProcessor sp = new StringPrinter();  
		sp.process(null);  
	}  
}
```

```bash
Exception in thread "main" java.lang.NullPointerException: Parameter specified as non-null is null: method action.junyoung.chapter06.StringPrinter.process, parameter value
```

## 2. 코틀린의 원시 타입

코틀린은 원시 타입과 래퍼 타입을 따로 구분하지 않는다. 이들이 어떻게 원시 타입에 대한 래핑이 작동하는지 살펴보자.

### 2-1. 원시 타입 : Int, Boolean 등

자바는 int와 같은 원시 타입(primitive type)과 String과 같은 참조 타입(reference type)을 구분한다. 원시 타입에는 변수에 그 값이 직접 들어가지만, 참조 타입의 경우 메모리상의 객체 위치가 들어간다.

원시 타입은 효율적으로 저장하고, 전달할 수 있지만, 값에 대한 메소드를 호출하거나 컬렉션에 담을 수 없다. 때문에 이런 원시 타입을 래퍼 타입으로 감싸서 `Collection<Integer>` 처럼 사용한다.

코틀린은 원시 타입과 래퍼 타입을 구분하지 않으므로 항상 같은 타입을 사용한다. 이런 방식을 사용하면 원시 타입의 값에 대해 메소드를 호출할 수 있게 된다.

```kotlin
fun main() {  
	val list: List<Int> = listOf(10, 50, 100, 150)  
	for (progress in list) {
		// 10% done -> 50% done -> 100% done -> 100% done
		showProgress(progress)  
	}  
}  
  
private fun showProgress(progress: Int) {  
	// 값ㅇ르 특정 범위로 제한
	val percent = progress.coerceIn(0, 100)  
	println("$percent done")  
}
```

하지만 위 처럼 원시 타입과 참조 타입을 같이 사용하게 되면, 비효율적일 것 같다는 생각이 들지만, 그렇지 않다. 실행 시점에 코틀린의 `Int` 타입은 자바의 `int`으로 컴파일 된다. 실제로 위 코드를 디컴파일 할 경우 `int percent`로 변환되는 것을 볼 수 있다.

하지만 `Int`를 제네릭 타입에 넣을 경우 자바 원시 타입은 `int`가 null 참조를 받아들일 수 없어, 래퍼 타입인 `Integer`로 변환되어 들어가게 된다.

### 2-2. 널이 될 수 있는 원시 타입 : Int?, Boolean? 등

자바에서의 원시 타입에는 null을 대입할 수 없지만, 코틀린에서는 원시 타입을 nullable한 타입으로 지정이 가능하다. `Int?`로 선언해 디컴파일해보면 `Integer`로 변환되어 들어가는 것을 볼 수 있을 것이다. 다음 코드를 통해 원시 타입의 널 가능성을 확인해보자.

```kotlin
data class Person(val name: String, val age: Int? = null) {  
	fun isOlderThan(other: Person): Boolean? {  
		if(age == null || other.age == null) return null  
		return age > other.age  
	}  
}
```

age 프로퍼티는 nullable하기 때문에 먼저 널에 대한 여부를 확인해야 한다. 이후 컴파일러는 두 값이 널이 아님을 판단해 널을 허용하지 않는 일반 타입으로써 사용할 수 있게 허용한다. 위 클래스를 디컴파일 해보면 다음과 같이 변환된다.

```java
public final class Person {  
	@NotNull  
	private final String name;  
	@Nullable  
	private final Integer age;  
	  
	@Nullable  
	public final Boolean isOlderThan(@NotNull Person other) {  
		Intrinsics.checkNotNullParameter(other, "other");  
		return this.age != null && other.age != null ? this.age > other.age : null;  
	}

	// equals, copy 등등 생략
}
```

또한, 제네릭 클래스의 경우 래퍼 타입을 사용하는데, 코틀린의 경우 원시 타입을 넘기면 래퍼인 Integer 타입으로 넘어가게 된다. 이렇게 컴파일 되는 이유는 JVM에서 제네릭 타입 인자로 원시 타입을 허용하지 않기 때문이다. 따라서 자바, 코틀린 모두 박스 타입을 사용해야 한다.

### 2-3. 숫자 변환

코틀린은 한 타입의 숫자를 다른 타입의 숫자로 자동 변환하지 않는다.

```kotlin
val i = 1  
// 컴파일 에러 : Type Mismatch
val l: Long = i
```

> [공식 문서](Why Cotlin Doesn't Automatically Transform Number Types)에 따르면, 작은 유형이 큰 유형의 하위 타입이 아니기 때문에 허용하지 않는다고 한다.

즉, 이를 허용할 경우 다음과 같은 혼란이 발생할 수 있다.

```kotlin
val x = 1  
val longList = listOf(1L, 2L, 3L)
x in longList
```

자바에서 실행할 경우 위 코드는 false를 반환한다. 하지만, 코틀린의 경우 묵시적 형변환을 허용하지 않기 때문에 컴파일 에러가 발생하게 된다. 이럴 경우 `x.toLong()`을 사용해 타입을 변환한 후 비교해야 한다.

이러한 혼란을 피하기 위해 타입 변환을 명시하기로 결정한 것이다.

### 2-4. Any, Any? : 최상위 타입

Any 타입은 `java.lang.Object`에 대응한다. 또한, Any 자체는 널이 될 수 없는 타입이기 때문에 널을 허용하려면 Any?를 사용해야 한다. 기본적으로 모든 클래스는 toString, equals, hashCode 메소드를 갖고 있는데, 이는 Any에 정의된 메소드를 상속한 것이다. 하지만 wait이나 notify 등의 메소드는 Any에서 사용할 수 없다. 이를 사용하려면 Object 타입으로 캐스트해야 한다.

### 2-5. Unit 타입 : 코틀린의 void

Unit 타입은 자바의 void와 같은 기능을 한다.

```kotlin
fun f(): Unit {}
fun f() {}
```

위 코드와 같이 반환 타입이 없을 경우 생략을 할 수도 있는데, 사실 Unit 값을 묵시적으로 반환하고 있는 것이다.

코틀린의 Unit과 자바의 void는 어떤 차이점이 있을까? 자바에서 void는 함수의 반환 값이 없을 때만 보통 사용된다. 하지만 코틀린의 Unit은 타입 인자로도 쓸 수 있다. 이는 제네릭 파라미터를 반환하는 함수를 오버라이드하면서 반환 타입으로 Unit을 쓸 때 유용하다.

```kotlin
interface Processor<T> { fun process(): T }

class NoResultProcessor : Processor<Unit> {
	override fun process() {
		// return을 명시할 필요가 없다.  
	}
}
```

위 인터페이스에 작성된 process 함수는 T 타입을 반환하도록 되어있지만, 해당 인터페이스를 구현한 구현체에서 타입을 Unit으로 선언해 process 함수에서 아무 것도 반환하지 않아도 된다.

> 코틀린에서 Void가 아닌 Unit이라는 이름을 선택한 이유는, 함수형 프로그래밍에서 전통적으로 Unit은 '단 하나의 인스턴스만 갖는 타입'을 의미해왔고, 이러한 인스턴스의 유무가 자바의 void와 코틀린의 Unit을 구분하는 큰 차이가 된다.

### 2-6. Nothing 타입 : 이 함수는 결코 정상적으로 끝나지 않는다.

결코 성공적으로 값을 돌려주는 일이 없어, '반환 값'이라는 개념 자체가 의미 없는 함수가 일부 존재한다. 이렇게 함수가 정상적으로 끝나지 않는다는 사실을 알릴 때, Nothing이라는 반환 타입을 사용한다.

```kotlin
fun fail(message: String): Nothing {
	throw IllegalStateException(message)
}
```

Nothing 타입은 아무 값도 포함하지 않아, 함수의 반환 타입 혹은 반환 타입으로 쓰일 타입 파라미터로만 사용할 수 있다.

```kotlin
// fail의 반환 타입이 Nothing이므로, address가 받아들일 수 있다.
val address = company.address ?: fail("No address")
println(address.city)
```

컴파일러는 Nothing이 반환 타입인 함수가 결코 정상 종료되지 않음을 알기 때문에 엘비스 연산자의 우항에서 예외가 발생한다는 사실을 파악하고 address의 값이 널이 아님을 추론할 수 있다.

## 3. 컬렉션과 배열

코틀린의 컬렉션 지원, 자바와 코틀린 컬렉션 간의 관계에 대해 살펴보자.

### 3-1. 널 가능성과 컬렉션

컬렉션 안에 널 값을 넣을 수 있는지 여부는, 어떤 변수의 값이 널이 될 수 있는지 여부와 마찬가지로 중요하다.

```kotlin
fun readNumbers(reader: BufferedReader): List<Int?> {  
    val result = ArrayList<Int?>()  
  
    for (line in reader.lineSequence()) {  
        try {  
            val number = line.toInt()  
            result += (number)  
        } catch (e: NumberFormatException) {  
            result += (null)  
        }  
    }  
    return result  
}
```

위 코드를 살펴보면, 현재 줄을 파싱할 수 있으면 result에 정수를 넣고, 그렇지 않으면 null을 넣는다. 코틀린 1.1부터 제공하는 `toIntOrNull`을 사용하면 코드를 더 줄일 수 있다.

`List<Int?>`와 `List<Int>?`의 차이를 보자. 첫 번째 경우 리스트 자체는 항상 널이 아니다. 하지만 리스트에 들어있는 각 원소는 널이 될 수 있다. 두 번째 경우는 리스트를 가리키는 변수에는 널이 들어갈 수 있지만, 리스트 안에는 널이 아닌 값만 들어갈 수 있다.

널이 될 수 있는 값으로 이뤄진 리스트를 다루는 예제를 확인해보자.

```kotlin
fun addValidNumbers(numbers: List<Int?>) {  
    var sumOfValidNumbers = 0  
    var invalidNumbers = 0  
  
    for (number in numbers) {  
        if (number != null) {  
            sumOfValidNumbers += number  
        } else {  
            invalidNumbers++  
        }  
    }  
    println("Sum of valid numbers : $sumOfValidNumbers")  
    println("Invalid numbers: $invalidNumbers")  
}
```

```kotlin
fun main() {  
    val reader = BufferedReader(StringReader("1\nabc\n42"))  
    val numbers = readNumbers(reader)  
    addValidNumbers(numbers)  
}
```

리스트의 원소에 접근하면, Int? 타입의 값을 얻으며, 그 값을 산술식에 사용하기 전에 널 여부를 검사해야 한다. 이렇게 널이 될 수 있는 값으로 이뤄진 컬렉션을 사용할 때, 널을 걸러내는 `filterNotNull`이라는 함수가 존재한다.

```kotlin
fun addValidNumbers(numbers: List<Int?>) {  
    val validNumbers = numbers.filterNotNull()  
    println("Sum of valid numbers : ${validNumbers.sum()}")  
    println("Invalid numbers: ${numbers.size - validNumbers.size}")  
}
```

### 3-2. 읽기 전용과 변경 가능한 컬렉션

코틀린 컬렉션을 다룰 때 사용하는 가장 기초적인 인터페이스인 `kotlin.collections.Collection`부터 시작한다. 해당 인터페이스를 사용하면, 컬렉션 안의 원소에 대한 이터레이션, 크기, 값이 들어있는지에 대한 검사 등에 대한 기능을 사용할 수 있다. 하지만 이 Collection에는 원소를 추가하거나 제거하는 메소드가 없다.

만약 데이터를 수정하려면, `kotlin.collections.MutableCollection` 인터페이스를 사용해야 한다. 해당 인터페이스는 Collection을 확장해 원소를 추가, 삭제에 대한 기능을 추가적으로 제공한다. var, val과 마찬가지로 읽기 전용 인터페이스와 변경 가능 인터페이스를 구별하는 이유는 프로그램에서 데이터에 어떤 일이 벌어지는지를 더 쉽게 이해하기 위함이다.

```kotlin
fun <T> copyElements(source: Collection<T>,  
                     target: MutableCollection<T>) {
    // 방어적 복사(defensive copy)
    for (item in source) {  
        target.add(item)  
    }  
}
```

### 3-3. 코틀린 컬렉션과 자바

모든 코틀린 컬렉션은 그에 상응하는 자바 컬렉션 인터페이스의 인스턴스이다. 하지만 코틀린에서는 모든 자바 컬렉션 인터페이스마다 읽기 전용과 변경 가능한 인터페이스라는 두 가지 표현(representation)을 제공한다.

![image](https://3553248446-files.gitbook.io/~/files/v0/b/gitbook-legacy-files/o/assets%2F-M5HOStxvx-Jr0fqZhyW%2F-MOWr_c5B-t37Wgi3DOg%2F-MOWtUmzBAL-Yftt33_M%2F4.jpg?alt=media&token=859232ca-4c04-4153-a9df-aa92ff41adcf)

Map 또한 Map, MutableMap으로 나뉜다.

| 컬렉션 타입 | 읽기 전용 타입 | 변경 가능 타입 |
| ---- | ---- | ---- |
| List | listOf | mutableListOf, arrayListOf |
| Set | setOf | mutableSetOf, hashSetOf, linkedSetOf, sortedSetOf |
| Map | mapOf | mutableMapOf, hashMapOf, linkedMapOf, sortedMapOf |
setOf, mapOf는 자바 라이브러리에 속한 클래스의 인스턴스를 반환하지만, 실제로는 불변 컬렉션 인스턴스를 반환한다. 때문에 자바 메소드를 호출할 때, 컬렉션을 인자로 넘겨야 한다면 Collection을 MutableCollection으로 변환하는 과정 없이 직접 컬렉션 자체를 넘기면 된다.

```java
public class CollectionUtils {  
    public static List<String> uppercaseAll(List<String> items) {  
        items.replaceAll(String::toUpperCase);  
        return items;  
    }  
}
```

```kotlin
val strList = listOf("hello", "water")  
CollectionUtils.uppercaseAll(strList) 
// 출력 : [HELLO, WATER]
println(strList)
```

이렇게 불변 컬렉션을 넘겨도 자바는 따로 구분하지 않기 때문에 컬력센 객체의 내부 내용을 변경할 수 있다.

### 3-4. 컬렉션을 플랫폼 타입으로 다루기

컬렉션을 플랫폼 타입으로 다루기 위해서는 다음을 잘 생각해야 한다.

- 컬렉션이 널이 될 수 있는가?
- 컬렉션의 원소가 널이 될 수 있는가?
- 오버라이드하는 메소드가 컬렉션을 변경할 수 있는가?

```java
public interface FileContentProcessor {  
    void processContents(File path, byte[] binaryContents, List<String> textContents);  
}
```

만약 위 인터페이스를 코틀린으로 구현한다면 다음을 선택해야 한다.

- 일부 파일은 이진 파일이다.
- 이진 파일 안의 내용은 텍스트로 표현할 수 없는 경우가 있으니, 리스트는 널이 될 수 있다.
- 파일의 각 줄은 널일 수 없으므로 이 리스트의 원소는 널이 될 수 없다.
- 이 리스트는 파일의 내용을 표현하며, 그 내용을 바꿀 필요가 없으므로 읽기 전용이다.

이를 고려해서 코틀린에서는 다음과 같이 구현할 수 있다.

```kotlin
class FileIndexer : FileContentProcessor {  
    override fun processContents(  
        path: File,  
        binaryContents: ByteArray?,  
        textContents: List<String>?) {  
        // ..  
    }  
}
```

그렇다면 컬렉션 파라미터가 있는 인터페이스는 어떨까?

```java
public interface DataParser<T> {  
    void parseData(String input, List<T> output, List<String> errors);  
}
```

위 인터페이스를 구현한 클래스가 텍스트를 폼에서 읽은 데이터를 파싱해서 객체 리스트를 만들고, 그 리스트의 객체들을 출력 리스트(output) 뒤에 추가하고, 데이터를 파싱하는 과정에서 발생한 오류 메시지를 별도의 리스트(errors)에 담는다. 여기서 고려해야할 사항은 다음과 같다.

- 호출하는 쪽에서 항상 오류 메시지(errors)를 받아야 한다.
    - `List<String>`은 널이 되면 안 된다.
    - errors의 원소는 널이 될 수도 있다.
    - 파싱할 때 오류가 발생하지 않으면 그 정보와 관련된 오류 메시지는 널이다.
- 구현 코드에서 원소를 추가할 수 있어야 하므로, `List<String>`은 변경 가능해야 한다.

```kotlin
class PersonParser : DataParser<Person> {  
    override fun parseData(  
        input: String,  
        output: MutableList<Person>,  
        errors: MutableList<String?>) {  
        // ..  
    }  
}
```

### 3-5. 객체의 배열과 원시 타입의 배열

코틀린에서 배열을 만드는 방법은 여러가지가 있다.

- arrayOf -> 원소 지정 가능
- arrayOfNulls -> 모든 원소가 null인 배열 / 크기 지정 가능
- Array -> 크기 지정 가능 / 원소 지정 가능

```kotlin
fun main() {  
    val letters = Array<String>(26) { i -> ('a' + i).toString() }  
    println(letters.joinToString(" "))  
}
```

만약 컬렉션을 사용 중일 때, vararg 파라미터를 받는 함수에 값을 넘겨줘야 한다면 toTypedArray를 사용하면 배열로 쉽게 변경할 수 있다.

```kotlin
val strings = listOf("a", "b", "c")  
println("%s%s%s".format(*strings.toTypedArray()))
```

format의 경우 vararg를 통해 값을 전달 받아 각각의 `%s`에 넣어주므로, 배열 자체를 넘겨줘도 값을 출력할 수 있다.
