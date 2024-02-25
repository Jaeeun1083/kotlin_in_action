# 10장 애노테이션과 리플렉션

## 10.1 애노테이션 선언과 적용
코틀린 애노테이션도 자바와 마찬가지로 메타데이터를 선언에 추가하면 애노테이션을 처리하는 도구가 컴파일 시점이나
실행 시점에 적절한 처리를 해준다.

### 10.1.1 애노테이션 적용
애노테이션은 @과 애노테이션 이름으로 이뤄진다.

애노테이션의 인자로는 원시 타입의 값, 문자열, enum, 클래스 참조, 다른 애노테이션 클래스, 그리고 지금까지 말한
요소들로 이뤄진 배열이 들어갈 수 있다. 애노테이션 인자를 지정하는 문법은 자바와 약간 다르다.

- 클래스를 애노테이션 인자로 지정할 때는 @MyAnnotation(MyClass::class)처럼 ::class를 클래스 이름 뒤에 뒤에 넣어야 한다.
- 다른 애노테이션을 인자로 지정할 때는 인자로 들어가는 애노테이션의 이름 앞에 @를 넣지 않아야 한다.
- 배열을 인자로 지정하려면 @RequestMapping(path = arrayOf("/foo", "/bar"))처럼 arrayOf 함수를 사용한다. 자바에서 선언한 애노테이션 클래스를 사용한다면 value라는 이름의 파라미터가 필요에 따라 자동으로 가변 길이 인자로 변환된다. 따라서 그런 경우에는 @JavaAnnotationWithArrayValue("abc", "foo", "bar")처럼 arrayOf 함수를 쓰지 않아도 된다.

애노테이션 인자를 컴파일 시점에 알 수 있어야 한다. 따라서 임의의 프로퍼티를 인자로 지정할 수는 없다.
프로퍼티를 애노테이션 인자로 사용하려면 그 앞에 `const` 변경자를 붙여야 한다. 컴파일러는 `const`가 붙은 프로퍼티를 컴파일 시점 상수로 취급한다.

### 10.1.2 애노테이션 대상
애노테이션을 붙일 때 어떤 요소에 애노테이션을 붙일지 표시할 필요가 있다.
사용 지점 대상(use-site target) 선언으로 애노테이션을 붙일 요소를 정할 수 있다.
사용 지점 대상은 @ 기호와 애노테이션 이름 사이에 붙으면 애노테이션 이름과는 콜론으로 분리된다.

```kotlin
class HasTempFolder {
    @get:Rule // 프로퍼티가 아니라 게터에 애노테이션이 붙는다.
    val folder = TemporaryFolder()
    
    @Test
    fun testUsingTempFolder() {
        val createdFile = folder.newFile("myfile.txt")
        val createdFolder = folder.newFolder("subfolder")
        // ...
    }
}
```

자바에 선언된 애노테이션을 사용해 프로퍼티에 애노테이션을 붙이는 경우 기본적으로 프로퍼티의 필드에 그 애노테이션이 붙는다.
하지만 코틀린으로 애노테이션을 선언하면 프로퍼티에 직접 적용할 수 있는 애노테이션을 만들 수 있다.

- `property` - 프로퍼티 전체
- `field` - 프로퍼티의 백킹 필드
- `get` - 게터
- `set` - 세터
- `receiver` - 확장 함수나 프로퍼티의 수신 객체 파라미터
- `param` - 생성자 파라미터
- `setparam` - 세터 파라미터
- `delegate` - 위임 프로퍼티의 위임 인스턴스를 담아둔 필드
- `file` - 파일 안에 선언된 최상위 함수와 프로퍼티를 담아두는 클래스

`file` 대상을 사용하는 애노테이션은 `package` 선언 앞에서 파일의 최상위 수준에만 적용할 수 있다.
파일에 흔히 적용하는 애노테이션으로는 파일에 있는 최상위 선언을 담는 클래스의 이름을 바꿔주는 `@file:JvmName`이 있다.

### 10.1.3 애노테이션을 활용한 JSON 직렬화 제어

애노테이션을 활용해 객체를 직렬화하거나 역직렬화하는 방법을 제어할 수 있다. 객체를 `JSON`으로 직렬화할 때
제이키드 라이브러리는 기본적으로 모든 프로퍼티를 직렬화 하며 이름을 키로 사용한다. 애노테이션을 사용하면
이런 동작을 변경할 수 있다. 이번 절에서는 `@JsonExclude`와 `@JsonName`이라는 두 애노테이션을 다룬다.

- `@JsonExclude` - 해당 프로퍼티를 제외한다.
- `@JsonName` - 해당 프로퍼티를 직렬화할 때 사용할 이름을 지정한다.

```kotlin
data class Person(
    @JsonName("alias") val firstName: String,
    @JsonExclude val age: Int? = null
)
```

### 10.1.4 애노테이션 선언

애노테이션을 선언하려면 `annotation` 키워드를 사용한다. 애노테이션 선언에는 파라미터를 가질 수 있다.

```kotlin
annotation class JsonName(val name: String)
```

### 10.1.5 메타애노테이션: 애노테이션을 처리하는 방법 제어
자바와 마찬가지로 코틀린 애노테이션 클래스에도 애노테이션을 붙일 수 있다. 애노테이션 클래스에 적용할 수 있는
애노테잉션을 메타애노테이션(meta-annotation)이라고 한다. 표준 라이브러리에는 몇 가지 메타애노테이션이 있으며,
그런 메타애노테이션들은 컴파일러가 애노테이션을 처리하는 방법을 제어한다.

표준 라이브러리에 있는 메타애노테이션 중 가장 흔히 쓰이는 메타애노테이션은 `@Target`이다.

```kotlin
@Target(AnnotationTarget.PROPERTY)
annotation class JsonExclude
```

`@Target` 애노테이션은 애노테이션을 적용할 수 있는 대상을 지정한다.

`@Retention` 애노테이션은 애노테이션을 클래스 소스 수준에서만 유지할지, .class 파일에 저장할지, 런타임에 리플렉션을 사용해
접근할 수 있게 할지를 결정한다.

### 10.1.6 애노테이션 파라미터로 클래스 사용
어떤 클래스를 선언 메타데이터로 참조할 수 있는 기능이 필요할 때도 있다. 클래스 참조를 파라미터로 하는 애노테이션 클래스를 선언하면
그런 기능을 사용할 수 있다.

```kotlin
interface Company {
    val name: String
}

data class CompanyImpl(override val name: String) : Company

data class Person(
    val name: String,
    @DeserializeInterface(CompanyImpl::class) val company: Company
)
```

클래스 참조를 인자로 받는 애노테이션을 어떻게 정의하는지 살펴보자.

```kotlin
annotation class DeserializeInterface(val targetClass: KClass<out Any>)
```

`KClass`는 자바 `java.lang.Class` 타입과 같은 역할을 하는 코틀린 타입이다.
코틀린 클래스에 대한 참조를 저장할 때 `KClass`를 사용한다.

`KClass`의 타입 파라미터를 쓸 때 `out` 변경자 없이 `KClass<Any>` 라고 쓰면 `CompanyImpl::class`를 인자로 넘길 수 없고
오직 `Any::class`만 넘길 수 있다. 반면 `out` 키워드가 있으면 모든 코틀린 타입 `T`에 대해 `KClass<T>`가 `KClass<out Any>`의 하위 타입이 된다.(공변성)

### 10.1.7 애노테이션 파라미터로 제네릭 클래스 받기

기본적으로 제이키드는 원시 타입이 아닌 프로퍼티를 중첩된 객체로 직렬화한다. 이런 기본 동작을 변경하고 싶으면 값을 직렬화하는 로직을 직접 제공하면 된다.
`@CustomSerializer` 애노테이션은 커스텀 직렬화 클래스에 대한 참조를 인자로 받는다.

이 직렬화 클래스는 `ValueSerializer` 인터페이스를 구현해야 한다.

```kotlin
interface ValueSerializer<T> {
    fun toJsonValue(value: T): Any?
    fun fromJsonValue(jsonValue: Any?): T
}
```

`ValueSerializer` 클래스는 제네릭 클래스라서 타입 파라미터가 있다. 따라서 `ValueSerializer`타입을 참조하려면
항상 타입 인자를 제공해야 한다. 하지만 이 애노테이션이 어떤 타입에 대해 쓰일지 전혀 알 수 없으므로 여기서는 스타 프로젝션을 사용할 수 있다.

```kotlin
annotation class CustomSerializer(
    val serializerClass: KClass<out ValueSerializer<*>>
)
```

클래스를 애노테이션 인자로 받아야 할 때마다 같은 패턴을 사용할 수 있다.
클래스를 인자로 받아야 하면 애노테이션 파라미터 타입에 `KClass<out 허용할 클래스 이름<*>>`처럼 허용할 클래스의 이름 뒤에 스타 프로젝션을 덧붙인다.


## 10.2 리플렉션: 실행 시점에 코틀린 객체 내부 관찰
리플렉션은 실행 시점에 동적으로 객체의 프로퍼티와 메소드에 접근할 수 있게 해주는 방법이다.

코틀린에서 리플렉션을 사용하려면 두 가지 서로 다른 리플렉션 API를 다뤄야 한다.
첫 번째는 자바가 `java.lang.reflect` 패키지를 통해 제공하는 표준 리플렉션이다.
코틀린 클래스는 일반 자바 바이트코드로 컴파일되므로 자바 리플렉션 API도 코틀린 클래스를 컴파일한 바이트코드를 완벽히 지원한다.

두 번째 API는 코틀린이 `kotlin.reflect` 패키지를 통해 제공하는 코틀린 리플렉션 API다. 
이는 자바에는 없는 프로퍼티나 널이 될 수 있는 타입과 같은 코틀린 고유 개념에 대한 리플렉션을 제공한다.

### 10.2.1 코틀린 리플렉션 API: KClass, KCallable, KFunction, KProperty
`KClass`를 사용하면 클래스 안에 있는 모든 선언을 열거하고 각 선언에 접근하거나 클래스의 상위 클래스를 얻는 등의 작업이 가능하다.
실행 시점에 객체 클래스를 얻으려면 먼저 객체의 `javaClass` 프로퍼티를 사용해 객체의 자바 클래스를 얻어야 한다.
`javaClass`는 자바의 `java.lang.Object.getClass()`와 같다.

일단 자바 클래스를 얻었으면 `.kotlin` 확장 프로퍼티를 통해 자바에서 코틀린 리플렉션 API로 옮겨올 수 있다.

`KCallable`은 함수나 프로퍼티를 아우르는 공통 상위 인터페이스다. 그 안에는 `call`이라는 메소드가 들어있다.
`call`을 사용하면 함수나 프로퍼티의 게터를 호출할 수 있다.

```kotlin
interface KCallable<out R> {
    fun call(vararg args: Any?): R
    // ...
}
```

call을 사용할 때는 함수 인자를 vararg 리스트로 전달한다. 다음 코드는 리플렉션이 제공하는 `call`을 이용해 함수를 호출할 수 있음을 보여준다.

```kotlin
fun isOdd(x: Int) = x % 2 != 0

val kFunction = ::isOdd
println(kFunction.call(5))  // true
```

함수를 호출하기 위해 더 구체적인 메소드를 사용할 수도 있다.
::foo의 타입 `KFuncation1<Int, Unit>`에는 파라미터와 반환 값 타입 정보가 들어있다.

1은 함수의 파라미터 개수를 나타낸다. 코틀린에서는 함수 타입의 이름 뒤에 숫자를 붙여 함수의 파라미터 개수를 나타낸다.

### 10.2.2 리플렉션을 사용한 객체 직렬화 구현

```kotlin
fun serialize(obj: Any): String = buildString {
    serializeObject(obj)
}
```

`serializeObject` 함수는 객체의 프로퍼티를 열거하고 각 프로퍼티의 이름과 값을 문자열로 만들어서 결과 문자열에 추가한다.

```kotlin
private fun StringBuilder.serializeObject(obj: Any) {
    obj.javaClass.kotlin.memberProperties
        .filter { it.findAnnotation<JsonExclude>() == null }
        .joinToStringBuilder(this, prefix = "{", postfix = "}") {
            serializeProperty(it, obj)
        }
}
```

`memberProperties` 확장 프로퍼티는 클래스의 모든 프로퍼티를 반환한다. `memberProperties`는 `KProperty`의 리스트를 반환한다.

```kotlin
private fun StringBuilder.serializeProperty(prop: KProperty1<Any, *>, obj: Any) {
    val jsonNameAnn = prop.findAnnotation<JsonName>()
    val propName = jsonNameAnn?.name ?: prop.name
    serializeString(propName)
    append(": ")
    serializePropertyValue(prop.get(obj))
}
```


## 10.2.4 JSON 파싱과 객체 역직렬화
제이키드의 JSON 역직렬화기는 흔히 쓰는 방법을 따라 3단계로 구현돼 있다. 첫 단계는 어휘 분석기(`lexical analyzer`)로 `lexer`라고도 부른다.
두 번째 단계는 문법 분석기(`syntax parser`)로 `parser`라고도 부른다. 세 번째 단계는 파싱한 결과로 객체를 생성하는 역직렬화 컴포넌트다.

어휘 분석기는 여러 문자로 이뤄진 입력 문자열을 토큰의 리스트로 변환한다.
토큰에는 2가지 종류가 있다. 문자 토큰은 문자를 표현하며 `JSON`문법에서 중요한 의미가 있다.
값 토큰은 문자열, 수, 불리언 값, null 상수를 말한다.

`JsonObject` 인터페이스는 현재 역직렬화하는 중인 객체나 배열을 추적한다.

```kotlin
interface JsonObject {
    fun setSimpleProperty(propertyName: String, value: Any?)
    fun createObject(propertyName: String): JsonObject
    fun createArray(propertyName: String): JsonObject
}
```

```kotlin
fun parseJsonObject(input: String): JsonObject {
    val lexer = JsonLexer(input)
    val parser = JsonParser(lexer)
    return parser.parse()
}
```